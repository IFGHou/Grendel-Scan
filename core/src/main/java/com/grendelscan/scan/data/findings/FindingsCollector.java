package com.grendelscan.scan.data.findings;

import java.util.Date;

import com.grendelscan.data.database.collections.DatabaseBackedMap;

/**
 * This class tracks all {@link Finding reportable findings} sent to it and is used to generate the final report for the scan results
 * 
 * @author David Byrne
 * 
 */
public class FindingsCollector extends DatabaseBackedMap<Integer, Finding> implements TableDataRepository<Finding>
{

    private long lastTime;

    public FindingsCollector()
    {
        super("findings");
        lastTime = new Date().getTime();
        int maxFindingId = 0;
        for (int i : keySet())
        {
            maxFindingId = Math.max(i, maxFindingId);
        }
        Finding.setLastId(maxFindingId);
    }

    public void addFinding(final Finding finding)
    {
        lastTime = new Date().getTime();
        put(finding.getId(), finding);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    @Override
    public void dispose()
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    @Override
    public Object[] getElements(final Object inputElement)
    {
        synchronized (this)
        {
            return values().toArray();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.ui.UpdateService.DataProvider#getLastModified()
     */
    @Override
    public long getLastModified()
    {
        return lastTime;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
    {
    }

}
