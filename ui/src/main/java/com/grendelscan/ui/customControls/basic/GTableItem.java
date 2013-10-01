/**
 * 
 */
package com.grendelscan.ui.customControls.basic;

import org.eclipse.swt.widgets.TableItem;

import com.grendelscan.ui.customControls.basic.GTable;
import com.grendelscan.ui.customControls.basic.GTableItem;

/**
 * @author dbyrne
 *
 */
public class GTableItem extends TableItem
{

	/**
	 * @param parent
	 * @param style
	 */
	public GTableItem(GTable parent, int style)
	{
		super(parent, style);
	}

	/**
	 * @param parent
	 * @param style
	 * @param index
	 */
	public GTableItem(GTable parent, int style, int index)
	{
		super(parent, style, index);
	}

}
