package dev.boletomicroservice.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.boletomicroservice.controllers.BoletoController;
import dev.boletomicroservice.models.Boleto;
import dev.boletomicroservice.services.BoletoService;

public class BoletoControllerTest {

	private BoletoController controller;
	@Autowired
	private BoletoService service;

	@Before
	public void setUp() {
		service = mock(BoletoService.class);
		controller = new BoletoController(service);
	}

	@Test
	public void testGetBoletoStatusSuccess() throws JsonProcessingException {
		String barCode = "12345678901234567890123456789012345678901234";
		String paymentDate = "2021-10-01";
		Map<String, String> data = new HashMap<>();
		data.put("bar_code", barCode);
		data.put("payment_date", paymentDate);

		Boleto boletoInfo = new Boleto();
		boletoInfo.setCode("12345678901234567890123456789012345678901234");
		boletoInfo.setType("NPC");
		when(service.getBoletoByBarCodeAPI(barCode)).thenReturn(boletoInfo);
		when(service.isExpired(boletoInfo)).thenReturn(true);
		when(service.boletoInterestProces(boletoInfo, paymentDate)).thenReturn(boletoInfo);

		String expectedResponse = new ObjectMapper().writeValueAsString(boletoInfo);

		ResponseEntity<String> response = controller.getBoletoStatus(data);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(expectedResponse, response.getBody());
	}

	@Test
	public void testGetBoletoStatusBadRequest() {
		Map<String, String> data = new HashMap<>();
		data.put("bar_code", "12345678901234567890123456789012345678901234");

		ResponseEntity<String> response = controller.getBoletoStatus(data);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void testGetBoletoStatusServiceUnavailable() {
		String barCode = "12345678901234567890123456789012345678901234";
		String paymentDate = "2021-10-01";
		Map<String, String> data = new HashMap<>();
		data.put("bar_code", barCode);
		data.put("payment_date", paymentDate);

		when(service.getBoletoByBarCodeAPI(barCode)).thenReturn(null);

		ResponseEntity<String> response = controller.getBoletoStatus(data);

		assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
	}

	@Test
	public void testGetBoletoStatusNotFound() {
		String barCode = "12345678901234567890123456789012345678901234";
		String paymentDate = "2021-10-01";
		Map<String, String> data = new HashMap<>();
		data.put("bar_code", barCode);
		data.put("payment_date", paymentDate);

		Boleto boletoInfo = new Boleto();
		when(service.getBoletoByBarCodeAPI(barCode)).thenReturn(boletoInfo);

		ResponseEntity<String> response = controller.getBoletoStatus(data);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	public void testGetBoletoStatusInvalidType() {
		String barCode = "12345678901234567890123456789012345678901234";
		String paymentDate = "2021-10-01";
		Map<String, String> data = new HashMap<>();
		data.put("bar_code", barCode);
		data.put("payment_date", paymentDate);

		Boleto boletoInfo = new Boleto();
		boletoInfo.setCode("12345678901234567890123456789012345678901234");
		boletoInfo.setType("invalid_type");
		when(service.getBoletoByBarCodeAPI(barCode)).thenReturn(boletoInfo);

		ResponseEntity<String> response = controller.getBoletoStatus(data);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}

	@Test
	public void testGetBoletoStatusNotExpired() {
		String barCode = "12345678901234567890123456789012345678901234";
		String paymentDate = "2021-10-01";
		Map<String, String> data = new HashMap<>();
		data.put("bar_code", barCode);
		data.put("payment_date", paymentDate);

		Boleto boletoInfo = new Boleto();
		boletoInfo.setCode("12345678901234567890123456789012345678901234");
		boletoInfo.setType("NPC");
		when(service.getBoletoByBarCodeAPI(barCode)).thenReturn(boletoInfo);
		when(service.isExpired(boletoInfo)).thenReturn(false);

		ResponseEntity<String> response = controller.getBoletoStatus(data);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
}
