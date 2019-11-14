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
package org.eclipse.nebula.widgets.pagination.tree;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.nebula.widgets.pagination.AbstractPaginationWidget;
import org.eclipse.nebula.widgets.pagination.IPageContentProvider;
import org.eclipse.nebula.widgets.pagination.PageLoaderStrategyHelper;
import org.eclipse.nebula.widgets.pagination.PageableController;
import org.eclipse.nebula.widgets.pagination.collections.PageResultContentProvider;
import org.eclipse.nebula.widgets.pagination.renderers.ICompositeRendererFactory;
import org.eclipse.nebula.widgets.pagination.renderers.navigation.ResultAndNavigationPageLinksRendererFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

/**
 * Abstract class SWT {@link Composite} which host a SWT {@link Tree} linked to
 * a pagination controller to display data with pagination. The
 * {@link PageableTree#refreshPage()} must be implemented to load paginated data
 * and update the total element of the controller.
 * 
 * This composite is able to to add another {@link Composite} on the top and on
 * the bottom of the tree. For instance you can display navigation page links on
 * the top of the tree.
 * 
 * @param <T>
 *            the pagination controller.
 */
public class PageableTree extends AbstractPaginationWidget<Tree> {

	protected static final int DEFAULT_TREE_STYLE = SWT.BORDER | SWT.MULTI
			| SWT.H_SCROLL | SWT.V_SCROLL;

	/** the tree viewer **/
	protected TreeViewer viewer;

	/** the tree style **/
	private final int treeStyle;

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
	 * 
	 */
	public PageableTree(Composite parent, int style) {
		this(parent, style, DEFAULT_TREE_STYLE,
				PageableController.DEFAULT_PAGE_SIZE);
	}

	/**
	 * Constructs a new instance of this class given its parent and a style
	 * value describing its behavior and appearance. Here defaut page size is
	 * used.
	 * 
	 * @param parent
	 *            a widget which will be the parent of the new instance (cannot
	 *            be null)
	 * @param style
	 *            the style of widget to construct
	 * @param treeStyle
	 *            the style of tree to construct
	 * @param pageSize
	 *            size of the page (number items displayed per page).
	 * 
	 */
	public PageableTree(Composite parent, int style, int treeStyle,
			int pageSize, IPageContentProvider pageContentProvider) {
		this(parent, style, treeStyle, pageSize, pageContentProvider,
				getDefaultPageRendererTopFactory(),
				getDefaultPageRendererBottomFactory(), true);
	}

	public PageableTree(Composite parent, int style, int treeStyle, int pageSize) {
		this(parent, style, treeStyle, pageSize, PageResultContentProvider
				.getInstance(), getDefaultPageRendererTopFactory(),
				getDefaultPageRendererBottomFactory(), true);
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
	public PageableTree(Composite parent, int style, int treeStyle,
			int pageSize, IPageContentProvider pageContentProvider,
			ICompositeRendererFactory pageRendererTopFactory,
			ICompositeRendererFactory pageRendererBottomFactory) {
		this(parent, style, treeStyle, pageSize, pageContentProvider,
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
	 * @param treeStyle
	 *            the style of tree to construct
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
	 * @param createUI
	 *            true if the UI must be created and false otherwise.
	 * 
	 */
	protected PageableTree(Composite parent, int style, int treeStyle,
			int pageSize, IPageContentProvider pageContentProvider,
			ICompositeRendererFactory pageRendererTopFactory,
			ICompositeRendererFactory pageRendererBottomFactory,
			boolean createUI) {
		super(parent, style, pageSize, pageContentProvider,
				pageRendererTopFactory, pageRendererBottomFactory, false);
		this.treeStyle = treeStyle;
		if (createUI) {
			createUI(this);
		}
	}

	@Override
	protected Tree createWidget(Composite parent) {
		Tree tree = createTree(parent);
		this.viewer = new TreeViewer(tree);
		return tree;
	}

	/**
	 * Create the tree widget and layout it.
	 * 
	 * @param parent
	 * @return
	 */
	protected Tree createTree(Composite parent) {
		Tree tree = createTree(parent, getTreeStyle());
		tree.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return tree;
	}

	/**
	 * Returns the tree style.
	 * 
	 * @return
	 */
	protected int getTreeStyle() {
		return treeStyle;
	}

	/**
	 * Returns the tree viewer.
	 * 
	 * @return
	 */
	public TreeViewer getViewer() {
		return viewer;
	}

	/**
	 * Create a tree.
	 * 
	 * @param parent
	 *            a widget which will be the parent of the new instance (cannot
	 *            be null)
	 * @param style
	 *            the style of tree to constr * @return
	 */
	protected Tree createTree(Composite parent, int style) {
		return new Tree(parent, style);
	}

	/**
	 * Returns the default page renderer factory for the top region.
	 * 
	 * @return
	 */
	public static ICompositeRendererFactory getDefaultPageRendererTopFactory() {
		return ResultAndNavigationPageLinksRendererFactory.getFactory();
	}

	/**
	 * Returns the default page renderer factory for the bottom region.
	 * 
	 * @return
	 */
	public static ICompositeRendererFactory getDefaultPageRendererBottomFactory() {
		return null;
	}

	@Override
	public void refreshPage() {
		PageLoaderStrategyHelper.loadPageAndReplaceItems(getController(),
				viewer, getPageLoader(), getPageContentProvider(), null);
	}
}
