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
package org.eclipse.nebula.widgets.pagination.table.forms;

import java.awt.print.Pageable;

import org.eclipse.nebula.widgets.pagination.IPageContentProvider;
import org.eclipse.nebula.widgets.pagination.renderers.ICompositeRendererFactory;
import org.eclipse.nebula.widgets.pagination.table.PageableTable;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Implementation of the paginated SWT Forms Table {@link PageableTable} with
 * Spring Data pagination structure {@link Page} and {@link Pageable}.
 * 
 * @see http://www.springsource.org/spring-data
 */
public class FormPageableTable extends PageableTable {

	private final FormToolkit toolkit;

	/**
	 * Constructs a new instance of this class given its parent and a style
	 * value describing its behavior and appearance.
	 * 
	 * @param parent
	 *            a widget which will be the parent of the new instance (cannot
	 *            be null)
	 * @param style
	 *            the style of widget to construct
	 * @param tableStyle
	 *            the style of table to construct
	 * @param toolkit
	 *            the {@link FormToolkit} used to create the SWT {@link Table}.
	 */
	public FormPageableTable(Composite parent, int style, int tableStyle,
			FormToolkit toolkit, IPageContentProvider pageContentProvider,
			ICompositeRendererFactory pageRendererTopFactory,
			ICompositeRendererFactory pageRendererBottomFactory) {
		this(parent, style, tableStyle, toolkit, DEFAULT_PAGE_SIZE,
				pageContentProvider, pageRendererTopFactory,
				pageRendererBottomFactory);
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
	 * @param tableStyle
	 *            the style of table to construct
	 * @param toolkit
	 *            the {@link FormToolkit} used to create the SWT {@link Table}.
	 */
	public FormPageableTable(Composite parent, int style, int tableStyle,
			FormToolkit toolkit, IPageContentProvider pageContentProvider) {
		this(parent, style, tableStyle, toolkit, DEFAULT_PAGE_SIZE,
				pageContentProvider, getDefaultPageRendererTopFactory(),
				getDefaultPageRendererBottomFactory());
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
	 * @param tableStyle
	 *            the style of table to construct
	 * @param toolkit
	 *            the {@link FormToolkit} used to create the SWT {@link Table}.
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
	public FormPageableTable(Composite parent, int style, int tableStyle,
			FormToolkit toolkit, int pageSize,
			IPageContentProvider pageContentProvider,
			ICompositeRendererFactory pageRendererTopFactory,
			ICompositeRendererFactory pageRendererBottomFactory) {
		super(parent, style, tableStyle, pageSize, pageContentProvider,
				pageRendererTopFactory, pageRendererBottomFactory, false);
		this.toolkit = toolkit;
		super.createUI(this);
		toolkit.adapt(this);
	}

	@Override
	protected Table createTable(Composite parent, int style) {
		return toolkit.createTable(parent, style);
	}

	@Override
	protected Composite createCompositeBottom(Composite parent) {
		Composite bottom = super.createCompositeBottom(parent);
		if (bottom != null) {
			toolkit.adapt(bottom);
		}
		return bottom;
	}

	@Override
	protected Composite createCompositeTop(Composite parent) {
		Composite top = super.createCompositeTop(parent);
		if (top != null) {
			toolkit.adapt(top);
		}
		return top;
	}
}
