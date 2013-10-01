package com.grendelscan.ui.http.transactionDisplay.parsedEntityComposites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

import com.grendelscan.commons.ArrayUtils;
import com.grendelscan.commons.StringUtils;
import com.grendelscan.ui.GuiUtils;
import com.grendelscan.ui.Verifiers.EnforceHexOnly;
import com.grendelscan.ui.customControls.basic.GComposite;
import com.grendelscan.ui.customControls.basic.GTable;
import com.grendelscan.ui.customControls.basic.GTableColumn;
import com.grendelscan.ui.customControls.basic.GTableItem;
import com.grendelscan.ui.customControls.basic.GText;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class HexViewComposite extends GComposite implements ParsedEntityComposite {
	GTable hexTable;
	TableEditor editor;
	private GTableColumn textColumn;
	private GTableColumn positionColumn;
	private boolean editable;

	public HexViewComposite(com.grendelscan.ui.customControls.basic.GComposite parent, int style, boolean editable) 
	{
		super(parent, style);
		this.editable = editable;
		initGUI();
	}
	
	/* (non-Javadoc)
     * @see com.grendelscan.ui.http.transactionDisplay.parsedEntityComposites.ParsedEntityComposite#updateData(byte[])
     */
	@Override
	public void updateData(byte[] data)
	{
		hexTable.removeAll();
		populateTable(data);
	}

	private void initGUI() 
	{
		GuiUtils.registerResourceUser(this);
			FillLayout thisLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
			this.setLayout(thisLayout);
			{
				final EnforceHexOnly hexOnlyVerifier = new EnforceHexOnly();
				hexTable = new GTable(this, SWT.NONE);
				hexTable.setHeaderVisible(true);
				hexTable.setLinesVisible(true);
				hexTable.setFont(GuiUtils.getFont("Courier New", 9, SWT.BOLD, false, false));

				editor = new TableEditor(hexTable);
				editor.horizontalAlignment = SWT.LEFT;
				editor.grabHorizontal = true;
				if (editable)
				{
					hexTable.addListener(SWT.MouseDoubleClick, new Listener()
					{
						@Override
						public void handleEvent(Event event)
						{
							Rectangle clientArea = hexTable.getClientArea();
							Point pt = new Point(event.x, event.y);
							int index = hexTable.getTopIndex();
							while (index < hexTable.getItemCount())
							{
								boolean tableEditorVisible = false;
								final GTableItem item = (GTableItem) hexTable.getItem(index);
								// We're starting at 1, because column 0 shouldn't be edited
								for (int i = 1; i < 17; i++)
								{
									Rectangle rect = item.getBounds(i);
									if (rect.contains(pt) && item.getText(i) != "")
									{
										final int column = i;
										final GText text = new GText(hexTable, SWT.NONE);
										final boolean hex;
										if (i > 0 && i < 17)
										{
											hex = true;
										}
										else
										{
											hex = false;
										}
	
										Listener textListener = new Listener()
										{
											@Override
											public void handleEvent(final Event e)
											{
												if (hex)
												{
													if (text.getText().length() == 0)
													{
														text.setText("00");
													}
													else if (text.getText().length() == 1)
													{
														String newText = "0" + text.getText();
														// This is due to problems with the verifier
														text.setText("");
														text.setText(newText);
													}
												}
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
												if (hex)
												{
													item.setText(17, getRowText(item));
												}
												else
												{
													String newText = item.getText(17);
													int offset = Integer.parseInt(item.getText(0));
													
													item.setText(createRowData(newText.getBytes(StringUtils.getDefaultCharset()), offset, newText.length()));
												}
											}
										};
										text.addListener(SWT.FocusOut, textListener);
										text.addListener(SWT.Traverse, textListener);
										text.setFont(GuiUtils.getFont("Courier New", 9, SWT.BOLD, false, false));
	
										editor.setEditor(text, item, i);
										// set limits
										if (hex)
										{
											text.addVerifyListener(hexOnlyVerifier);
											text.setTextLimit(2);
										}
										else
										{
											text.setTextLimit(16);
										}
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
				{
					positionColumn = new GTableColumn(hexTable, SWT.NONE);
					positionColumn.setWidth(80);
					positionColumn.setResizable(false);
					positionColumn.setText("Offset");
				}

				makeHexColumn(hexTable, "0");
				makeHexColumn(hexTable, "1");
				makeHexColumn(hexTable, "2");
				makeHexColumn(hexTable, "3");
				makeHexColumn(hexTable, "4");
				makeHexColumn(hexTable, "5");
				makeHexColumn(hexTable, "6");
				makeHexColumn(hexTable, "7");
				makeHexColumn(hexTable, "8");
				makeHexColumn(hexTable, "9");
				makeHexColumn(hexTable, "A");
				makeHexColumn(hexTable, "B");
				makeHexColumn(hexTable, "C");
				makeHexColumn(hexTable, "D");
				makeHexColumn(hexTable, "E");
				makeHexColumn(hexTable, "F");
				{
					textColumn = new GTableColumn(hexTable, SWT.NONE);
					textColumn.setResizable(false);
					textColumn.setWidth(160);
					textColumn.setText("GText");
				}
			}
			this.layout();
	}
	
	String getRowText(TableItem item)
	{
		String rowData[] = new String[17];
		for (int index = 1; index < 17; index++)
		{
			rowData[index] = item.getText(index);
		}
		return getRowText(rowData);
	}
	
	private String getRowText(String rowData[])
	{
		String text = "";
		for (int index = 1; index < 17; index++) 
		{
			String datum = rowData[index];
			if (datum != "")
			{
				int value = Integer.parseInt(datum, 16);
				if (value < 32)
				{
					text += ".";
				}
				else
				{
					text += String.format("%c", value);
				}
			}
		}
		return text;
	}
	
	private void populateTable(byte[] data)
	{
		for (int index = 0; index < data.length; index += 16)
		{
			GTableItem item = new GTableItem(hexTable, SWT.NONE);
			item.setFont(GuiUtils.getFont("Courier New", 9, SWT.BOLD, false, false));
			byte currentRow[] = ArrayUtils.copyOfRange(data, index, index + 15);
			item.setText(createRowData(currentRow, index, data.length - index));
		}
	}

	String[] createRowData(byte[] data, int offset, int maxLength)
	{
		String text[] = new String[18];
		text[0] = String.format("%08x", offset);
		for (int index = 0; index < 16; index++)
		{
			if (index < data.length && index < maxLength)
			{
				text[index + 1] = String.format("%02X", data[index]);
			}
			else
			{
				text[index + 1] = "";
			}
		}
		text[17] = getRowText(text);
		return text;
	}
	
	
	
	private GTableColumn makeHexColumn(GTable parent, String name)
	{
		GTableColumn hexColumn = new GTableColumn(parent, SWT.NONE);
		hexColumn.setText(name);
		hexColumn.setResizable(false);
		hexColumn.setWidth(30);
		return hexColumn;
	}
	
	@Override
	public byte[] getBytes()
	{
		int lastRowIndex = hexTable.getItemCount() - 1;
		if (lastRowIndex < 0)
		{
			return new byte[0];
		}
		int byteCount = 16 * lastRowIndex + getRowText(hexTable.getItem(lastRowIndex)).length(); 
		byte[] bytes = new byte[byteCount];
		int index = 0;
		for (TableItem item: hexTable.getItems())
		{
			for (int index2 = 1; index2 < 17; index2++)
			{
				String text = item.getText(index2);
				if (text != "")
				{
					bytes[index++] = (byte) Integer.parseInt(text, 16);
				}
				else
				{
					break;
				}
			}
		}
		
		return bytes;
	}

	@Override
	public Widget getWidget()
    {
	    return this;
    }
}
