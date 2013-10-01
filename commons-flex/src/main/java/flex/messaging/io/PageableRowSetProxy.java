/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL __________________
 * 
 * Copyright 2002 - 2007 Adobe Systems Incorporated All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of Adobe Systems Incorporated and its suppliers, if any. The intellectual and technical concepts contained herein are
 * proprietary to Adobe Systems Incorporated and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of
 * this information or reproduction of this material is strictly forbidden unless prior written permission is obtained from Adobe Systems Incorporated.
 **************************************************************************/

package flex.messaging.io;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.RowSet;

import flex.messaging.MessageException;

/**
 * A wrapped PageableRowSet instance requires custom serialization so this legacy type has been made to fit the PropertyProxy interface.
 * 
 * TODO: This proxy is similar to features made possible with Externalizable so PageableRowSet/RecordSet should be moved to an Externalizable implementation.
 * 
 * @author Peter Farland
 */
public class PageableRowSetProxy extends AbstractProxy
{
    static final long serialVersionUID = 1121859941216924326L;

    public static final int HUGE_PAGE_SIZE = Integer.MAX_VALUE;

    /**
     * AS Object Type Name.
     */
    public static final String AS_TYPE_NAME = "RecordSet";

    public static final Integer RECORD_SET_VERSION = new Integer(1);

    /**
     * ActionScript (AS) Object Key Names.
     */
    public static final String TOTAL_COUNT = "totalCount";
    public static final String COLUMN_NAMES = "columnNames";
    public static final String INITIAL_DATA = "initialData";
    public static final String SERVICE_NAME = "serviceName";
    public static final String SERVER_INFO = "serverInfo";
    public static final String VERSION = "version";
    public static final String CURSOR = "cursor";
    public static final String ID = "id";

    public static final List propertyNameCache = new ArrayList();
    static
    {
        propertyNameCache.add(SERVER_INFO);
    }

    public PageableRowSetProxy()
    {
        super(null);
        alias = AS_TYPE_NAME;
    }

    /*
     * TODO UCdetector: Remove unused code: public PageableRowSetProxy(RowSet defaultInstance) { super(defaultInstance); alias = AS_TYPE_NAME; }
     */

    /*
     * TODO UCdetector: Remove unused code: public PageableRowSetProxy(PageableRowSet defaultInstance) { super(defaultInstance); alias = AS_TYPE_NAME; }
     */

    @Override
    public String getAlias(Object instance)
    {
        return AS_TYPE_NAME;
    }

    @Override
    public List getPropertyNames(Object instance)
    {
        return propertyNameCache;
    }

    @Override
    public Class getType(Object instance, String propertyName)
    {
        if (SERVER_INFO.equals(propertyName))
        {
            return HashMap.class;
        }
        else
        {
            return null;
        }
    }

    @Override
    public Object getValue(Object instance, String propertyName)
    {
        Object value = null;

        if (instance instanceof RowSet)
        {
            // Wrap in PageableRowSet just for its utility methods and don't create an id.
            instance = new PagedRowSet((RowSet) instance, HUGE_PAGE_SIZE, false);
        }

        if (instance instanceof PageableRowSet)
        {
            PageableRowSet prs = (PageableRowSet) instance;

            if (SERVER_INFO.equals(propertyName))
            {
                try
                {
                    HashMap serverInfo = new HashMap();
                    serverInfo.put(ID, prs.getID());

                    Map pageInfo = prs.getRecords(1, prs.getInitialDownloadCount());

                    serverInfo.put(TOTAL_COUNT, new Integer(prs.getRowCount()));
                    serverInfo.put(INITIAL_DATA, pageInfo.get(PageableRowSet.PAGE)); // Array of Arrays - the first page returned
                    serverInfo.put(CURSOR, pageInfo.get(PageableRowSet.CURSOR)); // Integer
                    serverInfo.put(SERVICE_NAME, prs.getServiceName());
                    serverInfo.put(COLUMN_NAMES, prs.getColumnNames());
                    serverInfo.put(VERSION, RECORD_SET_VERSION);
                    value = serverInfo;
                }
                catch (SQLException ex)
                {
                    MessageException e = new MessageException();
                    e.setMessage("Error encountered serializing RowSet.");
                    e.setRootCause(ex);
                    throw e;
                }
            }
        }

        return value;
    }

    @Override
    public void setValue(Object instance, String propertyName, Object value)
    {
        return; // Client-to-server not supported
    }

    @Override
    public Object clone()
    {
        return super.clone();
    }
}
