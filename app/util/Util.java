/**
 * 
 */
package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;

/**
 * Utility class.
 * 
 * @author Sajeesh
 *
 */
public final class Util {
	/**
	 * Create an JSON object node for passed in response.
	 * 
	 * @param response Response to convert to.
	 * @param ok value of successful call
	 * @return JSON object node.
	 */
	public static ObjectNode createResponse(Object response, boolean ok) {

		ObjectNode result = Json.newObject();
		result.put("isSuccessfull", ok);
		if (response instanceof String) {
			result.put("body", (String) response);
		} else {
			result.put("body", (JsonNode) response);
		}

		return result;
	}

}
