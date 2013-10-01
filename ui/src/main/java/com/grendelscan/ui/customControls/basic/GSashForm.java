/**
 * 
 */
package com.grendelscan.ui.customControls.basic;

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;

/**
 * @author dbyrne
 *
 */
public class GSashForm extends SashForm
{

	/**
	 * @param parent
	 * @param style
	 */
	public GSashForm(GComposite parent, int style)
	{
		super(parent, style);
	}

	public GSashForm(GSashForm parent, int style)
	{
		super(parent, style);
	}

}
