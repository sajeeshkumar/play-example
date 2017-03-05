/**
 * 
 */
package controllers;

import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import data.UserRepository;
import exception.UserNotFoundException;
import models.User;
import play.mvc.Result;
import play.libs.Json;
import play.mvc.Controller;
import util.Util;

/**
 * Controller class to handle all REST APIs on user entity.
 * 
 * @author Sajeesh
 *
 */
public class UserController extends Controller {

	/**
	 * Add a user record.
	 * 
	 * @return User record created.
	 */
	public Result create() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest(Util.createResponse("Expecting Json data", false));
		}
		User user = UserRepository.getInstance().add(Json.fromJson(json, User.class));
		JsonNode jsonObject = Json.toJson(user);
		return created(Util.createResponse(jsonObject, true));
	}

	/**
	 * Update a user object.
	 * 
	 * @return updated user.
	 */
	public Result update() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			return badRequest(Util.createResponse("Expecting Json data", false));
		}
		try {
			User user = UserRepository.getInstance().update(Json.fromJson(json, User.class));
			JsonNode jsonObject = Json.toJson(user);
			return ok(Util.createResponse(jsonObject, true));
		} catch (UserNotFoundException e) {
			return notFound(Util.createResponse("user not found", false));
		}

	}

	/**
	 * Retrieve user object for passed in Id.
	 * @param id Id of the user to search for.
	 * @return User object searched for.
	 */
	public Result retrieve(int id) {
		final User user = UserRepository.getInstance().get(id);
		if (user == null) {
			return notFound(Util.createResponse("User with id:" + id + " not found", false));
		}
		JsonNode jsonObjects = Json.toJson(user);
		return ok(Util.createResponse(jsonObjects, true));
	}

	/**
	 * List all users.
	 * @return list of all users.
	 */
	public Result listUsers() {
		Set<User> result = UserRepository.getInstance().getAll();
		ObjectMapper mapper = new ObjectMapper();

		JsonNode jsonData = mapper.convertValue(result, JsonNode.class);
		return ok(Util.createResponse(jsonData, true));

	}

	/**
	 * Delete user with id.
	 * @param id id of user to delete.
	 * @return successful or not.
	 */
	public Result delete(final int id) {
		if (!UserRepository.getInstance().delete(id)) {
			return notFound(Util.createResponse("User with id:" + id + " not found", false));
		}
		return ok(Util.createResponse("User with id:" + id + " deleted", true));
	}

}
