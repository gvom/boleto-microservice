package dev.boletomicroservice.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import dev.boletomicroservice.controllers.UserController;
import dev.boletomicroservice.models.User;
import dev.boletomicroservice.services.UserService;

public class UserControllerTest {

	private UserController userController;
	private UserService userService;

	@Before
	public void setUp() {
		userService = mock(UserService.class);
		userController = new UserController(userService);
	}

	@Test
	public void testAddUser() {
		User user = new User("test", "teste@teste.com");

		ResponseEntity<?> expected = new ResponseEntity<>(HttpStatus.OK);

		assertEquals(expected, userController.addUser(user));
	}

	@Test
	public void testDeleteUser() {

		when(userService.exists("123")).thenReturn(true);

		ResponseEntity<?> expected = new ResponseEntity<>(HttpStatus.OK);

		assertEquals(expected, userController.deleteUser("123"));
	}

	@Test
	public void testDeleteUserNotFound() {
		when(userService.exists("123")).thenReturn(false);

		ResponseEntity<?> expected = new ResponseEntity<>(HttpStatus.NOT_FOUND);

		assertEquals(expected, userController.deleteUser("123"));
	}

	@Test
	public void testUpdateUser() {
		User user = new User("test", "teste@teste.com", "123");

		when(userService.exists("123")).thenReturn(true);

		ResponseEntity<?> expected = new ResponseEntity<>(HttpStatus.OK);

		assertEquals(expected, userController.updateUser(user));
	}

	@Test
	public void testUpdateUserNotFound() {
		User user = new User("test", "teste@teste.com", "123");

		when(userService.exists("123")).thenReturn(false);

		ResponseEntity<?> expected = new ResponseEntity<>(HttpStatus.NOT_FOUND);

		assertEquals(expected, userController.updateUser(user));
	}

	@Test
	public void testGetUser() {
		User user = new User("test", "teste@teste.com");

		when(userService.exists("123")).thenReturn(true);
		when(userService.findById("123")).thenReturn(user);

		ResponseEntity<User> expected = new ResponseEntity<>(user, HttpStatus.OK);

		assertEquals(expected, userController.getUser("123"));
	}

	@Test
	public void testGetUserNotFound() {
		when(userService.exists("123")).thenReturn(false);

		ResponseEntity<?> expected = new ResponseEntity<>(HttpStatus.NOT_FOUND);

		assertEquals(expected, userController.getUser("123"));
	}
}
