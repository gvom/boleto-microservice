package dev.boletomicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import dev.boletomicroservice.properties.AppProperties;

/**
 * Boleto microservice Application
 * 
 * This class is the main entry point for the Boletomicroservice application. It
 * extends the SpringBootServletInitializer class and is annotated with the
 * 
 * @SpringBootApplication annotation.
 * 
 * @author Gabriel Meneses
 * @version 1.0
 */
@EnableWebMvc
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

	private static AppProperties prop = AppProperties.factory();
	
	/**
	 * Main method to start the Boletomicroservice application.
	 * 
	 * @param args The command line arguments.
	 */
	public static void main(String[] args) {
		try {
			SpringApplication.run(Application.class, args);
		} catch (Exception e) {
			// Log the exception
			prop.logError(e);
			//e.printStackTrace();
		}
	}

}
