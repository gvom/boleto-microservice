package dev.boletomicroservice.repositorys;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import dev.boletomicroservice.models.Boleto;

/**
 * BoletoRepository
 * 
 * This class is a MongoRepository for the Boleto model. It provides methods to
 * query the MongoDB database for Boleto objects.
 * 
 * @author Gabriel Meneses
 * @version 1.0
 */
@Repository
public interface BoletoRepository extends MongoRepository<Boleto, String> {

	/**
	 * This method finds a Boleto object in the MongoDB database by its barcode.
	 * 
	 * @param Code The code of the Boleto object to find.
	 * @return The Boleto object with the given code, or null if no such object
	 *         exists.
	 */
	public Boleto findByCode(String Code);
}
