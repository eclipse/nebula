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

import java.awt.print.Pageable;
import java.util.List;

import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.pagination.collections.PageResult;
import org.eclipse.nebula.widgets.pagination.collections.PageResultContentProvider;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;

/**
 * This class help you to configure a {@link PageableController} to manage
 * paginated list data in a {@link Viewer}. This helper manages the 2 following
 * strategies for pagination :
 * 
 * <ul>
 * <li>
 * load the paginated list and <strong>replace data</strong> from the viewer
 * with the new list. See
 * {@link PageLoaderStrategyHelper#loadPageAndReplaceItems(PageableController, Viewer, IPageLoader)}
 * </li>
 * <li>
 * load the paginated list and <strong>add to the data</strong> of the viewer
 * the new list. See
 * {@link PageLoaderStrategyHelper#loadPageAndAddItems(PageableController, TableViewer, IPageLoader)}
 * </li>
 * </ul>
 * 
 */
public class PageLoaderStrategyHelper {

	// ---------------- Replace strategy

	/**
	 * This method loads the paginated list by using the given page loader
	 * {@link IPageLoader} and information about pagination from the given
	 * controller {@link PageableController}. After loading paginated list
	 * returned in a pagination structure {@link PageResult}, this method :
	 * 
	 * <ul>
	 * <li>update the total elements of the given controller
	 * {@link PageableController}</li>
	 * <li>refresh the given {@link Viewer} by replacing data with the new
	 * paginated list.</li>
	 * </ul>
	 * 
	 * @param controller
	 *            the controller to use to load paginated list and update the
	 *            total elements.
	 * @param viewer
	 *            the viewer to refresh with new paginated list.
	 * @param pageLoader
	 *            the page loader used to load paginated list.
	 * @pageContentProvider the page content provider to retrieves total
	 *                      elements+paginated list from the page result
	 *                      structure returned by the pageLoader.
	 * @param handler
	 *            the page loader handler to observe before/after page loading
	 *            process. If null no observation is done.
	 */
	public static void loadPageAndReplaceItems(
			final PageableController controller, final Viewer viewer,
			final IPageLoader<?> pageLoader,
			final IPageContentProvider pageContentProvider,
			final IPageLoaderHandler<PageableController> handler) {
		Object page = loadPageAndUpdateTotalElements(controller, pageLoader,
				pageContentProvider, handler);
		if (page != null) {
			List<?> content = pageContentProvider.getPaginatedList(page);
			if (content != null) {
				// Refresh the viewer with the paginated list.
				viewer.setInput(content);
			}
		}
	}

	/**
	 * Create {@link IPageChangedListener} with pagination "replace" strategy.
	 * See
	 * {@link PageLoaderStrategyHelper#loadPageAndReplaceItems(PageableController, Viewer, IPageLoader)}
	 * for more information.
	 * 
	 * @param controller
	 *            the controller to use to load paginated list and update the
	 *            total elements.
	 * @param viewer
	 *            the viewer to refresh with new paginated list.
	 * @param pageLoader
	 *            the page loader used to load paginated list.
	 * @pageContentProvider the page content provider to retrieves total
	 *                      elements+paginated list from the page result
	 *                      structure returned by the pageLoader.
	 * @param handler
	 *            the page loader handler to observe before/after page loading
	 *            process. If null no observation is done.
	 * @return
	 */
	public static IPageChangedListener createLoadPageAndReplaceItemsListener(
			final PageableController controller, final StructuredViewer viewer,
			final IPageLoader<?> pageLoader,
			final IPageContentProvider pageContentProvider,
			final IPageLoaderHandler<PageableController> handler) {
		return new PageChangedAdapter() {
			@Override
			public void pageIndexChanged(int oldPageIndex, int newPageIndex,
					PageableController controller) {
				PageLoaderStrategyHelper.loadPageAndReplaceItems(controller,
						viewer, pageLoader, pageContentProvider, handler);
			}

			@Override
			public void pageSizeChanged(int oldPageSize, int newPageSize,
					PageableController paginationController) {
				controller.reset();
			}

			@Override
			public void sortChanged(String oldPopertyName, String propertyName,
					int oldSortDirection, int sortDirection,
					PageableController controller) {
				controller.reset();
			}
		};
	}

	// ---------------- Add strategy

	// ---------------- For table

	/**
	 * This method loads the paginated list by using the given page loader
	 * {@link IPageLoader} and information about pagination from the given
	 * controller {@link PageableController}. After loading paginated list
	 * returned in a pagination structure {@link PageResult}, this method :
	 * 
	 * <ul>
	 * <li>update the total elements of the given controller
	 * {@link PageableController}</li>
	 * <li>refresh the given {@link Viewer} by replacing data with the new
	 * paginated list.</li>
	 * </ul>
	 * 
	 * @param controller
	 *            the controller to use to load paginated list and update the
	 *            total elements.
	 * @param viewer
	 *            the viewer to refresh with new paginated list.
	 * @param pageLoader
	 *            the page loader used to load paginated list.
	 * @pageContentProvider the page content provider to retrieves total
	 *                      elements+paginated list from the page result
	 *                      structure returned by the pageLoader.
	 * @param handler
	 *            the page loader handler to observe before/after page loading
	 *            process. If null no observation is done.
	 */
	public static void loadPageAndAddItems(final PageableController controller,
			final TableViewer viewer, final IPageLoader<?> pageLoader,
			final IPageContentProvider pageContentProvider,
			final IPageLoaderHandler<PageableController> handler) {
		Object page = loadPageAndUpdateTotalElements(controller, pageLoader,
				pageContentProvider, handler);
		if (page != null) {
			List<?> content = pageContentProvider.getPaginatedList(page);
			if (content != null && !content.isEmpty()) {
				viewer.add(content.toArray());
				int count = viewer.getTable().getItemCount();
				if (count > 0) {
					TableItem item = viewer.getTable().getItem(count - 1);
					item.setData(LazyItemsSelectionListener.LAST_ITEM_LOADED,
							true);
				}
			}
		}
	}

	/**
	 * Create {@link IPageChangedListener} with pagination "add" strategy. See
	 * {@link PageLoaderStrategyHelper#loadPageAndAddItems(PageableController, TableViewer, IPageLoader)}
	 * for more information.
	 * 
	 * @param controller
	 *            the controller to use to load paginated list and update the
	 *            total elements.
	 * @param viewer
	 *            the viewer to refresh with new paginated list.
	 * @param pageLoader
	 *            the page loader used to load paginated list.
	 * @pageContentProvider the page content provider to retrieves total
	 *                      elements+paginated list from the page result
	 *                      structure returned by the pageLoader.
	 * @param handler
	 *            the page loader handler to observe before/after page loading
	 *            process. If null no observation is done.
	 * 
	 * @return
	 */
	public static IPageChangedListener createLoadPageAndAddItemsListener(
			final PageableController controller, final TableViewer viewer,
			final IPageLoader<?> pageLoader,
			final IPageLoaderHandler<PageableController> handler) {
		return createLoadPageAndAddItemsListener(controller, viewer,
				pageLoader, PageResultContentProvider.getInstance(), handler);
	}

	/**
	 * Create {@link IPageChangedListener} with pagination "add" strategy. See
	 * {@link PageLoaderStrategyHelper#loadPageAndAddItems(PageableController, TableViewer, IPageLoader)}
	 * for more information.
	 * 
	 * @param controller
	 *            the controller to use to load paginated list and update the
	 *            total elements.
	 * @param viewer
	 *            the viewer to refresh with new paginated list.
	 * @param pageLoader
	 *            the page loader used to load paginated list.
	 * @pageContentProvider the page content provider to retrieves total
	 *                      elements+paginated list from the page result
	 *                      structure returned by the pageLoader.
	 * @param handler
	 *            the page loader handler to observe before/after page loading
	 *            process. If null no observation is done.
	 * 
	 * @return
	 */
	public static IPageChangedListener createLoadPageAndAddItemsListener(
			final PageableController controller, final TableViewer viewer,
			final IPageLoader<?> pageLoader,
			final IPageContentProvider pageContentProvider,
			final IPageLoaderHandler<PageableController> handler) {
		return new PageChangedAdapter() {
			@Override
			public void pageIndexChanged(int oldPageIndex, int newPageIndex,
					PageableController controller) {
				PageLoaderStrategyHelper.loadPageAndAddItems(controller,
						viewer, pageLoader, pageContentProvider, handler);
			}

			@Override
			public void pageSizeChanged(int oldPageSize, int newPageSize,
					PageableController paginationController) {
				controller.reset();
			}

			@Override
			public void sortChanged(String oldPopertyName, String propertyName,
					int oldSortDirection, int sortDirection,
					PageableController controller) {
				controller.reset();
			}
		};
	}

	// ---------------- For tree

	/**
	 * This method loads the paginated list by using the given page loader
	 * {@link IPageLoader} and information about pagination from the given
	 * controller {@link PageableController}. After loading paginated list
	 * returned in a pagination structure {@link PageResult}, this method :
	 * 
	 * <ul>
	 * <li>update the total elements of the given controller
	 * {@link PageableController}</li>
	 * <li>refresh the given {@link Viewer} by replacing data with the new
	 * paginated list.</li>
	 * </ul>
	 * 
	 * @param controller
	 *            the controller to use to load paginated list and update the
	 *            total elements.
	 * @param viewer
	 *            the viewer to refresh with new paginated list.
	 * @param pageLoader
	 *            the page loader used to load paginated list.
	 * @pageContentProvider the page content provider to retrieves total
	 *                      elements+paginated list from the page result
	 *                      structure returned by the pageLoader.
	 * @param handler
	 *            the page loader handler to observe before/after page loading
	 *            process. If null no observation is done.
	 */
	public static void loadPageAndAddItems(final PageableController controller,
			final Object parentElementOrTreePath, final TreeViewer viewer,
			final IPageLoader<?> pageLoader,
			final IPageContentProvider pageContentProvider,
			final IPageLoaderHandler<PageableController> handler) {
		Object page = loadPageAndUpdateTotalElements(controller, pageLoader,
				pageContentProvider, handler);
		if (page != null) {
			List<?> content = pageContentProvider.getPaginatedList(page);
			if (content != null && !content.isEmpty()) {
				viewer.add(parentElementOrTreePath, content.toArray());
				int count = viewer.getTree().getItemCount();
				if (count > 0) {
					TreeItem item = viewer.getTree().getItem(count - 1);
					item.setData(LazyItemsSelectionListener.LAST_ITEM_LOADED,
							true);
				}
			}
		}
	}

	/**
	 * Create {@link IPageChangedListener} with pagination "add" strategy. See
	 * {@link PageLoaderStrategyHelper#loadPageAndAddItems(PageableController, TableViewer, IPageLoader)}
	 * for more information.
	 * 
	 * @param controller
	 *            the controller to use to load paginated list and update the
	 *            total elements.
	 * @param viewer
	 *            the viewer to refresh with new paginated list.
	 * @param pageLoader
	 *            the page loader used to load paginated list.
	 * @pageContentProvider the page content provider to retrieves total
	 *                      elements+paginated list from the page result
	 *                      structure returned by the pageLoader.
	 * @param handler
	 *            the page loader handler to observe before/after page loading
	 *            process. If null no observation is done.
	 * 
	 * @return
	 */
	public static IPageChangedListener createLoadPageAndAddItemsListener(
			final PageableController controller,
			final Object parentElementOrTreePath, final TreeViewer viewer,
			final IPageLoader<?> pageLoader,
			final IPageContentProvider pageContentProvider,
			final IPageLoaderHandler<PageableController> handler) {
		return new PageChangedAdapter() {
			@Override
			public void pageIndexChanged(int oldPageIndex, int newPageIndex,
					PageableController controller) {
				PageLoaderStrategyHelper.loadPageAndAddItems(controller,
						parentElementOrTreePath, viewer, pageLoader,
						pageContentProvider, handler);
			}

			@Override
			public void pageSizeChanged(int oldPageSize, int newPageSize,
					PageableController paginationController) {
				controller.reset();
			}

			@Override
			public void sortChanged(String oldPopertyName, String propertyName,
					int oldSortDirection, int sortDirection,
					PageableController controller) {
				controller.reset();
			}
		};
	}

	// ---------------- For list view

	/**
	 * This method loads the paginated list by using the given page loader
	 * {@link IPageLoader} and information about pagination from the given
	 * controller {@link PageableController}. After loading paginated list
	 * returned in a pagination structure {@link PageResult}, this method :
	 * 
	 * <ul>
	 * <li>update the total elements of the given controller
	 * {@link PageableController}</li>
	 * <li>refresh the given {@link Viewer} by replacing data with the new
	 * paginated list.</li>
	 * </ul>
	 * 
	 * @param controller
	 *            the controller to use to load paginated list and update the
	 *            total elements.
	 * @param viewer
	 *            the viewer to refresh with new paginated list.
	 * @param pageLoader
	 *            the page loader used to load paginated list.
	 * @pageContentProvider the page content provider to retrieves total
	 *                      elements+paginated list from the page result
	 *                      structure returned by the pageLoader.
	 * @param handler
	 *            the page loader handler to observe before/after page loading
	 *            process. If null no observation is done.
	 */
	public static void loadPageAndAddItems(final PageableController controller,
			final AbstractListViewer viewer, final IPageLoader<?> pageLoader,
			final IPageContentProvider pageContentProvider,
			final IPageLoaderHandler<PageableController> handler) {
		Object page = loadPageAndUpdateTotalElements(controller, pageLoader,
				pageContentProvider, handler);
		List<?> content = pageContentProvider.getPaginatedList(page);
		if (content != null && !content.isEmpty()) {
			viewer.add(content.toArray());
		}
	}

	/**
	 * Create {@link IPageChangedListener} with pagination "add" strategy. See
	 * {@link PageLoaderStrategyHelper#loadPageAndAddItems(PageableController, TableViewer, IPageLoader)}
	 * for more information.
	 * 
	 * @param controller
	 *            the controller to use to load paginated list and update the
	 *            total elements.
	 * @param viewer
	 *            the viewer to refresh with new paginated list.
	 * @param pageLoader
	 *            the page loader used to load paginated list.
	 * @pageContentProvider the page content provider to retrieves total
	 *                      elements+paginated list from the page result
	 *                      structure returned by the pageLoader.
	 * @param handler
	 *            the page loader handler to observe before/after page loading
	 *            process. If null no observation is done.
	 * @return
	 */
	public static IPageChangedListener createLoadPageAndAddItemsListener(
			final PageableController controller,
			final AbstractListViewer viewer, final IPageLoader<?> pageLoader,
			final IPageContentProvider pageContentProvider,
			final IPageLoaderHandler<PageableController> handler) {
		return new PageChangedAdapter() {
			@Override
			public void pageIndexChanged(int oldPageIndex, int newPageIndex,
					PageableController controller) {
				PageLoaderStrategyHelper.loadPageAndAddItems(controller,
						viewer, pageLoader, pageContentProvider, handler);
			}

			@Override
			public void pageSizeChanged(int oldPageSize, int newPageSize,
					PageableController paginationController) {
				controller.reset();
			}

			@Override
			public void sortChanged(String oldPopertyName, String propertyName,
					int oldSortDirection, int sortDirection,
					PageableController controller) {
				controller.reset();
			}
		};
	}

	// ---------------- Utilities methods

	/**
	 * Load the paginated list and update the total element of the given
	 * controller.
	 * 
	 * @param controller
	 *            the controller to use to load paginated list and update the
	 *            total elements.
	 * @param pageLoader
	 *            the page loader used to load paginated list.
	 * @pageContentProvider the page content provider to retrieves total
	 *                      elements+paginated list from the page result
	 *                      structure returned by the pageLoader.
	 * @param handler
	 *            the page loader handler to observe before/after page loading
	 *            process. If null no observation is done.
	 * 
	 * @return the pagination {@link PageResult}.
	 */
	public static Object loadPageAndUpdateTotalElements(
			final PageableController controller,
			final IPageLoader<?> pageLoader,
			final IPageContentProvider pageContentProvider,
			final IPageLoaderHandler<PageableController> handler) {
		// Load the paginated list.
		Object page = null;
		if (handler == null) {
			page = loadPage(pageLoader, controller);
		} else {
			handler.onBeforePageLoad(controller);
			try {
				page = loadPage(pageLoader, controller);
				handler.onAfterPageLoad(controller, null);
			} catch (Throwable e) {
				boolean stop = handler.onAfterPageLoad(controller, e);
				if (stop) {
					return null;
				}
				if (e instanceof RuntimeException) {
					throw (RuntimeException) e;
				}
				throw new RuntimeException(e);
			}
		}
		// Update the total elements of the controller.
		controller.setTotalElements(pageContentProvider.getTotalElements(page));
		return page;
	}

	/**
	 * Load the paginated list.
	 * 
	 * @param pageLoader
	 *            the page loader used to load paginated list.
	 * @param pageable
	 *            the pagination {@link Pageable}.
	 * @return the pagination {@link PageResult}.
	 */
	public static Object loadPage(IPageLoader<?> pageLoader,
			PageableController controller) {
		if (pageLoader == null) {
			throw new NullPointerException("PageLoader cannot be null!");
		}
		return pageLoader.loadPage(controller);
	}
}
