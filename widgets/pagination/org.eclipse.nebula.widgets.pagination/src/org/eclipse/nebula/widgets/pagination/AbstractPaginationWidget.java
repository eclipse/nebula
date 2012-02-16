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

import java.util.Locale;

import org.eclipse.nebula.widgets.pagination.renderers.ICompositeRendererFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

/**
 * Abstract class SWT {@link Composite} which host a SWT {@link Widget} linked
 * to a pagination controller to display data with pagination. The
 * {@link AbstractPaginationWidget#refreshPage()} must be implemented to load
 * paginated data and update the total element of the controller.
 * 
 * This composite is able to to add another {@link Composite} on the top and on
 * the bottom of the widget. For instance you can display navigation page links
 * on the top of the widget.
 * 
 * @param<W> the widget linked to the pagination controller.
 * @param <T>
 *            the pagination controller to observe to refresh paginated data in
 *            the widget and update it with total elements.
 */
public abstract class AbstractPaginationWidget<W extends Widget> extends
		AbstractPageControllerComposite {

	/** the widget hosted by the composite (ex: table) **/
	private W widget;
	/**
	 * the page renderer factory to use to create a Composite on the top of the
	 * widget.
	 **/
	private ICompositeRendererFactory pageRendererTopFactory;
	/**
	 * the page renderer factory to use to create a Composite on the bottom of
	 * the widget.
	 **/
	private ICompositeRendererFactory pageRendererBottomFactory;
	/**
	 * The composite created by the pageRendererTopFactory on the top of the
	 * widget (null if none Composite must be added)
	 **/
	private Composite compositeTop;
	/**
	 * The composite created by the pageRendererBottomFactory on the bottom of
	 * the widget (null if none Composite must be added)
	 **/
	private Composite compositeBottom;

	/** the page loader used to load paginated data list */
	private IPageLoader pageLoader;

	/**
	 * The page loader handler to observe before/after page loading process
	 **/
	private IPageLoaderHandler pageLoaderHandler;

	/**
	 * Constructs a new instance of this class given its parent and a style
	 * value describing its behavior and appearance. Default page size is used
	 * here.
	 * 
	 * @param parent
	 *            a widget which will be the parent of the new instance (cannot
	 *            be null)
	 * @param style
	 *            the style of widget to construct
	 * 
	 * @param pageRendererTopFactory
	 *            the page renderer factory used to create a SWT Composite on
	 *            the top of the widget. Null if none Composite must be created.
	 * @param pageRendererBottomFactory
	 *            the page renderer factory used to create a SWT Composite on
	 *            the bottom of the widget. Null if none Composite must be
	 *            created.
	 * 
	 */
	public AbstractPaginationWidget(Composite parent, int style,
			IPageContentProvider pageContentProvider,
			ICompositeRendererFactory pageRendererTopFactory,
			ICompositeRendererFactory pageRendererBottomFactory) {
		this(parent, style, DEFAULT_PAGE_SIZE, pageContentProvider,
				pageRendererTopFactory, pageRendererBottomFactory, true);
	}

	/**
	 * Constructs a new instance of this class given its parent and a style
	 * value describing its behavior and appearance.
	 * 
	 * @param parent
	 *            a widget which will be the parent of the new instance (cannot
	 *            be null)
	 * @param style
	 *            the style of widget to construct
	 * @param pageSize
	 *            size of the page (number items displayed per page).
	 * 
	 * @param pageRendererTopFactory
	 *            the page renderer factory used to create a SWT Composite on
	 *            the top of the widget. Null if none Composite must be created.
	 * @param pageRendererBottomFactory
	 *            the page renderer factory used to create a SWT Composite on
	 *            the bottom of the widget. Null if none Composite must be
	 *            created.
	 * 
	 */
	public AbstractPaginationWidget(Composite parent, int style, int pageSize,
			IPageContentProvider pageContentProvider,
			ICompositeRendererFactory pageRendererTopFactory,
			ICompositeRendererFactory pageRendererBottomFactory) {
		this(parent, style, pageSize, pageContentProvider,
				pageRendererTopFactory, pageRendererBottomFactory, true);
	}

	/**
	 * Constructs a new instance of this class given its parent and a style
	 * value describing its behavior and appearance.
	 * 
	 * @param parent
	 *            a widget which will be the parent of the new instance (cannot
	 *            be null)
	 * @param style
	 *            the style of widget to construct
	 * @param pageSize
	 *            size of the page (number items displayed per page).
	 * 
	 * @param pageRendererTopFactory
	 *            the page renderer factory used to create a SWT Composite on
	 *            the top of the widget. Null if none Composite must be created.
	 * @param pageRendererBottomFactory
	 *            the page renderer factory used to create a SWT Composite on
	 *            the bottom of the widget. Null if none Composite must be
	 *            created.
	 * 
	 * @param createUI
	 *            true if the UI must be created and false otherwise.
	 * 
	 */
	protected AbstractPaginationWidget(Composite parent, int style,
			int pageSize, IPageContentProvider pageContentProvider,
			ICompositeRendererFactory pageRendererTopFactory,
			ICompositeRendererFactory pageRendererBottomFactory,
			boolean createUI) {
		super(parent, style, null, pageSize, pageContentProvider, false);
		this.pageRendererTopFactory = pageRendererTopFactory;
		this.pageRendererBottomFactory = pageRendererBottomFactory;
		if (createUI) {
			createUI(this);
		}
	}

	@Override
	protected void createUI(Composite parent) {
		this.setLayout(new GridLayout());
		// Create top composite if needed
		this.compositeTop = createCompositeTop(parent);
		// Create the widget linked to the controller.
		this.widget = createWidget(parent);
		PaginationHelper.setController(widget, getController());
		// Create bottom composite if needed
		this.compositeBottom = createCompositeBottom(parent);
		// set default locale
		super.setLocale(Locale.getDefault());
	}

	/**
	 * Create top composite if needed.
	 * 
	 * @param parent
	 */
	protected Composite createCompositeTop(Composite parent) {
		ICompositeRendererFactory pageRendererTopFactory = getPageRendererTopFactory();
		if (pageRendererTopFactory != null) {
			Composite compositeTop = pageRendererTopFactory.createComposite(
					parent, SWT.NONE, getController());
			if (compositeTop != null) {
				compositeTop.setLayoutData(new GridData(
						GridData.FILL_HORIZONTAL));
				return compositeTop;
			}
		}
		return null;
	}

	/**
	 * Create bottom composite if needed.
	 * 
	 * @param parent
	 */

	protected Composite createCompositeBottom(Composite parent) {
		ICompositeRendererFactory pageRendererBottomFactory = getPageRendererBottomFactory();
		if (pageRendererBottomFactory != null) {
			Composite compositeBottom = pageRendererBottomFactory
					.createComposite(parent, SWT.NONE, getController());
			if (compositeBottom != null) {
				compositeBottom.setLayoutData(new GridData(
						GridData.FILL_HORIZONTAL));
			}
			return compositeBottom;
		}
		return null;
	}

	/**
	 * Returns the page renderer factory to use to create a Composite on the top
	 * of the widget.
	 * 
	 * @return
	 */
	public ICompositeRendererFactory getPageRendererTopFactory() {
		return pageRendererTopFactory;
	}

	/**
	 * Returns the page renderer factory to use to create a Composite on the
	 * bottom of the widget.
	 * 
	 * @return
	 */
	public ICompositeRendererFactory getPageRendererBottomFactory() {
		return pageRendererBottomFactory;
	}

	/**
	 * Returns the composite created by the pageRendererTopFactory on the top of
	 * the widget (null if none Composite must be added)
	 * 
	 * @return
	 */
	public Composite getCompositeTop() {
		return compositeTop;
	}

	/**
	 * Returns the composite created by the pageRendererTopFactory on the bottom
	 * of the widget (null if none Composite must be added)
	 * 
	 * @return
	 */
	public Composite getCompositeBottom() {
		return compositeBottom;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.nebula.widgets.pagination.PageChangedListener#pageIndexChanged
	 * (int, int, org.eclipse.nebula.widgets.pagination.PaginationController)
	 */
	public void pageIndexChanged(int oldPageNumber, int newPageNumber,
			PageableController controller) {
		// when selected page changed, refresh the page
		internalRefreshPage();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.nebula.widgets.pagination.PageChangedListener#
	 * totalElementsChanged(long, long,
	 * org.eclipse.nebula.widgets.pagination.PaginationController)
	 */
	public void totalElementsChanged(long oldTotalElements,
			long newTotalElements, PageableController controller) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.nebula.widgets.pagination.PageChangedListener#sortChanged
	 * (java.lang.String, java.lang.String, int, int,
	 * org.eclipse.nebula.widgets.pagination.PaginationController)
	 */
	public void sortChanged(String oldPopertyName, String propertyName,
			int oldSortDirection, int sortDirection,
			PageableController paginationController) {
		refreshPage(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.nebula.widgets.pagination.PageChangedListener#pageSizeChanged
	 * (int, int, org.eclipse.nebula.widgets.pagination.PaginationController)
	 */
	public void pageSizeChanged(int oldPageSize, int newPageSize,
			PageableController paginationController) {
		refreshPage(false);
	}

	/**
	 * Refresh the page.
	 * 
	 * @param reset
	 *            true if page index must be reseted to the first page index
	 *            before refresh page and false otherwise.
	 */
	public void refreshPage(boolean reset) {
		if (reset) {
			getController().reset();
		} else {
			refreshPage();
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void setLocale(Locale locale) {
		super.setLocale(locale);
		if (compositeTop != null
				&& compositeTop instanceof AbstractPageControllerComposite) {
			((AbstractPageControllerComposite) compositeTop).setLocale(locale);
		}
		if (compositeBottom != null
				&& compositeBottom instanceof AbstractPageControllerComposite) {
			((AbstractPageControllerComposite) compositeBottom)
					.setLocale(locale);
		}
	}

	/**
	 * Refresh page by using page loader handler.
	 * 
	 * @param reset
	 */
	private void internalRefreshPage() {
		if (pageLoaderHandler == null) {
			refreshPage();
		} else {
			PageableController controller = getController();
			pageLoaderHandler.onBeforePageLoad(controller);
			try {
				refreshPage();
				pageLoaderHandler.onAfterPageLoad(controller, null);
			} catch (Throwable e) {
				pageLoaderHandler.onAfterPageLoad(controller, e);
			}
		}
	}

	/**
	 * Set the page loader handler to observe before/after page loading process.
	 * 
	 * @param pageLoaderHandler
	 */
	public void setPageLoaderHandler(IPageLoaderHandler pageLoaderHandler) {
		this.pageLoaderHandler = pageLoaderHandler;
	}

	/**
	 * Returns the page loader handler to observe before/after page loading
	 * process.
	 * 
	 * @return
	 */
	public IPageLoaderHandler getPageLoaderHandler() {
		return pageLoaderHandler;
	}

	/**
	 * Set the page loader to use to load paginated list.
	 * 
	 * @param pageLoader
	 */
	public void setPageLoader(IPageLoader pageLoader) {
		this.pageLoader = pageLoader;
	}

	/**
	 * Returns the page loader to use to load paginated list.
	 * 
	 * @return
	 */
	public IPageLoader getPageLoader() {
		return pageLoader;
	}

	/**
	 * Returns the widget.
	 * 
	 * @return
	 */
	public W getWidget() {
		return widget;
	}

	/**
	 * Create the widget linked to the pagination controller.
	 * 
	 * @param parent
	 *            a widget which will be the parent of the new instance (cannot
	 *            be null)
	 * @return
	 */
	protected abstract W createWidget(Composite parent);

	/**
	 * Refresh the page. This method should load paginated list data, update the
	 * viewer and update the pagination controller with total elements.
	 */
	public abstract void refreshPage();

}
