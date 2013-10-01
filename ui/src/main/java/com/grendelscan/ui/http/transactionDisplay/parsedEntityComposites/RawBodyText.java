package com.grendelscan.ui.http.transactionDisplay.parsedEntityComposites;

import org.eclipse.swt.accessibility.Accessible;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Widget;

import com.grendelscan.commons.StringUtils;
import com.grendelscan.ui.customControls.basic.GComposite;
import com.grendelscan.ui.customControls.basic.GShell;
import com.grendelscan.ui.customControls.basic.GText;

public class RawBodyText implements ParsedEntityComposite
{
    private final GText textBox;

    public RawBodyText(final GComposite parent, final int style)
    {
        textBox = new GText(parent, style);
    }

    @Override
    public boolean equals(final Object obj)
    {
        return textBox.equals(obj);
    }

    public Accessible getAccessible()
    {
        return textBox.getAccessible();
    }

    /*
     * TODO UCdetector: Remove unused code: public void addControlListener(ControlListener listener) { textBox.addControlListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void addDisposeListener(DisposeListener listener) { textBox.addDisposeListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void addDragDetectListener(DragDetectListener listener) { textBox.addDragDetectListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void addFocusListener(FocusListener listener) { textBox.addFocusListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void addHelpListener(HelpListener listener) { textBox.addHelpListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void addKeyListener(KeyListener listener) { textBox.addKeyListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void addListener(int eventType, Listener listener) { textBox.addListener(eventType, listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void addMenuDetectListener(MenuDetectListener listener) { textBox.addMenuDetectListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void addModifyListener(ModifyListener listener) { textBox.addModifyListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void addMouseListener(MouseListener listener) { textBox.addMouseListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void addMouseMoveListener(MouseMoveListener listener) { textBox.addMouseMoveListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void addMouseTrackListener(MouseTrackListener listener) { textBox.addMouseTrackListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void addMouseWheelListener(MouseWheelListener listener) { textBox.addMouseWheelListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void addPaintListener(PaintListener listener) { textBox.addPaintListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void addSelectionListener(SelectionListener listener) { textBox.addSelectionListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void addTraverseListener(TraverseListener listener) { textBox.addTraverseListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void addVerifyListener(VerifyListener listener) { textBox.addVerifyListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void append(String string) { textBox.append(string); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void clearSelection() { textBox.clearSelection(); }
     */

    /*
     * TODO UCdetector: Remove unused code: public Point computeSize(int hint, int hint2, boolean changed) { return textBox.computeSize(hint, hint2, changed); }
     */

    /*
     * TODO UCdetector: Remove unused code: public Point computeSize(int hint, int hint2) { return textBox.computeSize(hint, hint2); }
     */

    /*
     * TODO UCdetector: Remove unused code: public Rectangle computeTrim(int x, int y, int width, int height) { return textBox.computeTrim(x, y, width, height); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void copy() { textBox.copy(); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void cut() { textBox.cut(); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void dispose() { textBox.dispose(); }
     */

    /*
     * TODO UCdetector: Remove unused code: public boolean dragDetect(Event event) { return textBox.dragDetect(event); }
     */

    /*
     * TODO UCdetector: Remove unused code: public boolean dragDetect(MouseEvent event) { return textBox.dragDetect(event); }
     */

    public Color getBackground()
    {
        return textBox.getBackground();
    }

    /*
     * TODO UCdetector: Remove unused code: public boolean forceFocus() { return textBox.forceFocus(); }
     */

    public Image getBackgroundImage()
    {
        return textBox.getBackgroundImage();
    }

    public int getBorderWidth()
    {
        return textBox.getBorderWidth();
    }

    public Rectangle getBounds()
    {
        return textBox.getBounds();
    }

    @Override
    public byte[] getBytes()
    {
        return this.getText().getBytes(StringUtils.getDefaultCharset());
    }

    public int getCaretLineNumber()
    {
        return textBox.getCaretLineNumber();
    }

    public Point getCaretLocation()
    {
        return textBox.getCaretLocation();
    }

    public int getCaretPosition()
    {
        return textBox.getCaretPosition();
    }

    public int getCharCount()
    {
        return textBox.getCharCount();
    }

    public Rectangle getClientArea()
    {
        return textBox.getClientArea();
    }

    public Cursor getCursor()
    {
        return textBox.getCursor();
    }

    public Object getData()
    {
        return textBox.getData();
    }

    public Object getData(final String key)
    {
        return textBox.getData(key);
    }

    public Display getDisplay()
    {
        return textBox.getDisplay();
    }

    public boolean getDoubleClickEnabled()
    {
        return textBox.getDoubleClickEnabled();
    }

    public boolean getDragDetect()
    {
        return textBox.getDragDetect();
    }

    public char getEchoChar()
    {
        return textBox.getEchoChar();
    }

    public boolean getEditable()
    {
        return textBox.getEditable();
    }

    public boolean getEnabled()
    {
        return textBox.getEnabled();
    }

    public Font getFont()
    {
        return textBox.getFont();
    }

    public Color getForeground()
    {
        return textBox.getForeground();
    }

    public ScrollBar getHorizontalBar()
    {
        return textBox.getHorizontalBar();
    }

    public Object getLayoutData()
    {
        return textBox.getLayoutData();
    }

    public int getLineCount()
    {
        return textBox.getLineCount();
    }

    public String getLineDelimiter()
    {
        return textBox.getLineDelimiter();
    }

    public int getLineHeight()
    {
        return textBox.getLineHeight();
    }

    public Point getLocation()
    {
        return textBox.getLocation();
    }

    public Menu getMenu()
    {
        return textBox.getMenu();
    }

    public String getMessage()
    {
        return textBox.getMessage();
    }

    public Monitor getMonitor()
    {
        return textBox.getMonitor();
    }

    public int getOrientation()
    {
        return textBox.getOrientation();
    }

    public GComposite getParent()
    {
        return textBox.getParent();
    }

    public Point getSelection()
    {
        return textBox.getSelection();
    }

    public int getSelectionCount()
    {
        return textBox.getSelectionCount();
    }

    public String getSelectionText()
    {
        return textBox.getSelectionText();
    }

    public GShell getShell()
    {
        return textBox.getShell();
    }

    public Point getSize()
    {
        return textBox.getSize();
    }

    public int getStyle()
    {
        return textBox.getStyle();
    }

    public int getTabs()
    {
        return textBox.getTabs();
    }

    public String getText()
    {
        return textBox.getText();
    }

    public String getText(final int start, final int end)
    {
        return textBox.getText(start, end);
    }

    public int getTextLimit()
    {
        return textBox.getTextLimit();
    }

    public String getToolTipText()
    {
        return textBox.getToolTipText();
    }

    public int getTopIndex()
    {
        return textBox.getTopIndex();
    }

    public int getTopPixel()
    {
        return textBox.getTopPixel();
    }

    public ScrollBar getVerticalBar()
    {
        return textBox.getVerticalBar();
    }

    public boolean getVisible()
    {
        return textBox.getVisible();
    }

    @Override
    public Widget getWidget()
    {
        return textBox;
    }

    @Override
    public int hashCode()
    {
        return textBox.hashCode();
    }

    /*
     * TODO UCdetector: Remove unused code: public void insert(String string) { textBox.insert(string); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void internal_dispose_GC(int hdc, GCData data) { textBox.internal_dispose_GC(hdc, data); }
     */

    /*
     * TODO UCdetector: Remove unused code: public int internal_new_GC(GCData data) { return textBox.internal_new_GC(data); }
     */

    public boolean isDisposed()
    {
        return textBox.isDisposed();
    }

    public boolean isEnabled()
    {
        return textBox.isEnabled();
    }

    public boolean isFocusControl()
    {
        return textBox.isFocusControl();
    }

    /*
     * TODO UCdetector: Remove unused code: public boolean isListening(int eventType) { return textBox.isListening(eventType); }
     */

    public boolean isReparentable()
    {
        return textBox.isReparentable();
    }

    public boolean isVisible()
    {
        return textBox.isVisible();
    }

    /*
     * TODO UCdetector: Remove unused code: public void moveAbove(Control control) { textBox.moveAbove(control); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void moveBelow(Control control) { textBox.moveBelow(control); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void notifyListeners(int eventType, Event event) { textBox.notifyListeners(eventType, event); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void pack() { textBox.pack(); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void pack(boolean changed) { textBox.pack(changed); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void paste() { textBox.paste(); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void redraw() { textBox.redraw(); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void redraw(int x, int y, int width, int height, boolean all) { textBox.redraw(x, y, width, height, all); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void removeControlListener(ControlListener listener) { textBox.removeControlListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void removeDisposeListener(DisposeListener listener) { textBox.removeDisposeListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void removeDragDetectListener(DragDetectListener listener) { textBox.removeDragDetectListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void removeFocusListener(FocusListener listener) { textBox.removeFocusListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void removeHelpListener(HelpListener listener) { textBox.removeHelpListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void removeKeyListener(KeyListener listener) { textBox.removeKeyListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void removeListener(int eventType, Listener listener) { textBox.removeListener(eventType, listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void removeMenuDetectListener(MenuDetectListener listener) { textBox.removeMenuDetectListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void removeModifyListener(ModifyListener listener) { textBox.removeModifyListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void removeMouseListener(MouseListener listener) { textBox.removeMouseListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void removeMouseMoveListener(MouseMoveListener listener) { textBox.removeMouseMoveListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void removeMouseTrackListener(MouseTrackListener listener) { textBox.removeMouseTrackListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void removeMouseWheelListener(MouseWheelListener listener) { textBox.removeMouseWheelListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void removePaintListener(PaintListener listener) { textBox.removePaintListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void removeSelectionListener(SelectionListener listener) { textBox.removeSelectionListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void removeTraverseListener(TraverseListener listener) { textBox.removeTraverseListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void removeVerifyListener(VerifyListener listener) { textBox.removeVerifyListener(listener); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void selectAll() { textBox.selectAll(); }
     */

    public void setBackground(final Color color)
    {
        textBox.setBackground(color);
    }

    public void setBackgroundImage(final Image image)
    {
        textBox.setBackgroundImage(image);
    }

    public void setBounds(final int x, final int y, final int width, final int height)
    {
        textBox.setBounds(x, y, width, height);
    }

    public void setBounds(final Rectangle rect)
    {
        textBox.setBounds(rect);
    }

    public void setCapture(final boolean capture)
    {
        textBox.setCapture(capture);
    }

    public void setCursor(final Cursor cursor)
    {
        textBox.setCursor(cursor);
    }

    public void setData(final Object data)
    {
        textBox.setData(data);
    }

    public void setData(final String key, final Object value)
    {
        textBox.setData(key, value);
    }

    public void setDoubleClickEnabled(final boolean doubleClick)
    {
        textBox.setDoubleClickEnabled(doubleClick);
    }

    public void setDragDetect(final boolean dragDetect)
    {
        textBox.setDragDetect(dragDetect);
    }

    public void setEchoChar(final char echo)
    {
        textBox.setEchoChar(echo);
    }

    public void setEditable(final boolean editable)
    {
        textBox.setEditable(editable);
    }

    public void setEnabled(final boolean enabled)
    {
        textBox.setEnabled(enabled);
    }

    public boolean setFocus()
    {
        return textBox.setFocus();
    }

    public void setFont(final Font font)
    {
        textBox.setFont(font);
    }

    public void setForeground(final Color color)
    {
        textBox.setForeground(color);
    }

    public void setLayoutData(final Object layoutData)
    {
        textBox.setLayoutData(layoutData);
    }

    public void setLocation(final int x, final int y)
    {
        textBox.setLocation(x, y);
    }

    public void setLocation(final Point location)
    {
        textBox.setLocation(location);
    }

    public void setMenu(final Menu menu)
    {
        textBox.setMenu(menu);
    }

    public void setMessage(final String message)
    {
        textBox.setMessage(message);
    }

    public void setOrientation(final int orientation)
    {
        textBox.setOrientation(orientation);
    }

    public boolean setParent(final GComposite parent)
    {
        return textBox.setParent(parent);
    }

    public void setRedraw(final boolean redraw)
    {
        textBox.setRedraw(redraw);
    }

    public void setSelection(final int start)
    {
        textBox.setSelection(start);
    }

    public void setSelection(final int start, final int end)
    {
        textBox.setSelection(start, end);
    }

    public void setSelection(final Point selection)
    {
        textBox.setSelection(selection);
    }

    public void setSize(final int width, final int height)
    {
        textBox.setSize(width, height);
    }

    public void setSize(final Point size)
    {
        textBox.setSize(size);
    }

    public void setTabs(final int tabs)
    {
        textBox.setTabs(tabs);
    }

    // public void setText(String string)
    // {
    // textBox.setText(string);
    // }

    public void setTextLimit(final int limit)
    {
        textBox.setTextLimit(limit);
    }

    public void setToolTipText(final String string)
    {
        textBox.setToolTipText(string);
    }

    public void setTopIndex(final int index)
    {
        textBox.setTopIndex(index);
    }

    @Override
    public void setVisible(final boolean visible)
    {
        textBox.setVisible(visible);
    }

    /*
     * TODO UCdetector: Remove unused code: public void showSelection() { textBox.showSelection(); }
     */

    /*
     * TODO UCdetector: Remove unused code: public Point toControl(int x, int y) { return textBox.toControl(x, y); }
     */

    /*
     * TODO UCdetector: Remove unused code: public Point toControl(Point point) { return textBox.toControl(point); }
     */

    /*
     * TODO UCdetector: Remove unused code: public Point toDisplay(int x, int y) { return textBox.toDisplay(x, y); }
     */

    /*
     * TODO UCdetector: Remove unused code: public Point toDisplay(Point point) { return textBox.toDisplay(point); }
     */

    @Override
    public String toString()
    {
        return textBox.toString();
    }

    /*
     * TODO UCdetector: Remove unused code: public boolean traverse(int traversal) { return textBox.traverse(traversal); }
     */

    /*
     * TODO UCdetector: Remove unused code: public void update() { textBox.update(); }
     */

    @Override
    public void updateData(final byte[] data)
    {
        String s = new String(data, StringUtils.getDefaultCharset());
        s = s.replace('\0', ' ');
        textBox.setText(s);
    }

}
