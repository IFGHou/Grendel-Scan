/**
 * 
 */
package com.grendelscan.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.Widget;

import com.grendelscan.ui.Verifiers.EnforceIntegersOnly;
import com.grendelscan.ui.customControls.basic.GTable;
import com.grendelscan.ui.customControls.basic.GTableColumn;

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
	
	public static int[] getColumnWidths(GTable table)
	{
		int[] widths = new int[table.getColumnCount()];
		int i = 0;
		for(GTableColumn column: table.getColumns())
		{
			widths[i++] = column.getWidth();
		}
		return widths;
	}
	
	public static void restoreColumnWidths(GTable table, int[] widths)
	{
		for (int i = 0; i < widths.length; i++)
		{
			table.getColumn(i).setWidth(widths[i]);
		}
	}

	public static Font getFont(int offset)
	{
		return GuiUtils.getFont(GuiUtils.fontName,GuiUtils.fontSize + offset,0,false,false);
	}

	public static Font getFont(String name, int size, int style, boolean strikeout, boolean underline) {
		String fontName = name + "|" + size + "|" + style + "|" + strikeout + "|" + underline;
		if (resources.containsKey(fontName))
			return (Font) resources.get(fontName);
		FontData fd = new FontData(name, size, style);
		if (strikeout || underline) {
			try {
				Class lfCls = Class.forName("org.eclipse.swt.internal.win32.LOGFONT");
				Object lf = FontData.class.getField("data").get(fd);
				if (lf != null && lfCls != null) {
					if (strikeout)
						lfCls.getField("lfStrikeOut").set(lf, new Byte((byte) 1));
					if (underline)
						lfCls.getField("lfUnderline").set(lf, new Byte((byte) 1));
				}
			} catch (Throwable e) {
				System.err.println(
					"Unable to set underline or strikeout" + " (probably on a non-Windows platform). " + e);
			}
		}
		Font font = new Font(Display.getDefault(), fd);
		resources.put(fontName, font);
		return font;
	}

	public static Font getFont(String name, int size, int style) {
		return getFont(name, size, style, false, false);
	}

	public static final String fontName = "Courier New";
	public static final int fontSize = 8;
	public static final EnforceIntegersOnly integersOnlyVerifyer = new EnforceIntegersOnly();
	public static HashMap resources = new HashMap();
	public static Color getColor(int red, int green, int blue) {
		String name = "COLOR:" + red + "," + green + "," + blue;
		if (resources.containsKey(name))
			return (Color) resources.get(name);
		Color color = new Color(Display.getDefault(), red, green, blue);
		resources.put(name, color);
		return color;
	}

	/**
	 * This method should be called by *all* Widgets which use resources
	 * provided by this SWTResourceManager. When widgets are disposed,
	 * they are removed from the "users" Vector, and when no more
	 * registered Widgets are left, all resources are disposed.
	 * <P>
	 * If this method is not called for all Widgets then it should not be called
	 * at all, and the "dispose" method should be explicitly called after all
	 * resources are no longer being used.
	 */
	public static void registerResourceUser(Widget widget) {
		if (users.contains(widget))
			return;
		users.add(widget);
		widget.addDisposeListener(disposeListener);
	}

	public static Vector users = new Vector();
	public static DisposeListener disposeListener = new DisposeListener() {
		@Override public void widgetDisposed(DisposeEvent e) {
			users.remove(e.getSource());
			if (users.size() == 0)
				GuiUtils.dispose();
		}
	};
	public static void dispose() {
		Iterator it = resources.keySet().iterator();
		while (it.hasNext()) {
			Object resource = resources.get(it.next());
			if (resource instanceof Font)
				 ((Font) resource).dispose();
			else if (resource instanceof Color)
				 ((Color) resource).dispose();
			else if (resource instanceof Image)
				 ((Image) resource).dispose();
			else if (resource instanceof Cursor)
				 ((Cursor) resource).dispose();
		}
		resources.clear();
	}
	
}
