package nl.rabobank.validator.entity;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import nl.rabobank.validator.exception.TransactionValidatorException;
import nl.rabobank.validator.utility.NumberConverter;

/**
 * Entity class representing a transaction
 * 
 * @author Behnaz
 *
 */
public class Record {
	private static final int INDEX_REFERENCE = 0;
	private static final int INDEX_ACCOUNT_NUMBER = 1;
	private static final int INDEX_DESCRIPTION = 2;
	private static final int INDEX_START_BALANCE = 3;
	private static final int INDEX_MUTATION = 4;
	private static final int INDEX_END_BALANCE = 5;
	private static final int CSV_COLUMNS_COUNT = 6;
	private int recordNumber;
	private String reference;
	private String accountNumber;
	private String description;
	private BigInteger startBalance;
	private BigInteger mutation;
	private BigInteger endBalance;
	private String validationError;
	private Boolean valid;

	/**
	 * 
	 * @param records
	 * @return
	 */
	public boolean validate(List<Record> initialRecords) {
		List<Record> records = (initialRecords == null) ? new ArrayList<>() : initialRecords;
		StringBuilder sb = new StringBuilder();
		valid = records.stream().noneMatch(e -> e.reference.equals(this.reference));
		if (!valid) {
			sb.append("Duplicate reference: ")
			  .append(reference)
			  .append(" at entry number: ")
			  .append(recordNumber);
		}

		if (!startBalance.add(mutation).equals(endBalance)) {
			// If transaction number is not already in the message
			if (valid) {
				sb.append(" reference: ")
				  .append(reference);
			}
			sb.append(" Invalid transaction amounts: ")
			  .append(NumberConverter.toDisplayableString(startBalance))
			  .append('+')
			  .append(NumberConverter.toDisplayableString(mutation))
			  .append(" != ")
			  .append(NumberConverter.toDisplayableString(endBalance));
			valid = false;
		}

		validationError = sb.append(System.lineSeparator()).toString();
		return valid;
	}

	/**
	 * 
	 * @return true/false indicating the validation result null not validated
	 */
	public Boolean isValid() {
		return valid;
	}

	public String getValidationError() {
		return validationError;
	}

	public int getLineNumber() {
		return recordNumber;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public String getDescription() {
		return description;
	}

	public String getStartBalance() {
		return NumberConverter.toDisplayableString(startBalance);
	}

	public String getMutation() {
		return NumberConverter.toDisplayableString(mutation);
	}

	public String getEndBalance() {
		return NumberConverter.toDisplayableString(endBalance);
	}

	public String getReference() {
		return reference;
	}
	
	public Record(String reference, String transactionNumber, String description, String startBalance, String mutation,
			String endBalance, int lineNumber) throws TransactionValidatorException {
		this.reference = reference;
		this.accountNumber = transactionNumber;
		this.description = description;
		this.startBalance = NumberConverter.toInternalBigInteger(startBalance);
		this.mutation = NumberConverter.toInternalBigInteger(mutation);
		this.endBalance = NumberConverter.toInternalBigInteger(endBalance);
		this.recordNumber = lineNumber;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(" Record number")
				.append(recordNumber)
				.append(" Reference: ")
				.append(reference)
				.append(" Account number: ")
				.append(accountNumber)
				.append(" Description: ")
				.append(description)
			.toString();
	}

	/**
	 * Creates a Record from the passed array
	 * @param items
	 * 		transaction information
	 * @param recordNumber
	 * 		the order of the record in the in input file
	 * @return
	 * 		A new Record built from the passed information
	 */
	public static Record of(String[] items, int recordNumber) throws TransactionValidatorException {
		if (items == null || items.length != CSV_COLUMNS_COUNT) {
			throw new TransactionValidatorException("A valid record has 6 parts");
		}

		RecordBuilder recordBuilder = new RecordBuilder()
											.withReference(items[INDEX_REFERENCE])
											.withAccountNumber(items[INDEX_ACCOUNT_NUMBER])
											.withDescription(items[INDEX_DESCRIPTION])
											.withStartBalance(items[INDEX_START_BALANCE])
											.withMutation(items[INDEX_MUTATION])
											.withEndBalance(items[INDEX_END_BALANCE])
											.withRecordNumber(recordNumber);
		return recordBuilder.build();
	}
}