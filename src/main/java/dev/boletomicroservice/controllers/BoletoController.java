package dev.boletomicroservice.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.boletomicroservice.models.Boleto;
import dev.boletomicroservice.properties.AppProperties;
import dev.boletomicroservice.services.BoletoService;
import dev.boletomicroservice.util.JsonUtil;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * BoletoController
 * 
 * This class is responsible for handling requests for boleto information. It is
 * responsible for validating the request, retrieving the boleto information,
 * and returning a response with the boleto information.
 * 
 * @author Gabriel Meneses
 * @version 1.0
 */
@RestController
@RequestMapping("/api/boletoservice")
@Tag(name = "Boleto Controller", description = "Responsible for handling requests for boleto information.")
public class BoletoController {

	@Autowired
	private BoletoService service;
	private AppProperties prop = AppProperties.factory();

	public BoletoController(BoletoService service) {
		this.service = service;
	}

	/**
	 * Calculates the interest on a boleto.
	 * 
	 * @return A ResponseEntity containing the boleto information or an error
	 *         message.
	 */
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Juros do boleto calculados.", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = Boleto.class)) }),
			@ApiResponse(responseCode = "503", description = "API de Boletos Builders inalcançável.", content = @Content),
			@ApiResponse(responseCode = "400", description = "Tipo de boleto inválido. Somente boletos do tipo NPC são válidos.", content = @Content),
			@ApiResponse(responseCode = "400", description = "Boleto inválido. Somente boletos vencidos.", content = @Content),
			@ApiResponse(responseCode = "503", description = "API de Boletos Builders inalcançável.", content = @Content),
			@ApiResponse(responseCode = "500", description = "Erro: Não foi possível transformar o objeto Java em uma string JSON.O objeto não possui os campos corretos ou contem os dados esperados.", content = @Content) })
	@ResponseBody
	@RequestMapping(value = "/calc-interest", method = RequestMethod.POST)
	public ResponseEntity<String> getBoletoStatus(@RequestBody Map<String, String> data) {

		if (!data.containsKey("bar_code") || !data.containsKey("payment_date")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Não foram encontrados os dados necessarios para a executar a requsição.");
		}

		String barCode = data.get("bar_code");
		String paymentDate = data.get("payment_date");

		Boleto boletoInfo = service.getBoletoByBarCodeAPI(barCode);

		// Check if the API is unavailable
		if (boletoInfo == null) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("API de Boletos Builders inalcançável.");
		}

		// Check if the boleto was not found
		if (boletoInfo.getCode() == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Boleto não encontrado");
		}

		// Check if the boleto is of the valid format
		if (!boletoInfo.getType().equals(prop.getValue("valid_format"))) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Tipo de boleto inválido. Somente boletos do tipo NPC são válidos.");
		}

		// Check if the boleto is expired
		if (!service.isExpired(boletoInfo)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Boleto inválido. Somente boletos vencidos.");
		}

		// Calculate the interest for the boleto
		boletoInfo = service.boletoInterestProces(boletoInfo, paymentDate);

		try {
			// Convert the boleto object to a JSON string
			String response = JsonUtil.toString(boletoInfo);
			return ResponseEntity.status(HttpStatus.OK).body(response);
		} catch (JsonProcessingException e) {
			// Log the error
			prop.logError(e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
					"Erro: Não foi possível transformar o objeto Java em uma string JSON.O objeto não possui os campos corretos ou contem os dados esperados.");
		}
	}
}
