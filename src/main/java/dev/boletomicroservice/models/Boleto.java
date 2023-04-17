package dev.boletomicroservice.models;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import dev.boletomicroservice.properties.AppProperties;

/**
 * Boleto
 * 
 * This class represents a Boleto object.
 * 
 * @author Gabriel Meneses
 * @version 1.0
 */
@Document(collection = "boleto")
public class Boleto {

	/**
	 * Common info
	 */
	@Id
	private ObjectId id;
	private String code;
	@DateTimeFormat(pattern = "YYYY-MM-DD")
	private LocalDate due_date;
	@Field(targetType = FieldType.DECIMAL128)
	private BigDecimal amount;
	private String recipient_name;
	private String recipient_document;
	private String type;

	/**
	 * Only for response
	 */
	@Field(targetType = FieldType.DECIMAL128)
	private BigDecimal original_amount;
	@DateTimeFormat(pattern = "YYYY-MM-DD")
	private LocalDate payment_date;
	@Field(targetType = FieldType.DECIMAL128)
	private BigDecimal interest_amount_calculated;
	@Field(targetType = FieldType.DECIMAL128)
	private BigDecimal fine_amount_calculated;

	/**
	 * Empty Boleto Constructor
	 */
	public Boleto() {
	}

	/**
	 * Boleto Constructor without interest rates
	 * 
	 * @param code              the code of the Boleto
	 * @param dueDate           the due date of the Boleto
	 * @param amount            the amount of the Boleto
	 * @param recipientName     the name of the recipient of the Boleto
	 * @param recipientDocument the document of the recipient of the Boleto
	 * @param type              the type of the Boleto
	 */
	public Boleto(String code, LocalDate due_date, BigDecimal amount, String recipient_name, String recipient_document,
			String type) {
		super();
		this.code = code;
		this.due_date = due_date;
		this.amount = amount;
		this.recipient_name = recipient_name;
		this.recipient_document = recipient_document;
		this.type = type;
	}

	@JsonIgnore
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	@JsonIgnore
	public String getCode() {
		return code;
	}

	@JsonProperty
	public void setCode(String code) {
		this.code = code;
	}

	public LocalDate getDue_date() {
		return due_date;
	}

	@JsonProperty
	public void setDue_date(LocalDate due_date) {
		this.due_date = due_date;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	@JsonProperty
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@JsonIgnore
	public String getRecipient_name() {
		return recipient_name;
	}

	@JsonProperty
	public void setRecipient_name(String recipient_name) {
		this.recipient_name = recipient_name;
	}

	@JsonIgnore
	public String getRecipient_document() {
		return recipient_document;
	}

	@JsonProperty
	public void setRecipient_document(String recipient_document) {
		this.recipient_document = recipient_document;
	}

	@JsonIgnore
	public String getType() {
		return type;
	}

	@JsonProperty
	public void setType(String type) {
		this.type = type;
	}

	public BigDecimal getOriginal_amount() {
		return original_amount;
	}

	public void setOriginal_amount(BigDecimal original_amount) {
		this.original_amount = original_amount;
	}

	public LocalDate getPayment_date() {
		return payment_date;
	}

	public void setPayment_date(LocalDate payment_date) {
		this.payment_date = payment_date;
	}

	public BigDecimal getInterest_amount_calculated() {
		return interest_amount_calculated;
	}

	public void setInterest_amount_calculated(BigDecimal interest_amount_calculated) {
		this.interest_amount_calculated = interest_amount_calculated;
	}

	public BigDecimal getFine_amount_calculated() {
		return fine_amount_calculated;
	}

	public void setFine_amount_calculated(BigDecimal fine_amount_calculated) {
		this.fine_amount_calculated = fine_amount_calculated;
	}

}
