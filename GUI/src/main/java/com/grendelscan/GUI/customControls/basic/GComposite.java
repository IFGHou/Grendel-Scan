/**
 * 
 */
package com.grendelscan.GUI.customControls.basic;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * @author dbyrne
 *
 */
public class GComposite extends Composite
{

	/**
	 * @param parent
	 * @param style
	 */
	public GComposite(GComposite parent, int style)
	{
		super(parent, style);
	}
	
	public GComposite(GShell parent, int style)
	{
		super(parent, style);
	}
	
	public GComposite(GTabFolder parent, int style)
	{
		super(parent, style);
	}

	public GComposite(GSashForm parent, int style)
	{
		super(parent, style);
	}

	public GComposite(GGroup parent, int style)
	{
		super(parent, style);
	}
	public GComposite(GScrolledComposite parent, int style)
	{
		super(parent, style);
	}
	
	@Override
	public GShell getShell()
	{
		return (GShell) super.getShell();
	}


}
