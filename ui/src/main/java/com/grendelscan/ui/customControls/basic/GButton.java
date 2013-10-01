/**
 * 
 */
package com.grendelscan.ui.customControls.basic;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author dbyrne
 * 
 */
public class GButton extends Button
{

    /**
     * @param parent
     * @param style
     */
    public GButton(final Composite parent, final int style)
    {
        super(parent, style);
    }

    /**
     * @param parent
     * @param style
     */
    public GButton(final GComposite parent, final int style)
    {
        super(parent, style);
    }

    public GButton(final GGroup parent, final int style)
    {
        super(parent, style);
    }

    @Override
    public void setFont(final Font font)
    {
        super.setFont(font);
    }

}
