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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.RowSet;

import flex.messaging.util.UUIDUtils;

/**
 * A wrapper for a RowSet to make it pageable. This technique is recommended if the RowSet is 'too big' to download all at once, AND the developer's client-side code is capable of dealing with
 * non-fully-populated recordsets, that is, ActionScript RecordSets which are missing some of their data.
 * 
 * @author Mark Sheppard
 * @author Peter Farland
 * @version 1.0
 */
public class PagedRowSet implements PageableRowSet
{
    private RowSet rowSet;
    private String[] colNames;
    private int pageSize = 50; // Default to 50 records a page
    private int colCount = 0;
    private int rowCount = 0;

    private String id = null;
    private String serviceName = null;

    /**
     * Pageable Rowset Service Name.
     */
    public static final String DEFAULT_PAGING_SERVICE_NAME = "PageableRowSetCache";

    // TODO UCdetector: Remove unused code:
    // /**
    // * Constructor
    // * <p>
    // * Creates a UUID for this object. Format: `XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX'
    // * </p>
    // *
    // * @param r The RowSet to be paged.
    // * @param p The initial page size.
    // */
    // public PagedRowSet(RowSet r, int p)
    // {
    // serviceName = DEFAULT_PAGING_SERVICE_NAME;
    // rowSet = r;
    // pageSize = p;
    // id = UUIDUtils.createUUID();
    // init();
    // }

    /**
     * Allows the unique id generation of the RowSet to be toggled.
     * 
     * @see #PagedRowSet(RowSet, int)
     */
    public PagedRowSet(RowSet r, int p, boolean createID)
    {
        serviceName = DEFAULT_PAGING_SERVICE_NAME;
        rowSet = r;
        pageSize = p;
        if (createID)
        {
            id = UUIDUtils.createUUID();
        }
        init();
    }

    private void init()
    {
        if (rowSet != null)
        {
            // Initialize columns
            initColumns();

            // Initialize records
            initRecords();
        }
        else
        {
            colNames = new String[0];
        }
    }

    private synchronized void initColumns()
    {
        try
        {
            ResultSetMetaData rsmd = rowSet.getMetaData();
            if (rsmd != null)
            {
                colCount = rsmd.getColumnCount();
            }
        }
        catch (SQLException ex)
        {
            colCount = 0;
        }
    }

    private synchronized void initRecords()
    {
        // Determine rs size
        if (rowSet != null)
        {
            try
            {
                int currentIndex = rowSet.getRow();

                // Go to the end and get that row number
                if (rowSet.last())
                {
                    rowCount = rowSet.getRow();
                }

                // Put the cursor back
                if (currentIndex > 0)
                {
                    rowSet.absolute(currentIndex);
                }
                else
                {
                    rowSet.beforeFirst();
                }
            }
            catch (SQLException ex)
            {
                // TODO: Decide whether if absolute() not be supported, try first() as a last resort??
                try
                {
                    rowSet.first();
                }
                catch (SQLException se)
                {
                    // we won't try anymore.
                }
            }
        }
    }

    /**
     * List the column names of the result set.
     * 
     * @return String[] An array of the column names as strings, as ordered by the result set provider's column number assignment.
     */
    @Override
    public String[] getColumnNames()
    {
        // Cache the column names lookup
        if (colNames == null)
        {
            try
            {
                // Ensure column count is initialized
                if (colCount == 0)
                {
                    initColumns();
                }

                colNames = new String[colCount];

                for (int i = 0; i < colCount; i++)
                {
                    // Note: column numbers start at 1
                    colNames[i] = rowSet.getMetaData().getColumnName(i + 1);
                }
            }
            catch (SQLException ex)
            {
                colNames = new String[0];
            }
        }

        return colNames;
    }

    /**
     * Use this method to get a map of the index used to start the data page, and an array of arrays of the actual data page itself.
     * 
     * @return Map A map with two fields, the index of the row to start the page, and an array of arrays for the actual data page.
     */
    @Override
    public synchronized Map getRecords(int startIndex, int count) throws SQLException
    {
        List aRecords = new ArrayList(); // Don't initialize with count as it could be Integer.MAX_VALUE

        // Ensure column count is initialized
        if (colCount == 0)
        {
            initColumns();
        }

        try
        {
            // Starting index cannot be less than 1
            if (startIndex < 1)
                startIndex = 1;

            // Populate the page, moving cursor to index
            if (rowSet.absolute(startIndex))
            {
                // Loop over the result set for the count specified
                for (int i = 0; i < count; i++)
                {
                    boolean hasNext = true;

                    List row;

                    if (colCount > 0)
                    {
                        row = new ArrayList(rowCount + 1);
                        // Loop over columns to create an array for the row
                        for (int j = 1; j <= colCount; j++)
                        {
                            row.add(rowSet.getObject(j));
                        }
                    }
                    else
                    // HACK: Handle any ColdFusion Query Objects that have no column metadata!
                    {
                        row = new ArrayList();

                        try
                        {
                            // Get as many columns as possible to build the row
                            // Stop on error or the first null column returned.
                            for (int j = 1; j <= 50; j++)
                            {
                                Object o = rowSet.getObject(j);
                                if (o != null)
                                {
                                    row.add(o);
                                }
                                else
                                {
                                    break;
                                }
                            }
                        }
                        catch (SQLException ex)
                        {
                            // Stop looking and just add the row.
                        }
                    }

                    aRecords.add(row.toArray());

                    hasNext = rowSet.next();

                    // Cursor beyond last row, stop!
                    if (!hasNext)
                    {
                        break;
                    }
                }
            }
        }
        catch (SQLException ex)
        {
            throw ex;
        }

        Map result = new HashMap(2);
        result.put(PAGE, aRecords.toArray());
        result.put(CURSOR, new Integer(startIndex));

        return result;
    }

    /**
     * @return int The total number of rows in the result set.
     */
    @Override
    public int getRowCount()
    {
        return rowCount;
    }

    /**
     * If this function returns a number >= the total number of rows in the result set, then the result set should be simply returned to the client in full. However, if it is < the total size, then
     * this object itself is saved in Session data, and tagged with a unique ID.
     */
    @Override
    public int getInitialDownloadCount()
    {
        return pageSize;
    }

    @Override
    public String getID()
    {
        return id;
    }

    /**
     * @return String The name of the service that will manage this paged result.
     */
    @Override
    public String getServiceName()
    {
        return serviceName;
    }

    /**
     * @param serviceName
     *            Update the name of the service that manages the pages for this query.
     */
    @Override
    public void setServicename(String serviceName)
    {
        this.serviceName = serviceName;
    }
}
