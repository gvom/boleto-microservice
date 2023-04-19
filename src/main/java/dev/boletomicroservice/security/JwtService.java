package dev.boletomicroservice.security;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import dev.boletomicroservice.properties.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import net.minidev.json.JSONObject;

/**
 * JwtService is a class responsible for generating and validating JWT tokens.
 * It uses the AppProperties class to get the secret and expiration time from
 * the application's properties.
 * 
 * @author Gabriel Meneses
 * @version 1.0
 */
@Component
public class JwtService {

	private static AppProperties prop = AppProperties.factory();
	public static final String SECRET = prop.getValue("token");
	public static final Integer EXPIRATION_MILLIS = prop.getValueAsInteger("token_expiration_millis");

	/**
	 * Extracts the username from the JWT token.
	 * 
	 * @param token JWT token
	 * @return username
	 */
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	/**
	 * Extracts the expiration date from the JWT token.
	 * 
	 * @param token JWT token
	 * @return expiration date
	 */
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	/**
	 * Extracts the claim from the JWT token.
	 * 
	 * @param token          JWT token
	 * @param claimsResolver function used to extract the claim
	 * @return claim
	 */
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * Extracts all claims from the JWT token.
	 * 
	 * @param token JWT token
	 * @return claims
	 */
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
	}

	/**
	 * Checks if the JWT token is expired.
	 * 
	 * @param token JWT token
	 * @return true if the token is expired, false otherwise
	 */
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	/**
	 * Validates the JWT token.
	 * 
	 * @param token       JWT token
	 * @param userDetails user details
	 * @return true if the token is valid, false otherwise
	 */
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	/**
	 * Generates a JWT token.
	 * 
	 * @param userName username
	 * @return JWT token
	 */
	public String generateToken(String userName) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, userName);
	}

	/**
	 * Creates a JWT token.
	 * 
	 * @param claims claims
	 * @param email  email
	 * @return JWT token
	 */
	private String createToken(Map<String, Object> claims, String email) {
		Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_MILLIS);
		String token = Jwts.builder().setClaims(claims).setSubject(email)
				.setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(expirationDate)
				.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();

		SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		String formatedExpirationDate = dt.format(expirationDate);

		Map map = new HashMap<String, String>();
		map.put("token", token);
		map.put("expiration", formatedExpirationDate);

		return new JSONObject(map).toString();
	}

	/**
	 * Gets the signing key used to generate the JWT token.
	 * 
	 * @return signing key
	 */
	private Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET.replaceAll("-", ""));
		return Keys.hmacShaKeyFor(keyBytes);
	}
}