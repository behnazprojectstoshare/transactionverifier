package nl.rabo.validator.start;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.rabobank.validator.entity.Record;
import nl.rabobank.validator.exception.TransactionValidatorException;
import nl.rabobank.validator.reader.CSVFileReader;
import nl.rabobank.validator.reader.IFileReader;
import nl.rabobank.validator.reader.XMLFileReader;

public class Starter {
    private static final Logger logger = LogManager.getLogger(Starter.class.getName());

    public void run(File inputFile, File reportFile) throws TransactionValidatorException {
    	boolean isXML = false;
    	String fileName = inputFile.getName().toLowerCase();
    	if (fileName.endsWith(".xml")) {
    		isXML = true;
    	} else if (! fileName.endsWith(".csv")) {
    		logger.error("Unsupported file format " + fileName);
    		throw new TransactionValidatorException("Unsupported file format " + fileName);
    	}
    	
    	logger.info("Processing the input file");
    	
    	List<Record> records = null;
		try {
			IFileReader<Record> reader = (isXML) ? new XMLFileReader() : new CSVFileReader();
			records = reader.extractRecords(inputFile);
			try (OutputStreamWriter outWriter = new OutputStreamWriter(new FileOutputStream(reportFile), "utf-8")) {
				for (Record entry : records) {
					logger.info("Processing record: " + entry.toString());
					if (!entry.isValid()) {
						logger.info("Invalid record");
						outWriter.write(entry.getValidationError());
					} else {
						logger.info("Valid record " + entry.toString());
					}
				}
			} 
		} catch (TransactionValidatorException|IOException e) {
			logger.error(e.getStackTrace());
			throw new TransactionValidatorException(e);
		}
		logger.info("Finished processing " + records.size() + " records");//TODO calculate processing time
    }
    
	public static void main(String[] args) throws TransactionValidatorException {
		if (args.length != 2) {
			System.out.println("Usage: Enter the input file name to be validated followed by the file name for the result to be stored in");
			return;
		}
		Starter starter = new Starter();
		starter.run(new File(args[0]), new File(args[1]));
		System.out.println("Finished");
	}
}