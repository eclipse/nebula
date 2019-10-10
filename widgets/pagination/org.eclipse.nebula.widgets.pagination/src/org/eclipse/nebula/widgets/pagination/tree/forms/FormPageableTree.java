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
package org.eclipse.nebula.widgets.pagination.tree.forms;

import org.eclipse.nebula.widgets.pagination.IPageContentProvider;
import org.eclipse.nebula.widgets.pagination.PageableController;
import org.eclipse.nebula.widgets.pagination.renderers.ICompositeRendererFactory;
import org.eclipse.nebula.widgets.pagination.tree.PageableTree;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Implementation of the paginated SWT Forms Tree {@link PageableTree}.
 */
public class FormPageableTree extends PageableTree {

	private final FormToolkit toolkit;

	/**
	 * Constructs a new instance of this class given its parent and a style
	 * value describing its behavior and appearance. Here default page size
	 * {@link PageableController#DEFAULT_PAGE_SIZE} and default tree style
	 * SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL are used.
	 * 
	 * @param parent
	 *            a widget which will be the parent of the new instance (cannot
	 *            be null)
	 * @param style
	 *            the style of widget to construct
	 * @param toolkit
	 *            the {@link FormToolkit} used to create the SWT {@link Tree}.
	 */
	public FormPageableTree(Composite parent, int style, FormToolkit toolkit) {
		this(parent, style, DEFAULT_TREE_STYLE, toolkit, null);
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
	 * @param treeStyle
	 *            the style of tree to construct
	 * @param toolkit
	 *            the {@link FormToolkit} used to create the SWT {@link Tree}.
	 */
	public FormPageableTree(Composite parent, int style, int treeStyle,
			FormToolkit toolkit, IPageContentProvider pageContentProvider,
			ICompositeRendererFactory pageRendererTopFactory,
			ICompositeRendererFactory pageRendererBottomFactory) {
		this(parent, style, treeStyle, toolkit,
				PageableController.DEFAULT_PAGE_SIZE, pageContentProvider,
				pageRendererTopFactory, pageRendererBottomFactory);
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
	 * @param treeStyle
	 *            the style of tree to construct
	 * @param toolkit
	 *            the {@link FormToolkit} used to create the SWT {@link Tree}.
	 */
	public FormPageableTree(Composite parent, int style, int treeStyle,
			FormToolkit toolkit, IPageContentProvider pageContentProvider) {
		this(parent, style, treeStyle, toolkit,
				PageableController.DEFAULT_PAGE_SIZE, pageContentProvider,
				getDefaultPageRendererTopFactory(),
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
	 * @param treeStyle
	 *            the style of tree to construct
	 * @param toolkit
	 *            the {@link FormToolkit} used to create the SWT {@link Tree}.
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
	public FormPageableTree(Composite parent, int style, int treeStyle,
			FormToolkit toolkit, int pageSize,
			IPageContentProvider pageContentProvider,
			ICompositeRendererFactory pageRendererTopFactory,
			ICompositeRendererFactory pageRendererBottomFactory) {
		super(parent, style, treeStyle, pageSize, pageContentProvider,
				pageRendererTopFactory, pageRendererBottomFactory, false);
		this.toolkit = toolkit;
		super.createUI(this);
		toolkit.adapt(this);
	}

	@Override
	protected Tree createTree(Composite parent, int style) {
		return toolkit.createTree(parent, style);
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
