/**
 * 
 */
package com.grendelscan.GUI.customControls.basic;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author dbyrne
 *
 */
public class GTreeItem extends TreeItem
{

	/**
	 * @param parent
	 * @param style
	 */
	public GTreeItem(GTree parent, int style)
	{
		super(parent, style);
	}

	/**
	 * @param parentItem
	 * @param style
	 */
	public GTreeItem(GTreeItem parentItem, int style)
	{
		super(parentItem, style);
	}

	/**
	 * @param parent
	 * @param style
	 * @param index
	 */
	public GTreeItem(GTree parent, int style, int index)
	{
		super(parent, style, index);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param parentItem
	 * @param style
	 * @param index
	 */
	public GTreeItem(GTreeItem parentItem, int style, int index)
	{
		super(parentItem, style, index);
	}

}
