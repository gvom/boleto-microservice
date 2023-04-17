package dev.boletomicroservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import dev.boletomicroservice.models.User;
import dev.boletomicroservice.properties.AppProperties;
import dev.boletomicroservice.services.UserService;

/**
 * BoletoController
 * 
 * This class is responsible for handling all user related requests.
 * It contains methods for adding, deleting, updating and retrieving users.
 * 
 * @author Gabriel Meneses
 * @version 1.0
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	private UserService userService;
	private AppProperties prop = AppProperties.factory();
	
	public UserController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * This method is responsible for adding a new user.
	 *
	 * @param data The data of the user to be added.
	 * @return The response entity with the appropriate status.
	 */
	@ResponseBody
	@RequestMapping(value = "/addUser", method = RequestMethod.POST)
	public ResponseEntity<?> addUser(@RequestBody User user) {
		prop.logDebug("Adding user with name: " + user.getName() + ", and email: " + user.getEmail());
		userService.add(user);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * This method is responsible for deleting an existing user.
	 *
	 * @param data The data of the user to be deleted.
	 * @return The response entity with the appropriate status.
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteUser/{user-id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteUser(@PathVariable("user-id") String userId) {
		
		if (userId != null) {
			if (userService.exists(userId)) {
				userService.deleteById(userId);
				prop.logDebug("Deleting user with id: " + userId);
				return new ResponseEntity<>(HttpStatus.OK);
			}
			prop.logError("User with id: " + userId + " does not exist");
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	/**
	 * This method is responsible for updating an existing user.
	 *
	 * @param data The data of the user to be updated.
	 * @return The response entity with the appropriate status.
	 */
	@ResponseBody
	@RequestMapping(value = "/updateUser", method = RequestMethod.POST)
	public ResponseEntity<?> updateUser(@RequestBody User user) {
		if (user.getId() != null) {
			String id = "" + user.getId();
			if (userService.exists(id)) {
				userService.update(user);
				return new ResponseEntity<>(HttpStatus.OK);
			}
			prop.logError("User with id: " + id + " does not exist");
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	/**
	 * This method is responsible for retrieving an existing user.
	 *
	 * @param data The data of the user to be retrieved.
	 * @return The response entity with the appropriate status.
	 */
	@ResponseBody
	@RequestMapping(value = "/getUser/{user-id}", method = RequestMethod.GET)
	public ResponseEntity<?> getUser(@PathVariable("user-id") String userId) {
		if (userId != null) {
			if (userService.exists(userId)) {
				prop.logDebug("Getting user with id: " + userId);
				return new ResponseEntity<User>(userService.findById(userId), HttpStatus.OK);
			}
			prop.logError("User with id: " + userId + " does not exist");
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
}
