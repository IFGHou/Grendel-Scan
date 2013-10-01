package com.grendelscan.ui.customControls;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.MessageBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.grendelscan.ui.GuiUtils;
import com.grendelscan.ui.customControls.basic.GButton;
import com.grendelscan.ui.customControls.basic.GComposite;
import com.grendelscan.ui.customControls.basic.GGroup;
import com.grendelscan.ui.customControls.basic.GList;
import com.grendelscan.ui.customControls.basic.GText;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI Builder, which is free for non-commercial use. If Jigloo is being used commercially (ie, by a corporation, company or
 * business for any purpose whatever) then you should purchase a license for each developer using Jigloo. Please visit www.cloudgarden.com for details. Use of Jigloo implies acceptance of these
 * licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class AddRemoveListComposite extends GComposite
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AddRemoveListComposite.class);

    private GGroup groupComposite;
    private GComposite buttonComposite;
    private GButton clearButton;
    private GButton itemAddButton;
    GList itemList;
    private GButton itemRemoveButton;
    GText itemTextBox;
    final AddRemoveHandler handler;
    ArrayList<StringDataValidator> validators;
    {
        // Register as a resource user - SWTResourceManager will
        // handle the obtaining and disposing of resources
        GuiUtils.registerResourceUser(this);
    }

    public AddRemoveListComposite(final com.grendelscan.ui.customControls.basic.GComposite parent, final int style, final String titleText, final AddRemoveHandler handler, final String[] items)
    {
        super(parent, style);
        this.handler = handler;
        validators = new ArrayList<StringDataValidator>(1);
        initGUI(titleText);
        addItems(items);
    }

    public boolean addDataValidator(final StringDataValidator stringDataValidator)
    {
        return validators.add(stringDataValidator);
    }

    public void addItem(final String item)
    {
        itemList.add(item);
    }

    public void addItems(final String[] items)
    {
        if (items != null)
        {
            for (String item : items)
            {
                itemList.add(item);
            }
        }
    }

    /*
     * TODO UCdetector: Remove unused code: public void addVerifyListener(VerifyListener listener) { itemTextBox.addVerifyListener(listener); }
     */

    public String[] getListText()
    {
        return itemList.getItems();
    }

    private void initGUI(final String titleText)
    {
        setLayout(new FillLayout(SWT.HORIZONTAL | SWT.VERTICAL));
        groupComposite = new GGroup(this, SWT.NONE);
        groupComposite.setText(titleText);
        groupComposite.setLayout(new FormLayout());
        {
            itemTextBox = new GText(groupComposite, SWT.BORDER);
            FormData itemTextBoxLData = new FormData();
            itemTextBoxLData.width = 309;
            itemTextBoxLData.height = 19;
            itemTextBoxLData.top = new FormAttachment(0, 1000, 5);
            itemTextBoxLData.left = new FormAttachment(0, 1000, 5);
            itemTextBoxLData.right = new FormAttachment(494, 1000, 0);
            itemTextBox.setLayoutData(itemTextBoxLData);
        }
        {
            FormData buttonCompositeLData = new FormData();
            buttonCompositeLData.width = 365;
            buttonCompositeLData.height = 123;
            buttonCompositeLData.left = new FormAttachment(0, 1000, 2);
            buttonCompositeLData.top = new FormAttachment(0, 1000, 38);
            buttonCompositeLData.right = new FormAttachment(494, 1000, 0);
            buttonComposite = new GComposite(groupComposite, SWT.NONE);
            RowLayout buttonCompositeLayout = new RowLayout(org.eclipse.swt.SWT.HORIZONTAL);
            buttonCompositeLayout.spacing = 10;
            buttonComposite.setLayout(buttonCompositeLayout);
            buttonComposite.setLayoutData(buttonCompositeLData);
            {
                itemAddButton = new GButton(buttonComposite, SWT.PUSH | SWT.CENTER);
                itemAddButton.setText("Add");
                RowData itemAddButtonLData = new RowData();
                itemAddButtonLData.width = 70;
                itemAddButtonLData.height = 30;
                itemAddButton.setLayoutData(itemAddButtonLData);
                itemAddButton.addSelectionListener(new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(final SelectionEvent event)
                    {
                        event.doit = true;
                        String data = itemTextBox.getText();
                        for (StringDataValidator validator : validators)
                        {
                            if (!validator.validData(data))
                            {
                                MessageBox messageBox = new MessageBox(event.widget.getDisplay().getActiveShell(), SWT.OK);
                                messageBox.setMessage("Invalid data format.");
                                messageBox.open();
                                return;
                            }
                        }
                        if (handler != null)
                        {
                            try
                            {
                                handler.addItem(data);
                            }
                            catch (Throwable e)
                            {
                                LOGGER.error("Failed to add item ( " + data + "): " + e.toString(), e);
                                event.doit = false;
                            }
                        }
                        if (event.doit)
                        {
                            itemList.add(data);
                            itemTextBox.setText("");
                        }
                    }
                });
            }
            {
                itemRemoveButton = new GButton(buttonComposite, SWT.PUSH | SWT.CENTER);
                itemRemoveButton.setText("Remove");
                RowData itemRemoveButtonLData = new RowData();
                itemRemoveButtonLData.width = 70;
                itemRemoveButtonLData.height = 30;
                itemRemoveButton.setLayoutData(itemRemoveButtonLData);
                itemRemoveButton.addSelectionListener(new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(@SuppressWarnings("unused") final SelectionEvent event)
                    {
                        for (String value : itemList.getSelection())
                        {
                            boolean doit = true;
                            if (handler != null)
                            {
                                try
                                {
                                    handler.removeItem(value);
                                }
                                catch (Throwable e)
                                {
                                    LOGGER.debug("Problem removing item from list: " + e.toString(), e);
                                    doit = false;
                                }
                            }

                            if (doit)
                            {
                                itemList.remove(value);
                                itemTextBox.setText(value);
                            }
                            break;
                        }
                    }
                });
            }
            {
                clearButton = new GButton(buttonComposite, SWT.PUSH | SWT.CENTER);
                RowData clearButtonLData = new RowData();
                clearButtonLData.width = 70;
                clearButtonLData.height = 30;
                clearButton.setLayoutData(clearButtonLData);
                clearButton.setText("Clear");
                clearButton.addSelectionListener(new SelectionAdapter()
                {
                    @Override
                    public void widgetSelected(@SuppressWarnings("unused") final SelectionEvent event)
                    {
                        boolean doit = true;
                        if (handler != null)
                        {
                            try
                            {
                                handler.clear();
                            }
                            catch (Throwable e)
                            {
                                doit = false;
                            }
                        }
                        if (doit)
                        {
                            itemList.removeAll();
                        }
                    }
                });
            }
        }
        {
            itemList = new GList(groupComposite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
            FormData itemListLData = new FormData();
            itemListLData.width = 293;
            itemListLData.height = 59;
            itemListLData.left = new FormAttachment(539, 1000, 0);
            itemListLData.top = new FormAttachment(0, 1000, 5);
            itemListLData.right = new FormAttachment(1000, 1000, -5);
            itemListLData.bottom = new FormAttachment(1000, 1000, -5);
            itemList.setLayoutData(itemListLData);
        }
        this.layout();
    }

    public void removeAll()
    {
        itemList.removeAll();
    }

    public void setItems(final String[] items)
    {
        itemList.setItems(items);
    }

}
