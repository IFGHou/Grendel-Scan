package com.grendelscan.ui.customControls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.ui.customControls.basic.GButton;
import com.grendelscan.ui.customControls.basic.GComposite;

public abstract class AbstractSettingsComposite extends GComposite
{
    protected class TextboxChangeNotification implements FocusListener
    {
        private Widget lastWidget;

        @Override
        public void focusGained(final FocusEvent e)
        {
            lastWidget = e.widget;
        }

        @Override
        public void focusLost(final FocusEvent e)
        {
            if (e.widget != lastWidget)
            {
                LOGGER.warn("Why don't the widgets match?");
                return;
            }
            changedControls.add(e.widget);
        }
    }

    protected List<Widget> changedControls;
    protected GButton applyButton;

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSettingsComposite.class);

    public AbstractSettingsComposite(final GComposite parent, final int style)
    {
        super(parent, style);
        changedControls = new ArrayList<Widget>(1);
    }

    abstract public void updateFromSettings();

}
