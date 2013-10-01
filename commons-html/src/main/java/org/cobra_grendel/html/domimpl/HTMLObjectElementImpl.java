package org.cobra_grendel.html.domimpl;

import org.w3c.dom.Document;
import org.w3c.dom.html2.HTMLFormElement;
import org.w3c.dom.html2.HTMLObjectElement;

public class HTMLObjectElementImpl extends HTMLAbstractUIElement implements HTMLObjectElement
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public HTMLObjectElementImpl(final String name, final int transactionId)
    {
        super(name, transactionId);
    }

    @Override
    public String getAlign()
    {
        return getAttribute("align");
    }

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
    public String getBorder()
    {
        return getAttribute("border");
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
    public String getCodeType()
    {
        return getAttribute("codetype");
    }

    @Override
    public Document getContentDocument()
    {
        return getOwnerDocument();
    }

    @Override
    public String getData()
    {
        return getAttribute("data");
    }

    @Override
    public boolean getDeclare()
    {
        return "declare".equalsIgnoreCase(getAttribute("declare"));
    }

    @Override
    public HTMLFormElement getForm()
    {
        return (HTMLFormElement) getAncestorForJavaClass(HTMLFormElement.class);
    }

    @Override
    public String getHeight()
    {
        return getAttribute("height");
    }

    @Override
    public int getHspace()
    {
        try
        {
            return Integer.parseInt(getAttribute("hspace"));
        }
        catch (Exception err)
        {
            return 0;
        }
    }

    @Override
    public String getName()
    {
        return getAttribute("name");
    }

    public String getObject()
    {
        return getAttribute("object");
    }

    @Override
    public String getStandby()
    {
        return getAttribute("standby");
    }

    @Override
    public int getTabIndex()
    {
        try
        {
            return Integer.parseInt(getAttribute("tabindex"));
        }
        catch (Exception err)
        {
            return 0;
        }
    }

    @Override
    public String getType()
    {
        return getAttribute("type");
    }

    @Override
    public String getUseMap()
    {
        return getAttribute("usemap");
    }

    @Override
    public int getVspace()
    {
        try
        {
            return Integer.parseInt(getAttribute("vspace"));
        }
        catch (Exception err)
        {
            return 0;
        }
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
    public void setBorder(final String border)
    {
        setAttribute("border", border);
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
    public void setCodeType(final String codeType)
    {
        setAttribute("codetype", codeType);
    }

    @Override
    public void setData(final String data)
    {
        setAttribute("data", data);
    }

    @Override
    public void setDeclare(final boolean declare)
    {
        setAttribute("declare", declare ? "declare" : null);
    }

    @Override
    public void setHeight(final String height)
    {
        setAttribute("height", height);
    }

    @Override
    public void setHspace(final int hspace)
    {
        setAttribute("hspace", String.valueOf(hspace));
    }

    @Override
    public void setName(final String name)
    {
        setAttribute("name", name);
    }

    public void setObject(final String object)
    {
        setAttribute("object", object);
    }

    @Override
    public void setStandby(final String standby)
    {
        setAttribute("standby", standby);
    }

    @Override
    public void setTabIndex(final int tabIndex)
    {
        setAttribute("tabindex", String.valueOf(tabIndex));
    }

    @Override
    public void setType(final String type)
    {
        setAttribute("type", type);
    }

    @Override
    public void setUseMap(final String useMap)
    {
        setAttribute("usemap", useMap);
    }

    @Override
    public void setVspace(final int vspace)
    {
        setAttribute("vspace", String.valueOf(vspace));
    }

    @Override
    public void setWidth(final String width)
    {
        setAttribute("width", width);
    }
}
