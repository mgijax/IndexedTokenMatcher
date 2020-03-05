package org.jax.mgi.indexedtokenmatcher;

import java.util.Comparator;
import java.util.List;

/* Is: an object to be searched via an IndexedTokenMatcher
 * Does: defines methods required of these objects, including getting the object's primary term or name,
 * 	any synonyms for that term, a comparator for use in sorting them, and the implementing object itself.
 */
public interface IndexedObject<T> {
	// get a Comparator that can be used for sorting of the particular type of indexed objects
	Comparator<IndexedObject<T>> getComparator();
	
	// get a unique key that will uniquely identify the object
	String getUniqueKey();
	
	// get the object's primary term (or name)
	String getTerm();
	
	// get a list of synonyms for the object (other than its term)
	List<String> getSynonyms(); 
	
	// get the implementing object itself
	T getObject();
}