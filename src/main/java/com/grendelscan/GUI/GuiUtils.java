/**
 * 
 */
package com.grendelscan.GUI;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * @author david
 *
 */
public class GuiUtils
{
	public static int[] getColumnWidths(Tree tree)
	{
		int[] widths = new int[tree.getColumnCount()];
		int i = 0;
		for(TreeColumn column: tree.getColumns())
		{
			widths[i++] = column.getWidth();
		}
		return widths;
	}
	
	public static void restoreColumnWidths(Tree tree, int[] widths)
	{
		for (int i = 0; i < widths.length; i++)
		{
			tree.getColumn(i).setWidth(widths[i]);
		}
	}
	
	public static int[] getColumnWidths(Table table)
	{
		int[] widths = new int[table.getColumnCount()];
		int i = 0;
		for(TableColumn column: table.getColumns())
		{
			widths[i++] = column.getWidth();
		}
		return widths;
	}
	
	public static void restoreColumnWidths(Table table, int[] widths)
	{
		for (int i = 0; i < widths.length; i++)
		{
			table.getColumn(i).setWidth(widths[i]);
		}
	}
	
}
