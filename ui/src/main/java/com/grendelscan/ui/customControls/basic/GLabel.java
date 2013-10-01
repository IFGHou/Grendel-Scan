/**
 * 
 */
package com.grendelscan.ui.customControls.basic;

import org.eclipse.swt.graphics.Font;

import com.grendelscan.ui.customControls.basic.GComposite;

import org.eclipse.swt.widgets.Label;

/**
 * @author dbyrne
 *
 */
public class GLabel extends Label
{

	/**
	 * @param parent
	 * @param style
	 */
	public GLabel(GComposite parent, int style)
	{
		super(parent, style);
	}

	public GLabel(GGroup parent, int style)
	{
		super(parent, style);
	}

	@Override
	public void setFont(Font font)
	{
		super.setFont(font);
	}

}
