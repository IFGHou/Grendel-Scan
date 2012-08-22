package com.grendelscan.GUI.customControls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

import com.grendelscan.logging.Log;

public abstract class AbstractSettingsComposite extends Composite
{
	protected List<Widget> changedControls;
	protected Button applyButton;
	
	
	public AbstractSettingsComposite(Composite parent, int style)
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
