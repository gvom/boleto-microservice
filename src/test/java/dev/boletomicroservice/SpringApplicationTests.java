package dev.boletomicroservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import dev.boletomicroservice.models.Boleto;
import dev.boletomicroservice.properties.AppProperties;
import dev.boletomicroservice.services.BoletoService;

/**
 * SpringApplicationTests.java
 * 
 * This class contains the unit tests for the SpringApplication class.
 * 
 * @author Gabriel Meneses
 * @version 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
class SpringApplicationTests {

	@Autowired
	private BoletoService service;
	private AppProperties prop = AppProperties.factory();

	/**
	 * The code bar used to test the API
	 */
	private final String CODE_BAR = prop.getValue("test_code_bar");
	private final String RAMDOM_CODE_BAR = RandomStringUtils.randomAlphanumeric(10);
	
	/**
	 * Tests the creation of a Boleto object
	 * 
	 * This test creates a Boleto object and adds it to the database. It then checks
	 * if the object was successfully added to the database.
	 */
	@Test
	void testCreat() {
		// Create a Boleto object
		Boleto b = new Boleto();
		b.setAmount(BigDecimal.ZERO);
		b.setCode(RAMDOM_CODE_BAR);
		b.setDue_date(LocalDate.now().minusDays(1));
		b.setType("NORMAL");

		// Add the Boleto object to the database
		service.add(b);

		// Check if the Boleto object was successfully added to the database
		assertNotNull(service.findByBarCode("teste"));
		
		//Comands to return the DB in the previous state
		service.delete(b);
	}

	/**
	 * Tests the retrieval of a single Boleto object
	 * 
	 * This test retrieves a single Boleto object from the database and checks if
	 * the amount is correct.
	 */
	@Test
	void testReadSingle() {
		// Retrieve a Boleto object from the database
		Boleto b = service.findByBarCode("teste");

		// Check if the amount is correct
		assertEquals(BigDecimal.ZERO, b.getAmount());
	}

	/**
	 * Tests the retrieval of all Boleto objects
	 * 
	 * This test retrieves all Boleto objects from the database and checks if the
	 * list is not empty.
	 */
	@Test
	void testReadAll() {
		// Retrieve all Boleto objects from the database
		List<Boleto> list = service.findAll();

		// Check if the list is not empty
		assertTrue(list.size() > 0);
	}

	/**
	 * Tests the expiration of a Boleto object
	 * 
	 * This test checks if a Boleto object is expired.
	 */
	@Test
	void testIsExpired() {
		// Retrieve a Boleto object from the database
		Boleto b = service.findByBarCode("teste");

		// Check if the Boleto object is expired
		assertTrue(service.isExpired(b));
	}

	/**
	 * Tests the API
	 * 
	 * This test checks if the API returns a Boleto object with the correct code
	 * bar.
	 */
	@Test
	void testAPI() {
		// Retrieve a Boleto object from the API
		Boleto b = service.getBoletoByBarCodeAPI(CODE_BAR);

		// Check if the Boleto object is not null and has the correct code bar
		assertTrue(b != null && b.getCode().equals(CODE_BAR));
	}

	/**
	 * Tests the calculation of the interest
	 * 
	 * This test checks if the original amount of the Boleto object is correct after
	 * the interest calculation.
	 */
	@Test
	void testCalc() {
		// Retrieve a Boleto object from the API
		Boleto b = service.getBoletoByBarCodeAPI(CODE_BAR);

		// Store the original amount of the Boleto object
		BigDecimal amount = b.getAmount();
		
		// Transform the current date to String with the format: yyyy-MM-dd
		final String dateFormat = "yyyy-MM-dd";
		LocalDate localDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		String dateText = localDate.format(formatter);

		// Calculate the interest
		b = service.boletoInterestProces(b, dateText);

		// Check if the original amount is correct
		assertTrue(b != null && b.getOriginal_amount().equals(amount));
		
		//Comands to return the DB in the previous state
		service.delete(b);
	}

	/**
	 * Tests the update of a Boleto object
	 * 
	 * This test updates the type of a Boleto object and checks if the update was
	 * successful.
	 */
	@Test
	void testUpdate() {
		// The new type of the Boleto object
		String newType = "NPC", oldType = "NORMAL";

		// Retrieve a Boleto object from the database
		Boleto b = service.findByBarCode("teste");

		// Update the type of the Boleto object
		b.setType(newType);

		// Update the Boleto object in the database
		service.update(b);

		// Check if the update was successful
		assertEquals(service.findByBarCode("teste").getType(), newType);
		
		//Comands to return the DB in the previous state
		b.setType(oldType);
		service.update(b);
	}

	/**
	 * Tests the deletion of a Boleto object
	 * 
	 * This test deletes two Boleto objects from the database and checks if the
	 * deletion was successful.
	 */
	@Test
	void testDelete() {
		// Create a Boleto object
		Boleto b = new Boleto();
		b.setAmount(BigDecimal.ZERO);
		b.setCode(RAMDOM_CODE_BAR);
		b.setDue_date(LocalDate.now().minusDays(1));
		b.setType("NORMAL");

		// Add the Boleto object to the database
		service.add(b);
		
		// Retrieve two Boleto objects from the database
		b = service.findByBarCode(RAMDOM_CODE_BAR);

		// Delete the Boleto object from the database
		service.delete(b);

		// Check if the deletion was successful
		assertNull(service.findByBarCode(RAMDOM_CODE_BAR));
	}

}
