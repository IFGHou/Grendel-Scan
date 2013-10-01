/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2002 - 2007 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.io;

import java.sql.SQLException;
import java.util.Map;

/**
 * Implementations of this class are recoginized by the serialization filter
 * as result sets that are pageable. A pageable result set is a server side
 * cache of a query result (that implements java.sql.ResultSet) and is
 * typically stored in the session object. Users can request a subset of
 * data from the object given that they know it's id.
 *
 * @author Mark Sheppard
 * @author Peter Farland
 * @see javax.sql.RowSet
 */
public interface PageableRowSet
{
    /**
     * Constants for getRecords map keys.
     */
    String PAGE = "Page";
    String CURSOR = "Cursor";

    /**
     * List the column names of the result set.
     *
     * @return String[] An array of the column names as strings, as ordered
     *         by the result set provider's column number assignment.
     */
    String[] getColumnNames() throws SQLException;

    /**
     * Use this method to get a subset of records.
     * A map is returned with two fields, the first being the
     * row number the data page started from, and the second
     * being the array of arrays for the actual data page.
     *
     * @return Map Contains two fields, the page's row index and the actual data array.
     */
    Map getRecords(int startIndex, int count) throws SQLException;

    /**
     * @return int The total number of rows in the result set.
     */
    int getRowCount();

    /**
     * If this function returns a number >= the total number of records in the recordset,
     * then the recordset should be simply returned to the client in full. However,
     * if it is < the total size, then this object itself is saved in Session data,
     * and tagged with a unique ID.
     */
    int getInitialDownloadCount();


    /**
     * @return String This paged result's (universally unique) id.
     */
    String getID();

    /**
     * @return String The name of the service that will manage this paged result.
     */
    String getServiceName();

    /**
     * @param serviceName Update the name of the service that manages the pages for this query.
     */
    void setServicename(String serviceName);
}
