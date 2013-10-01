package com.grendelscan.GUI.customControls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;

import com.grendelscan.GUI.customControls.basic.GComposite;
import org.eclipse.swt.widgets.Widget;

import com.grendelscan.GUI.customControls.basic.GButton;
import com.grendelscan.logging.Log;

public abstract class AbstractSettingsComposite extends GComposite
{
	protected List<Widget> changedControls;
	protected GButton applyButton;
	
	
	public AbstractSettingsComposite(GComposite parent, int style)
	{
		super(parent, style);
		changedControls = new ArrayList<Widget>(1);
	}
	
	abstract public void updateFromSettings();
	
	
	
	protected class TextboxChangeNotification implements FocusListener
	{
		private Widget lastWidget;
		
		@Override
		public void focusGained(FocusEvent e)
		{
			lastWidget = e.widget;
		}

		@Override
		public void focusLost(FocusEvent e)
		{
			if (e.widget != lastWidget)
			{
				Log.warn("Why don't the widgets match?");
				return;
			}
			changedControls.add(e.widget);
		}
	}

}
