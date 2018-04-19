package nl.rabobank.validator.exception;

/**
 * Exception wrapper
 * 
 * @author Behnaz 
 *
 */
public class TransactionValidatorException extends Exception {
	private static final long serialVersionUID = 1L;

	public TransactionValidatorException(String message) {
		super(message);
	}

	public TransactionValidatorException(Throwable e) {
		super(e);
	}
}
