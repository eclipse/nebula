/*******************************************************************************
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com>, Pascal Leclercq <pascal.leclercq@gmail.com>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo ZERR - initial API and implementation
 *     Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.pagination;

import java.io.Serializable;
import java.util.Locale;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.SWT;

/**
 * 
 * The pagination controller is used to store information about pagination :
 * 
 * <ul>
 * <li>the current page index: index of the selected page.</li>
 * <li>the page size: number items to display per page.</li>
 * <li>the total elements: the total elements of the paginated list.</li>
 * <li>the current sort information: the property name and direction of the
 * sort.</li>
 * </ul>
 * 
 * <p>
 * The controller fire events as soon as pagination information change
 * (ex:selected page change). Those information can be observed by adding
 * {@link IPageChangedListener} to the controller.
 * </p>
 * 
 */
public class PageableController implements Serializable {

	private static final long serialVersionUID = 8456710060857724013L;

	public static final int DEFAULT_PAGE_INDEX = -1;
	public static final int DEFAULT_PAGE_SIZE = 10;

	// the current page index: index of the selected page
	private int currentPage;
	// the page size: number items to display per page
	private int pageSize;
	// the total elements: the total elements of the paginated list
	private long totalElements;
	// the current sort information: the property name and direction of the
	// sort.
	private String sortPropertyName;
	private int sortDirection;

	// The current locale used for the resources
	private Locale locale = Locale.getDefault();

	private ListenerList pageChangedListeners = new ListenerList();

	/**
	 * Constructor with default page size.
	 * 
	 */
	public PageableController() {
		this(DEFAULT_PAGE_SIZE);
	}

	/**
	 * Constructor with page size.
	 * 
	 * @param pageSize
	 *            size of the page (number items displayed per page).
	 */
	public PageableController(int pageSize) {
		this.currentPage = DEFAULT_PAGE_INDEX;
		this.pageSize = pageSize;
		this.totalElements = 0;
		this.sortPropertyName = null;
		this.sortDirection = SWT.NONE;
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when the page controller changed ( the current page page size, total
	 * elements or sort change.
	 * 
	 * @param listener
	 *            the listener which should be notified when the controller
	 *            change.
	 * @param listener
	 * 
	 * @see IPageChangedListener
	 * @see #removePageChangedListener(IPageChangedListener)
	 * 
	 */
	public void addPageChangedListener(IPageChangedListener listener) {
		if (listener == null) {
			throw new NullPointerException(
					"Cannot add a null page changed listener"); //$NON-NLS-1$
		}
		pageChangedListeners.add(listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will be
	 * notified when page controller changed ( the current page page size, total
	 * elements or sort change.
	 * 
	 * @param listener
	 *            the listener which should no longer be notified
	 * 
	 * @see IPageChangedListener
	 * @see #addPageChangedListener(IPageChangedListener)
	 */
	public void removePageChangedListener(IPageChangedListener listener) {
		if (listener != null) {
			pageChangedListeners.remove(listener);
		}
	}

	/**
	 * Returns true if there is a previous page and false otherwise.
	 * 
	 * @return true if there is a previous page and false otherwise.
	 */
	public boolean hasPreviousPage() {
		return getCurrentPage() > 0;
	}

	/**
	 * Returns whether the current page is the first one.
	 * 
	 * @return
	 */
	public boolean isFirstPage() {
		return !hasPreviousPage();
	}

	/**
	 * Returns true if there is a next page and false otherwise.
	 * 
	 * @return true if there is a next page and false otherwise.
	 */
	public boolean hasNextPage() {
		return ((getCurrentPage() + 1) * getPageSize()) < totalElements;
	}

	/**
	 * Returns the current page index.
	 * 
	 * @return
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	/**
	 * Set the current page index and fire events if the given page index is
	 * different from the current page index.
	 * 
	 * @param currentPage
	 *            new current page index.
	 */
	public void setCurrentPage(int currentPage) {
		if (this.currentPage != currentPage) {
			int oldPageNumber = this.currentPage;
			this.currentPage = currentPage;
			notifyListenersForPageIndexChanged(oldPageNumber, currentPage);
		}
	}

	/**
	 * Returns the page size: number items to display per page.
	 * 
	 * @return
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Set the page size and fire events if the given page size is different
	 * from the current page size.
	 * 
	 * @param pageSize
	 *            page size: number items to display per page.
	 */
	public void setPageSize(int pageSize) {
		if (this.pageSize != pageSize) {
			int oldPageSize = this.pageSize;
			this.pageSize = pageSize;
			notifyListenersForPageSizeChanged(oldPageSize, pageSize);
		}
	}

	/**
	 * Returns the total pages.
	 * 
	 * @return
	 */
	public int getTotalPages() {
		return getPageSize() == 0 ? 0 : (int) Math.ceil((double) totalElements
				/ (double) getPageSize());
	}

	/**
	 * Returns whether the current page is the last one.
	 * 
	 * @return
	 */
	public boolean isLastPage() {
		return !hasNextPage();
	}

	public void setTotalElements(long totalElements) {
		if (this.totalElements != totalElements) {
			long oldTotalElements = this.totalElements;
			this.totalElements = totalElements;
			notifyListenersForTotalElementsChanged(oldTotalElements,
					totalElements);
		}
	}

	/**
	 * Returns the total elements.
	 * 
	 * @return
	 */
	public long getTotalElements() {
		return totalElements;
	}

	/**
	 * Returns the current page offset.
	 * 
	 * @return
	 */
	public int getPageOffset() {
		return getCurrentPage() * getPageSize();
	}

	/**
	 * Set the sort and fire events if the given sort is different from the
	 * current sort.
	 * 
	 * @param propertyName
	 *            the sort property name.
	 * @param sortDirection
	 *            the sort direction {@link SWT.UP}, {@link SWT.DOWN}.
	 */
	public void setSort(String propertyName, int sortDirection) {
		if (this.sortPropertyName != propertyName
				|| this.sortDirection != sortDirection) {
			String oldPopertyName = this.sortPropertyName;
			this.sortPropertyName = propertyName;
			int oldSortDirection = this.sortDirection;
			this.sortDirection = sortDirection;
			notifyListenersForSortChanged(oldPopertyName, propertyName,
					oldSortDirection, sortDirection);
		}
	}

	/**
	 * Returns the property name used to sort.
	 * 
	 * @return the sort property name.
	 */
	public String getSortPropertyName() {
		return sortPropertyName;
	}

	/**
	 * Returns the sort direction {@link SWT.UP}, {@link SWT.DOWN}.
	 * 
	 * @return
	 */
	public int getSortDirection() {
		return sortDirection;
	}

	/**
	 * Reset the current page index and force the fire events of page index
	 * changed.
	 */
	public void reset() {
		int oldCurrentPage = currentPage;
		this.currentPage = 0;
		notifyListenersForPageIndexChanged(oldCurrentPage, currentPage);
		// this.propertyName = null;
		// this.sortDirection = SWT.NONE;
	}

	/**
	 * Set the local for the resources.
	 * 
	 * @param locale
	 */
	public void setLocale(Locale locale) {
		Locale oldLocale = this.locale;
		this.locale = locale;
		if (!oldLocale.equals(locale)) {
			notifyListenersForLocaleChanged(oldLocale, locale);
		}
	}

	private void notifyListenersForPageIndexChanged(int oldPageNumber,
			int newPageNumber) {
		final Object[] listeners = pageChangedListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			final IPageChangedListener listener = (IPageChangedListener) listeners[i];
			listener.pageIndexChanged(oldPageNumber, newPageNumber, this);
		}
	}

	private void notifyListenersForTotalElementsChanged(long oldTotalElements,
			long newTotalElements) {
		final Object[] listeners = pageChangedListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			final IPageChangedListener listener = (IPageChangedListener) listeners[i];
			listener.totalElementsChanged(oldTotalElements, newTotalElements,
					this);
		}
	}

	private void notifyListenersForSortChanged(String oldPopertyName,
			String propertyName, int oldSortDirection, int sortDirection) {
		final Object[] listeners = pageChangedListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			final IPageChangedListener listener = (IPageChangedListener) listeners[i];
			listener.sortChanged(oldPopertyName, propertyName,
					oldSortDirection, sortDirection, this);
		}
	}

	private void notifyListenersForPageSizeChanged(int oldPageSize,
			int newPageSize) {
		final Object[] listeners = pageChangedListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			final IPageChangedListener listener = (IPageChangedListener) listeners[i];
			listener.pageSizeChanged(oldPageSize, newPageSize, this);
		}
	}

	private void notifyListenersForLocaleChanged(Locale oldLocale,
			Locale newLocale) {
		final Object[] listeners = pageChangedListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			final IPageChangedListener listener = (IPageChangedListener) listeners[i];
			listener.localeChanged(oldLocale, newLocale, this);
		}
	}
}
