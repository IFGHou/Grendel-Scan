/**
 * 
 */
package com.grendelscan.GUI.customControls.basic;

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Composite;

/**
 * @author dbyrne
 *
 */
public class GScrolledComposite extends ScrolledComposite
{

	/**
	 * @param parent
	 * @param style
	 */
	public GScrolledComposite(GComposite parent, int style)
	{
		super(parent, style);
	}

	public GScrolledComposite(GSashForm parent, int style)
	{
		super(parent, style);
	}

}
