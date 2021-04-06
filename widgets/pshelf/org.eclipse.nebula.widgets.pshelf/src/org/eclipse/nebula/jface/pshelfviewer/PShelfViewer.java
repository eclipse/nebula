/*******************************************************************************
 * Copyright (c) 2007 Richard Michalsky.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors :
 * kralikX@gmail.com (Richard Michalsky) - initial API and implementation
 * laurent.caron@gmail.com (Laurent CARON) - code cleaning and finish implementation
 *******************************************************************************/
package org.eclipse.nebula.jface.pshelfviewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.opal.commons.ReflectionUtils;
import org.eclipse.nebula.widgets.pshelf.PShelf;
import org.eclipse.nebula.widgets.pshelf.PShelfItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * Viewer for PShelf widget.
 *
 * Uses label provider and content provider (which must implement {@link ITreeContentProvider})
 * to populate the shelves and {@link IShelfViewerFactory} instance to lazily create nested viewers.
 * Content provider uses top level of the input elements hierarchy to get shelf labels and icons
 * and passes <b>children</b> of each shelf item as input to the nested viewer.
 *
 * Also allows transfering selection between individual viewers - useful when viewers show the same
 * input in different arrangements.
 */
/**
 * 
 */
public class PShelfViewer extends StructuredViewer {
	private PShelf pshelf;
	private final IShelfViewerFactory viewerFactory;

	/** Viewer --> PShelfItem map */
	private Map<PShelfItem, Viewer> viewersMap = new HashMap<>();

	private final ArrayList<?> EMPTY_SELECTION_LIST = new ArrayList<>(0);
	protected List<?> lastFiredSelection = EMPTY_SELECTION_LIST;
	private boolean transferSelection;
	private ISelection transferredSelection = StructuredSelection.EMPTY;

	/**
	 * Create an instance of this viewer
	 * 
	 * @param container composite that holds the PShelf widget
	 * @param style style of the PShelf
	 * @param viewerFactory associated view factory
	 */
	public PShelfViewer(Composite container, int style, IShelfViewerFactory viewerFactory) {
		this.viewerFactory = viewerFactory;
		pshelf = new PShelf(container, style);
		hookControl(pshelf);
	}

	/**
	 * @see org.eclipse.jface.viewers.Viewer#getControl()
	 */
	public Control getControl() {
		return getPShelf();
	}

	/**
	 * Returns the underlying PShelf Control.
	 *
	 * @return PShelf control.
	 */
	public PShelf getPShelf() {
		return pshelf;
	}

	/**
	 * Transfer selection behavior. See {@link #setTransferSelection(boolean)}.
	 *
	 * @return Transfer selection behavior
	 */
	public boolean isTransferSelection() {
		return transferSelection;
	}

	/**
	 * Sets transfer selection behavior when another PShelf item is revealed.
	 *
	 * When set to <code>true</code>, PShelfViewer tries to set current
	 * selection to newly revealed viewer. This is helpful when
	 * individual viewers show the same model in different arrangement.
	 *
	 * <code>False</code> (the default) causes each viewer to retain its own
	 * selection.
	 *
	 * @param transferSelection
	 */
	public void setTransferSelection(boolean transferSelection) {
		this.transferSelection = transferSelection;
	}

	/**
	 * @see org.eclipse.jface.viewers.ContentViewer#labelProviderChanged()
	 */
	protected void labelProviderChanged() {
		Assert.isNotNull(getLabelProvider());
		if (!(getLabelProvider() instanceof ILabelProvider))
			throw new IllegalArgumentException("Label provider must implement ILabelProvider" + ", got " + getLabelProvider() == null ? "null" : getLabelProvider().getClass().toString());

		if (pshelf != null) {
			PShelfItem[] shelfItems = pshelf.getItems();

			for (int i = 0; i < shelfItems.length; i++) {
				PShelfItem item = shelfItems[i];

				// re-query texts and images for pshelf items
				ILabelProvider lp = (ILabelProvider) getLabelProvider();
				item.setText(lp.getText(item.getData()));
				item.setImage(lp.getImage(item.getData()));

				// change provider for sub-viewers
				Viewer viewer = getViewerForItem(item);
				ContentViewer contentViewer = (ContentViewer) viewer;
				if (contentViewer != null)
					contentViewer.setLabelProvider(lp);
			}
		}

		// refresh in super impl
		super.labelProviderChanged();
	}

	/**
	 * Returns a viewer, whose widget is embedded in <code>item</code>.
	 *
	 * Viewer is the one previously created by {@link IShelfViewerFactory}
	 * passed to constructor of PShelfViewer.
	 *
	 * @param item
	 * @return Viewer or <code>null</code> if <code>IShelfViewerFactory</code>
	 *         didn't create any viewer for the widget.
	 */
	public Viewer getViewerForItem(PShelfItem item) {
		return (Viewer) viewersMap.get(item);
	}

	/**
	 * @see org.eclipse.jface.viewers.Viewer#inputChanged(java.lang.Object, java.lang.Object)
	 */
	protected void inputChanged(Object input, Object oldInput) {
		preservingSelection(() -> {
			pshelf.setRedraw(false);
			removeAll();
			pshelf.setData(getRoot());
			internalInitializeWidget();
			pshelf.setRedraw(true);
		});
	}

	protected void internalInitializeWidget() {
		// create items, assuming there are no items yet
		if (pshelf.getItems().length > 0)
			throw new IllegalStateException("Cannot initialize nonempty pshelf widget.");

		ITreeContentProvider cp = (ITreeContentProvider) getContentProvider();
		Object[] elements = cp.getElements(getInput());
		for (int i = 0; i < elements.length; i++) {
			Object modelNode = elements[i];
			PShelfItem item;

			item = new PShelfItem(pshelf, SWT.NONE);
			item.setData(modelNode);
			item.getBody().setLayout(new FillLayout());

			// re-query texts and images for pshelf items
			ILabelProvider lp = (ILabelProvider) getLabelProvider();
			item.setText(lp.getText(modelNode));

			// create viewer for the item and initialize it
			Viewer contentViewer = viewerFactory.createViewerForContent(item.getBody(), modelNode);

			item.setImage(lp.getImage(modelNode));

			if (contentViewer != null) {
				viewersMap.put(item, contentViewer);
				contentViewer.setInput(cp.getChildren(modelNode));
				contentViewer.addSelectionChangedListener(sharedViewersListener);
			}

		}

		pshelf.addSelectionListener(pshelfSelectionListener);
		pshelfSelectionListener.widgetSelected(null); // initial selection notification
	}

	private SelectionListener pshelfSelectionListener = new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
			if (transferSelection) {
				preservingSelection(() -> {
					setSelection(transferredSelection);
				});
			}

			SelectionChangedEvent event = new SelectionChangedEvent(PShelfViewer.this, new StructuredSelection(getSelectionFromWidget()));
			fireSelectionChanged(event);
		}
	};

	// all content viewers share this single listener
	private ISelectionChangedListener sharedViewersListener = event -> {
		// event cannot be re-fired, this viewer must claim itself selection
		// provider
		SelectionChangedEvent newEvent = new SelectionChangedEvent(PShelfViewer.this, event.getSelection());
		fireSelectionChanged(newEvent);
		transferredSelection = event.getSelection();
	};

	protected void fireSelectionChanged(SelectionChangedEvent event) {
		List<?> selectionList = ((IStructuredSelection) event.getSelection()).toList();
		if (selectionList.equals(lastFiredSelection))
			return; // don't fire the same selection again

		super.fireSelectionChanged(event);
		lastFiredSelection = selectionList;
	}

	/**
	 * Removes all shelves.
	 */
	protected void removeAll() {
		pshelf.removeAll();
		viewersMap.clear();
	}

	/**
	 * @see org.eclipse.jface.viewers.StructuredViewer#doFindInputItem(java.lang.Object)
	 */
	protected Widget doFindInputItem(Object element) {
		Viewer viewer = getViewerForItem(pshelf.getSelection());
		if (viewer instanceof StructuredViewer) {
			return (Widget) ReflectionUtils.callMethod(viewer, "doFindInputItem", element);
		}
		return null;
	}

	protected Widget doFindItem(Object element) {
		Viewer viewer = getViewerForItem(pshelf.getSelection());
		if (viewer instanceof StructuredViewer) {
			return (Widget) ReflectionUtils.callMethod(viewer, "doFindItem", element);
		}
		return null;
	}

	/** 
	 * @see org.eclipse.jface.viewers.StructuredViewer#doUpdateItem(org.eclipse.swt.widgets.Widget, java.lang.Object, boolean)
	 */
	protected void doUpdateItem(Widget item, Object element, boolean fullMap) {
		Viewer viewer = getViewerForItem(pshelf.getSelection());
		if (viewer instanceof StructuredViewer) {
			ReflectionUtils.callMethod(viewer, "doUpdateItem", item, element, fullMap);
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.StructuredViewer#getSelectionFromWidget()
	 */
	@SuppressWarnings("rawtypes")
	protected List getSelectionFromWidget() {
		PShelfItem item = pshelf.getSelection();
		List retList = EMPTY_SELECTION_LIST;

		Viewer viewer = getViewerForItem(item);
		if (viewer == null)
			return retList;

		// cannot get a list of items when viewer doesn't return structured selection
		IStructuredSelection selection = null;
		if (viewer.getSelection() instanceof IStructuredSelection)
			selection = (IStructuredSelection) viewer.getSelection();
		if (selection != null)
			retList = selection.toList();
		return retList;
	}

	/**
	 * @see org.eclipse.jface.viewers.StructuredViewer#internalRefresh(java.lang.Object)
	 */
	protected void internalRefresh(Object element) {
		pshelf.redraw();
	}

	/**
	 * @see org.eclipse.jface.viewers.StructuredViewer#reveal(java.lang.Object)
	 */
	public void reveal(Object element) {
		Viewer viewer = getViewerForItem(pshelf.getSelection());
		if (viewer instanceof StructuredViewer) {
			((StructuredViewer) viewer).reveal(element);
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.StructuredViewer#setSelectionToWidget(java.util.List, boolean)
	 */
	@SuppressWarnings("rawtypes")
	protected void setSelectionToWidget(List l, boolean reveal) {
		if (l == null) // fail-fast
			throw new NullPointerException();
		Viewer viewer = getViewerForItem(pshelf.getSelection());
		if (viewer != null)
			viewer.setSelection(new StructuredSelection(l), reveal);
	}

	/**
	 * @see org.eclipse.jface.viewers.StructuredViewer#assertContentProviderType(org.eclipse.jface.viewers.IContentProvider)
	 */
	protected void assertContentProviderType(IContentProvider provider) {
		if (!(provider instanceof ITreeContentProvider))
			throw new IllegalArgumentException("Content provider for PShelf must implement ITreeContentProvider!");
		super.assertContentProviderType(provider);
	}
}
