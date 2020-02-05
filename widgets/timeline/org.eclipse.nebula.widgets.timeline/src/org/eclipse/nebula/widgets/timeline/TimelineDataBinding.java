/*******************************************************************************
 * Copyright (c) 2020 Christian Pontesegger and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Christian Pontesegger - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.timeline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.timeline.figures.detail.cursor.CursorFigure;
import org.eclipse.nebula.widgets.timeline.jface.TimelineViewer;
import org.eclipse.nebula.widgets.timeline.listeners.ICursorListener;
import org.eclipse.ui.progress.UIJob;

/**
 * Data binding that automatically updates the viewer on model updates. Cursor operations and selections on the view are also stored in the model. The model
 * needs to be an instance of the ITimeline EMF model to correctly receive notifications.
 */
public class TimelineDataBinding extends AdapterImpl implements ICursorListener, ISelectionChangedListener {

	/** Execution delay in [ms]. */
	private static final long DEFAULT_DISPLAY_UPDATE_DELAY = 300;

	private final TimelineViewer fViewer;
	private final ITimeline fModel;

	private ViewerRefreshJob fViewerRefresher = null;

	private volatile boolean fIgnoreModelChanges = false;
	private final long fDisplayUpdateDelay;

	/**
	 * @param timelineViewer
	 * @param model
	 * @param displayUpdateDelay
	 */
	public TimelineDataBinding(TimelineViewer viewer, ITimeline model, long displayUpdateDelay) {
		fViewer = viewer;
		fModel = model;
		fDisplayUpdateDelay = displayUpdateDelay;

		fViewer.getControl().getRootFigure().addCursorListener(this);
		fViewer.addSelectionChangedListener(this);

		fModel.eAdapters().add(this);
	}

	public TimelineDataBinding(TimelineViewer viewer, ITimeline model) {
		this(viewer, model, DEFAULT_DISPLAY_UPDATE_DELAY);
	}

	@Override
	public synchronized void notifyCursorCreated(ICursor cursor, CursorFigure figure) {
		fIgnoreModelChanges = true;
		fModel.getCursors().add(cursor);
		fIgnoreModelChanges = false;
	}

	@Override
	public synchronized void notifyCursorDeleted(ICursor cursor) {
		fIgnoreModelChanges = true;
		fModel.getCursors().remove(cursor);
		fIgnoreModelChanges = false;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		fIgnoreModelChanges = true;

		if (event.getStructuredSelection().isEmpty())
			fModel.setSelectedEvent(null);

		else if (event.getStructuredSelection().getFirstElement() instanceof ITimelineEvent)
			fModel.setSelectedEvent((ITimelineEvent) event.getStructuredSelection().getFirstElement());

		fIgnoreModelChanges = false;
	}

	private synchronized void refreshElement(EObject element) {
		if (!fIgnoreModelChanges)
			getUIRefreshJob().addElementForRefresh(element);
	}

	private synchronized void updateElement(EObject element) {
		if (!fIgnoreModelChanges)
			getUIRefreshJob().addElementForUpdate(element);
	}

	private synchronized void updateSelection() {
		if (!fIgnoreModelChanges)
			getUIRefreshJob().forceSelectionUpdate();
	}

	private ViewerRefreshJob getUIRefreshJob() {
		if (fViewerRefresher == null)
			fViewerRefresher = new ViewerRefreshJob();

		return fViewerRefresher;
	}

	@Override
	public void notifyChanged(Notification msg) {
		if (!msg.isTouch()) {

			if (msg.getEventType() == Notification.ADD) {
				final Object value = msg.getNewValue();
				if (value instanceof Notifier)
					((Notifier) value).eAdapters().add(this);

				refreshElement((EObject) msg.getNotifier());

			} else if (msg.getEventType() == Notification.REMOVE) {
				final Object value = msg.getOldValue();
				if (value instanceof Notifier)
					((Notifier) value).eAdapters().remove(this);

				refreshElement((EObject) msg.getNotifier());

			} else if (msg.getEventType() == Notification.SET) {
				if (msg.getNotifier() instanceof ITimeline) {
					if (msg.getFeature().equals(ITimelinePackage.eINSTANCE.getTimeline_SelectedEvent()))
						updateSelection();
				}

				updateElement((EObject) msg.getNotifier());
			}
		}
	}

	private class ViewerRefreshJob extends UIJob {

		private final List<EObject> fElementsToRefresh = new ArrayList<>();
		private final List<EObject> fElementsToUpdate = new ArrayList<>();

		private volatile boolean fSelectionUpdate = false;

		public ViewerRefreshJob() {
			super("Refresh timeline control");

			setSystem(true);
		}

		public void forceSelectionUpdate() {
			fSelectionUpdate = true;
			schedule(fDisplayUpdateDelay);
		}

		public void addElementForUpdate(EObject element) {
			synchronized (fElementsToUpdate) {
				if (!fElementsToUpdate.contains(element)) {
					fElementsToUpdate.add(element);
					schedule(fDisplayUpdateDelay);
				}
			}
		}

		public void addElementForRefresh(EObject element) {
			synchronized (fElementsToRefresh) {
				if (!fElementsToRefresh.contains(element)) {
					if (!containsParent(element, fElementsToRefresh)) {
						fElementsToRefresh.add(element);
						schedule(fDisplayUpdateDelay);
					}
				}
			}
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			Collection<EObject> elementsToRefresh;
			synchronized (fElementsToRefresh) {
				elementsToRefresh = new ArrayList<>(fElementsToRefresh);
				fElementsToRefresh.clear();
			}

			Collection<EObject> elementsToUpdate;
			synchronized (fElementsToUpdate) {
				elementsToUpdate = new ArrayList<>(fElementsToUpdate);
				fElementsToUpdate.clear();
			}

			minimizeElements(elementsToRefresh, elementsToRefresh);
			minimizeElements(elementsToUpdate, elementsToRefresh);
			elementsToUpdate.removeAll(elementsToRefresh);

			for (final EObject element : elementsToRefresh) {
				if (monitor.isCanceled())
					return Status.CANCEL_STATUS;

				fViewer.refresh(element);
			}

			for (final EObject element : elementsToUpdate) {
				if (monitor.isCanceled())
					return Status.CANCEL_STATUS;

				fViewer.update(element, null);
			}

			if (fSelectionUpdate) {
				fSelectionUpdate = false;

				final ITimelineEvent selectedEvent = fModel.getSelectedEvent();
				fViewer.setSelection((selectedEvent != null) ? new StructuredSelection(selectedEvent) : StructuredSelection.EMPTY);
			}

			return Status.OK_STATUS;
		}

		/**
		 * Remove elements that are contained multiple times.
		 *
		 * @param collection
		 *            to minimize
		 * @param elements
		 *            to be refreshed
		 */
		private void minimizeElements(Collection<EObject> candidates, Collection<EObject> elementsToRefresh) {
			final List<EObject> copy = new ArrayList<>(candidates);

			while (!copy.isEmpty()) {
				final EObject candidate = copy.remove(0);
				if (containsParent(candidate, elementsToRefresh))
					candidates.remove(candidate);
			}
		}

		/**
		 * Check if an element or one of its ancestors is already contained in a given list of elements.
		 *
		 * @param element
		 *            element to add
		 * @param scheduledElements
		 *            elements already scheduled
		 * @return <code>true</code> when a parent of element is already contained in scheduledElements
		 */
		private boolean containsParent(EObject element, Collection<EObject> scheduledElements) {
			EObject parent = element.eContainer();
			while (parent != null) {
				if (scheduledElements.contains(parent))
					return true;

				parent = parent.eContainer();
			}

			return false;
		}
	}
}
