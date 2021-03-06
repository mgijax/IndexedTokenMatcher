# IndexedTokenMatcher
This Java library provides provides quick, prioritized lookup of objects by matching tokens in their names and synonyms.  It
arose from needs at the Gene Expression Database, as the data became too large for previous technology to return suggestions
for an anatomy autocomplete when the system was under heavy load.

## Requirements
1. Search for objects by their terms (names) and associated synonyms.
2. Search against the tokens in each string.  (Do not search for tokens across different strings for the same object.)
3. Search in a case-insensitive manner.
4. For each search, order the list of matches into five bins:
   - exact matches to the object's term (name)
   - exact matches to one of the object's synonyms
   - prefix matches to the object's term (name)
   - prefix matches to one of the object's synonyms
   - other matches (eg- match to a second token in a string)
5. Sort the matches within each bin according to a comparator defined in the class of the object itself.
6. Keep memory requirements reasonable.
7. And return results quickly!
