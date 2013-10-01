package org.cobra_grendel.html.test;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.cobra_grendel.html.HtmlObject;
import org.w3c.dom.html2.HTMLElement;

/**
 * Simple implementation of {@link org.cobra_grendel.html.HtmlObject}.
 */
public class SimpleHtmlObject extends JComponent implements HtmlObject
{
    /**
	 * 
	 */
    private static final long serialVersionUID = -644333613887755347L;
    protected final HTMLElement element;

    public SimpleHtmlObject(final HTMLElement element)
    {
        this.element = element;
        setLayout(new FlowLayout());
        this.add(new JLabel("[" + element.getTagName() + "]"));
    }

    @Override
    public void destroy()
    {
    }

    @Override
    public Component getComponent()
    {
        return this;
    }

    @Override
    public void reset(final int availWidth, final int availHeight)
    {
        // nop
    }

    @Override
    public void resume()
    {
    }

    @Override
    public void suspend()
    {
    }
}
