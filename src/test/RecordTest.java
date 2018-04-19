package test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import nl.rabobank.validator.entity.Record;
import nl.rabobank.validator.entity.RecordBuilder;
import nl.rabobank.validator.exception.TransactionValidatorException;

public class RecordTest {
	final static String[] SAMPLE_RECORD = "112806,NL27SNSB0917829871,Clothes for Willem Dekker,-1.23,+15.57,106.8".split(",");
	final static String[] SAMPLE_RECORD_2 = "112816,NL27SNSB0917829871,,9.23,+15.57,16.8".split(",");
	final static String[] SAMPLE_RECORD_3 = "112836,NL27SNSB0917829871,Food,1.23,-1.57,-1.8".split(",");
	
	@Test
	public void testRecordBuilderOnEmptyRecord() {
		try {
			new RecordBuilder().build();
		} catch (TransactionValidatorException e) {
			//good
			return;
		}
		// Should throw exception
		Assert.fail();		
	}
	
	@Test
	public void testRecordCreationViaOf() {
		Record record = null;
		try {
			record = Record.of(SAMPLE_RECORD, 1);
		} catch (TransactionValidatorException e) {
			Assert.fail();		
		}
		Assert.assertNotNull(record);
		Assert.assertEquals(SAMPLE_RECORD[0], record.getReference());
		Assert.assertEquals(SAMPLE_RECORD[1], record.getAccountNumber());
		Assert.assertEquals(SAMPLE_RECORD[2], record.getDescription());
		Assert.assertEquals(SAMPLE_RECORD[3], record.getStartBalance());
		//+ sign will be eliminated during the process
		Assert.assertEquals(SAMPLE_RECORD[4].substring(1), record.getMutation());
		Assert.assertEquals(SAMPLE_RECORD[5], record.getEndBalance());
	}
	
	@Test
	public void testRecordValidationNullExistingRecords() {
		Record record = null;
		try {
			record = Record.of(SAMPLE_RECORD, 1);
		} catch (TransactionValidatorException e) {
			Assert.fail();		
		}
		Assert.assertFalse(record.validate(null));
		Assert.assertFalse(record.isValid());
		Assert.assertTrue(record.getValidationError().contains("Invalid transaction amounts"));
		Assert.assertFalse(record.getValidationError().contains("Duplicate reference"));
	}
	
	@Test
	public void testRecordValidation() {
		Record record = null;
		Record record2 = null;
		Record record3 = null;
		
		try {
			record = Record.of(SAMPLE_RECORD, 1);
			record2 = Record.of(SAMPLE_RECORD_2, 1);
			record3 = Record.of(SAMPLE_RECORD_3, 1);
		} catch (TransactionValidatorException e) {
			Assert.fail();		
		}
		List<Record> alreadyProcessedRecords = new ArrayList<>();
		alreadyProcessedRecords.add(record);
		alreadyProcessedRecords.add(record);
		alreadyProcessedRecords.add(record2);
		alreadyProcessedRecords.add(record3);
		Assert.assertFalse(record.validate(alreadyProcessedRecords));
		Assert.assertFalse(record.isValid());
		Assert.assertTrue(record.getValidationError().contains("Invalid transaction amounts"));
		Assert.assertTrue(record.getValidationError().contains("Duplicate reference"));
	}
}
