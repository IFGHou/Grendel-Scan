/**
 * 
 */
package com.grendelscan.ui.customControls.basic;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author dbyrne
 *
 */
public class GShell extends Shell
{

	/**
	 * 
	 */
	public GShell()
	{
	}

	/**
	 * @param style
	 */
	public GShell(int style)
	{
		super(style);
	}

	/**
	 * @param display
	 */
	public GShell(Display display)
	{
		super(display);
	}

	/**
	 * @param parent
	 */
	public GShell(GShell parent)
	{
		super(parent);
	}

	/**
	 * @param display
	 * @param style
	 */
	public GShell(Display display, int style)
	{
		super(display, style);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public GShell(GShell parent, int style)
	{
		super(parent, style);
	}

}
