/**
 * 
 */
package com.grendelscan.ui.customControls.dataTable;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;

import com.grendelscan.ui.MainWindow;
import com.grendelscan.ui.UpdateService.UpdateTarget;
import com.grendelscan.ui.customControls.basic.GComposite;
import com.grendelscan.ui.customControls.basic.GTable;
import com.grendelscan.ui.customControls.basic.GTableColumn;

/**
 * @author david
 * 
 */
public abstract class AbstractDataTable<ColumnsEnum extends Enum<?>, DataType> extends GComposite implements UpdateTarget
{

	protected ColumnComparator<ColumnsEnum>		comparator;
	protected TableDataRepository<DataType>	dataProvider;
	
	/**
	 * public to avoid synthesized access
	 */
	public TableViewer						tableViewer; 
	
	protected abstract void createColumns();
	protected abstract MenuDetectListener makeContextMenuHandler();
	protected abstract MouseAdapter makeMouseAdapter();
	protected abstract int initGUIStart(int top);
	protected abstract int initGUIBottomControls(int top);

	/**
	 * @param parent
	 * @param style
	 */
	public AbstractDataTable(GComposite parent, int style, TableDataRepository<DataType>	dataProvider, ColumnComparator<ColumnsEnum> comparator)
	{
		super(parent, style);
		this.comparator = comparator;
		this.dataProvider = dataProvider; 
		initGUI();
		MainWindow.getInstance().getUpdateService().add(dataProvider, this);
	}

	public void refreshData()
	{
		tableViewer.refresh();
	}

	private SelectionAdapter getColumnSelectionAdapter(final GTableColumn column, final ColumnsEnum columnType)
	{
		SelectionAdapter selectionAdapter = new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				comparator.setColumn(columnType);
				int dir = tableViewer.getTable().getSortDirection();
				if (tableViewer.getTable().getSortColumn() == column)
				{
					dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				}
				else
				{
					dir = SWT.DOWN;
				}
				tableViewer.getTable().setSortDirection(dir);
				tableViewer.getTable().setSortColumn(column);
				refreshData();
			}
		};
		return selectionAdapter;
	}

	protected TableViewerColumn addColumn(String title, int width, ColumnsEnum columnType)
	{
		final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final GTableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(width);
		column.setResizable(true);
		column.setMoveable(true);
		column.addSelectionListener(getColumnSelectionAdapter(column, columnType));
		return viewerColumn;

	}


	protected void createTableViewer(int top)
	{
		tableViewer = new TableViewer(this, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns();
		tableViewer.setUseHashlookup(true);
		final GTable table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
//		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setContentProvider(dataProvider);
		tableViewer.setInput(dataProvider);
		refreshData();
		// Make the selection available to other views

		// Layout the viewer
		FormData tableLData = new FormData();
		tableLData.width = 703;
		tableLData.height = 347;
		tableLData.left = new FormAttachment(0, 1000, 5);
		tableLData.top = new FormAttachment(0, 1000, top);
		tableLData.right = new FormAttachment(1000, 1000, -5);
		tableLData.bottom = new FormAttachment(1000, 1000, -5);

		tableViewer.getControl().setLayoutData(tableLData);

		MenuDetectListener mdl = makeContextMenuHandler();
		if (mdl != null)
		{
			table.addMenuDetectListener(mdl);
		}
		
		MouseAdapter ml = makeMouseAdapter();
		if (ml != null)
		{
			table.addMouseListener(ml);
		}
	}


	protected void initGUI()
	{
		setLayout(new FormLayout());
		int top = 0;
		top = initGUIStart(top);
		createTableViewer(top);
		initGUIBottomControls(top);
		tableViewer.setComparator(comparator);
		this.layout();
		refreshData();
	}
	
	
	@Override
	public void updateView()
	{
		Runnable r = new Runnable()
		{
			@Override
			public void run()
			{
				refreshData();
			}
		};
		this.getDisplay().asyncExec(r);
	}
	public final TableViewer getTableViewer()
	{
		return tableViewer;
	}
	
	public void setDataProvider(TableDataRepository<DataType> dataProvider)
	{
		if (this.dataProvider != null)
		{
			MainWindow.getInstance().getUpdateService().remove(this.dataProvider);
		}
		this.dataProvider = dataProvider;
		MainWindow.getInstance().getUpdateService().add(dataProvider, this);
	}

}
