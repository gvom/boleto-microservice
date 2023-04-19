package dev.boletomicroservice.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import dev.boletomicroservice.properties.AppProperties;

/**
 * This class is used to store user data and provide access to it.
 * It implements the UserDetails interface from the Spring Security library.
 * It is used to authenticate users and provide access to the application.
 * 
 * @author Gabriel Meneses
 * @version 1.0
 */
public class UserData implements UserDetails {

    private final Optional<User> user;
    
    private AppProperties prop = AppProperties.factory();

    /**
     * Constructor for the UserData class.
     * @param user The user object that contains the user data.
     */
    public UserData(Optional<User> user) {
        this.user = user;
    }

    /**
     * This method is used to get the user's authorities.
     * @return A collection of GrantedAuthority objects.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    /**
     * This method is used to get the user's password.
     * @return The user's password.
     */
    @Override
    public String getPassword() {
        String secret = user.orElse(new User()).getSecret();
        prop.logDebug("Retrieving user secret");
        return secret;
    }

    /**
     * This method is used to get the user's username.
     * @return The user's username.
     */
    @Override
    public String getUsername() {
        String email = user.orElse(new User()).getEmail();
        prop.logDebug("Retrieving user email");
        return email;
    }

    /**
	 * This method is used to check if the user's account is not expired.
	 * @return true if the account is not expired, false otherwise.
	 */
	@Override
	public boolean isAccountNonExpired() {
		//prop.logDebug("Checking if account is not expired");
		return true;
	}

	/**
	 * This method is used to check if the user's account is not locked.
	 * @return true if the account is not locked, false otherwise.
	 */
	@Override
	public boolean isAccountNonLocked() {
		//prop.logDebug("Checking if account is not locked");
		return true;
	}

	/**
	 * This method is used to check if the user's credentials are not expired.
	 * @return true if the credentials are not expired, false otherwise.
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		//prop.logDebug("Checking if credentials are not expired");
		return true;
	}

	/**
	 * This method is used to check if the user is enabled.
	 * @return true if the user is enabled, false otherwise.
	 */
	@Override
	public boolean isEnabled() {
		//prop.logDebug("Checking if user is enabled");
		return true;
	}

}