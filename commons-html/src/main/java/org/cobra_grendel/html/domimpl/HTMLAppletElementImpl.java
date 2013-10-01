package org.cobra_grendel.html.domimpl;

import org.w3c.dom.html.HTMLAppletElement;

public class HTMLAppletElementImpl extends HTMLAbstractUIElement implements HTMLAppletElement
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public HTMLAppletElementImpl(final String name, final int transactionId)
    {
        super(name, transactionId);
    }

    @Override
    public String getAlign()
    {
        return getAttribute("align");
    }

    @Override
    public String getAlt()
    {
        return getAttribute("alt");
    }

    @Override
    public String getArchive()
    {
        return getAttribute("archive");
    }

    @Override
    public String getCode()
    {
        return getAttribute("code");
    }

    @Override
    public String getCodeBase()
    {
        return getAttribute("codebase");
    }

    @Override
    public String getHeight()
    {
        return getAttribute("height");
    }

    @Override
    public String getHspace()
    {
        return getAttribute("hspace");
    }

    @Override
    public String getName()
    {
        return getAttribute("name");
    }

    @Override
    public String getObject()
    {
        return getAttribute("object");
    }

    @Override
    public String getVspace()
    {
        return getAttribute("vspace");
    }

    @Override
    public String getWidth()
    {
        return getAttribute("width");
    }

    @Override
    public void setAlign(final String align)
    {
        setAttribute("align", align);
    }

    @Override
    public void setAlt(final String alt)
    {
        setAttribute("alt", alt);
    }

    @Override
    public void setArchive(final String archive)
    {
        setAttribute("archive", archive);
    }

    @Override
    public void setCode(final String code)
    {
        setAttribute("code", code);
    }

    @Override
    public void setCodeBase(final String codeBase)
    {
        setAttribute("codebase", codeBase);
    }

    @Override
    public void setHeight(final String height)
    {
        setAttribute("height", height);
    }

    @Override
    public void setHspace(final String hspace)
    {
        setAttribute("hspace", hspace);
    }

    @Override
    public void setName(final String name)
    {
        setAttribute("name", name);
    }

    @Override
    public void setObject(final String object)
    {
        setAttribute("object", object);
    }

    @Override
    public void setVspace(final String vspace)
    {
        setAttribute("vspace", vspace);
    }

    @Override
    public void setWidth(final String width)
    {
        setAttribute("width", width);
    }
}
