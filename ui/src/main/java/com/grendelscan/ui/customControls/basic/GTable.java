/**
 * 
 */
package com.grendelscan.ui.customControls.basic;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

/**
 * @author dbyrne
 *
 */
public class GTable extends Table
{

	/**
	 * @param parent
	 * @param style
	 */
	public GTable(GComposite parent, int style)
	{
		super(parent, style);
	}

	public GTable(GGroup parent, int style)
	{
		super(parent, style);
	}

}
