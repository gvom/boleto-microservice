package dev.boletomicroservice.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import dev.boletomicroservice.models.Boleto;
import dev.boletomicroservice.properties.AppProperties;
import dev.boletomicroservice.repositorys.BoletoRepository;
import dev.boletomicroservice.util.ApiUtil;

/**
 * BoletoServiceTest is a class that contains unit tests for the BoletoService class.
 *
 * @author Gabriel Meneses
 * @version 1.0
 */
public class BoletoServiceTest {

	private BoletoService boletoService;
	private BoletoRepository boletoRepository;
	private AppProperties prop;
	private ApiUtil apiUtil;
	private String CODE_BAR;

	/**
	 * This method is called before each test is executed.
	 */
	@Before
	public void setUp() {
		boletoRepository = mock(BoletoRepository.class);
		prop = mock(AppProperties.class);
		apiUtil = mock(ApiUtil.class);
		boletoService = new BoletoService(boletoRepository);
		CODE_BAR = prop.getValue("test_code_bar");
	}

	@Test
	public void testIsExpired() {
		Boleto boleto = new Boleto();
		boleto.setDue_date(LocalDate.now().minusDays(1));
		assertTrue(boletoService.isExpired(boleto));

		boleto.setDue_date(LocalDate.now().plusDays(1));
		assertFalse(boletoService.isExpired(boleto));
	}

	@Test
	public void testCalcBoletoInterest() {
		Boleto boleto = new Boleto();
		boleto.setAmount(BigDecimal.valueOf(100));
		boleto.setDue_date(LocalDate.now().minusDays(10));

		String paymentDateText = LocalDate.now().minusDays(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		Boleto result = boletoService.calcBoletoInterest(boleto, paymentDateText);

		assertNotNull(result.getPayment_date());
		assertNotNull(result.getOriginal_amount());
		assertNotNull(result.getFine_amount_calculated());
		assertNotNull(result.getInterest_amount_calculated());
		assertNotNull(result.getAmount());
	}

	@Test
	public void testGetBoletoByBarCodeAPI() {
		String barCode = CODE_BAR;
		Boleto boleto = new Boleto();
		boleto.setCode(barCode);

		when(apiUtil.apiAcess(barCode)).thenReturn(boleto);

		Boleto result = boletoService.getBoletoByBarCodeAPI(barCode);

		assertEquals(boleto, result);
	}

	@Test
	public void testBoletoInterestProces() {
		Boleto boleto = new Boleto();
		boleto.setAmount(BigDecimal.valueOf(100));
		boleto.setDue_date(LocalDate.now().minusDays(10));

		String paymentDateText = LocalDate.now().minusDays(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		when(boletoRepository.findById(boleto.getCode())).thenReturn(Optional.empty());

		Boleto result = boletoService.boletoInterestProces(boleto, paymentDateText);

		assertNotNull(result.getPayment_date());
		assertNotNull(result.getOriginal_amount());
		assertNotNull(result.getFine_amount_calculated());
		assertNotNull(result.getInterest_amount_calculated());
		assertNotNull(result.getAmount());
	}

	@Test
	public void testAdd() {
		Boleto boleto = new Boleto();
		boletoService.add(boleto);
	}

	@Test
	public void testUpdate() {
		Boleto boleto = new Boleto();
		boletoService.update(boleto);
	}

	@Test
	public void testFindAll() {
		List<Boleto> boletos = new ArrayList<>();
		when(boletoRepository.findAll()).thenReturn(boletos);

		List<Boleto> result = boletoService.findAll();

		assertEquals(boletos, result);
	}

	@Test
	public void testCount() {
		when(boletoRepository.count()).thenReturn(10L);

		long result = boletoService.count();

		assertEquals(10L, result);
	}

	@Test
	public void testFindById() {
		ObjectId id = new ObjectId("123");
		Boleto boleto = new Boleto();
		boleto.setId(id);

		when(boletoRepository.findById(id.toString())).thenReturn(Optional.of(boleto));

		Boleto result = boletoService.findById(id.toString());

		assertEquals(boleto, result);
	}

	@Test
	public void testDeleteById() {
		String id = "123";
		boletoService.deleteById(id);
	}

	@Test
	public void testDelete() {
		Boleto boleto = new Boleto();
		ObjectId id = new ObjectId("123");
		boleto.setId(id);
		boletoService.delete(boleto);
	}

	@Test
	public void testExists() {
		String barCode = CODE_BAR;
		when(boletoRepository.findByCode(barCode)).thenReturn(new Boleto());

		assertTrue(boletoService.exists(barCode));
	}

	@Test
	public void testFindByBarCode() {
		String barCode = CODE_BAR;
		Boleto boleto = new Boleto();
		boleto.setCode(barCode);

		when(boletoRepository.findByCode(barCode)).thenReturn(boleto);

		Boleto result = boletoService.findByBarCode(barCode);

		assertEquals(boleto, result);
	}

}
