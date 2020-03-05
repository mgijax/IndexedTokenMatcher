package org.jax.mgi.indexedtokenmatcher;

import java.util.Arrays;
import java.util.List;

public class ITMUtils {
	/* Tokenize the given string for searching by included words.  The string is converted to lowercase and split
	 * on non-alphanumeric characters.
	 */
	public static List<String> tokenize(String searchString) {
		// Lowercase the string, remove non-alphanumerics, consolidate multiple spaces to single ones,
		// then split it on the single spaces.
		String[] tokenArray = searchString.toLowerCase().replaceAll("[^a-z0-9]", " ").trim().replaceAll("[ ]+", " ").split(" ");

		return Arrays.asList(tokenArray);
	}
}