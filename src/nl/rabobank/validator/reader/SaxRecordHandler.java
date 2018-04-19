package nl.rabobank.validator.reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import nl.rabobank.validator.entity.Record;
import nl.rabobank.validator.entity.RecordBuilder;
import nl.rabobank.validator.exception.TransactionValidatorException;

/**
 * Sax based solution to read a XML file
 * @see SAX documentations
 * @author Behnaz
 *
 */
public class SaxRecordHandler extends DefaultHandler {
    private static final Logger logger = LogManager.getLogger(SaxRecordHandler.class.getName());
	
    final static String ACCOUNT_NUMBER = "accountNumber";
	final static String RECORD = "record";
	final static String DESCRIPTION = "description";
	final static String START_BALANCE = "startBalance";
	final static String MUTATION = "mutation";
	final static String END_BALANCE = "endBalance";
	final static String REFERENCE = "reference";

	private int recordNumber = 0;
	private String accountNumber = null;
	private String description = null;
	private String startBalance = null;
	private String endBalance = null;
	private String mutation = null;
	private String reference = null;
	private List<Record> records = new ArrayList<>();

	public void endElement(String namespaceURI, String localName, String qName) {
		if (qName.equalsIgnoreCase(RECORD)) {
			recordNumber++;
			Record record;
			try {
				record = new RecordBuilder()
							.withAccountNumber(accountNumber)
							.withDescription(description)
							.withStartBalance(startBalance)
							.withMutation(mutation)
							.withEndBalance(endBalance)
							.withRecordNumber(recordNumber)
							.withReference(reference)
						.build();
				record.validate(records);
				records.add(record);
			} catch (TransactionValidatorException e) {
				logger.error(e.getStackTrace());
			}
		}
	}

	public List<Record> getRecords() {
		return records;
	}

	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {// todo??
		if (RECORD.equalsIgnoreCase(qName)) {
			reference = atts.getValue(REFERENCE);
		}
		if (ACCOUNT_NUMBER.equalsIgnoreCase(qName)) {
			accountNumber = "";
		}
		if (DESCRIPTION.equalsIgnoreCase(qName)) {
			description = "";
		}
		if (START_BALANCE.equalsIgnoreCase(qName)) {
			startBalance = "";
		}
		if (MUTATION.equalsIgnoreCase(qName)) {
			mutation = "";
		}
		if (END_BALANCE.equalsIgnoreCase(qName)) {
			endBalance = "";
		}
	}

	public void characters(char ch[], int start, int length) throws SAXException {
		if ("".equals(accountNumber)) {
			accountNumber = new String(ch, start, length);
		}

		if ("".equals(description)) {
			description = new String(ch, start, length);
		}

		if ("".equals(startBalance)) {
			startBalance = new String(ch, start, length);
		}

		if ("".equals(mutation)) {
			mutation = new String(ch, start, length);
		}

		if ("".equals(endBalance)) {
			endBalance = new String(ch, start, length);
		}
	}
}