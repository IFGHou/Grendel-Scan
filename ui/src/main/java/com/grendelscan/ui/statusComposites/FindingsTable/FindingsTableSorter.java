/**
 * 
 */
package com.grendelscan.ui.statusComposites.FindingsTable;

import org.eclipse.jface.viewers.Viewer;

import com.grendelscan.data.findings.Finding;
import com.grendelscan.ui.customControls.dataTable.ColumnComparator;

/**
 * @author david
 *
 */
public class FindingsTableSorter extends ColumnComparator<FindingsTableColumns>
{

	@Override
	public int compare(@SuppressWarnings("unused") Viewer viewer, Object e1, Object e2)
	{
		Finding f1 = (Finding) e1;
		Finding f2 = (Finding) e2;
		int rc = 0;
		switch (propertyIndex)
		{
			case ID:
				rc = Integer.valueOf(f1.getId()).compareTo(f2.getId());
				break;
			case SEVERITY:
				rc = f1.getSeverity().compare(f2.getSeverity());
				break;
			case TITLE:
				rc = f1.getTitle().compareTo(f2.getTitle());
				break;
			case URL:
				rc = f1.getUrl().compareTo(f2.getUrl());
				break;
		}
		// If descending order, flip the direction
		if (direction == DESCENDING)
		{
			rc = -rc;
		}
		return rc;
	}

	/* (non-Javadoc)
	 * @see com.grendelscan.ui.customControls.dataTable.ColumnComparator#getDefaultColumn()
	 */
	@Override
	public FindingsTableColumns getDefaultColumn()
	{
		return FindingsTableColumns.ID;
	}

}
