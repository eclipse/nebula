
package com.airbus.ds.s3.ibd.ui.parts.properties.checkbox;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.accessibility.AccessibleTextAdapter;
import org.eclipse.swt.accessibility.AccessibleTextEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.swt.widgets.Widget;

/**
 * The TableCombo class represents a selectable user interface object that combines a label, textfield, and a table and issues notification when an
 * item is selected from the table.
 *
 * Note: This widget is basically a extension of the CCombo widget. The list control was replaced by a Table control and a Label control was added so
 * that images can be displayed when a value from the drop down items has a image associated to it.
 *
 * <p>
 * TableCombo was written to allow the user to be able to display multiple columns of data in the "Drop Down" portion of the combo.
 * </p>
 * <p>
 * Special Note: Although this class is a subclass of <code>Composite</code>, it does not make sense to add children to it, or set a layout on it.
 * </p>
 * <dl>
 * <dt><b>Styles:</b>
 * <dd>BORDER, READ_ONLY, FLAT</dd>
 * <dt><b>Events:</b>
 * <dd>DefaultSelection, Modify, Selection, Verify</dd>
 * </dl>
 *
 */

public class CheckTableCombo extends Composite {
    private Shell popup;
    private Button arrow;
    private Label selectedImage;
    private Text text;
    private Table table;
    private Font font;
    private boolean hasFocus;
    private int visibleItemCount = 7;
    private Listener listener;
    private Listener focusFilter;
    private int displayColumnIndex = 0;
    private Color foreground;
    private Color background;
    private int[] columnWidths;
    private int tableWidthPercentage = 100;
    private boolean showImageWithinSelection = true;
    private boolean showColorWithinSelection = true;
    private boolean showFontWithinSelection = true;
    private boolean closePopupAfterSelection = false;
    private String separator = ";";
    private Function<Collection<TableItem>, String> convertToString = items -> {
        final int colIndexToUse = this.getDisplayColumnIndex();
        final String textToDisplay = items.stream().map(tableItem -> tableItem.getText(colIndexToUse)).collect(Collectors.joining(this.separator));
        return textToDisplay;
    };

    /**
     * Constructs a new instance of this class given its parent and a style value describing its behavior and appearance.
     * <p>
     * The style value is either one of the style constants defined in class <code>SWT</code> which is applicable to instances of this class, or must
     * be built by <em>bitwise OR</em>'ing together (that is, using the <code>int</code> "|" operator) two or
     * more of those <code>SWT</code> style constants. The class description lists the style constants that are applicable to the class. Style bits
     * are also inherited from superclasses.
     * </p>
     *
     * @param parent a widget which will be the parent of the new instance (cannot be null)
     * @param style the style of widget to construct
     *
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
     *                </ul>
     *
     * @see SWT#BORDER
     * @see SWT#READ_ONLY
     * @see SWT#FLAT
     * @see Widget#getStyle()
     */
    public CheckTableCombo(Composite parent, int style) {
        super(parent, style = checkStyle(style));

        // set the label style
        int textStyle = SWT.SINGLE;
        if ((style & SWT.READ_ONLY) != 0) {
            textStyle |= SWT.READ_ONLY;
        }
        if ((style & SWT.FLAT) != 0) {
            textStyle |= SWT.FLAT;
        }

        // set control background to white
        this.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

        // create label to hold image if necessary.
        this.selectedImage = new Label(this, SWT.NONE);
        this.selectedImage.setAlignment(SWT.RIGHT);

        this.getLayout();

        // create the control to hold the display text of what the user selected.
        this.text = new Text(this, textStyle);

        // set the arrow style.
        int arrowStyle = SWT.ARROW | SWT.DOWN;
        if ((style & SWT.FLAT) != 0) {
            arrowStyle |= SWT.FLAT;
        }

        // create the down arrow button
        this.arrow = new Button(this, arrowStyle);

        // now add a listener to listen to the events we are interested in.
        this.listener = new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (CheckTableCombo.this.isDisposed()) {
                    return;
                }

                // check for a popup event
                if (CheckTableCombo.this.popup == event.widget) {
                    CheckTableCombo.this.popupEvent(event);
                    return;
                }

                if (CheckTableCombo.this.text == event.widget) {
                    CheckTableCombo.this.textEvent(event);
                    return;
                }

                // check for a table event
                if (CheckTableCombo.this.table == event.widget) {
                    CheckTableCombo.this.tableEvent(event);
                    return;
                }

                // check for arrow event
                if (CheckTableCombo.this.arrow == event.widget) {
                    CheckTableCombo.this.arrowEvent(event);
                    return;
                }

                // check for this widget's event
                if (CheckTableCombo.this == event.widget) {
                    CheckTableCombo.this.comboEvent(event);
                    return;
                }

                // check for shell event
                if (CheckTableCombo.this.getShell() == event.widget) {
                    CheckTableCombo.this.getDisplay().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            if (CheckTableCombo.this.isDisposed()) {
                                return;
                            }
                            CheckTableCombo.this.handleFocus(SWT.FocusOut);
                        }
                    });
                }
            }
        };

        // create new focus listener
        this.focusFilter = new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (CheckTableCombo.this.isDisposed()) {
                    return;
                }
                final Shell shell = ((Control) event.widget).getShell();

                if (shell == CheckTableCombo.this.getShell()) {
                    CheckTableCombo.this.handleFocus(SWT.FocusOut);
                }
            }
        };

        // set the listeners for this control
        final int[] comboEvents = {SWT.Dispose, SWT.FocusIn, SWT.Move, SWT.Resize};
        for (final int comboEvent : comboEvents) {
            this.addListener(comboEvent, this.listener);
        }

        final int[] textEvents = {SWT.DefaultSelection, SWT.KeyDown, SWT.KeyUp, SWT.MenuDetect, SWT.Modify, SWT.MouseDown, SWT.MouseUp,
            SWT.MouseDoubleClick, SWT.MouseWheel, SWT.Traverse, SWT.FocusIn, SWT.Verify};
        for (final int textEvent : textEvents) {
            this.text.addListener(textEvent, this.listener);
        }

        // set the listeners for the arrow image
        final int[] arrowEvents = {SWT.Selection, SWT.FocusIn};
        for (final int arrowEvent : arrowEvents) {
            this.arrow.addListener(arrowEvent, this.listener);
        }

        // initialize the drop down
        this.createPopup(-1);

        this.initAccessible();
    }

    /**
     * @param style
     * @return
     */
    private static int checkStyle(int style) {
        final int mask = SWT.BORDER | SWT.READ_ONLY | SWT.FLAT | SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT;
        style = SWT.NO_FOCUS | (style & mask);
        return style;
    }

    /**
     * Adds the listener to the collection of listeners who will be notified when the receiver's text is modified, by sending it one of the messages
     * defined in the <code>ModifyListener</code> interface.
     *
     * @param listener the listener which should be notified
     *
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     *
     * @see ModifyListener
     * @see #removeModifyListener
     */
    public void addModifyListener(ModifyListener listener) {
        this.checkWidget();
        if (listener == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        final TypedListener typedListener = new TypedListener(listener);
        this.addListener(SWT.Modify, typedListener);
    }

    /**
     * Adds the listener to the collection of listeners who will be notified when the user changes the receiver's selection, by sending it one of the
     * messages defined in the <code>SelectionListener</code> interface.
     * <p>
     * <code>widgetSelected</code> is called when the combo's list selection changes. <code>widgetDefaultSelected</code> is typically called when
     * ENTER is pressed the combo's text area.
     * </p>
     *
     * @param listener the listener which should be notified when the user changes the receiver's selection
     *
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     *
     * @see SelectionListener
     * @see #removeSelectionListener
     * @see SelectionEvent
     */
    public void addSelectionListener(SelectionListener listener) {
        this.checkWidget();
        if (listener == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        final TypedListener typedListener = new TypedListener(listener);
        this.addListener(SWT.Selection, typedListener);
        this.addListener(SWT.DefaultSelection, typedListener);
    }

    /**
     * Adds the listener to the collection of listeners who will be notified when the user presses keys in the text field. interface.
     *
     * @param listener the listener which should be notified when the user presses keys in the text control.
     *
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     */
    public void addTextControlKeyListener(KeyListener listener) {
        this.checkWidget();
        if (listener == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        this.text.addKeyListener(listener);
    }

    /**
     * Removes the listener from the collection of listeners who will be notified when the user presses keys in the text control.
     *
     * @param listener the listener which should no longer be notified
     *
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     *
     */
    public void removeTextControlKeyListener(KeyListener listener) {
        this.checkWidget();
        if (listener == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        this.text.removeKeyListener(listener);
    }

    /**
     * Adds the listener to the collection of listeners who will be notified when the receiver's text is verified, by sending it one of the messages
     * defined in the <code>VerifyListener</code> interface.
     *
     * @param listener the listener which should be notified
     *
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     *
     * @see VerifyListener
     * @see #removeVerifyListener
     *
     * @since 3.3
     */
    public void addVerifyListener(VerifyListener listener) {
        this.checkWidget();
        if (listener == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        final TypedListener typedListener = new TypedListener(listener);
        this.addListener(SWT.Verify, typedListener);
    }

    /**
     * Handle Arrow Event
     *
     * @param event
     */
    private void arrowEvent(Event event) {
        switch (event.type) {
            case SWT.FocusIn: {
                this.handleFocus(SWT.FocusIn);
                break;
            }
            case SWT.MouseDown: {
                final Event mouseEvent = new Event();
                mouseEvent.button = event.button;
                mouseEvent.count = event.count;
                mouseEvent.stateMask = event.stateMask;
                mouseEvent.time = event.time;
                mouseEvent.x = event.x;
                mouseEvent.y = event.y;
                this.notifyListeners(SWT.MouseDown, mouseEvent);
                event.doit = mouseEvent.doit;
                break;
            }
            case SWT.MouseUp: {
                final Event mouseEvent = new Event();
                mouseEvent.button = event.button;
                mouseEvent.count = event.count;
                mouseEvent.stateMask = event.stateMask;
                mouseEvent.time = event.time;
                mouseEvent.x = event.x;
                mouseEvent.y = event.y;
                this.notifyListeners(SWT.MouseUp, mouseEvent);
                event.doit = mouseEvent.doit;
                break;
            }
            case SWT.Selection: {
                this.text.setFocus();
                this.dropDown(!this.isDropped());
                break;
            }
        }
    }

    /**
     * Sets the selection in the receiver's text field to an empty selection starting just before the first character. If the text field is editable,
     * this has the effect of placing the i-beam at the start of the text.
     * <p>
     * Note: To clear the selected items in the receiver's list, use <code>deselectAll()</code>.
     * </p>
     *
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     *
     * @see #deselectAll
     */
    public void clearSelection() {
        this.checkWidget();
        this.text.clearSelection();
        this.table.deselectAll();
    }

    /**
     * Handle Combo events
     *
     * @param event
     */
    private void comboEvent(Event event) {
        switch (event.type) {
            case SWT.Dispose:
                this.removeListener(SWT.Dispose, this.listener);
                this.notifyListeners(SWT.Dispose, event);
                event.type = SWT.None;

                if (this.popup != null && !this.popup.isDisposed()) {
                    this.table.removeListener(SWT.Dispose, this.listener);
                    this.popup.dispose();
                }
                final Shell shell = this.getShell();
                shell.removeListener(SWT.Deactivate, this.listener);
                final Display display = this.getDisplay();
                display.removeFilter(SWT.FocusIn, this.focusFilter);
                this.popup = null;
                this.text = null;
                this.table = null;
                this.arrow = null;
                this.selectedImage = null;
                break;
            case SWT.FocusIn:
                final Control focusControl = this.getDisplay().getFocusControl();
                if (focusControl == this.arrow || focusControl == this.table) {
                    return;
                }
                if (this.isDropped()) {
                    this.table.setFocus();
                } else {
                    this.text.setFocus();
                }
                break;
            case SWT.Move:
                this.dropDown(false);
                break;
            case SWT.Resize:
                this.internalLayout(false, false);
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        this.checkWidget();

        int overallWidth = 0;
        int overallHeight = 0;
        final int borderWidth = this.getBorderWidth();

        // use user defined values if they are specified.
        if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT) {
            overallWidth = wHint;
            overallHeight = hHint;
        } else {
            final TableItem[] tableItems = this.table.getItems();

            final GC gc = new GC(this.text);
            final int spacer = gc.stringExtent(" ").x; //$NON-NLS-1$
            int maxTextWidth = gc.stringExtent(this.text.getText()).x;
            final int colIndex = this.getDisplayColumnIndex();
            int maxImageHeight = 0;
            int currTextWidth = 0;

            // calculate the maximum text width and image height.
            for (final TableItem tableItem : tableItems) {
                currTextWidth = gc.stringExtent(tableItem.getText(colIndex)).x;

                // take image into account if there is one for the tableitem.
                if (tableItem.getImage() != null) {
                    currTextWidth += tableItem.getImage().getBounds().width;
                    maxImageHeight = Math.max(tableItem.getImage().getBounds().height, maxImageHeight);
                }

                maxTextWidth = Math.max(currTextWidth, maxTextWidth);
            }

            gc.dispose();
            final Point textSize = this.text.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
            final Point arrowSize = this.arrow.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);

            overallHeight = Math.max(textSize.y, arrowSize.y);
            overallHeight = Math.max(maxImageHeight, overallHeight);
            overallWidth = maxTextWidth + 2 * spacer + arrowSize.x + 2 * borderWidth;

            // use user specified if they were entered.
            if (wHint != SWT.DEFAULT) {
                overallWidth = wHint;
            }
            if (hHint != SWT.DEFAULT) {
                overallHeight = hHint;
            }
        }

        return new Point(overallWidth + 2 * borderWidth, overallHeight + 2 * borderWidth);
    }

    /**
     * Copies the selected text.
     * <p>
     * The current selection is copied to the clipboard.
     * </p>
     *
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     *
     * @since 3.3
     */
    public void copy() {
        this.checkWidget();
        this.text.copy();
    }

    /**
     * creates the popup shell.
     *
     * @param selectionIndex
     */
    void createPopup(int selectionIndex) {
        // create shell and table
        this.popup = new Shell(this.getShell(), SWT.NO_TRIM | SWT.ON_TOP);

        // create table
        this.table = new Table(this.popup, SWT.FULL_SELECTION | SWT.CHECK);

        if (this.font != null) {
            this.table.setFont(this.font);
        }
        if (this.foreground != null) {
            this.table.setForeground(this.foreground);
        }
        if (this.background != null) {
            this.table.setBackground(this.background);
        }

        // Add popup listeners
        final int[] popupEvents = {SWT.Close, SWT.Paint, SWT.Deactivate, SWT.Help};
        for (final int popupEvent : popupEvents) {
            this.popup.addListener(popupEvent, this.listener);
        }

        // add table listeners
        final int[] tableEvents = {SWT.MouseUp, SWT.Selection, SWT.Traverse, SWT.KeyDown, SWT.KeyUp, SWT.FocusIn, SWT.Dispose};
        // int[] tableEvents = { SWT.MouseUp, SWT.Traverse, SWT.KeyDown, SWT.KeyUp, SWT.FocusIn, SWT.Dispose };
        for (final int tableEvent : tableEvents) {
            this.table.addListener(tableEvent, this.listener);
        }

        // set the selection
        if (selectionIndex != -1) {
            this.table.setSelection(selectionIndex);
        }
    }

    /**
     * Cuts the selected text.
     * <p>
     * The current selection is first copied to the clipboard and then deleted from the widget.
     * </p>
     *
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     *
     * @since 3.3
     */
    public void cut() {
        this.checkWidget();
        this.text.cut();
    }

    /**
     * handle DropDown request
     *
     * @param drop
     */
    void dropDown(boolean drop) {

        // if already dropped then return
        if (drop == this.isDropped()) {
            return;
        }

        // closing the dropDown
        if (!drop) {
            this.popup.setVisible(false);
            if (!this.isDisposed() && this.isFocusControl()) {
                this.text.setFocus();
            }
            return;
        }

        // if not visible then return
        if (!this.isVisible()) {
            return;
        }

        // create a new popup if needed.
        if (this.getShell() != this.popup.getParent()) {
            final int selectionIndex = this.table.getSelectionIndex();
            this.table.removeListener(SWT.Dispose, this.listener);
            this.popup.dispose();
            this.popup = null;
            this.table = null;
            this.createPopup(selectionIndex);
        }

        // get the size of the TableCombo.
        final Point tableComboSize = this.getSize();

        // calculate the table height.
        int itemCount = this.table.getItemCount();
        itemCount = (itemCount == 0) ? this.visibleItemCount : Math.min(this.visibleItemCount, itemCount);
        int itemHeight = (this.table.getItemHeight() * itemCount);

        // add 1 to the table height if the table item count is less than the visible item count.
        if (this.table.getItemCount() <= this.visibleItemCount) {
            itemHeight += 1;
        }

        if (itemCount <= this.visibleItemCount) {
            if (this.table.getHorizontalBar() != null && !this.table.getHorizontalBar().isVisible()) {
                itemHeight -= this.table.getHorizontalBar().getSize().y;
            }
        }

        // add height of header if the header is being displayed.
        if (this.table.getHeaderVisible()) {
            itemHeight += this.table.getHeaderHeight();
        }

        // get table column references
        TableColumn[] tableColumns = this.table.getColumns();
        int totalColumns = (tableColumns == null ? 0 : tableColumns.length);

        // check to make sure at least one column has been specified. if it hasn't
        // then just create a blank one.
        if (this.table.getColumnCount() == 0) {
            new TableColumn(this.table, SWT.NONE);
            totalColumns = 1;
            tableColumns = this.table.getColumns();
        }

        int totalColumnWidth = 0;
        // now pack any columns that do not have a explicit value set for them.
        for (int colIndex = 0; colIndex < totalColumns; colIndex++) {
            if (!this.wasColumnWidthSpecified(colIndex)) {
                tableColumns[colIndex].pack();
            }
            totalColumnWidth += tableColumns[colIndex].getWidth();
        }

        // reset the last column's width to the preferred size if it has a explicit value.
        final int lastColIndex = totalColumns - 1;
        if (this.wasColumnWidthSpecified(lastColIndex)) {
            tableColumns[lastColIndex].setWidth(this.columnWidths[lastColIndex]);
        }

        // calculate the table size after making adjustments.
        final Point tableSize = this.table.computeSize(SWT.DEFAULT, itemHeight, false);

        // calculate the table width and table height.
        final double pct = this.tableWidthPercentage / 100d;
        int tableWidth = (int) (Math.max(tableComboSize.x - 2, tableSize.x) * pct);
        int tableHeight = tableSize.y;

        // add the width of a horizontal scrollbar to the table height if we are
        // not viewing the full table.
        if (this.tableWidthPercentage < 100) {
            tableHeight += this.table.getHorizontalBar().getSize().y;
        }

        // set the bounds on the table.
        this.table.setBounds(1, 1, tableWidth, tableHeight);

        // check to see if we can adjust the table width to by the amount the vertical
        // scrollbar would have taken since the table auto allocates the space whether
        // it is needed or not.
        if (!this.table.getVerticalBar().getVisible() && tableSize.x - this.table.getVerticalBar().getSize().x >= tableComboSize.x - 2) {

            tableWidth = tableWidth - this.table.getVerticalBar().getSize().x;

            // reset the bounds on the table.
            this.table.setBounds(1, 1, tableWidth, tableHeight);
        }

        // adjust the last column to make sure that there is no empty space.
        this.autoAdjustColumnWidthsIfNeeded(tableColumns, tableWidth, totalColumnWidth);

        // set the table top index if there is a valid selection.
        final int index = this.table.getSelectionIndex();
        if (index != -1) {
            this.table.setTopIndex(index);
        }

        // calculate popup dimensions.
        final Display display = this.getDisplay();
        final Rectangle tableRect = this.table.getBounds();
        final Rectangle parentRect = display.map(this.getParent(), null, this.getBounds());
        final Point comboSize = this.getSize();
        final Rectangle displayRect = this.getMonitor().getClientArea();

        int overallWidth = 0;

        // now set what the overall width should be.
        if (this.tableWidthPercentage < 100) {
            overallWidth = tableRect.width + 2;
        } else {
            overallWidth = Math.max(comboSize.x, tableRect.width + 2);
        }

        final int overallHeight = tableRect.height + 2;
        int x = parentRect.x;
        int y = parentRect.y + comboSize.y;
        if (y + overallHeight > displayRect.y + displayRect.height) {
            y = parentRect.y - overallHeight;
        }
        if (x + overallWidth > displayRect.x + displayRect.width) {
            x = displayRect.x + displayRect.width - tableRect.width;
        }

        // set the bounds of the popup
        this.popup.setBounds(x, y, overallWidth, overallHeight);

        this.lastRefreshIndex = -1;

        // set the popup visible
        this.popup.setVisible(true);

        // set focus on the table.
        this.table.setFocus();
    }

    /*
     * Return the Label immediately preceding the receiver in the z-order, or null if none.
     */
    private Label getAssociatedLabel() {
        final Control[] siblings = this.getParent().getChildren();
        for (int i = 0; i < siblings.length; i++) {
            if (siblings[i] == CheckTableCombo.this) {
                if (i > 0 && siblings[i - 1] instanceof Label) {
                    return (Label) siblings[i - 1];
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Control[] getChildren() {
        this.checkWidget();
        return new Control[0];
    }

    /**
     * Gets the editable state.
     *
     * @return whether or not the receiver is editable
     *
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     *
     * @since 3.0
     */
    public boolean getEditable() {
        this.checkWidget();
        return this.text.getEditable();
    }

    /**
     * Returns the item at the given, zero-relative index in the receiver's list. Throws an exception if the index is out of range.
     *
     * @param index the index of the item to return
     * @return the item at the given index
     *
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     */
    public String getItem(int index) {
        this.checkWidget();
        return this.table.getItem(index).getText(this.getDisplayColumnIndex());
    }

    /**
     * Returns the number of items contained in the receiver's list.
     *
     * @return the number of items
     *
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     */
    public int getItemCount() {
        this.checkWidget();
        return this.table.getItemCount();
    }

    /**
     * Returns the height of the area which would be used to display <em>one</em> of the items in the receiver's list.
     *
     * @return the height of one item
     *
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     */
    public int getItemHeight() {
        this.checkWidget();
        return this.table.getItemHeight();
    }

    /**
     * Returns an array of <code>String</code>s which are the items in the receiver's list.
     * <p>
     * Note: This is not the actual structure used by the receiver to maintain its list of items, so modifying the array will not affect the receiver.
     * </p>
     *
     * @return the items in the receiver's list
     *
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     */
    public String[] getItems() {
        this.checkWidget();

        // get a list of the table items.
        final TableItem[] tableItems = this.table.getItems();

        final int totalItems = (tableItems == null ? 0 : tableItems.length);

        // create string array to hold the total number of items.
        final String[] stringItems = new String[totalItems];

        final int colIndex = this.getDisplayColumnIndex();

        // now copy the display string from the tableitems.
        for (int index = 0; index < totalItems; index++) {
            stringItems[index] = tableItems[index].getText(colIndex);
        }

        return stringItems;
    }

    /**
     * Returns a <code>Point</code> whose x coordinate is the start of the selection in the receiver's text field, and whose y coordinate is the end
     * of the selection. The returned values are zero-relative. An "empty" selection as indicated by the the x
     * and y coordinates having the same value.
     *
     * @return a point representing the selection start and end
     *
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     */
    public Point getSelection() {
        this.checkWidget();
        return this.text.getSelection();
    }

    /**
     * Returns the zero-relative index of the item which is currently selected in the receiver's list, or -1 if no item is selected.
     *
     * @return the index of the selected item
     *
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     */
    public int[] getSelectionIndices() {
        this.checkWidget();
        return this.table.getSelectionIndices();
    }

    /**
     * Returns the selected table items.
     */
    public Collection<TableItem> getSelectedTableItems() {
        final Collection<TableItem> selectedTableItems = new ArrayList<>();
        final TableItem[] children = this.getTable().getItems();
        for (final TableItem item : children) {
            if (item.getChecked()) {
                selectedTableItems.add(item);
            }
        }

        return selectedTableItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStyle() {
        this.checkWidget();

        int style = super.getStyle();
        style &= ~SWT.READ_ONLY;
        if (!this.text.getEditable()) {
            style |= SWT.READ_ONLY;
        }
        return style;
    }

    /**
     * Returns a string containing a copy of the contents of the receiver's text field.
     *
     * @return the receiver's text
     *
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     */
    public String getText() {
        this.checkWidget();
        return this.text.getText();
    }

    /**
     * Returns the height of the receivers's text field.
     *
     * @return the text height
     *
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     */
    public int getTextHeight() {
        this.checkWidget();
        return this.text.getLineHeight();
    }

    /**
     * Gets the number of items that are visible in the drop down portion of the receiver's list.
     *
     * @return the number of items that are visible
     *
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     *
     * @since 3.0
     */
    public int getVisibleItemCount() {
        this.checkWidget();
        return this.visibleItemCount;
    }

    /**
     * Handle Focus event
     *
     * @param type
     */
    private void handleFocus(int type) {
        switch (type) {
            case SWT.FocusIn: {
                if (this.hasFocus) {
                    return;
                }
                if (this.getEditable()) {
                    this.text.selectAll();
                }
                this.hasFocus = true;
                final Shell shell = this.getShell();
                shell.removeListener(SWT.Deactivate, this.listener);
                shell.addListener(SWT.Deactivate, this.listener);
                final Display display = this.getDisplay();
                display.removeFilter(SWT.FocusIn, this.focusFilter);
                display.addFilter(SWT.FocusIn, this.focusFilter);
                final Event e = new Event();
                this.notifyListeners(SWT.FocusIn, e);
                break;
            }
            case SWT.FocusOut: {
                if (!this.hasFocus) {
                    return;
                }
                final Control focusControl = this.getDisplay().getFocusControl();
                if (focusControl == this.arrow || focusControl == this.table || focusControl == this.text) {
                    return;
                }
                this.hasFocus = false;
                final Shell shell = this.getShell();
                shell.removeListener(SWT.Deactivate, this.listener);
                final Display display = this.getDisplay();
                display.removeFilter(SWT.FocusIn, this.focusFilter);
                final Event e = new Event();
                this.notifyListeners(SWT.FocusOut, e);
                break;
            }
        }
    }

    /**
     * Searches the receiver's list starting at the first item (index 0) until an item is found that is equal to the argument, and returns the index
     * of that item. If no item is found, returns -1.
     *
     * @param string the search item
     * @return the index of the item
     *
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_NULL_ARGUMENT - if the string is null</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     */
    public int indexOf(String string) {
        this.checkWidget();
        if (string == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        // get a list of the table items.
        final TableItem[] tableItems = this.table.getItems();

        final int totalItems = (tableItems == null ? 0 : tableItems.length);
        final int colIndex = this.getDisplayColumnIndex();

        // now copy the display string from the tableitems.
        for (int index = 0; index < totalItems; index++) {
            if (string.equals(tableItems[index].getText(colIndex))) {
                return index;
            }
        }

        return -1;
    }

    /**
     * Searches the receiver's list starting at the given, zero-relative index until an item is found that is equal to the argument, and returns the
     * index of that item. If no item is found or the starting index is out of range, returns -1.
     *
     * @param string the search item
     * @param start the zero-relative index at which to begin the search
     * @return the index of the item
     *
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_NULL_ARGUMENT - if the string is null</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     */
    public int indexOf(String string, int start) {
        this.checkWidget();
        if (string == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        // get a list of the table items.
        final TableItem[] tableItems = this.table.getItems();

        final int totalItems = (tableItems == null ? 0 : tableItems.length);

        if (start < totalItems) {

            final int colIndex = this.getDisplayColumnIndex();

            // now copy the display string from the tableitems.
            for (int index = start; index < totalItems; index++) {
                if (string.equals(tableItems[index].getText(colIndex))) {
                    return index;
                }
            }
        }

        return -1;
    }

    /**
     * sets whether or not to show table lines
     *
     * @param showTableLines
     */
    public void setShowTableLines(boolean showTableLines) {
        this.checkWidget();
        this.table.setLinesVisible(showTableLines);
    }

    /**
     * sets whether or not to show table header.
     *
     * @param showTableHeader
     */
    public void setShowTableHeader(boolean showTableHeader) {
        this.checkWidget();
        this.table.setHeaderVisible(showTableHeader);
    }

    /**
     * Add Accessbile listeners to label and table.
     */
    void initAccessible() {
        final AccessibleAdapter accessibleAdapter = new AccessibleAdapter() {
            @Override
            public void getName(AccessibleEvent e) {
                String name = null;
                final Label label = CheckTableCombo.this.getAssociatedLabel();
                if (label != null) {
                    name = CheckTableCombo.this.stripMnemonic(CheckTableCombo.this.text.getText());
                }
                e.result = name;
            }

            @Override
            public void getKeyboardShortcut(AccessibleEvent e) {
                String shortcut = null;
                final Label label = CheckTableCombo.this.getAssociatedLabel();
                if (label != null) {
                    final String text = label.getText();
                    if (text != null) {
                        final char mnemonic = CheckTableCombo.this._findMnemonic(text);
                        if (mnemonic != '\0') {
                            shortcut = "Alt+" + mnemonic; //$NON-NLS-1$
                        }
                    }
                }
                e.result = shortcut;
            }

            @Override
            public void getHelp(AccessibleEvent e) {
                e.result = CheckTableCombo.this.getToolTipText();
            }
        };

        this.getAccessible().addAccessibleListener(accessibleAdapter);
        this.text.getAccessible().addAccessibleListener(accessibleAdapter);
        this.table.getAccessible().addAccessibleListener(accessibleAdapter);

        this.arrow.getAccessible().addAccessibleListener(new AccessibleAdapter() {
            @Override
            public void getName(AccessibleEvent e) {
                e.result = CheckTableCombo.this.isDropped() ? SWT.getMessage("SWT_Close") : SWT.getMessage("SWT_Open"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            @Override
            public void getKeyboardShortcut(AccessibleEvent e) {
                e.result = "Alt+Down Arrow"; //$NON-NLS-1$
            }

            @Override
            public void getHelp(AccessibleEvent e) {
                e.result = CheckTableCombo.this.getToolTipText();
            }
        });

        this.getAccessible().addAccessibleTextListener(new AccessibleTextAdapter() {
            @Override
            public void getCaretOffset(AccessibleTextEvent e) {
                e.offset = CheckTableCombo.this.text.getCaretPosition();
            }

            @Override
            public void getSelectionRange(AccessibleTextEvent e) {
                final Point sel = CheckTableCombo.this.text.getSelection();
                e.offset = sel.x;
                e.length = sel.y - sel.x;
            }
        });

        this.getAccessible().addAccessibleControlListener(new AccessibleControlAdapter() {
            @Override
            public void getChildAtPoint(AccessibleControlEvent e) {
                final Point testPoint = CheckTableCombo.this.toControl(e.x, e.y);
                if (CheckTableCombo.this.getBounds().contains(testPoint)) {
                    e.childID = ACC.CHILDID_SELF;
                }
            }

            @Override
            public void getLocation(AccessibleControlEvent e) {
                final Rectangle location = CheckTableCombo.this.getBounds();
                final Point pt = CheckTableCombo.this.getParent().toDisplay(location.x, location.y);
                e.x = pt.x;
                e.y = pt.y;
                e.width = location.width;
                e.height = location.height;
            }

            @Override
            public void getChildCount(AccessibleControlEvent e) {
                e.detail = 0;
            }

            @Override
            public void getRole(AccessibleControlEvent e) {
                e.detail = ACC.ROLE_COMBOBOX;
            }

            @Override
            public void getState(AccessibleControlEvent e) {
                e.detail = ACC.STATE_NORMAL;
            }

            @Override
            public void getValue(AccessibleControlEvent e) {
                e.result = CheckTableCombo.this.text.getText();
            }
        });

        this.text.getAccessible().addAccessibleControlListener(new AccessibleControlAdapter() {
            @Override
            public void getRole(AccessibleControlEvent e) {
                e.detail = CheckTableCombo.this.text.getEditable() ? ACC.ROLE_TEXT : ACC.ROLE_LABEL;
            }
        });

        this.arrow.getAccessible().addAccessibleControlListener(new AccessibleControlAdapter() {
            @Override
            public void getDefaultAction(AccessibleControlEvent e) {
                e.result = CheckTableCombo.this.isDropped() ? SWT.getMessage("SWT_Close") : SWT.getMessage("SWT_Open"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        });
    }

    /**
     * returns if the drop down is currently open
     *
     * @return
     */
    private boolean isDropped() {
        return this.popup.getVisible();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFocusControl() {
        this.checkWidget();
        // if (label.isFocusControl () || arrow.isFocusControl () || table.isFocusControl () || popup.isFocusControl ()) {
        if (this.arrow.isFocusControl() || this.table.isFocusControl() || this.popup.isFocusControl()) {
            return true;
        }
        return super.isFocusControl();
    }

    /**
     * This method is invoked when a resize event occurs.
     *
     * @param changed
     * @param closeDropDown
     */
    private void internalLayout(boolean changed, boolean closeDropDown) {
        if (closeDropDown && this.isDropped()) {
            this.dropDown(false);
        }
        final Rectangle rect = this.getClientArea();
        final int width = rect.width;
        final int height = rect.height;
        final Point arrowSize = this.arrow.computeSize(SWT.DEFAULT, height, changed);

        // calculate text vertical alignment.
        int textYPos = 0;
        final Point textSize = this.text.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        if (textSize.y < height) {
            textYPos = (height - textSize.y) / 2;
        }

        // does the selected entry have a image associated with it?
        if (this.selectedImage.getImage() == null) {
            // set image, text, and arrow boundaries
            this.selectedImage.setBounds(0, 0, 0, 0);
            this.text.setBounds(0, textYPos, width - arrowSize.x, textSize.y);
            this.arrow.setBounds(width - arrowSize.x, 0, arrowSize.x, arrowSize.y);
        } else {
            // calculate the amount of width left in the control after taking into account the arrow selector
            int remainingWidth = width - arrowSize.x;
            final Point imageSize = this.selectedImage.computeSize(SWT.DEFAULT, height, changed);
            int imageWidth = imageSize.x + 2;

            // handle the case where the image is larger than the available space in the control.
            if (imageWidth > remainingWidth) {
                imageWidth = remainingWidth;
                remainingWidth = 0;
            } else {
                remainingWidth = remainingWidth - imageWidth;
            }

            // set the width of the text.
            final int textWidth = remainingWidth;

            // set image, text, and arrow boundaries
            this.selectedImage.setBounds(0, 0, imageWidth, imageSize.y);
            this.text.setBounds(imageWidth, textYPos, textWidth, textSize.y);
            this.arrow.setBounds(imageWidth + textWidth, 0, arrowSize.x, arrowSize.y);
        }
    }

    /**
     * Handles Table Events.
     *
     * @param event
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    private void tableEvent(Event event) {
        switch (event.type) {
            case SWT.Dispose:
                if (this.getShell() != this.popup.getParent()) {
                    final int selectionIndex = this.table.getSelectionIndex();
                    this.popup = null;
                    this.table = null;
                    this.createPopup(selectionIndex);
                }
                break;
            case SWT.FocusIn: {
                this.handleFocus(SWT.FocusIn);
                break;
            }
            case SWT.MouseUp: {
                if (event.button != 1) {
                    return;
                }
                if (this.closePopupAfterSelection) {
                    this.dropDown(false);
                }
                break;
            }
            case SWT.Selection: {

                if (event.detail == SWT.CHECK) {
                    // TableItem checkTableItem = (TableItem) event.item;
                    this.refreshText();

                    final int[] checkedIndices = this.getCheckIndices();
                    this.table.select(checkedIndices);

                    final Event e = new Event();
                    e.time = event.time;
                    e.item = event.item;
                    e.data = event.data;
                    e.stateMask = event.stateMask;
                    e.doit = event.doit;
                    this.notifyListeners(SWT.Selection, e);
                    event.doit = e.doit;

                    this.table.deselectAll();
                    return;
                }

                final int index = this.table.getSelectionIndex();
                if (index == -1) {
                    return;
                }

                // refresh the text.
                this.refreshText(index, event);

                // set the selection in the table.
                this.table.select(index);

                // Event e = new Event();
                // e.time = event.time;
                // e.stateMask = event.stateMask;
                // e.doit = event.doit;
                // notifyListeners(SWT.Selection, e);
                // event.doit = e.doit;
                break;
            }
            case SWT.Traverse: {
                switch (event.detail) {
                    case SWT.TRAVERSE_RETURN:
                    case SWT.TRAVERSE_ESCAPE:
                    case SWT.TRAVERSE_ARROW_PREVIOUS:
                    case SWT.TRAVERSE_ARROW_NEXT:
                        event.doit = false;
                        break;
                    case SWT.TRAVERSE_TAB_NEXT:
                    case SWT.TRAVERSE_TAB_PREVIOUS:
                        event.doit = this.text.traverse(event.detail);
                        event.detail = SWT.TRAVERSE_NONE;
                        if (event.doit) {
                            this.dropDown(false);
                        }
                        return;
                }
                final Event e = new Event();
                e.time = event.time;
                e.detail = event.detail;
                e.doit = event.doit;
                e.character = event.character;
                e.keyCode = event.keyCode;
                this.notifyListeners(SWT.Traverse, e);
                event.doit = e.doit;
                event.detail = e.detail;
                break;
            }
            case SWT.KeyUp: {
                final Event e = new Event();
                e.time = event.time;
                e.character = event.character;
                e.keyCode = event.keyCode;
                e.stateMask = event.stateMask;
                this.notifyListeners(SWT.KeyUp, e);
                break;
            }
            case SWT.KeyDown: {
                if (event.character == SWT.ESC) {
                    // Escape key cancels popup list
                    this.dropDown(false);
                }
                if ((event.stateMask & SWT.ALT) != 0 && (event.keyCode == SWT.ARROW_UP || event.keyCode == SWT.ARROW_DOWN)) {
                    this.dropDown(false);
                }
                if (event.character == SWT.CR) {
                    // Enter causes default selection
                    this.dropDown(false);
                    final Event e = new Event();
                    e.time = event.time;
                    e.stateMask = event.stateMask;
                    this.notifyListeners(SWT.DefaultSelection, e);
                }
                // At this point the widget may have been disposed.
                // If so, do not continue.
                if (this.isDisposed()) {
                    break;
                }
                final Event e = new Event();
                e.time = event.time;
                e.character = event.character;
                e.keyCode = event.keyCode;
                e.stateMask = event.stateMask;
                this.notifyListeners(SWT.KeyDown, e);
                break;

            }
        }
    }

    /**
     * @return
     */
    private int[] getCheckIndices() {
        final TableItem[] items = this.getTable().getItems();
        final List<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            if (items[i].getChecked()) {
                indexList.add(i);
            }
        }
        final int[] checkedIndices = indexList.stream().mapToInt(index -> index).toArray();
        return checkedIndices;
    }

    /**
     * Pastes text from clipboard.
     * <p>
     * The selected text is deleted from the widget and new text inserted from the clipboard.
     * </p>
     *
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     *
     * @since 3.3
     */
    public void paste() {
        this.checkWidget();
        this.text.paste();
    }

    /**
     * Handles Popup Events
     *
     * @param event
     */
    private void popupEvent(Event event) {
        switch (event.type) {
            case SWT.Paint:
                // draw rectangle around table
                final Rectangle tableRect = this.table.getBounds();
                event.gc.setForeground(this.getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION));
                event.gc.drawRectangle(0, 0, tableRect.width + 1, tableRect.height + 1);
                break;
            case SWT.Close:
                event.doit = false;
                this.dropDown(false);
                break;
            case SWT.Deactivate:
                /*
                 * Bug in GTK. When the arrow button is pressed the popup control receives a deactivate event and then the arrow button receives a
                 * selection event. If we hide the popup in the deactivate event, the selection event will show it again. To
                 * prevent the popup from showing again, we will let the selection event of the arrow button hide the popup. In Windows, hiding the
                 * popup during the deactivate causes the deactivate to be called twice and the selection event to be disappear.
                 */
                if (!"carbon".equals(SWT.getPlatform())) {
                    final Point point = this.arrow.toControl(this.getDisplay().getCursorLocation());
                    final Point size = this.arrow.getSize();
                    final Rectangle rect = new Rectangle(0, 0, size.x, size.y);
                    if (!rect.contains(point)) {
                        this.dropDown(false);
                    }
                } else {
                    this.dropDown(false);
                }
                break;

            case SWT.Help:
                if (this.isDropped()) {
                    this.dropDown(false);
                }
                Composite comp = CheckTableCombo.this;
                do {
                    if (comp.getListeners(event.type) != null && comp.getListeners(event.type).length > 0) {
                        comp.notifyListeners(event.type, event);
                        break;
                    }
                    comp = comp.getParent();
                } while (null != comp);
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void redraw() {
        super.redraw();
        this.text.redraw();
        this.arrow.redraw();
        if (this.popup.isVisible()) {
            this.table.redraw();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void redraw(int x, int y, int width, int height, boolean all) {
        super.redraw(x, y, width, height, true);
    }

    /**
     * Removes the listener from the collection of listeners who will be notified when the receiver's text is modified.
     *
     * @param listener the listener which should no longer be notified
     *
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     *
     * @see ModifyListener
     * @see #addModifyListener
     */
    public void removeModifyListener(ModifyListener listener) {
        this.checkWidget();
        if (listener == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        this.removeListener(SWT.Modify, listener);
    }

    /**
     * Removes the listener from the collection of listeners who will be notified when the user changes the receiver's selection.
     *
     * @param listener the listener which should no longer be notified
     *
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     *
     * @see SelectionListener
     * @see #addSelectionListener
     */
    public void removeSelectionListener(SelectionListener listener) {
        this.checkWidget();
        if (listener == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        this.removeListener(SWT.Selection, listener);
        this.removeListener(SWT.DefaultSelection, listener);
    }

    /**
     * Removes the listener from the collection of listeners who will be notified when the control is verified.
     *
     * @param listener the listener which should no longer be notified
     *
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     *
     * @see VerifyListener
     * @see #addVerifyListener
     *
     * @since 3.3
     */
    public void removeVerifyListener(VerifyListener listener) {
        this.checkWidget();
        if (listener == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        this.removeListener(SWT.Verify, listener);
    }

    /**
     * Selects the items at the given zero-relative index in the receiver's list. If the item at the index was already selected, it remains selected.
     * Indices that are out of range are ignored.
     *
     * @param indices the indices of the items to select
     *
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     */
    public void select(int[] indices) {
        this.checkWidget();

        final int[] sortedIndices = indices.clone();
        Arrays.sort(sortedIndices);

        final TableItem[] children = this.getTable().getItems();
        for (int i = 0; i < children.length; i++) {
            final boolean foundIndex = Arrays.binarySearch(sortedIndices, i) >= 0;
            if (foundIndex) {
                if (!children[i].getChecked()) {
                    children[i].setChecked(true);
                }
            } else {
                if (children[i].getChecked()) {
                    children[i].setChecked(false);
                }
            }
        }

        // refresh the text field and image label
        this.refreshText();

        // select the row in the table.
        // table.setSelection(indices);

        final Event e = new Event();
        this.notifyListeners(SWT.Selection, e);
    }

    /**
     * Selects the items in the receiver's list. If the item at the index was already selected, it remains selected. Indices that are out of range are
     * ignored.
     *
     * @param items the items to select
     *
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     */
    public void select(TableItem[] items) {
        this.checkWidget();

        final List<TableItem> itemsList = Arrays.asList(items);

        final TableItem[] children = this.getTable().getItems();
        for (int i = 0; i < children.length; i++) {
            final boolean foundItem = itemsList.contains(children[i]);
            if (foundItem) {
                if (!children[i].getChecked()) {
                    children[i].setChecked(true);
                }
            } else {
                if (children[i].getChecked()) {
                    children[i].setChecked(false);
                }
            }
        }

        // refresh the text field and image label
        this.refreshText();

        // select the row in the table.
        // table.setSelection(indices);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        this.background = color;
        if (this.text != null) {
            this.text.setBackground(color);
        }
        if (this.selectedImage != null) {
            this.selectedImage.setBackground(color);
        }
        if (this.table != null) {
            this.table.setBackground(color);
        }
        if (this.arrow != null) {
            this.arrow.setBackground(color);
        }
    }

    /**
     * Sets the editable state.
     *
     * @param editable the new editable state
     *
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     *
     * @since 3.0
     */
    public void setEditable(boolean editable) {
        this.checkWidget();
        this.text.setEditable(editable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (this.popup != null && !enabled) {
            this.popup.setVisible(false);
        }
        if (this.selectedImage != null) {
            this.selectedImage.setEnabled(enabled);
        }
        if (this.text != null) {
            this.text.setEnabled(enabled);
        }
        if (this.arrow != null) {
            this.arrow.setEnabled(enabled);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setFocus() {
        this.checkWidget();
        if (!this.isEnabled() || !this.isVisible()) {
            return false;
        }
        if (this.isFocusControl()) {
            return true;
        }

        return this.text.setFocus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        this.font = font;
        this.text.setFont(font);
        this.table.setFont(font);
        this.internalLayout(true, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setForeground(Color color) {
        super.setForeground(color);
        this.foreground = color;
        if (this.text != null) {
            this.text.setForeground(color);
        }
        if (this.table != null) {
            this.table.setForeground(color);
        }
        if (this.arrow != null) {
            this.arrow.setForeground(color);
        }
    }

    /**
     * Sets the layout which is associated with the receiver to be the argument which may be null.
     * <p>
     * Note : No Layout can be set on this Control because it already manages the size and position of its children.
     * </p>
     *
     * @param layout the receiver's new layout or null
     *
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     */
    @Override
    public void setLayout(Layout layout) {
        this.checkWidget();
        return;
    }

    /**
     * Marks the receiver's list as visible if the argument is <code>true</code>, and marks it invisible otherwise.
     * <p>
     * If one of the receiver's ancestors is not visible or some other condition makes the receiver not visible, marking it visible may not actually
     * cause it to be displayed.
     * </p>
     *
     * @param visible the new visibility state
     *
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     *
     * @since 3.4
     */
    public void setTableVisible(boolean visible) {
        this.checkWidget();
        this.dropDown(visible);
    }

    /**
     * Sets the selection in the receiver's text field to the range specified by the argument whose x coordinate is the start of the selection and
     * whose y coordinate is the end of the selection.
     *
     * @param selection a point representing the new selection start and end
     *
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_NULL_ARGUMENT - if the point is null</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     */
    public void setSelection(Point selection) {
        this.checkWidget();
        if (selection == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        this.text.setSelection(selection.x, selection.y);
    }

    /**
     * Sets the contents of the receiver's text field to the given string.
     * <p>
     * Note: The text field in a <code>Combo</code> is typically only capable of displaying a single line of text. Thus, setting the text to a string
     * containing line breaks or other special characters will probably cause it to display incorrectly.
     * </p>
     *
     * @param string the new text
     *
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_NULL_ARGUMENT - if the string is null</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     */
    public void setText(String string) {
        this.checkWidget();
        if (string == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }

        // find the index of the given string.
        final String[] texts = string.split(this.separator);
        final int[] indices = new int[texts.length];

        // get a list of the table items.
        final TableItem[] tableItems = this.table.getItems();

        final int totalItems = (tableItems == null ? 0 : tableItems.length);
        final int colIndex = this.getDisplayColumnIndex();

        for (int i = 0; i < texts.length; i++) {
            indices[i] = -1;
            // now copy the display string from the tableitems.
            for (int index = 0; index < totalItems; index++) {
                if (texts[i].equals(tableItems[index].getText(colIndex))) {
                    indices[i] = index;
                    break;
                }
            }
        }

        // select the text and table row.
        this.select(indices);
        // clearSelection();
    }

    public void setDisplayText(String displayText) {
        this.text.setText(displayText);
    }

    /**
     * Sets the maximum number of characters that the receiver's text field is capable of holding to be the argument.
     *
     * @param limit new text limit
     *
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_CANNOT_BE_ZERO - if the limit is zero</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     */
    public void setTextLimit(int limit) {
        this.checkWidget();
        this.text.setTextLimit(limit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setToolTipText(String tipText) {
        this.checkWidget();
        super.setToolTipText(tipText);
        if (this.selectedImage != null) {
            this.selectedImage.setToolTipText(tipText);
        }
        if (this.text != null) {
            this.text.setToolTipText(tipText);
        }
        if (this.arrow != null) {
            this.arrow.setToolTipText(tipText);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        /*
         * At this point the widget may have been disposed in a FocusOut event. If so then do not continue.
         */
        if (this.isDisposed()) {
            return;
        }
        // TEMPORARY CODE
        if (this.popup == null || this.popup.isDisposed()) {
            return;
        }
        if (!visible) {
            this.popup.setVisible(false);
        }
    }

    /**
     * Sets the number of items that are visible in the drop down portion of the receiver's list.
     *
     * @param count the new number of items to be visible
     *
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     *                </ul>
     *
     * @since 3.0
     */
    public void setVisibleItemCount(int count) {
        this.checkWidget();
        if (count > 0) {
            this.visibleItemCount = count;
        }
    }

    private String stripMnemonic(String string) {
        int index = 0;
        final int length = string.length();
        do {
            while ((index < length) && (string.charAt(index) != '&')) {
                index++;
            }
            if (++index >= length) {
                return string;
            }
            if (string.charAt(index) != '&') {
                return string.substring(0, index - 1) + string.substring(index, length);
            }
            index++;
        } while (index < length);
        return string;
    }

    /**
     * Defines what columns the drop down table will have.
     *
     * Use this method when you don't care about the width of the columns but want to set the column header text.
     */
    public void defineColumns(String[] columnHeaders) {
        if (columnHeaders != null && columnHeaders.length > 0) {
            this.defineColumnsInternal(columnHeaders, null, columnHeaders.length);
        }
    }

    /**
     * Defines what columns the drop down table will have.
     *
     * Use this method when you don't care about the column header text but you want the fields to be a specific width.
     */
    public void defineColumns(int[] columnBounds) {
        this.columnWidths = columnBounds;

        if (columnBounds != null && columnBounds.length > 0) {
            this.defineColumnsInternal(null, columnBounds, columnBounds.length);
        }
    }

    /**
     * Defines what columns the drop down table will have.
     *
     * Use this method when you don't care about the column headers and you want the columns to be automatically sized based upon their content.
     *
     */
    public void defineColumns(int numberOfColumnsToCreate) {
        if (numberOfColumnsToCreate > 0) {
            this.defineColumnsInternal(null, null, numberOfColumnsToCreate);
        }

    }

    /**
     * Defines what columns the drop down table will have.
     * Use this method when you want to specify the column header text and the column widths.
     */
    public void defineColumns(String[] columnHeaders, int[] columnBounds) {
        if (columnHeaders != null || columnBounds != null) {
            int total = columnHeaders == null ? 0 : columnHeaders.length;
            if (columnBounds != null && columnBounds.length > total) {
                total = columnBounds.length;
            }

            this.columnWidths = columnBounds;

            // define the columns
            this.defineColumnsInternal(columnHeaders, columnBounds, total);
        }
    }

    /**
     * Defines what columns the drop down table will have.
     */
    private void defineColumnsInternal(String[] columnHeaders, int[] columnBounds, int totalColumnsToBeCreated) {

        this.checkWidget();

        final int totalColumnHeaders = columnHeaders == null ? 0 : columnHeaders.length;
        final int totalColBounds = columnBounds == null ? 0 : columnBounds.length;

        if (totalColumnsToBeCreated > 0) {

            for (int index = 0; index < totalColumnsToBeCreated; index++) {
                final TableColumn column = new TableColumn(this.table, SWT.NONE);

                if (index < totalColumnHeaders) {
                    column.setText(columnHeaders[index]);
                }

                if (index < totalColBounds) {
                    column.setWidth(columnBounds[index]);
                }

                column.setResizable(true);
                column.setMoveable(true);
            }
        }
    }

    /**
     * Sets the table width percentage in relation to the width of the label control.
     *
     * The default value if 100% which means that it will be the same size as the label control. If you want the table to be wider than the label then
     * just display a value higher than 100%.
     *
     * @param ddWidthPct
     */
    public void setTableWidthPercentage(int ddWidthPct) {
        this.checkWidget();

        // don't accept invalid input.
        if (ddWidthPct > 0 && ddWidthPct <= 100) {
            this.tableWidthPercentage = ddWidthPct;
        }
    }

    /**
     * Sets the zero-relative column index that will be used to display the currently selected item in the label control.
     *
     * @param displayColumnIndex
     */
    public void setDisplayColumnIndex(int displayColumnIndex) {
        this.checkWidget();

        if (displayColumnIndex >= 0) {
            this.displayColumnIndex = displayColumnIndex;
        }
    }

    /**
     * Modifies the behavior of the popup after an entry was selected. If {@code true} the popup will be closed, if {@code false} it will remain open.
     *
     * @param closePopupAfterSelection
     */
    public void setClosePopupAfterSelection(boolean closePopupAfterSelection) {
        this.closePopupAfterSelection = closePopupAfterSelection;
    }

    /**
     * returns the column index of the TableColumn to be displayed when selected.
     *
     * @return
     */
    private int getDisplayColumnIndex() {
        // make sure the requested column index is valid.
        return (this.displayColumnIndex <= (this.table.getColumnCount() - 1) ? this.displayColumnIndex : 0);
    }

    /*
     * Return the lowercase of the first non-'&' character following an '&' character in the given string. If there are no '&' characters in the given
     * string, return '\0'.
     */
    private char _findMnemonic(String string) {
        if (string == null) {
            return '\0';
        }
        int index = 0;
        final int length = string.length();
        do {
            while (index < length && string.charAt(index) != '&') {
                index++;
            }
            if (++index >= length) {
                return '\0';
            }
            if (string.charAt(index) != '&') {
                return Character.toLowerCase(string.charAt(index));
            }
            index++;
        } while (index < length);
        return '\0';
    }

    int lastRefreshIndex = -1;

    /**
     * Refreshes the label control with the selected object's details.
     */
    private void refreshText(int index, Event event) {
        final TableItem[] children = this.getTable().getItems();
        final TableItem item = children[index];
        if (this.lastRefreshIndex == index) {
            
            final Event e = new Event();
            e.time = event.time;
            e.item = item;
            e.data = event.data;
            e.stateMask = event.stateMask;
            e.doit = event.doit;
            e.display = event.display;
            e.button = event.button;
            e.character = event.character;
            e.count = event.count;
            e.end = event.end;
            e.gc = event.gc;
            e.detail= SWT.CHECK;
            e.height = event.height;            
            e.index = event.index;
            e.keyCode = event.keyCode;
            e.keyLocation = event.keyLocation;
            e.magnification = event.magnification;
            e.rotation = event.rotation;
            e.segments = event.segments;            
            e.segmentsChars = event.segmentsChars;
            e.start = event.start;
            e.stateMask = event.stateMask;
            e.text = event.text;
            e.touches = event.touches;
            e.type = event.type;
            e.widget = event.widget;
            e.width = event.width;
            e.x = event.x;
            e.xDirection = event.xDirection;
            e.y = event.y;
            e.yDirection = event.yDirection;            
            
            item.setChecked(!item.getChecked());
            
            this.getTable().notifyListeners(SWT.Selection, e);
        } else {
            this.lastRefreshIndex = index;
        }

        // // get a reference to the selected TableItem
        // TableItem tableItem = table.getItem(index);
        //
        // // get the TableItem index to use for displaying the text.
        // int colIndexToUse = getDisplayColumnIndex();
        //
        // // set image if requested
        // if (showImageWithinSelection) {
        // // set the selected image
        // selectedImage.setImage(tableItem.getImage(colIndexToUse));
        //
        // // refresh the layout of the widget
        // internalLayout(false, closePupupAfterSelection);
        // }
        //
        // // set color if requested
        // if (showColorWithinSelection) {
        // text.setForeground(tableItem.getForeground(colIndexToUse));
        // }
        //
        // // set font if requested
        // if (showFontWithinSelection) {
        // // set the selected font
        // text.setFont(tableItem.getFont(colIndexToUse));
        // }
        //
        // // set the label text.
        // text.setText(tableItem.getText(colIndexToUse));
        // System.out.println("TODO2");
        //
        // text.selectAll();
    }

    /**
     * Set the separator
     *
     * @param separator
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    /**
     * Return the separator
     */
    public String getSeparator() {
        return this.separator;
    }

    public void setConvertToString(Function<Collection<TableItem>, String> convertToString) {
        this.convertToString = convertToString;
    }

    /**
     * Refreshes the label control with the selected object's details.
     */
    public void refreshText() {

        // get the TableItem index to use for displaying the text.
        final int colIndexToUse = this.getDisplayColumnIndex();

        final Collection<TableItem> selectedTableItems = this.getSelectedTableItems();
        final TableItem tableItem = selectedTableItems.isEmpty() ? null : selectedTableItems.iterator().next();

        // set image if requested
        if (this.showImageWithinSelection) {
            // set the selected image
            if (tableItem != null) {
                this.selectedImage.setImage(tableItem.getImage(colIndexToUse));
            }

            // refresh the layout of the widget
            this.internalLayout(false, this.closePopupAfterSelection);
        }

        // set color if requested
        if (this.showColorWithinSelection) {
            if (tableItem != null) {
                this.text.setForeground(tableItem.getForeground(colIndexToUse));
            }
        }

        // set font if requested
        if (this.showFontWithinSelection) {
            // set the selected font
            if (tableItem != null) {
                this.text.setFont(tableItem.getFont(colIndexToUse));
            }
        }

        // set the label text.
        final String displayText = this.convertToString.apply(selectedTableItems);
        this.text.setText(displayText);

        this.getParent().layout(true);
    }

    /**
     * @param showImageWithinSelection
     */
    public void setShowImageWithinSelection(boolean showImageWithinSelection) {
        this.checkWidget();
        this.showImageWithinSelection = showImageWithinSelection;
    }

    /**
     * @param showColorWithinSelection
     */
    public void setShowColorWithinSelection(boolean showColorWithinSelection) {
        this.checkWidget();
        this.showColorWithinSelection = showColorWithinSelection;
    }

    /**
     * @param showFontWithinSelection
     */
    public void setShowFontWithinSelection(boolean showFontWithinSelection) {
        this.checkWidget();
        this.showFontWithinSelection = showFontWithinSelection;
    }

    /**
     * returns the Table reference.
     *
     * NOTE: the access is public for now but will most likely be changed in a future release.
     *
     * @return
     */
    public Table getTable() {
        this.checkWidget();
        return this.table;
    }

    /**
     * determines if the user explicitly set a column width for a given column index.
     *
     * @param columnIndex
     * @return
     */
    private boolean wasColumnWidthSpecified(int columnIndex) {
        return (this.columnWidths != null && this.columnWidths.length > columnIndex && this.columnWidths[columnIndex] != SWT.DEFAULT);
    }

    void textEvent(Event event) {
        switch (event.type) {
            case SWT.FocusIn: {
                this.handleFocus(SWT.FocusIn);
                break;
            }
            case SWT.DefaultSelection: {
                this.dropDown(false);
                final Event e = new Event();
                e.time = event.time;
                e.stateMask = event.stateMask;
                this.notifyListeners(SWT.DefaultSelection, e);
                break;
            }
            case SWT.KeyDown: {
                final Event keyEvent = new Event();
                keyEvent.time = event.time;
                keyEvent.character = event.character;
                keyEvent.keyCode = event.keyCode;
                keyEvent.stateMask = event.stateMask;
                this.notifyListeners(SWT.KeyDown, keyEvent);
                if (this.isDisposed()) {
                    break;
                }
                event.doit = keyEvent.doit;
                if (!event.doit) {
                    break;
                }
                if (event.keyCode == SWT.ARROW_UP || event.keyCode == SWT.ARROW_DOWN) {
                    event.doit = false;
                    if ((event.stateMask & SWT.ALT) != 0) {
                        final boolean dropped = this.isDropped();
                        this.text.selectAll();
                        if (!dropped) {
                            this.setFocus();
                        }
                        this.dropDown(!dropped);
                        break;
                    }

                    final int oldIndex = this.table.getSelectionIndex();
                    if (event.keyCode == SWT.ARROW_UP) {
                        this.select(new int[] {Math.max(oldIndex - 1, 0)});
                    } else {
                        this.select(new int[] {Math.min(oldIndex + 1, this.getItemCount() - 1)});
                    }
                    if (oldIndex != this.table.getSelectionIndex()) {
                        final Event e = new Event();
                        e.time = event.time;
                        e.stateMask = event.stateMask;
                        this.notifyListeners(SWT.Selection, e);
                    }
                    if (this.isDisposed()) {
                        break;
                    }
                }

                // Further work : Need to add support for incremental search in
                // pop up list as characters typed in text widget
                break;
            }
            case SWT.KeyUp: {
                final Event e = new Event();
                e.time = event.time;
                e.character = event.character;
                e.keyCode = event.keyCode;
                e.stateMask = event.stateMask;
                this.notifyListeners(SWT.KeyUp, e);
                event.doit = e.doit;
                break;
            }
            case SWT.MenuDetect: {
                final Event e = new Event();
                e.time = event.time;
                this.notifyListeners(SWT.MenuDetect, e);
                break;
            }
            case SWT.Modify: {
                // table.deselectAll();
                final Event e = new Event();
                e.time = event.time;
                this.notifyListeners(SWT.Modify, e);
                break;
            }
            case SWT.MouseDown: {
                final Event mouseEvent = new Event();
                mouseEvent.button = event.button;
                mouseEvent.count = event.count;
                mouseEvent.stateMask = event.stateMask;
                mouseEvent.time = event.time;
                mouseEvent.x = event.x;
                mouseEvent.y = event.y;
                this.notifyListeners(SWT.MouseDown, mouseEvent);
                if (this.isDisposed()) {
                    break;
                }
                event.doit = mouseEvent.doit;
                if (!event.doit) {
                    break;
                }
                if (event.button != 1) {
                    return;
                }
                if (this.text.getEditable()) {
                    return;
                }
                final boolean dropped = this.isDropped();
                this.text.selectAll();
                if (!dropped) {
                    this.setFocus();
                }
                this.dropDown(!dropped);
                break;
            }
            case SWT.MouseUp: {
                final Event mouseEvent = new Event();
                mouseEvent.button = event.button;
                mouseEvent.count = event.count;
                mouseEvent.stateMask = event.stateMask;
                mouseEvent.time = event.time;
                mouseEvent.x = event.x;
                mouseEvent.y = event.y;
                this.notifyListeners(SWT.MouseUp, mouseEvent);
                if (this.isDisposed()) {
                    break;
                }
                event.doit = mouseEvent.doit;
                if (!event.doit) {
                    break;
                }
                if (event.button != 1) {
                    return;
                }
                if (this.text.getEditable()) {
                    return;
                }
                this.text.selectAll();
                break;
            }
            case SWT.MouseDoubleClick: {
                final Event mouseEvent = new Event();
                mouseEvent.button = event.button;
                mouseEvent.count = event.count;
                mouseEvent.stateMask = event.stateMask;
                mouseEvent.time = event.time;
                mouseEvent.x = event.x;
                mouseEvent.y = event.y;
                this.notifyListeners(SWT.MouseDoubleClick, mouseEvent);
                break;
            }
            case SWT.MouseWheel: {
                final Event keyEvent = new Event();
                keyEvent.time = event.time;
                keyEvent.keyCode = event.count > 0 ? SWT.ARROW_UP : SWT.ARROW_DOWN;
                keyEvent.stateMask = event.stateMask;
                this.notifyListeners(SWT.KeyDown, keyEvent);
                if (this.isDisposed()) {
                    break;
                }
                event.doit = keyEvent.doit;
                if (!event.doit) {
                    break;
                }
                if (event.count != 0) {
                    event.doit = false;
                    final int oldIndex = this.table.getSelectionIndex();
                    if (event.count > 0) {
                        this.select(new int[] {Math.max(oldIndex - 1, 0)});
                    } else {
                        this.select(new int[] {Math.min(oldIndex + 1, this.getItemCount() - 1)});
                    }
                    if (oldIndex != this.table.getSelectionIndex()) {
                        final Event e = new Event();
                        e.time = event.time;
                        e.stateMask = event.stateMask;
                        this.notifyListeners(SWT.Selection, e);
                    }
                    if (this.isDisposed()) {
                        break;
                    }
                }
                break;
            }
            case SWT.Traverse: {
                switch (event.detail) {
                    case SWT.TRAVERSE_ARROW_PREVIOUS:
                    case SWT.TRAVERSE_ARROW_NEXT:
                        // The enter causes default selection and
                        // the arrow keys are used to manipulate the list contents so
                        // do not use them for traversal.
                        event.doit = false;
                        break;
                    case SWT.TRAVERSE_TAB_PREVIOUS:
                        event.doit = this.traverse(SWT.TRAVERSE_TAB_PREVIOUS);
                        event.detail = SWT.TRAVERSE_NONE;
                        return;
                }
                final Event e = new Event();
                e.time = event.time;
                e.detail = event.detail;
                e.doit = event.doit;
                e.character = event.character;
                e.keyCode = event.keyCode;
                this.notifyListeners(SWT.Traverse, e);
                event.doit = e.doit;
                event.detail = e.detail;
                break;
            }
            case SWT.Verify: {
                final Event e = new Event();
                e.text = event.text;
                e.start = event.start;
                e.end = event.end;
                e.character = event.character;
                e.keyCode = event.keyCode;
                e.stateMask = event.stateMask;
                this.notifyListeners(SWT.Verify, e);
                event.doit = e.doit;
                break;
            }
        }
    }

    /**
     * adjusts the last table column width to fit inside of the table if the table column data does not fill out the table area.
     */
    private void autoAdjustColumnWidthsIfNeeded(TableColumn[] tableColumns, int totalAvailWidth, int totalColumnWidthUsage) {

        int scrollBarSize = 0;
        final int totalColumns = (tableColumns == null ? 0 : tableColumns.length);

        // determine if the vertical scroll bar needs to be taken into account
        if (this.table.getVerticalBar().getVisible()) {
            scrollBarSize = (this.table.getVerticalBar() == null ? 0 : this.table.getVerticalBar().getSize().x);
        }

        // is there any extra space that the table is not using?
        if (totalAvailWidth > totalColumnWidthUsage + scrollBarSize) {
            final int totalAmtToBeAllocated = (totalAvailWidth - totalColumnWidthUsage - scrollBarSize);

            // add unused space to the last column.
            if (totalAmtToBeAllocated > 0) {
                tableColumns[totalColumns - 1].setWidth(tableColumns[totalColumns - 1].getWidth() + totalAmtToBeAllocated);
            }
        }
    }

    /**
     * Returns the Text control reference.
     *
     * @return
     */
    public Text getTextControl() {
        this.checkWidget();
        return this.text;
    }
}
