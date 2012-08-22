package org.cobra_grendel.html.domimpl;

public interface DocumentNotificationListener
{
	/**
	 * This is called when the whole document is potentially invalid, e.g. when
	 * a new style sheet has been added.
	 */
	public void allInvalidated();
	
	/**
	 * Called when a external script (a SCRIPT tag with a src attribute) is
	 * about to start loading.
	 * 
	 * @param node
	 */
	public void externalScriptLoading(NodeImpl node);
	
	/**
	 * This is called when the node has changed, but it is unclear if it's a
	 * size change or a look change.
	 * 
	 * @param node
	 */
	public void invalidated(NodeImpl node);
	
	/**
	 * Called if something such as a color or decoration has changed. This would
	 * be something which does not affect the rendered size.
	 * 
	 * @param node
	 */
	public void lookInvalidated(NodeImpl node);
	
	/**
	 * Called when the node (with all its contents) is first created by the
	 * parser.
	 * 
	 * @param node
	 */
	public void nodeLoaded(NodeImpl node);
	
	/**
	 * Changed if the position of the node in a parent has changed.
	 * 
	 * @param node
	 */
	public void positionInvalidated(NodeImpl node);
	
	/**
	 * Called if a property related to the node's size has changed.
	 * 
	 * @param node
	 */
	public void sizeInvalidated(NodeImpl node);
}
