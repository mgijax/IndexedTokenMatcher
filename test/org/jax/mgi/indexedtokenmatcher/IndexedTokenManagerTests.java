package org.jax.mgi.indexedtokenmatcher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

// includes various tests for the IndexedTokenMatcher class
public class IndexedTokenManagerTests {
	@Test
	public void termSingleMatch() {
		IndexedTokenMatcher<FauxItem> itm = getPopulatedITM();
		Assert.assertEquals(1,  itm.search("chimney").size());
		Assert.assertEquals("chimney",  itm.search("chimney").get(0).getRawObject().name);
	}

	@Test
	public void termAndSynonymMatch() {
		IndexedTokenMatcher<FauxItem> itm = getPopulatedITM();
		Assert.assertEquals(2,  itm.search("house").size());
	}
	
	@Test
	public void termAndSynonymOrdering() {
		IndexedTokenMatcher<FauxItem> itm = getPopulatedITM();
		Assert.assertEquals("house",  itm.search("house").get(0).getRawObject().name);
		Assert.assertEquals("housetop",  itm.search("house").get(1).getRawObject().synonym1);
	}
	
	@Test
	public void exactSynonymMatch() {
		IndexedTokenMatcher<FauxItem> itm = getPopulatedITM();
		Assert.assertEquals(1,  itm.search("housetop").size());
		Assert.assertEquals("housetop",  itm.search("housetop").get(0).getRawObject().synonym1);
	}
	
	@Test
	public void noMatches() {
		IndexedTokenMatcher<FauxItem> itm = getPopulatedITM();
		Assert.assertEquals(0,  itm.search("mortgage").size());
	}
	
	// instantiate, populate, and return a sample matcher with ten items
	private IndexedTokenMatcher<FauxItem> getPopulatedITM() {
		List<IndexedObject<FauxItem>> wrappedItems = new ArrayList<IndexedObject<FauxItem>>();
		
		wrappedItems.add(new FauxWrapper(new FauxItem("id1", "house", "home", "domicile")));
		wrappedItems.add(new FauxWrapper(new FauxItem("id2", "roof", "housetop", null)));
		wrappedItems.add(new FauxWrapper(new FauxItem("id3", "chimney", "smokestack", null)));
		wrappedItems.add(new FauxWrapper(new FauxItem("id4", "living room", "family room", null)));
		wrappedItems.add(new FauxWrapper(new FauxItem("id5", "kitchen", null, null)));
		wrappedItems.add(new FauxWrapper(new FauxItem("id6", "bathroom", "washroom", "loo")));
		wrappedItems.add(new FauxWrapper(new FauxItem("id7", "cellar", null, "basement")));
		wrappedItems.add(new FauxWrapper(new FauxItem("id8", "roofing material", "shingle", "slate")));
		wrappedItems.add(new FauxWrapper(new FauxItem("id9", "cellar dweller", "mouse", "rat")));
		wrappedItems.add(new FauxWrapper(new FauxItem("id10", "cupboard", "kitchen cabinet", null)));
		
		return new IndexedTokenMatcher<FauxItem>(wrappedItems);
	}
	
	// a sample data item
	class FauxItem {
		String id;
		String name;
		String synonym1;
		String synonym2;
		
		public FauxItem(String id, String name, String synonym1, String synonym2) {
			this.id = id;
			this.name = name;
			this.synonym1 = synonym1;
			this.synonym2 = synonym2;
		}
		
		public Comparator<FauxItem> getComparator() {
			return new FauxItemComparator();
		}
	}
	
	// a wrapper over the fake item, meeting the IndexedObject interface
	class FauxWrapper implements IndexedObject<FauxItem> {
		FauxItem item;
		
		public FauxWrapper(FauxItem item) {
			this.item = item;
		}

		@Override
		public String getUniqueKey() {
			return this.item.id;
		}

		@Override
		public String getTerm() {
			return this.item.name;
		}

		@Override
		public List<String> getSynonyms() {
			List<String> synonyms = new ArrayList<String>();
			if (this.item.synonym1 != null) synonyms.add(this.item.synonym1);
			if (this.item.synonym2 != null) synonyms.add(this.item.synonym2);
			return synonyms;
		}

		@Override
		public FauxItem getObject() {
			return this.item;
		}

		@Override
		public Comparator<IndexedObject<FauxItem>> getComparator() {
			return new FauxWrapperComparator();
		}
	}

	// a comparator for two fake items
	class FauxItemComparator implements Comparator<FauxItem> {
		@Override
		public int compare(FauxItem arg0, FauxItem arg1) {
			// sort by name then id
			int i = arg0.name.toLowerCase().compareTo(arg1.name.toLowerCase());
			if (i == 0) {
				i = arg0.id.toLowerCase().compareTo(arg1.id.toLowerCase());
			}
			return i;
		}
	}

	// a comparator for two wrapped fake items
	class FauxWrapperComparator implements Comparator<IndexedObject<FauxItem>> {
		FauxItemComparator itemComparator = new FauxItemComparator();

		@Override
		public int compare(IndexedObject<FauxItem> o1, IndexedObject<FauxItem> o2) {
			return this.itemComparator.compare(o1.getObject(), o2.getObject());
		}
	}
}
