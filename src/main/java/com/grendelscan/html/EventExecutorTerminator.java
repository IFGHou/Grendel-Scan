package com.grendelscan.html;
import org.w3c.dom.Node;
/**
 * This is used to let EventExecutorTerminator know when
 * it can stop it's DOM crawl
 * @author David Byrne
 *
 */
public interface EventExecutorTerminator
{
	/**
	 * 
	 * @param node The node (usually a document) that is being tested
	 * @return True, if the DOM crawl and event exectution should stop
	 */
	public boolean stopExecution(Node node);
	
}
