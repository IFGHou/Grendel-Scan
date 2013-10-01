/**
 * 
 */
package com.grendelscan.ui.customControls.dataTable;

import org.eclipse.jface.viewers.IStructuredContentProvider;

import com.grendelscan.ui.UpdateService.UpdateServiceDataProvider;

/**
 * @author david
 *
 */
public interface TableDataRepository<DataType> extends UpdateServiceDataProvider, IStructuredContentProvider
{

//	public Collection<Finding> getData();
}
