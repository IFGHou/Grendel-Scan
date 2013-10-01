/**
 * 
 */
package com.grendelscan.ui.customControls.basic;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author dbyrne
 *
 */
public class GTableColumn extends TableColumn
{

	/**
	 * @param parent
	 * @param style
	 */
	public GTableColumn(GTable parent, int style)
	{
		super(parent, style);
	}

	/**
	 * @param parent
	 * @param style
	 * @param index
	 */
	public GTableColumn(GTable parent, int style, int index)
	{
		super(parent, style, index);
	}

}
