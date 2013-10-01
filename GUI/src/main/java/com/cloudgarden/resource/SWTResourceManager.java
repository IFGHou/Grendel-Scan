//package com.cloudgarden.resource;
//
//
//import org.eclipse.swt.graphics.Cursor;
//import org.eclipse.swt.graphics.Image;
//import org.eclipse.swt.widgets.Control;
//import org.eclipse.swt.widgets.Display;
//
//import com.grendelscan.GUI.GuiUtils;
//
//
///**
// * Class to manage SWT resources (Font, Color, Image and Cursor)
// * There are no restrictions on the use of this code.
// *
// * You may change this code and your changes will not be overwritten,
// * but if you change the version number below then this class will be
// * completely overwritten by Jigloo.
// * #SWTResourceManager:version4.0.0#
// */
//public class SWTResourceManager {
//
//	private static SWTResourceManager instance = new SWTResourceManager();
//
//	public static Image getImage(String url, Control widget) {
//		Image img = getImage(url);
//		if(img != null && widget != null)
//			img.setBackground(widget.getBackground());
//		return img;
//	}
//
//	public static Image getImage(String url) {
//			url = url.replace('\\', '/');
//			if (url.startsWith("/"))
//				url = url.substring(1);
//			if (GuiUtils.resources.containsKey(url))
//				return (Image) GuiUtils.resources.get(url);
//			Image img = new Image(Display.getDefault(), instance.getClass().getClassLoader().getResourceAsStream(url));
//			if (img != null)
//				GuiUtils.resources.put(url, img);
//			return img;
//	}
//
//	public static Cursor getCursor(int type) {
//		String name = "CURSOR:" + type;
//		if (GuiUtils.resources.containsKey(name))
//			return (Cursor) GuiUtils.resources.get(name);
//		Cursor cursor = new Cursor(Display.getDefault(), type);
//		GuiUtils.resources.put(name, cursor);
//		return cursor;
//	}
//
//}
