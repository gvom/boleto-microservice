package dev.boletomicroservice.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import dev.boletomicroservice.models.Boleto;
import dev.boletomicroservice.models.User;
import dev.boletomicroservice.properties.AppProperties;
import dev.boletomicroservice.security.JwtService;
import dev.boletomicroservice.services.UserService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * BoletoController
 * 
 * This class is responsible for handling all user related requests. It contains
 * methods for adding, deleting, updating and retrieving users.
 * 
 * @author Gabriel Meneses
 * @version 1.0
 */
@RestController
@RequestMapping("/api/user")
@Tag(name = "User Controller", description = "Responsible for handling requests for users like: ADD, DELETE, UPDATE, AUTH.")
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtService jwtService;

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
	@ApiResponse(responseCode = "200", description = "Usuario adicionado com sucesso.", content = @Content)
	@ResponseBody
	@RequestMapping(value = "/addUser", method = RequestMethod.POST)
	public ResponseEntity<String> addUser(@RequestBody User user) {
		prop.logDebug("Adding user with name: " + user.getName() + ", and email: " + user.getEmail());
		userService.add(user);
		return new ResponseEntity<>("Usuario adicionado com sucesso.", HttpStatus.OK);
	}

	/**
	 * This method is responsible for deleting an existing user.
	 *
	 * @param data The data of the user to be deleted.
	 * @return The response entity with the appropriate status.
	 */
	@ApiResponses(value = {
	@ApiResponse(responseCode = "200", description = "Usuario removido com sucesso.", content = @Content),
	@ApiResponse(responseCode = "404", description = "Usuario não encontrado.", content = @Content)})
	@ResponseBody
	@RequestMapping(value = "/deleteUser/{user-id}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteUser(@PathVariable("user-id") String userId) {

		if (userId != null) {
			if (userService.exists(userId)) {
				userService.deleteById(userId);
				prop.logDebug("Deleting user with id: " + userId);
				return new ResponseEntity<>("Usuario removido com sucesso.", HttpStatus.OK);
			}
			prop.logError("Usuario com id: " + userId + " não existe.");
		}

		return new ResponseEntity<>("Usuario não encontrado.", HttpStatus.NOT_FOUND);
	}

	/**
	 * This method is responsible for updating an existing user.
	 *
	 * @param data The data of the user to be updated.
	 * @return The response entity with the appropriate status.
	 */
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Usuario atualizado com sucesso.", content = @Content),
			@ApiResponse(responseCode = "404", description = "Usuario não encontrado.", content = @Content) })
	@ResponseBody
	@RequestMapping(value = "/updateUser", method = RequestMethod.POST)
	public ResponseEntity<String> updateUser(@RequestBody User user) {
		if (user.getId() != null) {
			String id = "" + user.getId();
			if (userService.exists(id)) {
				userService.update(user);
				return new ResponseEntity<String>("Usuario atualizado com sucesso.", HttpStatus.OK);
			}
			prop.logError("User with id: " + id + " does not exist");
		}

		return new ResponseEntity<>("Usuario não encontrado.", HttpStatus.NOT_FOUND);
	}

	/**
	 * This method is responsible for retrieving an existing user.
	 *
	 * @param data The data of the user to be retrieved.
	 * @return The response entity with the appropriate status.
	 */
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Usuario coletado com sucesso.", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = User.class)) }),
			@ApiResponse(responseCode = "404", description = "Usuario não encontrado.", content = @Content) })
	@ResponseBody
	@RequestMapping(value = "/getUser/{user-id}", method = RequestMethod.GET)
	public ResponseEntity<?> getUser(@PathVariable("user-id") String userId) {
		if (userId != null) {
			if (userService.exists(userId)) {
				prop.logDebug("Getting user with id: " + userId);
				return new ResponseEntity<User>(userService.findById(userId), HttpStatus.OK);
			}
			prop.logError("Usuario com id: " + userId + " não existe.");
		}

		return new ResponseEntity<String>("Usuario não encontrado.", HttpStatus.NOT_FOUND);
	}

	/**
	 * This method is responsible for authenticate an user.
	 *
	 * @param data The data of the user to be retrieved.
	 * @return The response entity with the appropriate status.
	 */
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Usuario autentificado com sucesso.", content = {
					@Content(mediaType = "application/json", schema = @Schema(example = "{\r\n"
							+ "    \"expiration\": \"2023-27-18 07:27:03\",\r\n"
							+ "    \"token\": \"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnYWJyaWVsQHRlc3RlLmNvbSIsImlhdCI6MTY4MTg1NjIyMywiZXhwIjoxNjgxODU2ODIzfQ.tiHeZhCkfzex3FISWbU38kPmUVqwtMEMzZ36UvRmcmM\"\r\n"
							+ "}")) }),
			@ApiResponse(responseCode = "404", description = "Usuario não encontrado.", content = @Content) })
	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> authenticateAndGetToken(@RequestBody Map<String, String> data) {

		if (!data.containsKey("email") || !data.containsKey("secret")) {
			return new ResponseEntity<String>("Não foram encontrados os dados necessarios para a executar a requsição.",
					HttpStatus.BAD_REQUEST);
		}

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(data.get("email"), data.get("secret")));
		if (authentication.isAuthenticated()) {
			String json = jwtService.generateToken(data.get("email"));
			return new ResponseEntity<String>(json, HttpStatus.OK);
		}

		return new ResponseEntity<>("Usuario não encontrado.", HttpStatus.NOT_FOUND);
	}
}
