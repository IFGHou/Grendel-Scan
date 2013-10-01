/**
 * 
 */
package com.grendelscan.GUI.customControls.basic;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author dbyrne
 *
 */
public class GDialog extends Dialog
{

	/**
	 * @param parent
	 */
	public GDialog(GShell parent)
	{
		super(parent);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public GDialog(GShell parent, int style)
	{
		super(parent, style);
	}

	@Override
	public GShell getParent()
	{
		return (GShell) super.getParent();
	}

}
