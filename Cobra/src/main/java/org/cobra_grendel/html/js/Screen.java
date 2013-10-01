package org.cobra_grendel.html.js;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import org.cobra_grendel.js.AbstractScriptableDelegate;

public class Screen extends AbstractScriptableDelegate
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private final GraphicsDevice graphicsDevice;
	private final GraphicsEnvironment graphicsEnvironment;
	
	/**
	 * @param context
	 */
	public Screen()
	{
		super(-1);
		graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
	}
	
	public int getAvailHeight()
	{
		return graphicsEnvironment.getMaximumWindowBounds().height;
	}
	
	public int getAvailWidth()
	{
		return graphicsEnvironment.getMaximumWindowBounds().width;
	}
	
	public int getColorDepth()
	{
		return graphicsDevice.getDisplayMode().getBitDepth();
	}
	
	public int getHeight()
	{
		return graphicsDevice.getDisplayMode().getHeight();
	}
	
	public int getPixelDepth()
	{
		return getColorDepth();
	}
	
	public int getWidth()
	{
		GraphicsDevice gd = graphicsEnvironment.getDefaultScreenDevice();
		return gd.getDisplayMode().getWidth();
	}
}
