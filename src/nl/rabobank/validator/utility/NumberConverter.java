package nl.rabobank.validator.utility;

import java.math.BigInteger;

import nl.rabobank.validator.entity.RecordBuilder;
import nl.rabobank.validator.exception.TransactionValidatorException;

/**
 * A utility class to convert the amounts between mathematically safe and displayable formats 
 * 
 * @author Behnaz
 *
 */
public class NumberConverter {
	private static final BigInteger BIG_INTEGER__10 = BigInteger.valueOf(10);
	public static final int FLOATING_DIGITS_LENGTH = 2; // TODO make it configurable
	private static final BigInteger VALUE_OF_10_POWER_MAX_ALLOWED_FLOATING_DIGITS = 
			BigInteger.valueOf((long) Math.pow(10, FLOATING_DIGITS_LENGTH)); //100
	private static final char FLOAT_SIGN = '.';

	private NumberConverter() {
		//Do not instantiate
	}
	
	/**
	 * Extract the number value from the passed string
	 * @param amount
	 * 			the floating number to be read from the string
	 * @return
	 * 			A BigInteger equivalent to the passed string
	 * @throws TransactionValidatorException
	 */
	public static BigInteger toInternalBigInteger(String amount) throws TransactionValidatorException {
		RecordBuilder.validateFormat(amount, RecordBuilder.REGEX_AMOUNT, "Amount cannot be empty", "Invalid amount");
		
		int pointIndex = amount.indexOf(FLOAT_SIGN);
		
		if (pointIndex > -1) {
			// Get the whole part of the number
			long wholeNumber = Long.valueOf(amount.substring(0, pointIndex));
			
			// Number of digits after point
			int floatingPartLength = (amount.length() - (pointIndex + 1));
			
			// The floating part of the number
			short floatingPiece = 0;
			switch (floatingPartLength) {
				case 1:
					floatingPiece = (short) (10 * Short.valueOf(amount.substring(pointIndex + 1)));
					break;
				case 2:
					floatingPiece = Short.valueOf(amount.substring(pointIndex + 1));
					break;	
				default:
					break;
			}
			
			// Putting the pieces together
			long temp = (long) (wholeNumber * (Math.pow(10, FLOATING_DIGITS_LENGTH))
					+ (getSign(amount) * floatingPiece));
			return BigInteger.valueOf(temp);
		}

		// When there is no floating point
		long wholeNumber = Long.valueOf(amount);
		long temp = (long) (wholeNumber * (Math.pow(10, FLOATING_DIGITS_LENGTH)));
		return BigInteger.valueOf(temp);
	}

	/**
	 * If the number is negative return -1 otherwise 1
	 * @param floatNumber
	 * 		A number to be checked passed as string
	 * @return
	 * 		-1 or 1 depending if the number is negative or not
	 */
	public static double getSign(String floatNumber) {
		return (floatNumber.charAt(0) == '-') ? -1 : 1;
	}

	/**
	 * Converting the number to a string similar to the original human readable format
	 * @param bigInteger
	 * 		Number to be converted to string
	 * @return
	 * 		String representation of the number
	 */
	public static String toDisplayableString(BigInteger bigInteger) {
		BigInteger wholeNumber = bigInteger.divide(VALUE_OF_10_POWER_MAX_ALLOWED_FLOATING_DIGITS);
		BigInteger floatingPiece = bigInteger
				.remainder(VALUE_OF_10_POWER_MAX_ALLOWED_FLOATING_DIGITS);
		
		// Trimming unnecessary 0
		if (floatingPiece.remainder(BIG_INTEGER__10).longValue() == 0) {
			floatingPiece = floatingPiece.divide(BIG_INTEGER__10);
		}

		// Add '-' sign for negative numbers greater than -1
		StringBuilder sb = new StringBuilder().append(wholeNumber.doubleValue() == 0 && bigInteger.signum() < 0 ? '-' : "").append(wholeNumber);
		int absFloatingPiece = Math.abs(floatingPiece.intValue());
		if (absFloatingPiece != 0) {
			sb.append(FLOAT_SIGN)
			  .append(absFloatingPiece);
		}
		return sb.toString();
	}
}
