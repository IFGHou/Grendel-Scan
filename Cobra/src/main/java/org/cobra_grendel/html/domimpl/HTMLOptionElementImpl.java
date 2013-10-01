package org.cobra_grendel.html.domimpl;

import org.w3c.dom.html2.HTMLFormElement;
import org.w3c.dom.html2.HTMLOptionElement;
import org.w3c.dom.html2.HTMLSelectElement;

public class HTMLOptionElementImpl extends HTMLElementImpl implements HTMLOptionElement
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public HTMLOptionElementImpl(String name, int transactionId)
	{
		super(name, true, transactionId);
	}
	
	@Override public boolean getDefaultSelected()
	{
		return getAttributeAsBoolean("selected");
	}
	
	@Override public boolean getDisabled()
	{
		return false;
	}
	
	@Override public HTMLFormElement getForm()
	{
		return getForm();
	}
	
	@Override public int getIndex()
	{
		Object parent = getParentNode();
		if (parent instanceof HTMLSelectElement)
		{
			HTMLOptionsCollectionImpl options = (HTMLOptionsCollectionImpl) ((HTMLSelectElement) parent).getOptions();
			return options.indexOf(this);
		}
		else
		{
			return -1;
		}
	}
	
	@Override public String getLabel()
	{
		return getAttribute("label");
	}
	
	@Override public boolean getSelected() 
	{
		return this.selected;
	}
	
	@Override public String getText()
	{
		return getRawInnerText(false);
	}
	
	@Override public String getValue()
	{
		return getAttribute("value");
	}
	
	@Override public void setDefaultSelected(boolean defaultSelected)
	{
		setAttribute("selected", defaultSelected ? "selected" : null);
	}
	
	@Override public void setDisabled(boolean disabled)
	{
		// TODO Unsupported
	}
	
	@Override public void setLabel(String label)
	{
		setAttribute("label", label);
	}
	
	private boolean selected;

	void setSelectedImpl(boolean selected) {
		this.selected = selected;
	}

	
	@Override public void setSelected(boolean selected)
	{
		Object parent = getParentNode();
		if (parent instanceof HTMLSelectElement)
		{
			if (selected)
			{
				((HTMLSelectElement) parent).setSelectedIndex(getIndex());
			}
			else
			{
				((HTMLSelectElement) parent).setSelectedIndex(-1);
			}
		}
	}
	
	@Override public void setValue(String value)
	{
		setAttribute("value", value);
	}
}
