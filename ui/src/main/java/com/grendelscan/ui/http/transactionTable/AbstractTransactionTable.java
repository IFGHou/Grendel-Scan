/**
 * 
 */
package com.grendelscan.ui.http.transactionTable;

import java.text.DateFormat;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.grendelscan.commons.http.transactions.StandardHttpTransaction;
import com.grendelscan.scan.Scan;
import com.grendelscan.ui.MainWindow;
import com.grendelscan.ui.customControls.basic.GButton;
import com.grendelscan.ui.customControls.basic.GComposite;
import com.grendelscan.ui.customControls.basic.GTableItem;
import com.grendelscan.ui.customControls.basic.GText;
import com.grendelscan.ui.customControls.dataTable.AbstractDataTable;
import com.grendelscan.ui.customControls.dataTable.TableDataRepository;
import com.grendelscan.ui.http.transactionDisplay.TransactionComposite;

/**
 * @author david
 * 
 */
public abstract class AbstractTransactionTable extends AbstractDataTable<TransactionTableColumns, TransactionSummary>
{
    protected GButton exportAllButton;
    protected GButton exportDisplayedButton;
    protected boolean includeSourceColumn;
    protected GButton settingsButton;
    protected TransactionTableSettingsDialog settingsDialog;
    protected DateFormat timeFormater;
    protected GText transactionCountTextBox;

    /**
     * @param parent
     * @param style
     * @param dataProvider
     * @param comparator
     */
    public AbstractTransactionTable(final GComposite parent, final int style, final TableDataRepository<TransactionSummary> dataProvider, final boolean includeSourceColumn)
    {
        super(parent, style, dataProvider, new TransactionTableSorter());
        this.includeSourceColumn = includeSourceColumn;
    }

    protected abstract void addContextMenuItems(Menu menu, final int transactionID);

    @Override
    protected void createColumns()
    {
        timeFormater = DateFormat.getTimeInstance(DateFormat.FULL);
        addColumn("ID", 50, TransactionTableColumns.ID).setLabelProvider(new ColumnLabelProvider()
        {
            @Override
            public String getText(final Object element)
            {
                TransactionSummary t = (TransactionSummary) element;
                return t.getTransactionID() + "";
            }
        });

        addColumn("Time", 90, TransactionTableColumns.TIME).setLabelProvider(new ColumnLabelProvider()
        {
            @Override
            public String getText(final Object element)
            {
                TransactionSummary t = (TransactionSummary) element;
                if (t.getTime() > 0)
                {
                    return timeFormater.format(t.getTime());
                }
                return "Unexecuted";
            }
        });

        if (this instanceof AllTransactionTable)
        {
            addColumn("Source", 60, TransactionTableColumns.SOURCE).setLabelProvider(new ColumnLabelProvider()
            {
                @Override
                public String getText(final Object element)
                {
                    TransactionSummary t = (TransactionSummary) element;
                    return t.getSource() + " - " + t.getReason();
                }
            });
        }

        addColumn("Method", 60, TransactionTableColumns.METHOD).setLabelProvider(new ColumnLabelProvider()
        {
            @Override
            public String getText(final Object element)
            {
                TransactionSummary t = (TransactionSummary) element;
                return t.getMethod();
            }
        });

        addColumn("Host", 150, TransactionTableColumns.HOST).setLabelProvider(new ColumnLabelProvider()
        {
            @Override
            public String getText(final Object element)
            {
                TransactionSummary t = (TransactionSummary) element;
                return t.getHost();
            }
        });

        addColumn("Path", 150, TransactionTableColumns.PATH).setLabelProvider(new ColumnLabelProvider()
        {
            @Override
            public String getText(final Object element)
            {
                TransactionSummary t = (TransactionSummary) element;
                return t.getPath();
            }
        });

        addColumn("Query", 150, TransactionTableColumns.QUERY).setLabelProvider(new ColumnLabelProvider()
        {
            @Override
            public String getText(final Object element)
            {
                TransactionSummary t = (TransactionSummary) element;
                return t.getQuery();
            }
        });

        addColumn("Response Code", 100, TransactionTableColumns.RESPONSE_CODE).setLabelProvider(new ColumnLabelProvider()
        {
            @Override
            public String getText(final Object element)
            {
                TransactionSummary t = (TransactionSummary) element;
                if (t.getResponseCode() > 0)
                {
                    return t.getResponseCode() + "";
                }
                return "";
            }
        });
    }

    protected Menu createContextMenu()
    {
        Menu menu = new Menu(getShell(), SWT.POP_UP);
        return menu;
    }

    protected void exportAllButtonWidgetSelected(final SelectionEvent evt)
    {
        Scan.getInstance().getTransactionRecord().writeAllExecutedToDisk();
        MainWindow.getInstance().displayMessage("Notice:", "A separate thread has been spawned\n" + "to export all of the transactions.\n", false);
    }

    protected void exportDisplayedButtonWidgetSelected(final SelectionEvent evt)
    {
        for (GTableItem item : tableViewer.getTable().getItems())
        {
            exportTransaction(Integer.valueOf(item.getText(0)));
        }
    }

    protected void exportTransaction(final int id)
    {
        StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(id);
        transaction.writeToDisk(true);
    }

    @Override
    protected int initGUIStart(final int top)
    {
        settingsDialog = new TransactionTableSettingsDialog(getShell(), SWT.NONE);
        return top;
    }

    @Override
    protected MenuDetectListener makeContextMenuHandler()
    {
        return new MenuDetectListener()
        {

            @Override
            public void menuDetected(final MenuDetectEvent e)
            {
                final GTableItem selection[] = tableViewer.getTable().getSelection();
                if (selection.length == 1)
                {
                    Menu menu = createContextMenu();
                    final int transactionID = Integer.valueOf(selection[0].getText());

                    {
                        MenuItem item = new MenuItem(menu, SWT.PUSH);
                        item.setText("Open");
                        item.addSelectionListener(new SelectionAdapter()
                        {
                            @Override
                            public void widgetSelected(@SuppressWarnings("unused") final SelectionEvent evt)
                            {
                                StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(transactionID);
                                TransactionComposite.displayTransaction(transaction, false, false);
                            }
                        });
                    }

                    {
                        MenuItem item = new MenuItem(menu, SWT.PUSH);
                        item.setText("Send to manual request");
                        item.addSelectionListener(new SelectionAdapter()
                        {
                            @Override
                            public void widgetSelected(@SuppressWarnings("unused") final SelectionEvent evt)
                            {
                                MainWindow.getInstance().displayTransactionInManualRequest(transactionID);
                                MainWindow.getInstance().setSelection(MainWindow.getInstance().getManualRequestTab());
                            }
                        });
                    }

                    addContextMenuItems(menu, transactionID);

                    menu.setLocation(e.x, e.y);
                    menu.setVisible(true);
                    while (!menu.isDisposed() && menu.isVisible())
                    {
                        if (!menu.getDisplay().readAndDispatch())
                        {
                            menu.getDisplay().sleep();
                        }
                    }
                    menu.dispose();
                }
            }
        };
    }

    @Override
    protected MouseAdapter makeMouseAdapter()
    {
        return new MouseAdapter()
        {
            @Override
            public void mouseDoubleClick(@SuppressWarnings("unused") final MouseEvent evt)
            {
                GTableItem selection[] = tableViewer.getTable().getSelection();
                if (selection.length != 1)
                {
                    return;
                }
                int id = Integer.valueOf(selection[0].getText());
                StandardHttpTransaction transaction = Scan.getInstance().getTransactionRecord().getTransaction(id);
                TransactionComposite.displayTransaction(transaction, false, false);
            }
        };
    }

    public void setTransactionCount(final int value)
    {
        transactionCountTextBox.setText(String.valueOf(value));
    }

}
