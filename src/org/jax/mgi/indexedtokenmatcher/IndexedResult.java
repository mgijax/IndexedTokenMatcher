package org.jax.mgi.indexedtokenmatcher;

import java.util.Comparator;
import java.util.List;

/* Is: a result that can be returned for a given search.  It includes the IndexedObject itself, the particular
 * 	term or synonym used to find it, and various other data.
 * Notes: IndexedObjects are denormalized to make coding easier.  Each one of these IndexedResults will represent
 *  either a term/name or a synonym for an IndexedObject.  So, an IndexedObject with two synonyms will have three
 *  of these IndexedResult objects (one for its term/name, plus one for each synonym).
 */
public class IndexedResult<T> {
	//--- constants ---//
	
	// flags to indicate what type of match was found when comparing this ACTerm to a search String
	public static int NO_MATCH = 0;				// Search string does not match this ACTerm.
	public static int EXACT_TERM_MATCH = 1;		// Search string is an exact match for this ACTerm.
	public static int EXACT_SYNONYM_MATCH = 2;	// Search string is an exact match for a synonym of this ACTerm.
	public static int BEGINS_TERM_MATCH = 3;	// Search string is a begins match for this ACTerm.
	public static int BEGINS_SYNONYM_MATCH = 4;	// Search string is a begins match for a synonym of this ACTerm.
	public static int OTHER_MATCH = 5;			// Search string is matches this ACTerm otherwise.
	
	//--- instance variables ---//
	
	private IndexedObject<T> indexedObject;	// the object that we find if this IndexedResult is a match
	private boolean isTerm;					// true if the object is for the term (name) itself, false if it is for a synonym
	private List<String> tokens;			// list of lowercase tokens that can be used to match this result
	private String lowerString;				// lowercase version of the searchable String (a term or synonym)
	private String sortableTerm;			// the term itself (for sorting)
	
	//--- public methods ---//

	public IndexedResult(IndexedObject<T> indexedObject, boolean isTerm, String searchableString) {
		this.indexedObject = indexedObject;
		this.isTerm = isTerm;
		this.tokens = ITMUtils.tokenize(searchableString);
		this.lowerString = searchableString.toLowerCase();
		this.sortableTerm = indexedObject.getTerm();
	}
	
	// Get the list of tokens for this term.
	public List<String> getTokens() {
		return this.tokens;
	}
	
	/* Return a String that represents this term for a pick list.
	 * Format for a term: "term"
	 * Format for a synonym: "term (synonym)"
	 */
	public String getDisplayValue() {
		if (isTerm) {
			return this.lowerString;
		}

		StringBuffer sb = new StringBuffer();
		sb.append(this.sortableTerm.toLowerCase());
		sb.append(" (");
		sb.append(this.lowerString);
		sb.append(")");
		return sb.toString();
	}
	
	// Compares 'queryString' with this term and its synonyms, returning an integer flag to indicate the highest-priority
	// type of match that was found.  Flags are defined as constants above.  This method is included for convenience, but
	// because it tokenizes the queryString on every call, it would be more efficient to use the 2-parameter version.
	public int getMatchType(String queryString) {
		return this.getMatchType(queryString, ITMUtils.tokenize(queryString));
	}
	
	// Compares 'queryStringLower' (a lowercased version of the query string) with this term and its synonyms, returning
	// an integer flag to indicate the highest-priority type of match that was found.  Flags are defined as constants above.
	// This method allows a List of String tokens to be passed in to aid efficiency, as we can just tokenize the query
	// string once in the calling method and re-use those tokens when looping through many IndexedResult objects.
	public int getMatchType(String queryStringLower, List<String> queryTokens) {
		// Assume we're dealing with the term itself.
		int exactFlag = EXACT_TERM_MATCH;
		int beginsFlag = BEGINS_TERM_MATCH;
		
		// If we're dealing with a synonym for the term, update the flags we'll return.
		if (!this.isTerm) {
			exactFlag = EXACT_SYNONYM_MATCH;
			beginsFlag = BEGINS_SYNONYM_MATCH;
		}

		// Is the query string a prefix to this string?
		if (this.lowerString.startsWith(queryStringLower)) {

			// If so, is it also an exact match?  (highest priority)
			if (this.lowerString.equals(queryStringLower)) {
				return exactFlag;
			}

			// Not an exact match, but a begins match.  (second priority)
			return beginsFlag;
		}

		// Are all the query tokens prefixes to the tokens for this string? (third priority)
		for (String qt : queryTokens) {
			boolean found = false;				// did not match this token yet
			for (String token : this.tokens) {
				if (token.startsWith(qt)) {
					found = true;
					break;
				}
			}
			
			// If we found a query token that didn't match a token for this term, bail out.
			if (!found) {
				return NO_MATCH;
			}
		}
		return OTHER_MATCH;
	}
	
	// returns the IndexedObject included in this IndexedResult
	public IndexedObject<T> getIndexedObject() {
		return this.indexedObject;
	}
	
	// returns the raw object included in the IndexedObject, which is itself included in this IndexedResult
	public T getRawObject() {
		return this.indexedObject.getObject();
	}
	
	// returns true if this object represents the term itself, false if it represents a synonym for it
	public boolean byTerm() {
		return this.isTerm;
	}
	
	// get a Comparator for sorting these objects
	public Comparator<IndexedResult<T>> getComparator() {
		return new IndexedResultComparator();
	}

	//--- private inner classes ---//
	
	// Comparator for use in sorting IndexedResults.  Note that this is non-optimal, as it requests a new comparator
	// each time.  Not a big deal, though, as the IndexedTokenMatcher does all the sorting once up front.
	private class IndexedResultComparator implements Comparator<IndexedResult<T>> {
		public int compare(IndexedResult<T> a, IndexedResult<T> b) {
			Comparator<IndexedObject<T>> objectComparator = a.getIndexedObject().getComparator();

			// If the indexed objects define a comparator, use it.
			if (objectComparator != null) {
				return objectComparator.compare(a.getIndexedObject(), b.getIndexedObject());
			}

			// Otherwise define a default comparison function: sort by the term (name) and the unique ID.
			IndexedObject<T> aObject = a.getIndexedObject();
			IndexedObject<T> bObject = b.getIndexedObject();
			
			int i = 0;
			if (aObject.getTerm() != null) {
				if (bObject.getTerm() != null) {
					// both strings are defined and can be compared
					i = aObject.getTerm().compareTo(bObject.getTerm());
				} else {
					// a has a term but not b, so a comes first
					i = -1;
				}
			} else if (bObject.getTerm() != null) {
				// a has no term but b does, so b comes first
				i = 1;
			}

			// terms match, so sort by the unique ID (assume both exist - let the NullPointer happen if not)
			if (i == 0) {
				i = aObject.getUniqueKey().compareTo(bObject.getUniqueKey());
			}
			return i;
		}
	}
}
