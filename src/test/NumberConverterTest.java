package test;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import nl.rabobank.validator.exception.TransactionValidatorException;
import nl.rabobank.validator.utility.NumberConverter;

public class NumberConverterTest {
	@Test
	public void testPositiveWithOneFloat() throws TransactionValidatorException {
		Assert.assertEquals(1010, NumberConverter.toInternalBigInteger("10.1").intValue());
		Assert.assertEquals("10.1", NumberConverter.toDisplayableString(BigInteger.valueOf(1010)));
	}

	@Test
	public void testSmallerThan1Negative() throws TransactionValidatorException {
		Assert.assertEquals(-30, NumberConverter.toInternalBigInteger("-0.3").intValue());
		Assert.assertEquals("-0.3", NumberConverter.toDisplayableString(BigInteger.valueOf(-30)));
	}
}
