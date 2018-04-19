package nl.rabobank.validator.entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.rabobank.validator.exception.TransactionValidatorException;

public class RecordBuilder {
    private static final String REGEX_DESCRIPTION_ANYTHING = ".*";
	private static final String REGEX_ACCOUNT_NUMBER = "\\w{2}\\d{2}\\w{4}\\d{10}";
	private static final String REGEX_REFERENCE_6_DIGITS = "\\d{6}";
	public static final String REGEX_AMOUNT = "(-|\\+)?\\d{1,10}(\\.?\\d{1,2})";

	private static final Logger logger = LogManager.getLogger(RecordBuilder.class.getName());

	private int lineNumber;
	private String reference;
	private String accountNumber;
	private String endBalance;
	private String mutation;
	private String startBalance;
	private String description;

	public static void validateFormat(String input, String pattern, String emptyError, String badFormatError) throws TransactionValidatorException {
		if (input == null) {
			throw new TransactionValidatorException(emptyError);
		}
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(input);
		if (!m.matches()) {
			logger.warn(badFormatError);
			throw new TransactionValidatorException(badFormatError);
		}
	}
	public RecordBuilder withReference(String reference) throws TransactionValidatorException {
		validateFormat(reference, 
				REGEX_REFERENCE_6_DIGITS,
				"Empty reference",
				"Reference should be 6 digits, but it is " + reference);
		this.reference = reference;
		return this;
	}

	public RecordBuilder withAccountNumber(String accountNumber) throws TransactionValidatorException {
		validateFormat(accountNumber, 
				REGEX_ACCOUNT_NUMBER, 
				"Empty accountNumber",
				"Account number should be 6 digits, but it is " + accountNumber);
		this.accountNumber = accountNumber;
		return this;
	}

	public RecordBuilder withDescription(String description) throws TransactionValidatorException {
		validateFormat(description, 
				REGEX_DESCRIPTION_ANYTHING, 
				"Description is null",
				"Never showing message");
		this.description = description;
		return this;
	}

	public RecordBuilder withStartBalance(String startBalance) throws TransactionValidatorException {
		validateFormat(startBalance, 
				REGEX_AMOUNT, 
				"Start balance is null",
				"Start banace should be a number with maximum two floating digits");
		this.startBalance = startBalance;
		return this;
	}

	public RecordBuilder withMutation(String mutation) throws TransactionValidatorException {
		validateFormat(mutation, 
				REGEX_AMOUNT, 
				"Mutation is null",
				"Mutation should be a number with maximum two floating digits");
		this.mutation = mutation;
		return this;
	}

	public RecordBuilder withEndBalance(String endBalance) throws TransactionValidatorException {
		validateFormat(endBalance, 
				REGEX_AMOUNT, 
				"End balance is null",
				"End banace should be a number with maximum two floating digits");
		this.endBalance = endBalance;
		return this;
	}

	public RecordBuilder withRecordNumber(int lineNumber) {
		this.lineNumber = lineNumber;
		return this;
	}

	public Record build() throws TransactionValidatorException {
		return new Record(reference, accountNumber, description, startBalance, mutation, endBalance, lineNumber);
	}
}
