package org.jax.mgi.indexedtokenmatcher;

import java.util.ArrayList;
import java.util.List;

/* Is: a simple collection of timestamped log messages
 * Notes: We don't really need the complexity of log4j in this library, and most of the time we won't
 * 	even want the log messages.  But in case of debugging or performance tuning, we may want to ask
 * 	for them.  So, we'll collect them here and supply them on demand.  Messages are timestamped to the
 * 	millisecond level, measured from object instantiation.
 */
public class MessageCollector {
	private long initialTime = System.currentTimeMillis();		// time of object instantiation
	List<String> messages = new ArrayList<String>();
	
	public void log(String msg) {
		long elapsed = System.currentTimeMillis() - this.initialTime;
		this.messages.add(String.format("%.3f", (double) elapsed / 1000.0) + " sec : " + msg);
	}
	
	public List<String> getMessages() {
		return this.messages;
	}
}
