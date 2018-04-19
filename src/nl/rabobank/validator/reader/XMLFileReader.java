package nl.rabobank.validator.reader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import nl.rabobank.validator.entity.Record;
import nl.rabobank.validator.exception.TransactionValidatorException;

/**
 * XML reader to extract transaction records 
 * 
 * @author Behnaz
 *
 */
public class XMLFileReader implements IFileReader<Record> {
    private static final Logger logger = LogManager.getLogger(XMLFileReader.class.getName());

	private SaxRecordHandler handler;
	private SAXParser saxParser;

	public XMLFileReader() throws TransactionValidatorException {
		handler = new SaxRecordHandler();
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			saxParser = factory.newSAXParser();
		} catch (ParserConfigurationException|SAXException e) {
			logger.error(e.getStackTrace());
			throw new TransactionValidatorException(e);
		}
	}

	/**
	 * Reading an XML file of the following content
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
		try (InputStream inputStream = new FileInputStream(file);
			Reader reader = new InputStreamReader(inputStream, "UTF-8");) {
		    InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			saxParser.parse(is, handler);
		} catch (SAXException|IOException e) {
			logger.error(e.getStackTrace());
			throw new TransactionValidatorException(e);
		} 
		return handler.getRecords();
	}
}
