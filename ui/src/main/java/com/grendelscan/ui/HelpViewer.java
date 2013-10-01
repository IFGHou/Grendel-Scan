package com.grendelscan.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Display;

import com.grendelscan.GrendelScan;
import com.grendelscan.ui.customControls.SwingBrowserComposite;
import com.grendelscan.ui.customControls.basic.GComposite;
import com.grendelscan.ui.customControls.basic.GGroup;
import com.grendelscan.ui.customControls.basic.GLabel;
import com.grendelscan.ui.customControls.basic.GShell;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class HelpViewer implements Runnable
{
	private Display display;
	private String firstSelectionName = "";
	private HelpViewerComposite viewerComposite;
	private ArrayList<HelpSection> sections;
	private Map<String, HelpSection> sectionNames;
	GLabel selectedLabel;
	int topSpacing = 15;
	
	private class HelpSection implements MouseListener
	{
		private GLabel label;
		private String labelText;
		private HelpViewerComposite parent;
		private String text;
		
		public HelpSection(String text, String labelText)
		{
			this.text = text;
			this.labelText = labelText;
		}
		
		public void clicked()
		{
			if (selectedLabel != null)
			{
				selectedLabel.setForeground(GuiUtils.getColor(0, 0, 0));
			}
			selectedLabel = label;
			label.setForeground(GuiUtils.getColor(0, 0, 255));
			parent.getBrowser().setHtmlText(text);
		}
		
		public void display(HelpViewerComposite parentComposite)
		{
			this.parent = parentComposite;
			label = new GLabel(parentComposite, SWT.NONE);
			FormData labelLData = new FormData();
			labelLData.width = 170;
			labelLData.height = 17;
			labelLData.left = new FormAttachment(0, 1000, 10);
			labelLData.top = new FormAttachment(0, 1000, topSpacing);
			topSpacing += 30;
			label.setLayoutData(labelLData);
			label.setText(labelText);
			label.addMouseListener(this);
		}
		
		@Override
		public void mouseDoubleClick(@SuppressWarnings("unused") MouseEvent e)
		{
			// Stub
		}
		
		@Override
		public void mouseDown(@SuppressWarnings("unused") MouseEvent e)
		{
			clicked();
		}
		
		@Override
		public void mouseUp(@SuppressWarnings("unused") MouseEvent e)
		{
			//Stub
		}

		public String getLabelText()
        {
        	return labelText;
        }
	}
	
	private class HelpViewerComposite extends GComposite
	{
		private SwingBrowserComposite browser;
		{
			// Register as a resource user - SWTResourceManager will
			// handle the obtaining and disposing of resources
			GuiUtils.registerResourceUser(this);
		}
		
//		public void selectSection(final String sectionName)
//		{
//			getDisplay().asyncExec(new Runnable()
//			{
//				public void run()
//				{
//					sectionNames.get(sectionName).clicked();
//				}
//			});
//		}
		
		public HelpViewerComposite(GComposite parent, int style)
		{
			super(parent, style);
			initGUI();
		}

		public HelpViewerComposite(GGroup parent, int style)
		{
			super(parent, style);
			initGUI();
		}

		public HelpViewerComposite(GShell parent, int style)
		{
			super(parent, style);
			initGUI();
		}

		public SwingBrowserComposite getBrowser()
		{
			return browser;
		}
		
		private void initGUI()
		{
				setLayout(new FormLayout());
				{
					FormData browserLData = new FormData();
					browserLData.width = 461;
					browserLData.height = 380;
					browserLData.left = new FormAttachment(0, 1000, 180);
					browserLData.top = new FormAttachment(0, 1000, 7);
					browserLData.right = new FormAttachment(1000, 1000, -6);
					browserLData.bottom = new FormAttachment(1000, 1000, -7);
					browser = new SwingBrowserComposite(this, SWT.BORDER);
					browser.setLayoutData(browserLData);
//					browser.setEnabled(false);
				}
				this.layout();
		}
	}
	
	public HelpViewer(Display display)
	{
		this.display = display;
		sections = new ArrayList<HelpSection>(1);
		sectionNames = new HashMap<String, HelpSection>(1);
	}
	
	
	public void addHelpSection(String sectionName, String text)
	{
		HelpSection section = new HelpSection(text, sectionName);
		sections.add(section);
		sectionNames.put(sectionName, section);
	}
	
/* TODO UCdetector: Remove unused code: 
	public void selectSection(final String sectionName)
	{
		viewerComposite.selectSection(sectionName);
	}
*/
	
	@Override
	public void run()
	{
		GShell shell = new GShell(display);
		Image icon = new Image(display, GrendelScan.defaultConfigDirectory + File.separator + "icon.JPG");
		shell.setImage(icon);

		viewerComposite = new HelpViewerComposite(shell, SWT.NULL);
		shell.setText(GrendelScan.versionText + " -- Help");
		for (HelpSection section: sections)
		{
			section.display(viewerComposite);
			if (firstSelectionName.equals(section.getLabelText()))
			{
				section.clicked();
			}
		}
		Point size = viewerComposite.getSize();
		shell.setLayout(new FillLayout());
		shell.layout();
		if ((size.x == 0) && (size.y == 0))
		{
			viewerComposite.pack();
			shell.pack();
		}
		else
		{
			Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
			shell.setSize(shellBounds.width, shellBounds.height);
		}
		shell.open();
//		while (!shell.isDisposed())
//		{
//			if (!display.readAndDispatch())
//			{
//				display.sleep();
//			}
//		}
	}
	
	public void showGUI()
	{
		this.run();
//		Thread thread = new Thread(this, "HelpWindowThread");
//		thread.start();
	}
	
	public void setFirstSelectionName(String selectionName)
	{
		this.firstSelectionName = selectionName;
	}
}
