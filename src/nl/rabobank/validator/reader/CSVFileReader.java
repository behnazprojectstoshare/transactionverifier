package nl.rabobank.validator.reader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.rabobank.validator.entity.Record;
import nl.rabobank.validator.exception.TransactionValidatorException;

/**
 * CSV file reader to extract transaction records 
 * 
 * @author Behnaz
 *
 */
public class CSVFileReader implements IFileReader<Record> {
    private static final Logger logger = LogManager.getLogger(CSVFileReader.class.getName());

	/**
	 * Reading a file of the following format
	 * Reference,AccountNumber,Description,Start Balance,Mutation,End Balance
	 * and returning a list of records from the file
	 *
	 * @param file
	 * @return
	 * 		List of transaction records
	 * @throws TransactionValidatorException
	 */
	@Override
	public List<Record> extractRecords(File file) throws TransactionValidatorException {
		List<Record> records = new ArrayList<>();
		try (Scanner inputStream = new Scanner(file, "UTF-8")) {
			inputStream.useDelimiter("\n");// TODO make it property
			// Skipping titles
			inputStream.next();

			int recordNumber = 0;
			while (inputStream.hasNext()) {
				String data = inputStream.next();
				String[] items = data.split(",");
				Record record = Record.of(items, ++recordNumber);
				record.validate(records);
				records.add(record);
			}

		} catch (FileNotFoundException e) {
			logger.error(e.getStackTrace());
			throw new TransactionValidatorException(e);
		}
		return records;
	}
}
