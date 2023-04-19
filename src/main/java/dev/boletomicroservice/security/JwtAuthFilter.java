package dev.boletomicroservice.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import dev.boletomicroservice.properties.AppProperties;
import dev.boletomicroservice.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * JwtAuthFilter
 * 
 * This class is a filter that is used to validate a JWT token.
 * It extends the BasicAuthenticationFilter class and overrides the doFilterInternal() method.
 * 
 * @author Gabriel Meneses
 * @version 1.0
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	@Autowired
	private JwtService jwtService;
	@Autowired
	private UserService service;

	private AppProperties prop = AppProperties.factory();

	/**
	 * This method is used to validate a JWT token. It is called once per request
	 * and checks if the request contains a valid JWT token. If the token is valid,
	 * the user is authenticated.
	 * 
	 * @param request     The request object
	 * @param response    The response object
	 * @param filterChain The filter chain object
	 * 
	 * @throws ServletException If a servlet-related error occurs
	 * @throws IOException      If an I/O error occurs
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader(prop.getValue("header_att"));
		String authAtt = prop.getValue("auth_att");
		String token = null;
		String email = null;

		// Check if the authorization header is present and starts with the auth
		// attribute
		if (authHeader != null && authHeader.startsWith(authAtt)) {
			// Extract the token from the authorization header
			token = authHeader.substring(authAtt.toCharArray().length);
			// Extract the email from the token
			email = jwtService.extractUsername(token);
		}

		// Check if the username is present and the user is not authenticated
		if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			// Load the user details from the username
			UserDetails userDetails = service.loadUserByUsername(email);
			// Validate the token
			if (jwtService.validateToken(token, userDetails)) {
				// Create an authentication token
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());
				// Set the authentication details
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				// Set the authentication token in the security context
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		} else if (isPreflight(request) || isSwaggerRequest(request)) {
			// Create an authentication token
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(null, null, null);
			// Set the authentication details
			authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			// Set the authentication token in the security context
			SecurityContextHolder.getContext().setAuthentication(authToken);
		}

		// Continue with the filter chain
		filterChain.doFilter(request, response);
	}

	/**
	 * This method is used to check if the request is a preflight request.
	 * 
	 * @param request The request object
	 * 
	 * @return true if the request is a preflight request, false otherwise
	 */
	private boolean isSwaggerRequest(HttpServletRequest request) {
		String uri = request.getRequestURI();
		return uri.contains("swagger") || uri.contains("api-docs") || uri.contains("webjars");
	}

	/**
	 * This method is used to check if the request is a Swagger request.
	 * 
	 * @param request The request object
	 * 
	 * @return true if the request is a Swagger request, false otherwise
	 */
	private boolean isPreflight(HttpServletRequest request) {
		return request.getMethod().equals(HttpMethod.OPTIONS.toString());
	}
}