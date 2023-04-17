package dev.boletomicroservice.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.boletomicroservice.properties.AppProperties;

/**
 * User
 * 
 * This class represents a User object.
 * 
 * @author Gabriel Meneses
 * @version 1.0
 */
@Document(collection = "user")
public class User {

	@Id
	private ObjectId id;
	private String name;
	@Indexed(unique=true)
	private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String secret;
    
    /**
	 * This is the constructor of the User class.
	 */
    public User() {}

	/**
	 * This is the constructor of the User class.
	 * 
	 * @param id    The id of the user.
	 * @param name  The name of the user.
	 * @param email The email of the user.
	 */
	public User(ObjectId id, String name, String email) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
	}

	/**
	 * This is the constructor of the User class.
	 * 
	 * @param name  The name of the user.
	 * @param email The email of the user.
	 */
	public User(String name, String email) {
		super();
		this.name = name;
		this.email = email;
	}
	
	/**
	 * This is the constructor of the User class.
	 * 
	 * @param email The email of the user.
	 * @param secret  The name of the user.
	 */
	public User(String name, String email, String secret) {
		super();
		this.name = name;
		this.secret = secret;
		this.email = email;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
}
