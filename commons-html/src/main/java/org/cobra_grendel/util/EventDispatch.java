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
 * Created on Mar 19, 2005
 */
package org.cobra_grendel.util;

import java.util.Collection;
import java.util.EventObject;
import java.util.LinkedList;

/**
 * @author J. H. S.
 */
public class EventDispatch
{
    private Collection listeners;

    public EventDispatch()
    {
    }

    public final void addListener(final GenericEventListener listener)
    {
        synchronized (this)
        {
            if (listeners == null)
            {
                listeners = createListenerCollection();
            }
            listeners.add(listener);
        }
    }

    public Collection createListenerCollection()
    {
        return new LinkedList();
    }

    public final void fireEvent(final EventObject event)
    {
        GenericEventListener[] larray = null;
        synchronized (this)
        {
            if (listeners != null)
            {
                larray = (GenericEventListener[]) listeners.toArray(GenericEventListener.EMPTY_ARRAY);
            }
        }
        if (larray != null)
        {
            for (GenericEventListener element : larray)
            {
                // Call holding no locks
                element.processEvent(event);
            }
        }
    }

    public final void removeListener(final GenericEventListener listener)
    {
        synchronized (this)
        {
            if (listeners != null)
            {
                listeners.remove(listener);
            }
        }
    }
}
