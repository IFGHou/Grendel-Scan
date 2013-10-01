package com.grendelscan.GUI.http.transactionDisplay;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;

import com.grendelscan.GUI.customControls.basic.GComposite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import com.grendelscan.GUI.customControls.basic.GTable;
import com.grendelscan.GUI.customControls.basic.GTableColumn;
import com.grendelscan.GUI.customControls.basic.GTableItem;
import com.grendelscan.GUI.customControls.basic.GText;

import com.grendelscan.GUI.GuiUtils;
import com.grendelscan.GUI.customControls.basic.GButton;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class NamePairComposite extends GComposite
{

	{
		//Register as a resource user - SWTResourceManager will
		//handle the obtaining and disposing of resources
		GuiUtils.registerResourceUser(this);
	}
	 
	private GButton addButton;
//	private boolean colapsed;
//	private GLabel colapseLabel;
	private GButton downButton;
	private boolean editable;
	TableEditor editor;
	private GComposite mainComposite;
	private GTableColumn nameColumn;
	private int oldHeight;
	protected GTable pairsTable;
	private GButton removeButton;
	private GButton upButton;
	private GTableColumn valueColumn;
	
	public NamePairComposite(com.grendelscan.GUI.customControls.basic.GComposite parent, int style, int initialHeight, boolean editable)
	{
		super(parent, style);
		this.editable = editable;
//		this.colapsed = colapsed;
//this.colapsed = false;
		oldHeight = initialHeight;
		initGUI();
	}
	


	

//	private void resizeNotify(int increment)
//	{
//		for (NamePairResizeNotification notificationTarget: notificationTargets)
//		{
//			notificationTarget.resizeNotify(increment);
//		}
//	}

	public void addPair(String name, String value)
	{
		GTableItem item = new GTableItem(pairsTable, SWT.NONE);
		item.setText(new String[] { name, value });
	}
	
	public int getItemCount()
	{
		return pairsTable.getItemCount();
	}
	
	public NameValuePair[] getNameValuePairs()
	{
		NameValuePair pairs[] = new NameValuePair[pairsTable.getItemCount()];
		int index = 0;
		for (GTableItem item: pairsTable.getItems())
		{
			NameValuePair pair = new BasicNameValuePair(item.getText(0), item.getText(1));
			pairs[index++] = pair;
		}
		return pairs;
	}
	
	
//	private void changeHeight(int increment, GComposite composite)
//	{
//		// Weird bug? where groups don't resize normally
//		if (composite instanceof GGroup)
//		{
//			((FormData) composite.getLayoutData()).height += increment;
//		}
//		else
//		{
//			Rectangle bounds = composite.getBounds();
//			bounds.height += increment;
//			composite.setBounds(bounds);
//		}
//		composite.layout(true);
//		if (composite.getParent() != null)
//		{
//			changeHeight(increment, composite.getParent());
//		}
//	}
//	
//	public void handleColapse(boolean makeColapsed)
//	{
//		boolean visible = mainComposite.getVisible();
//		if (makeColapsed == visible)
//		{
//			int increment;
//			mainComposite.setVisible(!makeColapsed);
//			if (makeColapsed)
//			{
//				colapseLabel.setFontf(SWTResourceManager.getFont("Tahoma", 9, SWT.BOLD, false, false));
//				colapseLabel.setText("+");
//				oldHeight = mainComposite.getBounds().height - 15;
//				increment = -oldHeight;
//				colapsed = true;
//			}
//			else
//			{
//				colapseLabel.setFontf(SWTResourceManager.getFont("Tahoma", 10, SWT.BOLD, false, false));
//				colapsed = false;
//				colapseLabel.setText("- ");
//				increment = oldHeight;
//			}
//			changeHeight(increment, this);
//			resizeNotify(increment);
//		}
//	}
	

	protected void initGUI()
	{
		GuiUtils.registerResourceUser(this);

			setLayout(new FormLayout());
			{
//				colapseLabel = new GLabel(this, SWT.NONE | SWT.LEFT);
//				FormData colapseLabelLData1 = new FormData();
//				colapseLabelLData1.width = 10;
//				colapseLabelLData1.height = 15;
//				colapseLabelLData1.left =  new FormAttachment(0, 1000, 0);
//				colapseLabelLData1.top =  new FormAttachment(0, 1000, 0);
//				colapseLabel.setLayoutData(colapseLabelLData1);
//				colapseLabel.setForeground(this.getDisplay().getSystemColor(SWT.COLOR_RED));
//				if (colapsed)
//				{
//					colapseLabel.setFontf(SWTResourceManager.getFont("Tahoma", 9, SWT.BOLD, false, false));
//					colapseLabel.setText("+");
//				}
//				else
//				{
//					colapseLabel.setFontf(SWTResourceManager.getFont("Tahoma", 10, SWT.BOLD, false, false));
//					colapseLabel.setText("-");
//				}
//				colapseLabel.addListener(SWT.MouseDown, new Listener()
//				{
//					public void handleEvent(Event event)
//					{
//						handleColapse(mainComposite.getVisible());
//					}
//				});
			}
			{
				FormData mainCompositeLData = new FormData();
				mainCompositeLData.width = 353;
				mainCompositeLData.height = oldHeight;
				mainCompositeLData.top = new FormAttachment(0, 1000, 0);
				mainCompositeLData.right = new FormAttachment(1000, 1000, 0);
				mainCompositeLData.bottom = new FormAttachment(1000, 1000, 0);
				mainCompositeLData.left = new FormAttachment(0, 1000, 10);
				
				mainComposite = new GComposite(this, SWT.NONE);
				FormLayout composite1Layout = new FormLayout();
				mainComposite.setLayout(composite1Layout);
//				mainComposite.setVisible(!colapsed);
				mainComposite.setLayoutData(mainCompositeLData);
			}
			if (editable)
			{
				{
					downButton = new GButton(mainComposite, SWT.PUSH | SWT.CENTER);
					FormData downButtonLData = new FormData();
					downButtonLData.width = 60;
					downButtonLData.height = 20;
					downButtonLData.top = new FormAttachment(0, 1000, 0);
					downButtonLData.left = new FormAttachment(0, 1000, 220);
					downButton.setLayoutData(downButtonLData);
					downButton.setText("Down");
					downButton.addSelectionListener(new SelectionAdapter()
					{
						@Override
						public void widgetSelected(@SuppressWarnings("unused") SelectionEvent event)
						{
							int index = pairsTable.getSelectionIndex();
							// Don't do anything if it's already on top
							if ((index >= 0) && (index < pairsTable.getItemCount() - 1))
							{
								GTableItem item = pairsTable.getItem(index);
								GTableItem higherItem = pairsTable.getItem(index + 1);
								String itemName = item.getText(0);
								String itemValue = item.getText(1);
								item.setText(new String[] { higherItem.getText(0), higherItem.getText(1) });
								higherItem.setText(new String[] { itemName, itemValue });
								pairsTable.setSelection(index + 1);
							}
						}
					});
				}
				{
					upButton = new GButton(mainComposite, SWT.PUSH | SWT.CENTER);
					FormData upButtonLData = new FormData();
					upButtonLData.width = 60;
					upButtonLData.height = 20;
					upButtonLData.top = new FormAttachment(0, 1000, 0);
					upButtonLData.left = new FormAttachment(0, 1000, 150);
					upButton.setLayoutData(upButtonLData);
					upButton.setText("Up");
					upButton.addSelectionListener(new SelectionAdapter()
					{
						@Override
						public void widgetSelected(@SuppressWarnings("unused") SelectionEvent event)
						{
							int index = pairsTable.getSelectionIndex();
							// Don't do anything if it's already on top
							if (index > 0)
							{
								GTableItem item = pairsTable.getItem(index);
								GTableItem higherItem = pairsTable.getItem(index - 1);
								String itemName = item.getText(0);
								String itemValue = item.getText(1);
								item.setText(new String[] { higherItem.getText(0), higherItem.getText(1) });
								higherItem.setText(new String[] { itemName, itemValue });
								pairsTable.setSelection(index - 1);
							}
						}
					});
				}
				{
					removeButton = new GButton(mainComposite, SWT.PUSH | SWT.CENTER);
					FormData removeButtonLData = new FormData();
					removeButtonLData.width = 60;
					removeButtonLData.height = 20;
					removeButtonLData.top = new FormAttachment(0, 1000, 0);
					removeButtonLData.left = new FormAttachment(0, 1000, 70);
					removeButton.setLayoutData(removeButtonLData);
					removeButton.setText("Remove");
					removeButton.addSelectionListener(new SelectionAdapter()
					{
						@Override
						public void widgetSelected(@SuppressWarnings("unused") SelectionEvent event)
						{
							int index = pairsTable.getSelectionIndex();
							if (index >= 0)
							{
								GTableItem item = pairsTable.getItem(index);
								pairsTable.remove(index);
								item.dispose();
							}
						}
					});
				}
				{
					addButton = new GButton(mainComposite, SWT.PUSH | SWT.CENTER);
					FormData addButtonLData = new FormData();
					addButtonLData.width = 60;
					addButtonLData.height = 20;
					addButtonLData.top = new FormAttachment(0, 1000, 0);
					addButtonLData.left = new FormAttachment(0, 1000, 0);
					addButton.setLayoutData(addButtonLData);
					addButton.setText("Add");
					addButton.addSelectionListener(new SelectionAdapter()
					{
						@Override
						public void widgetSelected(@SuppressWarnings("unused") SelectionEvent event)
						{
							addPair("<name>", "<value>");
						}
					});
				}
			}
			{
				FormData pairsTableLData = new FormData();
				pairsTableLData.width = 537;
				pairsTableLData.height = 366;
				pairsTableLData.left = new FormAttachment(0, 1000, 0);
				int border = 0;
				if (editable)
				{
					border = 25;
				}
				pairsTableLData.top = new FormAttachment(0, 1000, border);
				pairsTableLData.right = new FormAttachment(1000, 1000, 0);
				pairsTableLData.bottom = new FormAttachment(1000, 1000, 0);
				pairsTableLData.left = new FormAttachment(0, 1000, 0);
				pairsTable = new GTable(mainComposite, SWT.FULL_SELECTION | SWT.BORDER);
				pairsTable.setLinesVisible(true);
				pairsTable.setHeaderVisible(true);
				pairsTable.setLayoutData(pairsTableLData);
				{
					nameColumn = new GTableColumn(pairsTable, SWT.NONE);
					nameColumn.setText("Name");
					nameColumn.setWidth(150);
				}
				{
					valueColumn = new GTableColumn(pairsTable, SWT.NONE);
					valueColumn.setText("Value");
					valueColumn.setWidth(300);
				}
				
				editor = new TableEditor(pairsTable);
				editor.horizontalAlignment = SWT.LEFT;
				editor.grabHorizontal = true;
				
				if (editable) pairsTable.addListener(SWT.MouseDoubleClick, new Listener()
				{
					@Override
					public void handleEvent(Event event)
					{
						Rectangle clientArea = pairsTable.getClientArea();
						Point pt = new Point(event.x, event.y);
						int index = pairsTable.getTopIndex();
						while (index < pairsTable.getItemCount())
						{
							boolean tableEditorVisible = false;
							final GTableItem item = pairsTable.getItem(index);
							for (int i = 0; i < pairsTable.getColumnCount(); i++)
							{
								Rectangle rect = item.getBounds(i);
								if (rect.contains(pt))
								{
									final int column = i;
									final GText text = new GText(pairsTable, SWT.NONE);
									Listener textListener = new Listener()
									{
										@Override
										public void handleEvent(final Event e)
										{
											switch (e.type)
											{
												case SWT.FocusOut:
													item.setText(column, text.getText());
													text.dispose();
													break;
												case SWT.Traverse:
													switch (e.detail)
													{
														case SWT.TRAVERSE_RETURN:
															item.setText(column, text.getText());
															//$FALL-THROUGH$
														case SWT.TRAVERSE_ESCAPE:
															text.dispose();
															e.doit = false;
													}
													break;
											}
										}
									};
									text.addListener(SWT.FocusOut, textListener);
									text.addListener(SWT.Traverse, textListener);
									editor.setEditor(text, item, i);
									text.setText(item.getText(i));
									text.selectAll();
									text.setFocus();
									return;
								}
								if (!tableEditorVisible && rect.intersects(clientArea))
								{
									tableEditorVisible = true;
								}
							}
							if (!tableEditorVisible)
							{
								return;
							}
							index++;
						}
					}
				});
			}
			this.layout();
	}
	
	public String getFirstValue(String name, boolean caseSensative)
	{
		for (GTableItem item: pairsTable.getItems())
		{
			if ((!caseSensative && item.getText(0).equalsIgnoreCase(name)) 
				|| (caseSensative && item.getText(0).equals(name)))
			{
				return item.getText(1);
			}
		}
		return null;
	}
	
	public void clearData()
	{
		for (GTableItem item: pairsTable.getItems())
		{
			item.dispose();
		}
		pairsTable.clearAll();
	}

	
	
}
