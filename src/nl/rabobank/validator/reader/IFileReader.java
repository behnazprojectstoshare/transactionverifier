package nl.rabobank.validator.reader;

import java.io.File;
import java.util.List;

import nl.rabobank.validator.entity.Record;
import nl.rabobank.validator.exception.TransactionValidatorException;
/**
 * Interface class for transaction file readers
 * @author Behnaz
 *
 * @param <T>
 */
public interface IFileReader<T> {
	public List<Record> extractRecords(File file) throws TransactionValidatorException;
}
