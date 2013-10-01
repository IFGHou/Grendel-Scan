/**
 * 
 */
package com.grendelscan.GUI.customControls.dataTable;


import org.eclipse.jface.viewers.ViewerComparator;


/**
 * @author david
 *
 */
public abstract class ColumnComparator<ColumnsEnum extends Enum<?>> extends ViewerComparator
{

	protected static final int		DESCENDING	= 1;
	protected static final int		ASCENDING	= 0;
	protected int						direction	= DESCENDING;
	protected ColumnsEnum	propertyIndex;

	/**
	 * 
	 */
	public ColumnComparator()
	{
		propertyIndex = getDefaultColumn();
		direction = ASCENDING;
	}


	public abstract ColumnsEnum getDefaultColumn();
	
	public void setColumn(ColumnsEnum column)
	{
		if (column == propertyIndex)
		{
			// Same column as last sort; toggle the direction
			direction = 1 - direction;
		}
		else
		{
			// New column; do an ascending sort
			propertyIndex = column;
			direction = ASCENDING;
		}
	}
}
