package dev.boletomicroservice.repositorys;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import dev.boletomicroservice.models.User;

/**
 * UserRepository
 * 
 * This class is a repository class for the User model. It extends the MongoRepository class
 * and provides methods for querying the database for User objects.
 * 
 * @author Gabriel Meneses
 * @version 1.0
 */
@Repository
public interface UserRepository extends MongoRepository<User, String>{
	/**
	 * This method finds a User object in the MongoDB database by its email.
	 * 
	 * @param email The email of the User object to find.
	 * @return The User object with the given email, or null if no such object
	 *         exists.
	 */
	public User findByEmail(String email);
}
