import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;

import models.User;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.data.DynamicForm;
import play.data.validation.ValidationError;
import play.data.validation.Constraints.RequiredValidator;
import play.i18n.Lang;
import play.libs.F;
import play.libs.F.*;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.twirl.api.Content;

import static play.test.Helpers.*;
import static org.junit.Assert.*;


/**
 *
 * Simple (JUnit) tests that can call all parts of a play app.
 * If you are interested in mocking a whole application, see the wiki for more details.
 *
 */
public class ApplicationTest {

	 private static final String BASE_URL = "http://localhost:9000/user";
	 
	 private static final Logger LOGGER = Logger.getLogger(ApplicationTest.class.getName());

		@Test
	public void testInServer() throws Exception {
	    TestServer server = testServer(3333);
	    running(server, () -> {
	        try {
	            WSClient ws = play.libs.ws.WS.newClient(3333);
	            CompletionStage<WSResponse> completionStage = ws.url("/").get();
	            WSResponse response = completionStage.toCompletableFuture().get();
	            ws.close();
	            assertEquals(OK, response.getStatus());
	        } catch (Exception e) {
	        	LOGGER.log(Level.SEVERE, e.getMessage(), e);
	        }
	    });
	}
	    @Test
	    public void whenCreatesRecord_thenCorrect() {
	        User user = new User("jody", "west", 50);
	        JSONObject obj = new JSONObject(makeRequest(BASE_URL, "POST", new JSONObject(user)));
	        assertTrue(obj.getBoolean("isSuccessfull"));
	        JSONObject body = obj.getJSONObject("body");
	        assertEquals(user.getAge(), body.getInt("age"));
	        assertEquals(user.getFirstName(), body.getString("firstName"));
	        assertEquals(user.getLastName(), body.getString("lastName"));
	    }

	    @Test
	    public void whenDeletesCreatedRecord_thenCorrect() {
	        User user = new User("Usain", "Bolt", 25);
	        JSONObject ob1 = new JSONObject(makeRequest(BASE_URL, "POST", new JSONObject(user))).getJSONObject("body");
	        int id = ob1.getInt("id");
	        JSONObject obj1 = new JSONObject(makeRequest(BASE_URL + "/" + id, "POST", new JSONObject()));
	        assertTrue(obj1.getBoolean("isSuccessfull"));
	        makeRequest(BASE_URL + "/" + id, "DELETE", null);
	        JSONObject obj2 = new JSONObject(makeRequest(BASE_URL + "/" + id, "POST", new JSONObject()));
	        assertFalse(obj2.getBoolean("isSuccessfull"));
	    }

	    @Test
	    public void whenUpdatesCreatedRecord_thenCorrect() {
	        User user = new User("john", "doe", 50);
	        JSONObject body1 = new JSONObject(makeRequest(BASE_URL, "POST", new JSONObject(user))).getJSONObject("body");
	        assertEquals(user.getAge(), body1.getInt("age"));
	        int newAge = 60;
	        body1.put("age", newAge);
	        JSONObject body2 = new JSONObject(makeRequest(BASE_URL, "PUT", body1)).getJSONObject("body");
	        assertFalse(user.getAge() == body2.getInt("age"));
	        assertTrue(newAge == body2.getInt("age"));
	    }

	    @Test
	    public void whenGetsAllRecords_thenCorrect() {
	        User user1 = new User("jane", "daisy", 50);
	        User user2 = new User("john", "daniel", 60);
	        User user3 = new User("don", "mason", 55);
	        User user4 = new User("scarlet", "ohara", 90);

	        makeRequest(BASE_URL, "POST", new JSONObject(user1));
	        makeRequest(BASE_URL, "POST", new JSONObject(user2));
	        makeRequest(BASE_URL, "POST", new JSONObject(user3));
	        makeRequest(BASE_URL, "POST", new JSONObject(user4));

	        JSONObject objects = new JSONObject(makeRequest(BASE_URL, "GET", null));
	        assertTrue(objects.getBoolean("isSuccessfull"));
	        JSONArray array = objects.getJSONArray("body");
	        assertTrue(array.length() >= 4);
	    }

	    public static String makeRequest(String myUrl, String httpMethod, JSONObject parameters) {

	        URL url = null;
	        try {
	            url = new URL(myUrl);
	        } catch (MalformedURLException e) {
	            e.printStackTrace();
	        }
	        HttpURLConnection conn = null;
	        try {

	            conn = (HttpURLConnection) url.openConnection();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        conn.setDoInput(true);

	        conn.setReadTimeout(10000);

	        conn.setRequestProperty("Content-Type", "application/json");
	        DataOutputStream dos = null;
	        int respCode = 0;
	        String inputString = null;
	        try {
	            conn.setRequestMethod(httpMethod);

	            if (Arrays.asList("POST", "PUT").contains(httpMethod)) {
	                String params = parameters.toString();

	                conn.setDoOutput(true);

	                dos = new DataOutputStream(conn.getOutputStream());
	                dos.writeBytes(params);
	                dos.flush();
	                dos.close();
	            }
	            respCode = conn.getResponseCode();
	            if (respCode != 200 && respCode != 201) {
	                String error = inputStreamToString(conn.getErrorStream());
	                return error;
	            }
	            inputString = inputStreamToString(conn.getInputStream());

	        } catch (IOException e) {

	            e.printStackTrace();
	        }
	        return inputString;
	    }

	    public static String inputStreamToString(InputStream is) {
	        BufferedReader br = null;
	        StringBuilder sb = new StringBuilder();

	        String line;
	        try {

	            br = new BufferedReader(new InputStreamReader(is));
	            while ((line = br.readLine()) != null) {
	                sb.append(line);
	            }

	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (br != null) {
	                try {
	                    br.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }

	        return sb.toString();

	    }


}
