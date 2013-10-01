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
 * Created on Oct 15, 2005
 */
package org.cobra_grendel.html.domimpl;

import org.apache.commons.lang.NotImplementedException;
import org.cobra_grendel.html.UserAgentContext;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

public class DOMImplementationImpl implements DOMImplementation
{
    private final UserAgentContext context;
    protected final int transactionId;

    public DOMImplementationImpl(final UserAgentContext context, final int transactionId)
    {
        this.context = context;
        this.transactionId = transactionId;
    }

    @Override
    public Document createDocument(final String namespaceURI, final String qualifiedName, final DocumentType doctype) throws DOMException
    {
        // return new HTMLDocumentImpl(context);
        throw new NotImplementedException();
    }

    @Override
    public DocumentType createDocumentType(final String qualifiedName, final String publicId, final String systemId) throws DOMException
    {
        return new DocumentTypeImpl(qualifiedName, publicId, systemId, transactionId);
    }

    @Override
    public Object getFeature(final String feature, final String version)
    {
        if ("HTML".equals(feature) && "2.0".compareTo(version) <= 0)
        {
            return this;
        }
        else
        {
            return null;
        }
    }

    @Override
    public boolean hasFeature(final String feature, final String version)
    {
        return "HTML".equals(feature) && "2.0".compareTo(version) <= 0;
    }
}
