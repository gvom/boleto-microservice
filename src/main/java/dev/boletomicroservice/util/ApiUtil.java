package dev.boletomicroservice.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.boletomicroservice.models.Boleto;
import dev.boletomicroservice.properties.AppProperties;

/**
 * ApiUtil
 * 
 * ApiUtil is a class that provides methods to make API calls and get a Boleto
 * object from the response.
 * 
 * @author Gabriel Meneses
 * @version 1.0
 */
public class ApiUtil {

	private static AppProperties prop = AppProperties.factory();
	private static final String API_AUTH_URL = prop.getValue("api_auth_url");
	private static final String API_URL = prop.getValue("api_url");
	private static final String CLIENT_ID = prop.getValue("client_id");
	private static final String CLIENT_SECRET = prop.getValue("client_secret");

	private static String authToken;
	private static LocalDateTime expireDate;
	
	private ApiUtil() {}

	/**
	 * Makes an API call to the given barcode and returns a Boleto object
	 *
	 * @param barCode the barcode to be used in the API call
	 * @return Boleto object
	 */
	public static Boleto apiAcess(String barCode) {
		String response;
		try {
			// If the expired time object is not null, cheks if the token has expired
			boolean timeExpired = false;
			if (expireDate != null) {
				timeExpired = expireDate.isAfter(LocalDateTime.now());
			}

			// Checks if the token is alredy generated and if is not expired
			if (authToken == null || timeExpired) {
				if (!apiAuth())
					throw new IOException("Authentication failed.");
			}

			// Prepare a connection HTTP
			URL url = new URL(API_URL);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", authToken);
			conn.setDoOutput(true);

			// Prepare the request body
			Map<String, String> mapBody = new HashMap<>();
			mapBody.put("code", barCode);

			String requestBody = new ObjectMapper().writeValueAsString(mapBody);
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			writer.write(requestBody);
			writer.flush();

			// Read API response
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder responseBuilder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				responseBuilder.append(line);
			}
			reader.close();
			response = responseBuilder.toString();

			prop.logDebug("Server response: \n" + response);

			// Close the HTTP connection
			conn.disconnect();

		} catch (IOException e) {
			prop.logError(e);
			return null;
		}

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			return objectMapper.readValue(response, Boleto.class);
		} catch (JsonProcessingException e) {
			prop.logError(e);
			return new Boleto();
		}
	}

	/**
	 * Makes an authentication API call.
	 * 
	 * @return true if the authentication was successful, false otherwise.
	 */
	private static boolean apiAuth() {
		try {
			// Prepare a connection HTTP
			URL url = new URL(API_AUTH_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			// Prepare the request body
			Map<String, String> mapBody = new HashMap<>();
			mapBody.put("client_id", CLIENT_ID);
			mapBody.put("client_secret", CLIENT_SECRET);

			String requestBody = new ObjectMapper().writeValueAsString(mapBody);
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			writer.write(requestBody);
			writer.flush();

			// Read API response
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder responseBuilder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				responseBuilder.append(line);
			}
			reader.close();
			String response = responseBuilder.toString();

			prop.logDebug("Server authentication: \n" + response);

			JsonNode jsonNode = new ObjectMapper().readTree(response);
			authToken = jsonNode.get("token").asText();

			// Date Time Zoned Example: "2022-09-13T14:29:10.286494"
			// Match with format -> yyyy-MM-dd'T'HH:mm:ss'.'SSS
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
			expireDate = LocalDateTime.parse(jsonNode.get("expires_in").asText(), formatter);

			// Close the HTTP connection
			conn.disconnect();

			return true;

		} catch (Exception e) {
			prop.logError(e);
			return false;
		}
	}
}
