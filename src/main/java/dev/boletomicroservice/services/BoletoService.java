package dev.boletomicroservice.services;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.boletomicroservice.models.Boleto;
import dev.boletomicroservice.properties.AppProperties;
import dev.boletomicroservice.repositorys.BoletoRepository;
import dev.boletomicroservice.util.ApiUtil;

/**
 * BoletoService
 * 
 * This class contains the boleto's services for the SpringApplication class.
 * 
 * @author Gabriel Meneses
 * @version 1.0
 */
@Service
public class BoletoService {

	@Autowired
	private BoletoRepository boletoRepository;
	private AppProperties prop = AppProperties.factory();
	
	public BoletoService(BoletoRepository boletoRepository) {
		this.boletoRepository = boletoRepository;
	}
	
	private static final String LOG_DATE_FORMAT = "yyyy-MM-dd";

	/**
	 * Checks if the given boleto is expired.
	 * 
	 * @param boleto The boleto to check.
	 * @return True if the boleto is expired, false otherwise.
	 */
	public Boolean isExpired(Boleto boleto) {
		boolean isExpired = boleto.getDue_date().atTime(LocalTime.MAX).isAfter(LocalDateTime.now());
		prop.logDebug("Checking if boleto '" + boleto.getCode() + "' is Expired: " + isExpired);
		return isExpired;
	}

	/**
	 * Calculates the interest of the given boleto.
	 * 
	 * @param boleto The boleto to calculate the interest.
	 * @return The boleto with the interest calculated.
	 */
	protected Boleto calcBoletoInterest(Boleto boleto, String paymentDateText) {
		prop.logDebug("Calculating the boleto with bar code: '" + boleto.getCode() + "' interest.");

		// Default values for the calcs
		final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
		final BigDecimal FINE_AMOUNT_PERCENT = BigDecimal.valueOf(2);
		final BigDecimal INTEREST_AMOUNT_PERCENT = BigDecimal.valueOf(0.033);

		BigDecimal amount = boleto.getAmount();

		// Calcs the fine amount
		BigDecimal fineAmount = amount.multiply(FINE_AMOUNT_PERCENT).divide(ONE_HUNDRED);
		boleto.setFine_amount_calculated(fineAmount);

		// Calcs the interest amount
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LOG_DATE_FORMAT);
		LocalDate paymentDate = LocalDate.parse(paymentDateText, formatter);
		Long lDays = Duration.between(paymentDate.atStartOfDay(), boleto.getDue_date().atTime(LocalTime.MAX)).toDays(); // another
																														// option
		BigDecimal days = new BigDecimal(lDays);
		BigDecimal interestAmount = amount.multiply(INTEREST_AMOUNT_PERCENT).divide(ONE_HUNDRED).multiply(days);
		boleto.setInterest_amount_calculated(interestAmount);

		// Set the new Boleto's info
		boleto.setPayment_date(paymentDate);
		boleto.setOriginal_amount(amount);

		amount = amount.add(fineAmount).add(interestAmount);
		boleto.setAmount(amount);

		return boleto;
	}

	/**
	 * Gets the boleto from the API using the given bar code.
	 * 
	 * @param barCode The bar code of the boleto.
	 * @return The boleto from the API.
	 */
	public Boleto getBoletoByBarCodeAPI(String barCode) {
		prop.logDebug("Getting from API the boleto with bar code: '" + barCode + "' interest.");
		return ApiUtil.apiAcess(barCode);
	}

	/**
	 * Calculates the interest of the given boleto and saves or updates it in the
	 * database.
	 * 
	 * @param boleto The boleto to calculate the interest.
	 * @return The boleto with the interest calculated.
	 */
	public Boleto boletoInterestProces(Boleto boleto, String paymentDate) {
		Boleto newBoleto = calcBoletoInterest(boleto, paymentDate);

		// Save or Update the boleto
		if (exists(newBoleto.getCode())) {
			update(newBoleto);
		} else {
			add(newBoleto);
		}

		return newBoleto;
	}

	/**
	 * Adds the given boleto to the database.
	 * 
	 * @param boleto The boleto to add.
	 */
	public void add(Boleto boleto) {
		prop.logDebug("Adding the boleto in the DB.");
		boletoRepository.insert(boleto);
	}

	/**
	 * Updates the given boleto in the database.
	 * 
	 * @param boleto The boleto to update.
	 */
	public void update(Boleto boleto) {
		prop.logDebug("Updating the boleto in the DB.");
		boletoRepository.save(boleto);
	}

	/**
	 * Finds all the boletos in the database.
	 * 
	 * @return A list of all the boletos in the database.
	 */
	public List<Boleto> findAll() {
		prop.logDebug("Searching for all boletos in the DB.");
		return boletoRepository.findAll();
	}

	/**
	 * Counts the boletos in the database.
	 * 
	 * @return The number of boletos in the database.
	 */
	public long count() {
		prop.logDebug("Counting the boletos in the DB.");
		return boletoRepository.count();
	}

	/**
	 * Finds the boleto with the given id in the database.
	 * 
	 * @param id The id of the boleto to find.
	 * @return The boleto with the given id.
	 */
	public Boleto findById(String id) {
		prop.logDebug("Searching the boleto with id: '" + id + "' in the DB.");
		Optional<Boleto> opBoleto = boletoRepository.findById(id);
		
		if(!opBoleto.isPresent()) {
			return null;
		}
		
		return opBoleto.get();
	}

	/**
	 * Deletes the boleto with the given id from the database.
	 * 
	 * @param id The id of the boleto to delete.
	 */
	public void deleteById(String id) {
		prop.logDebug("Deleting the boleto with id: '" + id + "' in the DB.");
		boletoRepository.deleteById(id);
	}

	/**
	 * Deletes the given boleto from the database.
	 * 
	 * @param boleto The boleto to delete.
	 */
	public void delete(Boleto boleto) {
		prop.logDebug("Deleting the boleto with id: '" + boleto.getId() + "' in the DB.");
		boletoRepository.delete(boleto);
	}

	/**
	 * Checks if a boleto with the given bar code exists in the database.
	 * 
	 * @param barCode The bar code of the boleto to check.
	 * @return True if the boleto exists, false otherwise.
	 */
	public boolean exists(String barCode) {
		prop.logDebug("Checking if boleto with bar code: '" + barCode + "' existe in the DB.");
		return findByBarCode(barCode) != null;
	}

	/**
	 * Finds the boleto with the given bar code in the database.
	 * 
	 * @param barCode The bar code of the boleto to find.
	 * @return The boleto with the given bar code.
	 */
	public Boleto findByBarCode(String barCode) {
		prop.logDebug("Searching the boleto with bar code: '" + barCode + "' in the DB.");
		return boletoRepository.findByCode(barCode);
	}
}
