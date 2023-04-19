package dev.boletomicroservice.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import dev.boletomicroservice.models.User;
import dev.boletomicroservice.repositorys.UserRepository;

/**
 * UserServiceTest is a class that contains unit tests for the UserService class.
 *
 * @author Gabriel Meneses
 * @version 1.0
 */
public class UserServiceTest {

	@Autowired
	private UserService userService;
	private UserRepository userRepository;

	/**
	 * This method is called before each test is executed.
	 */
	@Before
	public void setUp() {
		userRepository = mock(UserRepository.class);
		userService = new UserService(userRepository);		
	}

	@Test
	public void testAdd() {
		User user = new User("teste", "teste@teste.com");
		userService.add(user);
	}

	@Test
	public void testUpdate() {
		User user = new User("teste", "teste@teste.com");
		userService.update(user);
	}

	@Test
	public void testFindAll() {
		List<User> userList = new ArrayList<>();
		when(userRepository.findAll()).thenReturn(userList);
		List<User> result = userService.findAll();
		assertNotNull(result);
		assertEquals(userList, result);
	}

	@Test
	public void testCount() {
		when(userRepository.count()).thenReturn(1L);
		long result = userService.count();
		assertEquals(1L, result);
	}

	@Test
	public void testFindById() {
		User user = new User("teste", "teste@teste.com");
		ObjectId id = new ObjectId("123");
		user.setId(id);
		Optional<User> opUser = Optional.of(user);
		when(userRepository.findById("123")).thenReturn(opUser);
		User result = userService.findById("123");
		assertNotNull(result);
		assertEquals(user, result);
	}

	@Test
	public void testDeleteById() {
		userService.deleteById("123");
	}

	@Test
	public void testDelete() {
		User user = new User("teste", "teste@teste.com");
		ObjectId id = new ObjectId("123");
		user.setId(id);
		userService.delete(user);
	}

	@Test
	public void testExists() {
		User user = new User("teste", "teste@teste.com");
		ObjectId id = new ObjectId("123");
		user.setId(id);
		Optional<User> opUser = Optional.of(user);
		when(userRepository.findById("123")).thenReturn(opUser);
		boolean result = userService.exists("123");
		assertTrue(result);
		result = userService.exists("222");
		assertFalse(result);
	}
}
