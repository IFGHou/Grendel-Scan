/*
 * GNU LESSER GENERAL PUBLIC LICENSE Copyright (C) 2006 The Lobo Project
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * Contact info: xamjadmin@users.sourceforge.net
 */
/*
 * Created on Jan 15, 2006
 */
package org.cobra_grendel.html.domimpl;

import java.util.ArrayList;

import org.cobra_grendel.html.FormInput;
import org.cobra_grendel.html.js.Executor;
import org.mozilla.javascript.Function;
import org.w3c.dom.Node;
import org.w3c.dom.html2.HTMLFormElement;

public abstract class HTMLBaseInputElement extends HTMLAbstractUIElement
{
    private class LocalImageListener implements ImageListener
    {
        private final String expectedImgSrc;

        public LocalImageListener(final String imgSrc)
        {
            expectedImgSrc = imgSrc;
        }

        @Override
        public void imageLoaded(final ImageEvent event)
        {
            dispatchEvent(expectedImgSrc, event);
        }
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private java.awt.Image image = null;

    private final ArrayList imageListeners = new ArrayList(1);

    private String imageSrc;

    private Function onload;

    protected InputContext inputContext;

    public HTMLBaseInputElement(final String name, final int transactionId)
    {
        super(name, transactionId);
    }

    /**
     * Adds a listener of image loading events. The listener gets called right away if there's already an image.
     * 
     * @param listener
     */
    public void addImageListener(final ImageListener listener)
    {
        ArrayList l = imageListeners;
        java.awt.Image currentImage;
        synchronized (l)
        {
            currentImage = image;
            l.add(listener);
        }
        if (currentImage != null)
        {
            // Call listener right away if there's already an
            // image; holding no locks.
            listener.imageLoaded(new ImageEvent(this, currentImage));
            // Should not call onload handler here. That's taken
            // care of otherwise.
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xamjwg.html.domimpl.HTMLElementImpl#assignAttributeField(java.lang.String, java.lang.String)
     */
    @Override
    protected void assignAttributeField(final String normalName, final String value)
    {
        if ("value".equals(normalName))
        {
            InputContext ic = inputContext;
            if (ic != null)
            {
                ic.setValue(value);
            }
        }
        else if ("src".equals(normalName))
        {
            loadImage(value);
        }
        else
        {
            super.assignAttributeField(normalName, value);
        }
    }

    @Override
    public void blur()
    {
        InputContext ic = inputContext;
        if (ic != null)
        {
            ic.blur();
        }
    }

    private void dispatchEvent(final String expectedImgSrc, final ImageEvent event)
    {
        ArrayList l = imageListeners;
        ImageListener[] listenerArray;
        synchronized (l)
        {
            if (!expectedImgSrc.equals(imageSrc))
            {
                return;
            }
            image = event.image;
            // Get array of listeners while holding lock.
            listenerArray = (ImageListener[]) l.toArray(ImageListener.EMPTY_ARRAY);
        }
        int llength = listenerArray.length;
        for (int i = 0; i < llength; i++)
        {
            // Inform listener, holding no lock.
            listenerArray[i].imageLoaded(event);
        }
        Function onload = getOnload();
        if (onload != null)
        {
            // TODO: onload event object?
            Executor.executeFunction(HTMLBaseInputElement.this, onload, null);
        }
    }

    @Override
    public void focus()
    {
        InputContext ic = inputContext;
        if (ic != null)
        {
            ic.focus();
        }
    }

    public String getAccept()
    {
        return getAttribute("accept");
    }

    public String getAccessKey()
    {
        return getAttribute("accessKey");
    }

    public String getAlign()
    {
        return getAttribute("align");
    }

    public String getAlt()
    {
        return getAttribute("alit");
    }

    public String getDefaultValue()
    {
        return getAttribute("defaultValue");
    }

    public boolean getDisabled()
    {
        InputContext ic = inputContext;
        return ic == null ? false : ic.getDisabled();
    }

    protected java.io.File getFileValue()
    {
        InputContext ic = inputContext;
        if (ic != null)
        {
            return ic.getFileValue();
        }
        else
        {
            return null;
        }
    }

    public HTMLFormElement getForm()
    {
        Node parent = getParentNode();
        while (parent != null && !(parent instanceof HTMLFormElement))
        {
            parent = parent.getParentNode();
        }
        return (HTMLFormElement) parent;
    }

    public final java.awt.Image getImage()
    {
        synchronized (imageListeners)
        {
            return image;
        }
    }

    public String getName()
    {
        // TODO: Should this return valid of "id"?
        return getAttribute("name");
    }

    public Function getOnload()
    {
        return getEventFunction(onload, "onload");
    }

    public boolean getReadOnly()
    {
        InputContext ic = inputContext;
        return ic == null ? false : ic.getReadOnly();
    }

    public int getTabIndex()
    {
        InputContext ic = inputContext;
        return ic == null ? 0 : ic.getTabIndex();
    }

    public String getValue()
    {
        InputContext ic = inputContext;
        if (ic != null)
        {
            // Note: Per HTML Spec, setValue does not set attribute.
            return ic.getValue();
        }
        else
        {
            String val = getAttribute("value");
            return val == null ? "" : val;
        }
    }

    private void loadImage(final String src)
    {
        HTMLDocumentImpl document = (HTMLDocumentImpl) this.document;
        if (document != null)
        {
            synchronized (imageListeners)
            {
                imageSrc = src;
                image = null;
            }
            if (src != null)
            {
                document.loadImage(src, new LocalImageListener(src));
            }
        }
    }

    public void removeImageListener(final ImageListener listener)
    {
        ArrayList l = imageListeners;
        synchronized (l)
        {
            l.remove(l);
        }
    }

    public void resetForm()
    {
        HTMLFormElement form = getForm();
        if (form != null)
        {
            form.reset();
        }
    }

    void resetInput()
    {
        InputContext ic = inputContext;
        if (ic != null)
        {
            ic.resetInput();
        }
    }

    public void select()
    {
        InputContext ic = inputContext;
        if (ic != null)
        {
            ic.select();
        }
    }

    public void setAccept(final String accept)
    {
        setAttribute("accept", accept);
    }

    public void setAccessKey(final String accessKey)
    {
        setAttribute("accessKey", accessKey);
    }

    public void setAlign(final String align)
    {
        setAttribute("align", align);
    }

    public void setAlt(final String alt)
    {
        setAttribute("alt", alt);
    }

    public void setDefaultValue(final String defaultValue)
    {
        setAttribute("defaultValue", defaultValue);
    }

    public void setDisabled(final boolean disabled)
    {
        InputContext ic = inputContext;
        if (ic != null)
        {
            ic.setDisabled(disabled);
        }
    }

    public void setInputContext(final InputContext ic)
    {
        inputContext = ic;
    }

    public void setName(final String name)
    {
        setAttribute("name", name);
    }

    public void setOnload(final Function onload)
    {
        this.onload = onload;
    }

    public void setReadOnly(final boolean readOnly)
    {
        InputContext ic = inputContext;
        if (ic != null)
        {
            ic.setReadOnly(readOnly);
        }
    }

    public void setTabIndex(final int tabIndex)
    {
        InputContext ic = inputContext;
        if (ic != null)
        {
            ic.setTabIndex(tabIndex);
        }
    }

    public void setValue(final String value)
    {
        InputContext ic = inputContext;
        if (ic != null)
        {
            // Note: Per HTML Spec, it does not set attribute.
            ic.setValue(value);
        }
        else
        {
            setAttribute("value", value);
        }
    }

    public void submitForm(final FormInput[] extraFormInputs)
    {
        HTMLFormElementImpl form = (HTMLFormElementImpl) getForm();
        if (form != null)
        {
            form.submit(extraFormInputs);
        }
    }

}
