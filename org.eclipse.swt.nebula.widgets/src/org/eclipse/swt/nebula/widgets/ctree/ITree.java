package org.eclipse.swt.nebula.widgets.ctree;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public interface ITree {

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when the receiver's selection changes, by sending
	 * it one of the messages defined in the <code>SelectionListener</code>
	 * interface.
	 * <p>
	 * When <code>widgetSelected</code> is called, the item field of the event object is valid.
	 * If the receiver has <code>SWT.CHECK</code> style set and the check selection changes,
	 * the event object detail field contains the value <code>SWT.CHECK</code>.
	 * <code>widgetDefaultSelected</code> is typically called when an item is double-clicked.
	 * The item field of the event object is valid for default selection, but the detail field is not used.
	 * </p>
	 *
	 * @param listener the listener which should be notified
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @see SelectionListener
	 * @see #removeSelectionListener
	 * @see SelectionEvent
	 */
	public abstract void addSelectionListener(SelectionListener listener);

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when an item in the receiver is expanded or collapsed
	 * by sending it one of the messages defined in the <code>TreeListener</code>
	 * interface.
	 *
	 * @param listener the listener which should be notified
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @see TreeListener
	 * @see #removeTreeListener
	 */
	public abstract void addTreeListener(TreeListener listener);

	/**
	 * Clears the item at the given zero-relative index in the receiver.
	 * The text, icon and other attributes of the item are set to the default
	 * value.  If the tree was created with the <code>SWT.VIRTUAL</code> style,
	 * these attributes are requested again as needed.
	 *
	 * @param index the index of the item to clear
	 * @param all <code>true</code> if all child items of the indexed item should be
	 * cleared recursively, and <code>false</code> otherwise
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * 
	 * @see SWT#VIRTUAL
	 * @see SWT#SetData
	 * 
	 * @since 3.2
	 */
	public abstract void clear(int index, boolean all);

	/**
	 * Clears all the items in the receiver. The text, icon and other
	 * attributes of the items are set to their default values. If the
	 * tree was created with the <code>SWT.VIRTUAL</code> style, these
	 * attributes are requested again as needed.
	 * 
	 * @param all <code>true</code> if all child items should be cleared
	 * recursively, and <code>false</code> otherwise
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * 
	 * @see SWT#VIRTUAL
	 * @see SWT#SetData
	 * 
	 * @since 3.2
	 */
	public abstract void clearAll(boolean all);

	public abstract Point computeSize(int wHint, int hHint, boolean changed);

	/**
	 * Deselects all selected items in the receiver.
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public abstract void deselectAll();

	public abstract Rectangle getClientArea();

	/**
	 * Returns the column at the given, zero-relative index in the
	 * receiver. Throws an exception if the index is out of range.
	 * Columns are returned in the order that they were created.
	 * If no <code>CTableTreeColumn</code>s were created by the programmer,
	 * this method will throw <code>ERROR_INVALID_RANGE</code> despite
	 * the fact that a single column of data may be visible in the tree.
	 * This occurs when the programmer uses the tree like a list, adding
	 * items but never creating a column.
	 *
	 * @param index the index of the column to return
	 * @return the column at the given index
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * 
	 * @see Tree#getColumnOrder()
	 * @see Tree#setColumnOrder(int[])
	 * @see CTreeColumn#getMoveable()
	 * @see CTreeColumn#setMoveable(boolean)
	 * @see SWT#Move
	 * 
	 * @since 3.1
	 */
	public abstract CTreeColumn getColumn(int index);

	/**
	 * Returns the number of columns contained in the receiver.
	 * If no <code>CTableTreeColumn</code>s were created by the programmer,
	 * this value is zero, despite the fact that visually, one column
	 * of items may be visible. This occurs when the programmer uses
	 * the tree like a list, adding items but never creating a column.
	 *
	 * @return the number of columns
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * 
	 * @since 3.1
	 */
	public abstract int getColumnCount();

	/**
	 * Returns an array of zero-relative integers that map
	 * the creation order of the receiver's items to the
	 * order in which they are currently being displayed.
	 * <p>
	 * Specifically, the indices of the returned array represent
	 * the current visual order of the items, and the contents
	 * of the array represent the creation order of the items.
	 * </p><p>
	 * Note: This is not the actual structure used by the receiver
	 * to maintain its list of items, so modifying the array will
	 * not affect the receiver. 
	 * </p>
	 *
	 * @return the current visual order of the receiver's items
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * 
	 * @see Tree#setColumnOrder(int[])
	 * @see CTreeColumn#getMoveable()
	 * @see CTreeColumn#setMoveable(boolean)
	 * @see SWT#Move
	 * 
	 * @since 3.2
	 */
	public abstract int[] getColumnOrder();

	/**
	 * Returns an array of <code>CTableTreeColumn</code>s which are the
	 * columns in the receiver. Columns are returned in the order
	 * that they were created.  If no <code>CTableTreeColumn</code>s were
	 * created by the programmer, the array is empty, despite the fact
	 * that visually, one column of items may be visible. This occurs
	 * when the programmer uses the tree like a list, adding items but
	 * never creating a column.
	 * <p>
	 * Note: This is not the actual structure used by the receiver
	 * to maintain its list of items, so modifying the array will
	 * not affect the receiver. 
	 * </p>
	 *
	 * @return the items in the receiver
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * 
	 * @see Tree#getColumnOrder()
	 * @see Tree#setColumnOrder(int[])
	 * @see CTreeColumn#getMoveable()
	 * @see CTreeColumn#setMoveable(boolean)
	 * @see SWT#Move
	 * 
	 * @since 3.1
	 */
	public abstract CTreeColumn[] getColumns();

	/**
	 * Returns the width in pixels of a grid line.
	 *
	 * @return the width of a grid line in pixels
	 * 
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * 
	 * @since 3.1
	 */
	public abstract int getGridLineWidth();

	/**
	 * Returns the height of the receiver's header 
	 *
	 * @return the height of the header or zero if the header is not visible
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * 
	 * @since 3.1 
	 */
	public abstract int getHeaderHeight();

	/**
	 * Returns <code>true</code> if the receiver's header is visible,
	 * and <code>false</code> otherwise.
	 * <p>
	 * If one of the receiver's ancestors is not visible or some
	 * other condition makes the receiver not visible, this method
	 * may still indicate that it is considered visible even though
	 * it may not actually be showing.
	 * </p>
	 *
	 * @return the receiver's header's visibility state
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * 
	 * @since 3.1
	 */
	public abstract boolean getHeaderVisible();

	/**
	 * Returns the item at the given, zero-relative index in the
	 * receiver. Throws an exception if the index is out of range.
	 *
	 * @param index the index of the item to return
	 * @return the item at the given index
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the list minus 1 (inclusive)</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * 
	 * @since 3.1
	 */
	public abstract CTreeItem getItem(int index);

	/**
	 * Returns the item at the given point in the receiver
	 * or null if no such item exists. The point is in the
	 * coordinate system of the receiver.
	 * <p>
	 * The item that is returned represents an item that could be selected by the user.
	 * For example, if selection only occurs in items in the first column, then null is 
	 * returned if the point is outside of the item. 
	 * Note that the SWT.FULL_SELECTION style hint, which specifies the selection policy,
	 * determines the extent of the selection.
	 * </p>
	 *
	 * @param point the point used to locate the item
	 * @return the item at the given point, or null if the point is not in a selectable item
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the point is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public abstract CTreeItem getItem(Point point);

	/**
	 * Returns the number of items contained in the receiver
	 * that are direct item children of the receiver.  The
	 * number that is returned is the number of roots in the
	 * tree.
	 *
	 * @return the number of items
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public abstract int getItemCount();

	/**
	 * Returns the height of the area which would be used to
	 * display <em>one</em> of the items in the tree.
	 *
	 * @return the height of one item
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public abstract int getItemHeight();

	/**
	 * Returns a (possibly empty) array of items contained in the
	 * receiver that are direct item children of the receiver.  These
	 * are the roots of the tree.
	 * <p>
	 * Note: This is not the actual structure used by the receiver
	 * to maintain its list of items, so modifying the array will
	 * not affect the receiver. 
	 * </p>
	 *
	 * @return the items
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public abstract CTreeItem[] getItems();

	/**
	 * Returns <code>true</code> if the receiver's lines are visible,
	 * and <code>false</code> otherwise.
	 * <p>
	 * If one of the receiver's ancestors is not visible or some
	 * other condition makes the receiver not visible, this method
	 * may still indicate that it is considered visible even though
	 * it may not actually be showing.
	 * </p>
	 *
	 * @return the visibility state of the lines
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * 
	 * @since 3.1
	 */
	public abstract boolean getLinesVisible();

	/**
	 * Returns the receiver's parent item, which must be a
	 * <code>CTableTreeItem</code> or null when the receiver is a
	 * root.
	 *
	 * @return the receiver's parent item
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public abstract CTreeItem getParentItem();

	/**
	 * Returns an array of <code>CTableTreeItem</code>s that are currently
	 * selected in the receiver. The order of the items is unspecified.
	 * An empty array indicates that no items are selected. 
	 * <p>
	 * Note: This is not the actual structure used by the receiver
	 * to maintain its selection, so modifying the array will
	 * not affect the receiver. 
	 * </p>
	 * @return an array representing the selection
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public abstract CTreeItem[] getSelection();

	/**
	 * Returns the number of selected items contained in the receiver.
	 *
	 * @return the number of selected items
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public abstract int getSelectionCount();

	/**
	 * Returns the column which shows the sort indicator for
	 * the receiver. The value may be null if no column shows
	 * the sort indicator.
	 *
	 * @return the sort indicator 
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * 
	 * @see #setSortColumn(CTreeColumn)
	 * 
	 * @since 3.2
	 */
	public abstract CTreeColumn getSortColumn();

	/**
	 * Returns the direction of the sort indicator for the receiver. 
	 * The value will be one of <code>UP</code>, <code>DOWN</code> 
	 * or <code>NONE</code>.
	 *
	 * @return the sort direction
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * 
	 * @see #setSortDirection(int)
	 * 
	 * @since 3.2
	 */
	public abstract int getSortDirection();

	/**
	 * Returns the item which is currently at the top of the receiver.
	 * This item can change when items are expanded, collapsed, scrolled
	 * or new items are added or removed.
	 *
	 * @return the item at the top of the receiver 
	 * 
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * 
	 * @since 2.1
	 */
	public abstract CTreeItem getTopItem();

	/**
	 * Searches the receiver's list starting at the first column
	 * (index 0) until a column is found that is equal to the 
	 * argument, and returns the index of that column. If no column
	 * is found, returns -1.
	 *
	 * @param column the search column
	 * @return the index of the column
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the string is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * 
	 * @since 3.1
	 */
	public abstract int indexOf(CTreeColumn column);

	/**
	 * Searches the receiver's list starting at the first item
	 * (index 0) until an item is found that is equal to the 
	 * argument, and returns the index of that item. If no item
	 * is found, returns -1.
	 *
	 * @param item the search item
	 * @return the index of the item
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the tool item is null</li>
	 *    <li>ERROR_INVALID_ARGUMENT - if the tool item has been disposed</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * 
	 * @since 3.1
	 */
	public abstract int indexOf(CTreeItem item);

	/**
	 * Removes all of the items from the receiver.
	 * 
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public abstract void removeAll();

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when the receiver's selection changes.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @see SelectionListener
	 * @see #addSelectionListener
	 */
	public abstract void removeSelectionListener(SelectionListener listener);

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when items in the receiver are expanded or collapsed.
	 *
	 * @param listener the listener which should no longer be notified
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @see TreeListener
	 * @see #addTreeListener
	 */
	public abstract void removeTreeListener(TreeListener listener);

	/**
	 * Display a mark indicating the point at which an item will be inserted.
	 * The drop insert item has a visual hint to show where a dragged item 
	 * will be inserted when dropped on the tree.
	 * 
	 * @param item the insert item.  Null will clear the insertion mark.
	 * @param before true places the insert mark above 'item'. false places 
	 *	the insert mark below 'item'.
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_INVALID_ARGUMENT - if the item has been disposed</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public abstract void setInsertMark(CTreeItem item, boolean before);

	/**
	 * Sets the number of root-level items contained in the receiver.
	 *
	 * @param count the number of items
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @since 3.2
	 */
	public abstract void setItemCount(int count);

	/**
	 * Selects all of the items in the receiver.
	 * <p>
	 * If the receiver is single-select, do nothing.
	 * </p>
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 */
	public abstract void selectAll();

	/**
	 * Sets the order that the items in the receiver should 
	 * be displayed in to the given argument which is described
	 * in terms of the zero-relative ordering of when the items
	 * were added.
	 *
	 * @param order the new order to display the items
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the item order is null</li>
	 *    <li>ERROR_INVALID_ARGUMENT - if the item order is not the same length as the number of items</li>
	 * </ul>
	 * 
	 * @see Tree#getColumnOrder()
	 * @see CTreeColumn#getMoveable()
	 * @see CTreeColumn#setMoveable(boolean)
	 * @see SWT#Move
	 * 
	 * @since 3.2
	 */
	public abstract void setColumnOrder(int[] order);

	/**
	 * Marks the receiver's header as visible if the argument is <code>true</code>,
	 * and marks it invisible otherwise. 
	 * <p>
	 * If one of the receiver's ancestors is not visible or some
	 * other condition makes the receiver not visible, marking
	 * it visible may not actually cause it to be displayed.
	 * </p>
	 *
	 * @param show the new visibility state
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * 
	 * @since 3.1
	 */
	public abstract void setHeaderVisible(boolean show);

	/**
	 * Marks the receiver's lines as visible if the argument is <code>true</code>,
	 * and marks it invisible otherwise. 
	 * <p>
	 * If one of the receiver's ancestors is not visible or some
	 * other condition makes the receiver not visible, marking
	 * it visible may not actually cause it to be displayed.
	 * </p>
	 *
	 * @param show the new visibility state
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * 
	 * @since 3.1
	 */
	public abstract void setLinesVisible(boolean show);

	/**
	 * Sets the receiver's selection to the given item.
	 * The current selection is cleared before the new item is selected.
	 * <p>
	 * If the item is not in the receiver, then it is ignored.
	 * </p>
	 *
	 * @param item the item to select
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the item is null</li>
	 *    <li>ERROR_INVALID_ARGUMENT - if the item has been disposed</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * 
	 * @since 3.2
	 */
	public abstract void setSelection(CTreeItem item);

	/**
	 * Sets the receiver's selection to be the given array of items.
	 * The current selection is cleared before the new items are selected.
	 * <p>
	 * Items that are not in the receiver are ignored.
	 * If the receiver is single-select and multiple items are specified,
	 * then all items are ignored.
	 * </p>
	 *
	 * @param items the array of items
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the array of items is null</li>
	 *    <li>ERROR_INVALID_ARGUMENT - if one of the items has been disposed</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @see Tree#deselectAll()
	 */
	public abstract void setSelection(CTreeItem[] items);

	/**
	 * Sets the column used by the sort indicator for the receiver. A null
	 * value will clear the sort indicator.  The current sort column is cleared 
	 * before the new column is set.
	 *
	 * @param column the column used by the sort indicator or <code>null</code>
	 * 
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_INVALID_ARGUMENT - if the column is disposed</li> 
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * 
	 * @since 3.2
	 */
	public abstract void setSortColumn(CTreeColumn column);

	/**
	 * Sets the direction of the sort indicator for the receiver. The value 
	 * can be one of <code>UP</code>, <code>DOWN</code> or <code>NONE</code>.
	 *
	 * @param direction the direction of the sort indicator 
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 * 
	 * @since 3.2
	 */
	public abstract void setSortDirection(int direction);

	/**
	 * Sets the item which is currently at the top of the receiver.
	 * This item can change when items are expanded, collapsed, scrolled
	 * or new items are added or removed.
	 *
	 * @param item the item to be shown
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the item is null</li>
	 *    <li>ERROR_INVALID_ARGUMENT - if the item has been disposed</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @see Tree#getTopItem()
	 * 
	 * @since 2.1
	 */
	public abstract void setTopItem(CTreeItem item);

	/**
	 * Shows the column.  If the column is already showing in the receiver,
	 * this method simply returns.  Otherwise, the columns are scrolled until
	 * the column is visible.
	 *
	 * @param column the column to be shown
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the item is null</li>
	 *    <li>ERROR_INVALID_ARGUMENT - if the item has been disposed</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @since 3.1
	 */
	public abstract void showColumn(CTreeColumn column);

	/**
	 * Shows the selection.  If the selection is already showing in the receiver,
	 * this method simply returns.  Otherwise, the items are scrolled until
	 * the selection is visible.
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @see Tree#showItem(CTreeItem)
	 */
	public abstract void showSelection();

	/**
	 * Shows the item.  If the item is already showing in the receiver,
	 * this method simply returns.  Otherwise, the items are scrolled
	 * and expanded until the item is visible.
	 *
	 * @param item the item to be shown
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the item is null</li>
	 *    <li>ERROR_INVALID_ARGUMENT - if the item has been disposed</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 * </ul>
	 *
	 * @see Tree#showSelection()
	 */
	public abstract void showItem(CTreeItem item);

}