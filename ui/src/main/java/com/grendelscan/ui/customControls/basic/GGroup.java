/**
 * 
 */
package com.grendelscan.ui.customControls.basic;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * @author dbyrne
 *
 */
public class GGroup extends Group
{

	/**
	 * @param parent
	 * @param style
	 */
	public GGroup(GComposite parent, int style)
	{
		super(parent, style);
	}
	
	public GGroup(GGroup parent, int style)
	{
		super(parent, style);
	}

}
