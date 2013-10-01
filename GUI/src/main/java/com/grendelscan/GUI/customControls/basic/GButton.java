/**
 * 
 */
package com.grendelscan.GUI.customControls.basic;


import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.grendelscan.GUI.customControls.basic.GComposite;

/**
 * @author dbyrne
 *
 */
public class GButton extends Button
{

	/**
	 * @param parent
	 * @param style
	 */
	public GButton(GComposite parent, int style)
	{
		super(parent, style);
	}
	public GButton(GGroup parent, int style)
	{
		super(parent, style);
	}
	
	/**
	 * @param parent
	 * @param style
	 */
//	public GButton(Composite parent, int style)
//	{
//		super(parent, style);
//	}
	@Override
	public void setFont(Font font)
	{
		super.setFont(font);
	}

}
