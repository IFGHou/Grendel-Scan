package org.cobra_grendel.html.domimpl;

// import org.cobra_grendel.html.style.*;
import org.w3c.dom.html2.HTMLParagraphElement;

public class HTMLPElementImpl extends HTMLAbstractUIElement implements HTMLParagraphElement
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public HTMLPElementImpl(String name, int transactionId)
	{
		super(name, transactionId);
	}
	
	@Override public String getAlign()
	{
		return getAttribute("align");
	}
	
	@Override public void setAlign(String align)
	{
		setAttribute("align", align);
	}
	
	@Override
	protected void appendInnerTextImpl(StringBuffer buffer)
	{
		int length = buffer.length();
		int lineBreaks;
		if (length == 0)
		{
			lineBreaks = 2;
		}
		else
		{
			int start = length - 4;
			if (start < 0)
			{
				start = 0;
			}
			lineBreaks = 0;
			for (int i = start; i < length; i++)
			{
				char ch = buffer.charAt(i);
				if (ch == '\n')
				{
					lineBreaks++;
				}
			}
		}
		for (int i = 0; i < 2 - lineBreaks; i++)
		{
			buffer.append("\r\n");
		}
		super.appendInnerTextImpl(buffer);
		buffer.append("\r\n\r\n");
	}
	
	// protected RenderState createRenderState(RenderState prevRenderState) {
	// return new BlockRenderState(prevRenderState, this);
	// }
}
