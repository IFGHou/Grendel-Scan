/**
 * 
 */
package com.grendelscan.ui.customControls.basic;

import org.eclipse.swt.graphics.Font;

import com.grendelscan.ui.customControls.basic.GComposite;

import org.eclipse.swt.widgets.List;

/**
 * @author dbyrne
 *
 */
public class GList extends List
{

	/**
	 * @param parent
	 * @param style
	 */
	public GList(GComposite parent, int style)
	{
		super(parent, style);
	}

	public GList(GGroup parent, int style)
	{
		super(parent, style);
	}

	@Override
	public void setFont(Font font)
	{
		super.setFont(font);
	}

}
