package org.cobra_grendel.html.domimpl;

import org.cobra_grendel.html.BrowserFrame;

/**
 * Tag interface for frame nodes.
 */
public interface FrameNode
{
	public BrowserFrame getBrowserFrame();
	
	public void setBrowserFrame(BrowserFrame frame);
}
