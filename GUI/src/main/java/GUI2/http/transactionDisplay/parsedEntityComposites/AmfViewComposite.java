package com.grendelscan.GUI2.http.transactionDisplay.parsedEntityComposites;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import com.grendelscan.GUI.customControls.basic.GComposite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import com.grendelscan.GUI.customControls.basic.GText;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.grendelscan.GUI.GuiUtils;
import com.grendelscan.logging.Log;
import com.grendelscan.requester.http.dataHandling.references.DataReference;
import com.grendelscan.requester.http.dataHandling.references.NamedDataContainerDataReference;
import com.grendelscan.requester.http.dataHandling.references.NumberedListDataReference;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfDataContainer;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AbstractAmfNamedDataContainer;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AmfOutputStream;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.AmfPrimitiveData;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.arrays.AmfByteArray;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.arrays.AmfObjectArray;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.arrays.AmfPrimitiveArray;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.complexTypes.AmfActionMessageRoot;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.dataTypeDefinitions.AmfDataType;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.interfaces.AmfGenericObject;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.interfaces.ArbitraryChildren;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.interfaces.ArbitraryUnnamedChildren;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.interfaces.Orderable;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.messages.AmfOperation;
import com.grendelscan.requester.http.dataHandling.simplifiedAMF.messages.CommandMessageTypeEnum;
import com.grendelscan.utils.AmfUtils;

import flex.messaging.MessageException;
import flex.messaging.io.ClassAliasRegistry;
import flex.messaging.io.MessageDeserializer;
import flex.messaging.io.MessageIOConstants;
import flex.messaging.io.SerializationContext;
import flex.messaging.io.amf.ActionContext;
import flex.messaging.io.amf.ActionMessage;
import flex.messaging.io.amf.AmfMessageDeserializer;
import flex.messaging.io.amf.AmfMessageSerializer;
import flex.messaging.io.amf.AmfTrace;
import flex.messaging.messages.AcknowledgeMessageExt;
import flex.messaging.messages.AsyncMessageExt;
import flex.messaging.messages.CommandMessageExt;


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
public class AmfViewComposite extends GComposite implements ParsedEntityComposite 
{

	protected final boolean editable;
	protected Tree tree;
	protected TreeEditor editor;
	protected AmfActionMessageRoot rootMessage;
	
	protected static final String TYPE_LOCKED = "TYPE_LOCKED";
	protected static final String NAME_LOCKED = "NAME_LOCKED";
	protected static final String DATA_TYPE = "DATA_TYPE";
	protected static final String DELETABLE = "DELETABLE";
	protected static final String INDEX = "INDEX";

	protected static final int NAME_COLUMN = 0;
	protected static final int DATA_TYPE_COLUMN = 1;
	protected static final int VALUE_COLUMN = 2;
	
	protected static int[] widths;

	public AmfViewComposite(com.grendelscan.GUI.customControls.basic.GComposite parent, int style, boolean editable) {
		super(parent, style);
		this.editable = editable;
		initGUI();
	}

	private void initGUI() 
	{
		this.addDisposeListener(new DisposeListener()
		{
			@Override
			public void widgetDisposed(DisposeEvent arg0)
			{
				widths = GuiUtils.getColumnWidths(tree);
			}
		});
		FillLayout thisLayout = new FillLayout(SWT.HORIZONTAL);
		this.setLayout(thisLayout);


		tree = new Tree(this, SWT.FULL_SELECTION | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(true);
		TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
		column1.setText("Name");
		TreeColumn column2 = new TreeColumn(tree, SWT.LEFT);
		column2.setText("Data Type");
		TreeColumn column3 = new TreeColumn(tree, SWT.LEFT);
		column3.setText("Value");

		if (widths == null)
		{
			widths = new int[] {250, 250, 500};
		}
		GuiUtils.restoreColumnWidths(tree, widths);
		
		
		if (editable)
		{
			editor = new TreeEditor (tree);
			tree.addMouseListener(new CustomMouseListener());
		}
		this.layout();
	}

	protected void deleteTreeData(TreeItem treeItem)
	{
		TreeItem parentItem = treeItem.getParentItem();
		AbstractAmfData datum = (AbstractAmfData) treeItem.getData();
		AbstractAmfData parentDatum = (AbstractAmfData) parentItem.getData();
		
		if (parentDatum instanceof Orderable)
		{
			((Orderable) parentDatum).removeChild((Integer)treeItem.getData(INDEX));
		}
		else if (parentDatum instanceof AbstractAmfNamedDataContainer)
		{
			((AbstractAmfNamedDataContainer) parentDatum).removeChild(datum.getName());
		}
		else if (parentDatum instanceof ArbitraryUnnamedChildren)
		{
			((ArbitraryUnnamedChildren) parentDatum).remove(datum);
		}
        if (parentDatum instanceof Orderable)
        {
        	renumberArray(parentItem);
        }

	}
	

	protected class CustomMouseListener implements MouseListener
	{
		@Override
		public void mouseDoubleClick(@SuppressWarnings("unused") MouseEvent event)
        {
			//Don't do anything special for double clicks
        }


		@Override
		public void mouseDown(MouseEvent event)
        {
			Point point = new Point(event.x, event.y);
			TreeItem item = tree.getItem(point);
			switch(event.button)
			{
				case 1:
					handleEdit(item, point, event);
					break;
				case 3:
					handleContextMenu(item, event);
					break;
			}
        }


		@Override
		public void mouseUp(@SuppressWarnings("unused") MouseEvent event)
        {
			// Don't care about mouse up
        }
		
        private void handleContextMenu(final TreeItem treeItem, MouseEvent mouseEvent)
        {
			final TreeItem parentItem = treeItem.getParentItem();
			final AbstractAmfData parentData = (AbstractAmfData) treeItem.getData();

			Menu menu = new Menu(getShell(), SWT.POP_UP);
			final AbstractAmfData datum = (AbstractAmfData) treeItem.getData();
			Boolean deletable;
			if (datum == null)
			{
				deletable = (Boolean) treeItem.getData(DELETABLE);
			}
			else
			{
				deletable = datum.isDeletable();
			}
			
			final AbstractAmfData parentDatum = (parentItem == null ? null : (AbstractAmfData) parentItem.getData());
			final int datumIndex = (parentItem == null ? -1 : parentItem.indexOf(treeItem));
			
			if (datum != null)
			{
				if ((datum instanceof Orderable) || (datum instanceof ArbitraryChildren) && !(datum instanceof AmfByteArray))
				{
					MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
					String location;
					if (datum instanceof Orderable)
					{
						location = "Append element to '" + datum.getName() + "' at index " + datum.getChildren().size();
					}
					else
					{
						location = "Add field to '" + datum.getName() + "'";
					}
					menuItem.setText(location + " (type '" + datum.getType().getDescription() + "')");
					SelectionListener selectionAdapter = new SelectionAdapter()
					{
						@Override
                        public void widgetSelected(SelectionEvent event)
                        {
                            AmfDataType type = (AmfDataType) event.widget.getData();
                            DataReference reference;
                            if (datum instanceof Orderable)
                            {
                            	reference = new NumberedListDataReference(datum.getChildren().size());
                            }
                            else
                            {
                            	reference = new NamedDataContainerDataReference("unnamed".getBytes());
                            }
                            AbstractAmfData newData = AmfUtils.amfFactory(type, (AbstractAmfDataContainer<?>) parentData, reference, -4, rootMessage);
                            newData.setDeletable(true);
                            TreeItem childItem = new TreeItem(treeItem, SWT.NONE);
                            String name;
                            if ((datum instanceof Orderable))
                            {
                            	((Orderable) datum).addChild(newData);
                            	if (!(datum instanceof AmfObjectArray))
                            	{
                            		newData.setTypeLocked(true);
                            	}
                            	name = "";
                            }
                            else
                            {
                                name = getNewName((AbstractAmfNamedDataContainer) datum);
                            	((AbstractAmfNamedDataContainer) datum).putChild(name, newData);
                            }
                            displayAmfData(newData, childItem, name);
                            if (datum instanceof Orderable)
                            {
                            	renumberArray(treeItem);
                            }
                        }
					};
					if (datum instanceof ArbitraryChildren)
					{
						menuItem.setMenu(generateDataTypeSubMenu(menu, selectionAdapter, AmfDataType.getCreatableTypes()));
					}
					else
					{
						menuItem.setMenu(generateDataTypeSubMenu(menu, selectionAdapter, ((Orderable) datum).getChildTypes()));
					}
				}
			}
			
			if (parentDatum != null)
			{
				if (deletable && (parentDatum instanceof Orderable || parentDatum instanceof ArbitraryChildren))
				{
					MenuItem item = new MenuItem(menu, SWT.PUSH);
					item.setText("Delete");
					item.addSelectionListener(new SelectionAdapter()
					{
						@Override
                        public void widgetSelected(@SuppressWarnings("unused") SelectionEvent evt)
						{
							deleteTreeData(treeItem);
							treeItem.dispose();
						}
					});
				}

				if (parentDatum instanceof Orderable)
				{
					final Orderable orderableParent = (Orderable) parentDatum;
					{
						MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);

						menuItem.setText("Insert element at index " + datumIndex + " to field '" + parentDatum.getName() + "' (type '" + parentDatum.getType().getDescription() + "')");
						SelectionListener selectionAdapter = new SelectionAdapter()
						{
							@Override
	                        public void widgetSelected(SelectionEvent event)
	                        {
	                            AmfDataType type = (AmfDataType) event.widget.getData();
	                            AbstractAmfData newData = AmfUtils.amfFactory(type, (AbstractAmfDataContainer<?>) parentData, new NumberedListDataReference(datumIndex), -4, rootMessage);
	                            newData.setDeletable(true);
	                            TreeItem childItem = new TreeItem(parentItem, SWT.NONE, datumIndex);
	                            if (!(datum instanceof AmfObjectArray))
	                            {
	                            	newData.setTypeLocked(true);
	                            }
	                            orderableParent.addChild(datumIndex, newData);
	                            displayAmfData(newData, childItem, "");
                            	renumberArray(parentItem);
	                        }
						};
						menuItem.setMenu(generateDataTypeSubMenu(menu, selectionAdapter, orderableParent.getChildTypes()));
					}

//						{
//							MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
//							menuItem.setText("Move up");
//							menuItem.addSelectionListener(new SelectionAdapter()
//							{
//								public void widgetSelected(SelectionEvent evt)
//								{
//								}
//							});
//						}
//			
//						{
//							MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
//							menuItem.setText("Move down");
//							menuItem.addSelectionListener(new SelectionAdapter()
//							{
//								public void widgetSelected(SelectionEvent evt)
//								{
//								}
//							});
//						}
				}
			}


			/*
			 * This is really ugly, but I can't figure out how to get coordinates for 
			 * the mouse event. The even only contains them relative to the widget.
			 * A normal event would give them on the screen. AAAAHHHHHH!!!!!!
			 */
			menu.setLocation(mouseEvent.display.getCursorLocation());
			menu.setVisible(true);
			while (!menu.isDisposed() && menu.isVisible()) 
			{
	          if (!menu.getDisplay().readAndDispatch())
	        	  menu.getDisplay().sleep();
	        }
	        menu.dispose();
        }
        
        private Menu generateDataTypeSubMenu(Menu parentMenu, SelectionListener selectionListener, AmfDataType[] types)
        {
			Menu subMenu = new Menu(parentMenu);
			for (AmfDataType type: types)
			{
				MenuItem subItem = new MenuItem(subMenu, SWT.PUSH);
				subItem.setText(type.getDescription());
				subItem.addSelectionListener(selectionListener);
				subItem.setData(type);
			}
			return subMenu;
        }
        
		private void handleEdit (TreeItem item, Point point, @SuppressWarnings("unused") MouseEvent event) 
		{
			if (item == null)
				return;
			Boolean typeLocked = (Boolean) item.getData(TYPE_LOCKED);
			Boolean nameLocked = (Boolean) item.getData(NAME_LOCKED);
			AbstractAmfData datum = (AbstractAmfData) item.getData();
			AmfDataType type;
			if (datum == null)
			{
				type = (AmfDataType) item.getData(DATA_TYPE);
			}
			else if (item.getData(CLASSNAME) != null)
			{
				type = AmfDataType.kString;
			}
			else
			{
				type = datum.getType();
			}
			
			for (int columnNumber = 0; columnNumber < tree.getColumnCount(); columnNumber++) 
			{
				Rectangle rect = item.getBounds(columnNumber);
				if (rect.contains(point)) 
				{
 					EditControl editControl = null;
					if (columnNumber == NAME_COLUMN && !nameLocked)
					{
						editControl = new TextControl();
					}
					else if (columnNumber == DATA_TYPE_COLUMN && !typeLocked)
					{
						editControl = new DataTypeControl(type);
					}
					else if (columnNumber == VALUE_COLUMN && type.equals(AmfDataType.kBoolean))
					{
						editControl = new BooleanEditControl();
					}
					else if (columnNumber == VALUE_COLUMN && type.equals(AmfDataType.kCommandType))
					{
						editControl = new CommandMessageEditControl();
					}
					else if (columnNumber == VALUE_COLUMN && datum != null && (! datum.isValueLocked() || item.getData(CLASSNAME) != null))
					{
						editControl = new TextControl();
					}
					if (editControl != null)
					{
						handleEditClick(item, columnNumber, editControl);
					}
				}
			}
		}
	}
	
	
	protected String getNewName(AbstractAmfNamedDataContainer collection)
	{
		final String root = "New item";
		String name = root;
		if (collection.getChild(name) != null)
		{
			int i = 2;
			while(true)
            {
				name = root + " (" + i + ")";
				if (collection.getChild(name) == null)
					break;
				i++;
            }
		}
		return name;
	}
	
	private abstract class EditControl
	{
		public abstract void setValue(String text);
		public abstract String getValue();
		public abstract Control getControl();
		
		protected final GComposite composite;
		public GComposite getComposite()
        {
        	return composite;
        }
		protected EditControl()
		{
			composite = new GComposite (tree, SWT.NONE);
		}
	}
	
	private class ComboEditControl extends EditControl
	{
		protected final Combo combo;
		
		@Override
        public Control getControl()
        {
        	return combo;
        }

		public ComboEditControl()
		{
			combo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		
		@Override
        public void setValue(String text)
		{
			int index = combo.indexOf(text);
			if (index < 0) index = 0;
			combo.select(index);
		}

		@Override
        public String getValue()
        {
	        return combo.getText();
        }
	}
	

	private class DataTypeControl extends ComboEditControl
	{
		public DataTypeControl(AmfDataType currentType)
		{
			for (AmfDataType type: AmfDataType.getCreatableTypes())
			{
				combo.add(type.getDescription());
				combo.setData(type.getDescription(), type);
			}
			combo.setText(currentType.getDescription());
			combo.setSize(250, 25);
		}
	}
	

	private class BooleanEditControl extends ComboEditControl
	{
		public BooleanEditControl()
		{
			combo.add("True");
			combo.add("False");
			combo.setSize(250, 25);
		}
	}
	
	private class CommandMessageEditControl extends ComboEditControl
	{
		public CommandMessageEditControl()
		{
			for (CommandMessageTypeEnum type: CommandMessageTypeEnum.values())
			{
				combo.add(type.getDescription());
				combo.setData(type.getDescription(), type);
			}
			combo.setSize(250, 25);
		}
	}
	

	
	
	private class TextControl extends EditControl
	{
		private final GText textbox;
		
		@Override
        public Control getControl()
        {
        	return textbox;
        }

		public TextControl()
		{
			textbox = new GText(composite, SWT.NONE);
		}
		
		@Override
        public void setValue(String text)
		{
			textbox.setText(text);
		}

		@Override
        public String getValue()
        {
	        return textbox.getText();
        }

//		@Override
//        public void selected()
//        {
//        }
	}
	
	
	
		
		
		
		
		
	
	protected void handleEditClick(final TreeItem treeItem, final int column, final EditControl control)
	{
		if (treeItem != null ) 
		{
			boolean showBorder = true;
			final GComposite composite = control.getComposite();
			if (showBorder) 
				composite.setBackground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
			final int inset = showBorder ? 1 : 0;
			composite.addListener (SWT.Resize, new Listener () 
			{
				@Override
				public void handleEvent (@SuppressWarnings("unused") Event e) 
				{
					Rectangle rect = composite.getClientArea ();
					control.getControl().setBounds (rect.x + inset + 4, rect.y + inset, rect.width - inset * 2 + 4, rect.height - inset * 2);
				}
			});
			Listener textListener = new Listener () 
			{
				@Override
				@SuppressWarnings("fallthrough")
                public void handleEvent (final Event e) 
				{
					switch (e.type) 
					{
						case SWT.FocusOut:
							updateItemData(treeItem, column, control);
							composite.dispose ();
							break;
//						case SWT.Verify:
						case SWT.FocusIn:
							String newText = control.getValue ();
							String leftText = newText.substring (0, e.start);
							String rightText = newText.substring (e.end, newText.length ());
							GC gc = new GC (control.getControl());
							Point size = gc.textExtent (leftText + e.text + rightText);
							gc.dispose ();
							size = control.getControl().computeSize (size.x, SWT.DEFAULT);
							editor.horizontalAlignment = SWT.LEFT;
							Rectangle itemRect = treeItem.getBounds ();
							Rectangle rect = tree.getClientArea ();
							editor.minimumWidth = Math.max (size.x, itemRect.width) + inset * 2;
							int left = itemRect.x;
							int right = rect.x + rect.width;
							editor.minimumWidth = Math.min (editor.minimumWidth, right - left);
							editor.minimumHeight = size.y + inset * 2;
							editor.layout();
							break;
						case SWT.Traverse:
							switch (e.detail) 
							{
								case SWT.TRAVERSE_RETURN:
									updateItemData(treeItem, column, control);
									//FALL THROUGH
								case SWT.TRAVERSE_ESCAPE:
									composite.dispose ();
									e.doit = false;
							}
							break;
					}
				}
			};
			control.getControl().addListener(SWT.FocusIn, textListener);
			control.getControl().addListener (SWT.FocusOut, textListener);
			control.getControl().addListener (SWT.Traverse, textListener);
//			control.getControl().addListener (SWT.Verify, textListener);
			editor.setColumn(column);
			editor.grabHorizontal = (control instanceof TextControl) ? true : false;
			editor.horizontalAlignment = SWT.LEFT;
			editor.setEditor (composite, treeItem, column);
			control.setValue(treeItem.getText(column));
			control.getControl().setFocus();
		}
		
	}
	
	protected void updateItemData(final TreeItem treeItem, final int column, final EditControl control)
	{
		if (!control.getValue().equals(treeItem.getText(column)))
		{
			treeItem.setText (column, control.getValue());
			if ((control instanceof TextControl) || (control instanceof BooleanEditControl))
			{
				if (treeItem.getData(CLASSNAME) != null)
				{
					((AmfGenericObject) treeItem.getData()).setClassName(control.getValue());
				}
				else
				{
					AmfUtils.setAmfValue((AbstractAmfData) treeItem.getData(), control.getValue());
				}
			}
			else if (control instanceof DataTypeControl)
			{
				String name = treeItem.getText(NAME_COLUMN);
				deleteTreeData(treeItem);
				for (TreeItem subItem: treeItem.getItems())
				{
					subItem.dispose();
				}
                AmfDataType type = (AmfDataType) control.getControl().getData(control.getValue());
				AbstractAmfData parent = (AbstractAmfData) treeItem.getData();
                AbstractAmfData newData = AmfUtils.amfFactory(type, (AbstractAmfDataContainer<?>) parent, null, -4, rootMessage);
                newData.setDeletable(true);
                displayAmfData(newData, treeItem, name);
			}
			else if (control instanceof CommandMessageEditControl)
			{
				AmfOperation command = (AmfOperation) treeItem.getData();
				CommandMessageEditControl cmeControl = (CommandMessageEditControl) control;
				CommandMessageTypeEnum commandType = (CommandMessageTypeEnum) cmeControl.getControl().getData(control.getValue());
				command.setCommandType(commandType); 
			}
		}
	}
	
	protected void renumberArray(TreeItem item)
	{
		int pos = 0;
		for(TreeItem child: item.getItems())
		{
			child.setData(INDEX, pos);
			child.setText(0, "[" + pos + "]");
			AbstractAmfData d = (AbstractAmfData) child.getData();
			NumberedListDataReference ref = (NumberedListDataReference) d.getReference();
			ref.setIndex(pos);
			pos++;
		}
	}
	
	@Override
	public void updateData(byte[] data)
    {
		if (data == null || data.length == 0)
		{
			rootMessage = null;
			for (TreeItem item: tree.getItems())
			{
				item.dispose();
			}
			return;
		}
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		tree.removeAll();
		
        ClassAliasRegistry registry = ClassAliasRegistry.getRegistry();
        registry.registerAlias(AsyncMessageExt.CLASS_ALIAS, AsyncMessageExt.class.getName());
        registry.registerAlias(AcknowledgeMessageExt.CLASS_ALIAS, AcknowledgeMessageExt.class.getName());
        registry.registerAlias(CommandMessageExt.CLASS_ALIAS, CommandMessageExt.class.getName());

        ActionContext context = new ActionContext();
		AmfTrace debugTrace = null;

        context.setResponseMessage(new ActionMessage());
        context.setLegacy(false);
        SerializationContext sc = SerializationContext.getSerializationContext();
        sc.setDeserializerClass(AmfMessageDeserializer.class);
        sc.setSerializerClass(AmfMessageSerializer.class);

        try
        {
            // Deserialize the input stream into an "ActionMessage" object.
            MessageDeserializer deserializer = sc.newMessageDeserializer();

            // Set up the deserialization context
//            InputStream in = FlexContext.getHttpRequest().getInputStream();
            deserializer.initialize(sc, in, debugTrace);

            // record the length of the input stream for performance metrics
//            int reqLen = FlexContext.getHttpRequest().getContentLength();
            int reqLen = data.length;
            context.setDeserializedBytes(reqLen);

            ActionMessage m = new ActionMessage();
            context.setRequestMessage(m);
            deserializer.readMessage(m, context);
            rootMessage = new AmfActionMessageRoot(m, -4);
    		TreeItem item = new TreeItem(tree, SWT.NONE);
    		item.setData(rootMessage);
            displayAmfData(rootMessage, item, "Message root");
    		item.setExpanded(true);
        }
        catch (EOFException eof)
        {
            context.setStatus(MessageIOConstants.STATUS_NOTAMF);
            Log.error("Problem parsing AMF message for display", eof);
        }
        catch (ClassNotFoundException e)
        {
	        e.printStackTrace();
            Log.error("Problem parsing AMF message for display", e);
        }
        catch (IOException e)
        {
	        e.printStackTrace();
            Log.error("Problem parsing AMF message for display", e);
        }
        catch (MessageException e)
        {
	        e.printStackTrace();
            Log.error("Problem parsing AMF message for display", e);
        }
    }
	private static final String CLASSNAME = "Class name";
	protected void displayAmfData(AbstractAmfData data, TreeItem item, String name)
	{
		String nameToUse = name;
		TreeItem parent = item.getParentItem();
		if (parent != null)
		{
			AbstractAmfData parentDatum = (AbstractAmfData) parent.getData();
			if (parentDatum instanceof Orderable && (nameToUse == null || nameToUse.equals("")))
			{
//				int pos = parent.indexOf(item);
//				name = "[" + pos + "]";
//				item.setData(INDEX, pos);
				data.setNameLocked(true);
			}
		}
		if (nameToUse == null || nameToUse.equals(""))
			nameToUse = "----";
		
		item.setData(data);
		item.setData(TYPE_LOCKED, data.isTypeLocked());
		item.setData(NAME_LOCKED, data.isNameLocked());
		
		if (data instanceof AmfByteArray)
		{
			item.setText(new String[] {nameToUse, data.getType().getDescription(), ""});
		}
		else if (data instanceof AmfPrimitiveArray && ! (data instanceof AmfObjectArray))
		{
			item.setText(new String[] {nameToUse, data.getType().getDescription(), ""});
			displayPrimitiveArray((AmfPrimitiveArray) data, item);
		}
		else
		{
			if (data instanceof AmfGenericObject)
			{
				TreeItem classItem = new TreeItem(item, SWT.NONE);
				classItem.setData(NAME_LOCKED, true);
				classItem.setData(TYPE_LOCKED, true);
				classItem.setData(DELETABLE, false);
				classItem.setData(CLASSNAME, true);
				classItem.setData(data);
				classItem.setText(new String[] {CLASSNAME, ((AmfGenericObject) data).getClassName()});
			}
			item.setText(new String[] {nameToUse, data.getType().getDescription(), AmfUtils.getAmfValue(data)});
			ArrayList<AbstractAmfData> children = data.getChildren();
			if (children != null)
			{
				int pos = 0;
				for (AbstractAmfData child: children)
				{
					if (child == null)
					{
						Log.warn("Null  child");
					}
					TreeItem subItem = new TreeItem(item, SWT.NONE);
					subItem.setData(child);
					String childName = child.getName();
					boolean nameLocked = child.isNameLocked();
					if (childName.equals(""))
					{
						childName = "[" + pos + "]";
						nameLocked = true;
					}
					subItem.setData(INDEX, pos);
					pos++;
					if (data instanceof AmfPrimitiveArray)
					{
						nameLocked = true;
					}
					child.setNameLocked(nameLocked);
					subItem.setData(NAME_LOCKED, nameLocked);
//					subItem.setText(new String[] {childName, child.getType().getDescription(), getAmfValue(child)});
					displayAmfData(child, subItem, childName);
					subItem.setExpanded(true);
				}
			}
		}
	}
	
	
	
	
	protected void displayPrimitiveArray(AmfPrimitiveArray array, TreeItem item)
	{
		int pos = 0;
		for (Object o: array.getChildren())
		{
			TreeItem subItem = new TreeItem(item, SWT.BORDER);
			AmfDataType type = null;
			if (o instanceof Integer)
			{
				type = AmfDataType.kInteger;
			}
			else if (o instanceof Boolean)
			{
				type = AmfDataType.kBoolean;
			} 
			else if (o instanceof Double)
			{
				type = AmfDataType.kDouble;
			}
			else if (o instanceof AmfPrimitiveData)
			{
				type = ((AmfPrimitiveData) o).getType();
			}
			subItem.setText(new String[] {String.valueOf(pos), type.getDescription(), o.toString()});
			subItem.setData(TYPE_LOCKED, true);
			subItem.setData(DATA_TYPE, type);
			subItem.setData(NAME_LOCKED, true);
			subItem.setData(DELETABLE, true);
			subItem.setData(INDEX, pos);
		}
	}

	@Override
	public byte[] getBytes() 
    {
		if (rootMessage == null)
		{
			return new byte[0];
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream(0);
		rootMessage.writeBytes(baos);
	    return baos.toByteArray();
    }

	@Override
	public Widget getWidget()
    {
	    return this;
    }

}
