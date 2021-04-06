/*******************************************************************************
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com>, Pascal Leclercq <pascal.leclercq@gmail.com>
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Angelo ZERR - initial API and implementation
 *     Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.pagination;

import java.util.Locale;

import org.eclipse.swt.widgets.Composite;

/**
 * 
 * Classes which implement this interface are SWT {@link Composite} which must
 * observe changed of a pagination controller to react and update the UI
 * according the change of the pagination controller. Ex: control to display
 * page navigation, paginated data in a table, etc...
 * 
 * @param <T>
 *            pagination controller.
 */
public abstract class AbstractPageControllerComposite extends Composite
		implements IPageChangedListener {

	/** the controller to observe and update according the UI state **/
	private PageableController controller;
	/** local used for the resources bundle **/
	private Locale locale = Locale.getDefault();

	private IPageContentProvider pageContentProvider;

	/**
	 * Constructs a new instance of this class given its parent and a style
	 * value describing its behavior and appearance. Here the pagination
	 * controller is created in this class with default page size.
	 * 
	 * @param parent
	 *            a widget which will be the parent of the new instance (cannot
	 *            be null)
	 * @param style
	 *            the style of widget to construct
	 * 
	 */
	public AbstractPageControllerComposite(Composite parent, int style) {
		this(parent, style, null);
	}

	/**
	 * Constructs a new instance of this class given its parent and a style
	 * value describing its behavior and appearance. Here the pagination
	 * controller is filled.
	 * 
	 * @param parent
	 *            a widget which will be the parent of the new instance (cannot
	 *            be null)
	 * @param style
	 *            the style of widget to construct
	 * @param controller
	 *            the pagination controller to observe and update.
	 * 
	 */
	public AbstractPageControllerComposite(Composite parent, int style,
			PageableController controller) {
		this(parent, style, controller, PageableController.DEFAULT_PAGE_SIZE, null, true);
	}

	/**
	 * Constructs a new instance of this class given its parent and a style
	 * value describing its behavior and appearance. Here the pagination
	 * controller is created in this class with the given page size.
	 * 
	 * @param parent
	 *            a widget which will be the parent of the new instance (cannot
	 *            be null)
	 * @param style
	 *            the style of widget to construct
	 * @param pageSize
	 *            size of the page (number items displayed per page).
	 */
	public AbstractPageControllerComposite(Composite parent, int style,
			int pageSize, IPageContentProvider pageContentProvider) {
		this(parent, style, null, pageSize, pageContentProvider, true);
	}

	/**
	 * Constructs a new instance of this class given its parent and a style
	 * value describing its behavior and appearance. The pagination controller
	 * is created in this class or filled.
	 * 
	 * @param parent
	 *            a widget which will be the parent of the new instance (cannot
	 *            be null)
	 * @param style
	 *            the style of widget to construct
	 * @param controller
	 *            the pagination controller to observe and update. If this
	 *            controller is null, this Composite create the pagination
	 *            controller with the given pageSize.
	 * @param pageSize
	 *            size of the page (number items displayed per page).
	 * @param createUI
	 *            true if the UI must be created and false otherwise.
	 * 
	 */
	protected AbstractPageControllerComposite(Composite parent, int style,
			PageableController controller, int pageSize,
			IPageContentProvider pageContentProvider, boolean createUI) {
		super(parent, style);
		this.pageContentProvider = pageContentProvider;
		// Get or create controller
		this.controller = controller != null ? controller
				: createController(pageSize);
		PaginationHelper.setController(this, controller);
		if (createUI) {
			// Create the UI
			createUI(this);
		}
		// add listener from the pagination controller.
		this.controller.addPageChangedListener(this);
	}

	protected PageableController createController(int pageSize) {
		if (pageContentProvider != null) {
			return pageContentProvider.createController(pageSize);
		}
		return (PageableController) new PageableController(pageSize);
	}

	public PageableController getController() {
		return controller;
	}

	/**
	 * Set the current page index to teh pagination controller.
	 * 
	 * @param currentPage
	 *            new current page index.
	 */
	public void setCurrentPage(int currentPage) {
		getController().setCurrentPage(currentPage);
	}

	@Override
	public void dispose() {
		// remove listener from the pagination controller.
		getController().removePageChangedListener(this);
		super.dispose();
	}

	/**
	 * Set the locale to use for resources.
	 * 
	 * @param locale
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * returns the locale to use for resources.
	 * 
	 * @return
	 */
	public Locale getLocale() {
		return locale;
	}

	public void localeChanged(Locale oldLocale, Locale newLocale,
			PageableController paginationController) {
		setLocale(newLocale);
	}

	public IPageContentProvider getPageContentProvider() {
		return pageContentProvider;
	}

	/**
	 * Create the UI content.
	 * 
	 * @param parent
	 */
	protected abstract void createUI(Composite parent);
}
