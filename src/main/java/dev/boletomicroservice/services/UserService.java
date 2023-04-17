package dev.boletomicroservice.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//import dev.boletomicroservice.data.UserData;
import dev.boletomicroservice.models.User;
import dev.boletomicroservice.properties.AppProperties;
import dev.boletomicroservice.repositorys.UserRepository;

/**
 * UserService
 * 
 * This class contains the user's services for the SpringApplication class.
 * 
 * @author Gabriel Meneses
 * @version 1.0
 */
@Service
public class UserService //implements UserDetailsService{
{

	@Autowired
	private UserRepository userRepository;
	private AppProperties prop = AppProperties.factory();
	
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * Adds the given user to the database.
	 * 
	 * @param user The user to add.
	 */
	public void add(User user) {
		prop.logDebug("Adding the user in the DB.");
		userRepository.insert(user);
	}

	/**
	 * Updates the given user in the database.
	 * 
	 * @param user The user to update.
	 */
	public void update(User user) {
		prop.logDebug("Updating the user in the DB.");
		userRepository.save(user);
	}

	/**
	 * Finds all the users in the database.
	 * 
	 * @return A list of all the users in the database.
	 */
	public List<User> findAll() {
		prop.logDebug("Searching for all users in the DB.");
		return userRepository.findAll();
	}

	/**
	 * Counts the users in the database.
	 * 
	 * @return The number of users in the database.
	 */
	public long count() {
		prop.logDebug("Counting the users in the DB.");
		return userRepository.count();
	}

	/**
	 * Finds the user with the given id in the database.
	 * 
	 * @param id The id of the user to find.
	 * @return The user with the given id.
	 */
	public User findById(String id) {
		prop.logDebug("Searching the user with id: '" + id + "' in the DB.");
		Optional<User> opUser = userRepository.findById(id);
		
		if(!opUser.isPresent()) {
			return null;
		}
		
		return opUser.get();
	}

	/**
	 * Deletes the user with the given id from the database.
	 * 
	 * @param id The id of the user to delete.
	 */
	public void deleteById(String id) {
		prop.logDebug("Deleting the user with id: '" + id + "' in the DB.");
		userRepository.deleteById(id);
	}

	/**
	 * Deletes the given user from the database.
	 * 
	 * @param user The user to delete.
	 */
	public void delete(User user) {
		prop.logDebug("Deleting the user with id: '" + user.getId() + "' in the DB.");
		userRepository.delete(user);
	}

	/**
	 * Checks if a user with the given id exists in the database.
	 * 
	 * @param id Id code of the user to check.
	 * @return True if the user exists, false otherwise.
	 */
	public boolean exists(String id) {
		prop.logDebug("Checkig if the user with id: '" + id + "' exists in the DB.");
		return findById(id) != null;
	}

	/**
	 * Locates the user based on the email. 
	 * @param username the username identifying the user whose data is required.
	 * @return a fully populated user record (never <code>null</code>)
	 * @throws UsernameNotFoundException if the user could not be found or the user has no
	 * GrantedAuthority
	 */
//	@Override
//	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//		prop.logDebug("Authenticating the user with the email: '" + email + "' in the DB.");
//		Optional<User> user = Optional.of(userRepository.findByEmail(email));
//		
//		//Default user in the properties file
//		if(user.isEmpty() && prop.containsKey("default_email") && prop.containsKey("default_secret")) {
//			user = Optional.of(new User("", prop.getValue("default_email"), prop.getValue("default_secret")));
//		}
//		
//		if(user.isEmpty()) {
//			throw new UsernameNotFoundException("User with email: " + email + " not found.");
//		}
//		
//		return new UserData(user);
//	}
}
