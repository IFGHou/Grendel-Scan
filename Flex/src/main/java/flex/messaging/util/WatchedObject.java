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
package flex.messaging.util;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Used by redeployment manager to monitor for changes to the files.
 *
 * @exclude
 */
public class WatchedObject
{
    private String filename;
    private long modified;

    /**
     * Creates a watched object for the specified file.
     * 
     * @param filename file to watch
     * @throws FileNotFoundException when specified file could not be found
     */
    public WatchedObject(String filename) throws FileNotFoundException
    {
        this.filename = filename;
        File file = new File(filename);

        if (!file.isFile() && !file.isDirectory())
        {
            throw new FileNotFoundException();
        }
        this.modified = file.lastModified();
    }

    /**
     * Returns true if the watched file has been modified since creation of this 
     * watched object or since the last call to this method.
     * 
     * @return true if the watched file has been modified since creation of this 
     * watched object or since the last call to this method
     */
    public boolean isUptodate()
    {
        boolean uptodate = true;

        long current = new File(filename).lastModified();

        if (Math.abs(current - modified) > 1000)
        {
            uptodate = false;
        }

        modified = current;

        return uptodate;
    }
}
