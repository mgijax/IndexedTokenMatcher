package org.jax.mgi.indexedtokenmatcher;

import org.junit.Assert;
import org.junit.Test;

// includes various tests for methods in ITMUtils
public class ITMUtilsTests {
	@Test
	public void oneWord() {
		Assert.assertEquals(1,  ITMUtils.tokenize("hello").size());
	}

	@Test
	public void multipleWords() {
		Assert.assertEquals(7, ITMUtils.tokenize("here is a string with seven words").size());
	}
	
	@Test
	public void extraCharacters() {
		Assert.assertEquals(7,  ITMUtils.tokenize("and a string with non-alphanumeric characters3").size());
	}
	
	@Test
	public void extraWhitespace() {
		Assert.assertEquals(5,  ITMUtils.tokenize("last one     has     extra       whitespace").size());
	}
}
