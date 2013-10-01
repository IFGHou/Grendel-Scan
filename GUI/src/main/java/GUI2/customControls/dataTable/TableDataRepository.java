/**
 * 
 */
package com.grendelscan.GUI2.customControls.dataTable;

import org.eclipse.jface.viewers.IStructuredContentProvider;

import com.grendelscan.GUI.UpdateService.UpdateServiceDataProvider;

/**
 * @author david
 *
 */
public interface TableDataRepository<DataType> extends UpdateServiceDataProvider, IStructuredContentProvider
{

//	public Collection<Finding> getData();
}
