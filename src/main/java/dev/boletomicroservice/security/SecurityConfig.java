package dev.boletomicroservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import dev.boletomicroservice.properties.AppProperties;
import dev.boletomicroservice.services.UserService;

/**
 * SecurityConfig
 * 
 * This class is used to configure the authentication and authorization of the application.
 * It implements the configure() methods to configure the authentication and authorization of the application.
 * 
 * @author Gabriel Meneses
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	
    @Autowired
    private JwtAuthFilter authFilter;
    @Autowired
    private UserService userService;
    
    private AppProperties prop = AppProperties.factory();

    public static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	private static final String[] SWAGGER_PATHS = {
			"/swagger-ui.html", "/v3/api-docs/**", "/api-docs", "/swagger-ui.html",
			"/swagger-ui/**", "/webjars/swagger-ui/**"};

	/**
	 * This method is used to create a UserDetailsService bean.
	 * 
	 * @return UserDetailsService
	 */
    @Bean
    public UserDetailsService userDetailsService() {
    	if(prop.getValueAsBoolean("in_memory_user")) {
	    	//In memory user
	        UserDetails admin = User.withUsername(prop.getValue("default_email"))
	                .password(passwordEncoder.encode(prop.getValue("default_secret")))
	                .roles("ADMIN")
	                .build();
	        return new InMemoryUserDetailsManager(admin);
    	}else {        
	        //Users in DB
	        return userService;
    	}
    }

    /**
     * Configures the HttpSecurity of the application
     * 
     * @param http The HttpSecurity to be used
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable().cors().disable()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.POST, "/api/user/authenticate").permitAll()
                .requestMatchers(SWAGGER_PATHS).permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * This bean is used to create a PasswordEncoder object which is used to encode passwords.
     * 
     * @return PasswordEncoder object
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
    	return passwordEncoder;
    }

    /**
     * This bean is used to create an AuthenticationProvider object which is used to authenticate users.
     * 
     * @return AuthenticationProvider object
     */
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
    
    /**
     * This bean is used to create an AuthenticationManager object which is used to manage authentication.
     * 
     * @param config AuthenticationConfiguration object
     * @return AuthenticationManager object
     * @throws Exception if authentication fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    /**
     * Configures the CorsConfigurationSource of the application
     * 
     * @return The CorsConfigurationSource to be used
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

}