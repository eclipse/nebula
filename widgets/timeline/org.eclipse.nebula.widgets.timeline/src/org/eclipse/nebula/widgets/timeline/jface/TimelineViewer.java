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

package org.eclipse.nebula.widgets.timeline.jface;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IToolTipProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.nebula.widgets.timeline.ICursor;
import org.eclipse.nebula.widgets.timeline.ITimelineEvent;
import org.eclipse.nebula.widgets.timeline.ITimelineFactory;
import org.eclipse.nebula.widgets.timeline.TimeBaseConverter;
import org.eclipse.nebula.widgets.timeline.TimelineComposite;
import org.eclipse.nebula.widgets.timeline.Timing;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.cursor.CursorFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.cursor.CursorLayer;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.TrackFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.TracksLayer;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.lane.EventFigure;
import org.eclipse.nebula.widgets.timeline.figures.detail.track.lane.LaneFigure;
import org.eclipse.nebula.widgets.timeline.figures.overview.OverviewCursorLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

public class TimelineViewer extends StructuredViewer {

	/**
	 * Convert a color into its color code.
	 *
	 * @param color
	 *            color to convert
	 * @return HTML color code, eg '#12557F'
	 */
	private static String toColorCode(Color color) {
		final RGB rgb = color.getRGB();

		return "#" + toHexValue(rgb.red) + toHexValue(rgb.green) + toHexValue(rgb.blue);
	}

	/**
	 * Convert a numeric value to a 2 digit hex value.
	 *
	 * @param value
	 * @return 2 digit hex value
	 */
	private static String toHexValue(int value) {
		final String result = Integer.toString(value, 16);
		return (result.length() == 2) ? result : "0" + result;
	}

	private final TimelineComposite fControl;

	private final ModelMap fElementToFigureMap = new ModelMap();

	/**
	 * Create a timeline viewer. The viewer will automatically populate input, a content provider and a label provider. When replacing the input, make sure to
	 * also replace content and label providers according to your used datatypes.
	 *
	 * @param parent
	 *            parent composite
	 * @param flags
	 *            SWT flags
	 */
	public TimelineViewer(Composite parent, int flags) {

		fControl = new TimelineComposite(parent, flags);

		setContentProvider(new DefaultTimelineContentProvider());
		setLabelProvider(new DefaultTimelineLabelProvider(fControl.getRootFigure().getResourceManager()));
		setInput(ITimelineFactory.eINSTANCE.createTimeline());
	}

	/**
	 * Create a timeline viewer. The viewer will automatically populate input, a content provider and a label provider. When replacing the input, make sure to
	 * also replace content and label providers according to your used datatypes.
	 *
	 * @param parent
	 *            parent composite
	 */
	public TimelineViewer(Composite parent) {
		this(parent, SWT.NONE);
	}

	/**
	 * Set the style provider. The style provider allows to customize the look and feel of the widget. By default a {@link DefaultTimelineStyleProvider} will be
	 * used.
	 *
	 * @param styleProvider
	 *            style provider to use or <code>null</code> to switch to the default style provider
	 */
	public void setStyleProvider(ITimelineStyleProvider styleProvider) {
		getControl().getRootFigure().setStyleProvider(styleProvider);
	}

	/**
	 * Get the current style provider
	 *
	 * @return style provider
	 */
	public ITimelineStyleProvider getStyleProvider() {
		return getControl().getRootFigure().getStyleProvider();
	}

	@Override
	protected void inputChanged(Object input, Object oldInput) {
		fElementToFigureMap.clear();
		registerFigure(input, getControl().getRootFigure());

		final ITimelineContentProvider contentProvider = getContentProvider();
		if (contentProvider != null)
			contentProvider.inputChanged(this, oldInput, input);

		super.inputChanged(input, oldInput);
	}

	/**
	 * Set the content provider for this viewer.
	 *
	 * @param provider
	 *            provider implementing {@link ITimelineContentProvider}
	 */
	@Override
	public void setContentProvider(IContentProvider provider) {
		if (!(provider instanceof ITimelineContentProvider))
			throw new IllegalArgumentException("Content provider needs to implement ITimelineContentProvider");

		super.setContentProvider(provider);
	}

	@Override
	public ITimelineContentProvider getContentProvider() {
		return (ITimelineContentProvider) super.getContentProvider();
	}

	/**
	 * Set the label provider for this viewer. Optional the lable provider may implement {@link IColorProvider} and {@link IToolTipProvider}.
	 *
	 * @param labelProvider
	 *            provider implementing {@link ITimelineLabelProvider}
	 */
	@Override
	public void setLabelProvider(IBaseLabelProvider labelProvider) {
		if (!(labelProvider instanceof ITimelineLabelProvider))
			throw new IllegalArgumentException("Label provider needs to implement ITimelineLabelProvider");

		super.setLabelProvider(labelProvider);
	}

	@Override
	public ITimelineLabelProvider getLabelProvider() {
		return (ITimelineLabelProvider) super.getLabelProvider();
	}

	@Override
	protected Widget doFindInputItem(Object element) {
		return null;
	}

	@Override
	protected Widget doFindItem(Object element) {
		return null;
	}

	private boolean isRootElement(Object element) {
		return getInput().equals(element);
	}

	private boolean isTrackElement(Object element) {
		return getTrackElements().contains(element);
	}

	private boolean isLaneElement(Object element) {
		return getLaneElements().contains(element);
	}

	private boolean isEventElement(Object element) {
		return geEventElements().contains(element);
	}

	private boolean isCursorElement(Object element) {
		return getCursorElements().contains(element);
	}

	private Collection<Object> getCursorElements() {
		return Arrays.asList(getContentProvider().getCursors(getInput()));
	}

	private Collection<Object> getTrackElements() {
		return Arrays.asList(getContentProvider().getTracks(getInput()));
	}

	private Collection<Object> getLaneElements() {
		final Collection<Object> lanes = new HashSet<>();
		for (final Object track : getTrackElements())
			lanes.addAll(Arrays.asList(getContentProvider().getLanes(track)));

		return lanes;
	}

	private Collection<Object> geEventElements() {
		final Collection<Object> events = new HashSet<>();
		for (final Object track : getLaneElements())
			events.addAll(Arrays.asList(getContentProvider().getEvents(track)));

		return events;
	}

	@Override
	public void update(Object element, String[] properties) {
		final IFigure figure = fElementToFigureMap.get(element);
		if (figure != null) {

			if (isCursorElement(element))
				getControl().getRootFigure().updateCursorFigure(figure, toCursor(element));

			else if (isTrackElement(element))
				getControl().getRootFigure().updateTrackFigure(figure, getLabelProvider().getText(element));

			else if (isEventElement(element))
				getControl().getRootFigure().updateEventFigure(figure, toEvent(element));

			else if (isLaneElement(element)) {
				if (getLabelProvider() instanceof IColorProvider) {
					// color update for all elements
					Color foreground = ((IColorProvider) getLabelProvider()).getForeground(element);
					if (foreground == null)
						foreground = getStyleProvider().getLaneColor();

					figure.setForegroundColor(foreground);

					internalRefresh(element);
				}
			}
		}
	}

	private ICursor toCursor(Object element) {
		if (element instanceof ICursor)
			return (ICursor) element;

		final Timing timings = getLabelProvider().getTimings(element);
		if (timings != null) {
			final ICursor cursor = ITimelineFactory.eINSTANCE.createCursor();
			cursor.setTimestamp((long) timings.getTimestamp());

			return cursor;
		}

		return null;
	}

	private ITimelineEvent toEvent(Object element) {
		if (element instanceof ITimelineEvent)
			return (ITimelineEvent) element;

		final Timing timings = getLabelProvider().getTimings(element);
		if (timings != null) {
			final ITimelineEvent event = ITimelineFactory.eINSTANCE.createTimelineEvent();
			event.setStartTimestamp((long) timings.getTimestamp());
			event.setDuration((long) timings.getDuration());

			final ITimelineLabelProvider labelProvider = getLabelProvider();
			event.setTitle(labelProvider.getText(element));

			if (labelProvider instanceof IToolTipProvider)
				event.setMessage(((IToolTipProvider) labelProvider).getToolTipText(element));

			if (labelProvider instanceof IColorProvider) {
				final Color color = ((IColorProvider) labelProvider).getForeground(element);
				if (color != null)
					event.setColorCode(toColorCode(color));
			}

			return event;
		}

		return null;
	}

	@Override
	protected void internalRefresh(Object element) {

		final IFigure figure = fElementToFigureMap.get(element);
		if (figure != null) {
			if (figure instanceof RootFigure) {
				unregisterModelElements(new HashSet<>(fElementToFigureMap.keySet()));
				registerFigure(getInput(), getControl().getRootFigure());
				((RootFigure) figure).clear();
				RootFigure.getTimeViewDetails(figure).resetEventArea();

				final TracksLayer tracksLayer = RootFigure.getFigure(figure, TracksLayer.class);
				for (final Object track : getContentProvider().getTracks(getInput())) {
					final TrackFigure trackFigure = ((RootFigure) figure).createTrackFigure(getLabelProvider().getText(track));

					tracksLayer.add(trackFigure);
					registerFigure(track, trackFigure);

					internalRefresh(track);
				}

				for (final Object cursorElement : getContentProvider().getCursors(getInput())) {
					final CursorFigure cursorFigure = ((RootFigure) figure).createCursorFigure(toCursor(cursorElement));
					registerFigure(cursorElement, cursorFigure);
				}

			} else if (figure instanceof TrackFigure) {
				unregisterFigures(figure.getChildren());
				((TrackFigure) figure).removeAll();

				final Object track = getModelElementFor(figure);
				for (final Object lane : getContentProvider().getLanes(track)) {
					final LaneFigure laneFigure = new LaneFigure(getStyleProvider());

					figure.add(laneFigure);
					fElementToFigureMap.put(lane, laneFigure);

					internalRefresh(lane);
				}

			} else if (figure instanceof LaneFigure) {
				unregisterFigures(figure.getChildren());
				for (final EventFigure eventFigure : ((LaneFigure) figure).getEventFigures())
					getControl().getRootFigure().deleteEventFigure(eventFigure);

				final Object lane = getModelElementFor(figure);
				for (final Object event : getContentProvider().getEvents(lane)) {
					final EventFigure eventFigure = getControl().getRootFigure().createEventFigure(((LaneFigure) figure), toEvent(event));
					fElementToFigureMap.put(event, eventFigure);
				}

			} else if (figure instanceof CursorFigure) {

				if (Arrays.asList(getContentProvider().getCursors(getInput())).contains(element)) {
					// this cursor is still available in the model

				} else {
					// cursor got deleted from the model
					RootFigure.getFigure(figure, CursorLayer.class).remove(figure);
					unregisterModelElement(element);
				}

				final RootFigure rootFigure = RootFigure.getRootFigure(figure);
				RootFigure.getFigure(rootFigure, CursorLayer.class).revalidate();
				RootFigure.getFigure(rootFigure, OverviewCursorLayer.class).revalidate();
			}

		} else {
			// the object does not have a figure representation
			if (Arrays.asList(getContentProvider().getCursors(getInput())).contains(element)) {
				// this is a new cursor
				final CursorFigure cursorFigure = getControl().getRootFigure().createCursorFigure(toCursor(element));
				registerFigure(element, cursorFigure);
			}
		}
	}

	private Object getModelElementFor(IFigure figure) {
		return fElementToFigureMap.getKey(figure);
	}

	private void unregisterModelElements(Collection<?> modelElements) {
		for (final Object element : modelElements)
			unregisterModelElement(element);
	}

	private void unregisterModelElement(Object modelElement) {
		fElementToFigureMap.remove(modelElement);
	}

	private void unregisterFigures(Collection<?> figures) {
		for (final Object element : figures)
			fElementToFigureMap.removeValue(element);
	}

	private void registerFigure(Object modelElement, IFigure figure) {
		fElementToFigureMap.put(modelElement, figure);
	}

	public void createCursor(ICursor cursor) {
		getControl().getRootFigure().createCursorFigure(cursor);
	}

	public void deleteCursor(ICursor cursor) {
		getControl().getRootFigure().deleteCursor(cursor);
	}

	@Override
	public void reveal(Object element) {
		element = toEvent(element);
		if (element == null)
			element = toCursor(element);

		if (element instanceof ITimelineEvent) {
			final TimeBaseConverter timeViewDetails = RootFigure.getTimeViewDetails(getControl().getRootFigure());
			timeViewDetails.revealEvent(new Timing(((ITimelineEvent) element).getStartTimestamp(), ((ITimelineEvent) element).getDuration()));

		} else if (element instanceof ICursor) {
			final TimeBaseConverter timeViewDetails = RootFigure.getTimeViewDetails(getControl().getRootFigure());
			timeViewDetails.revealEvent(new Timing(((ICursor) element).getTimestamp(), 0));
		}
	}

	@Override
	protected List<Object> getSelectionFromWidget() {
		final EventFigure selectedFigure = getControl().getRootFigure().getSelection();
		if (selectedFigure != null) {
			final Object modelElement = fElementToFigureMap.getKey(selectedFigure);
			if (modelElement != null)
				return Arrays.asList(modelElement);
		}

		return Collections.emptyList();
	}

	@Override
	protected void setSelectionToWidget(List l, boolean reveal) {
		if (!l.isEmpty()) {
			final ITimelineEvent event = toEvent(l.get(0));
			if (event != null) {
				final IFigure eventFigure = fElementToFigureMap.get(event);
				if (eventFigure instanceof EventFigure)
					getControl().getRootFigure().setSelection((EventFigure) eventFigure);
			}

		} else
			getControl().getRootFigure().setSelection(null);
	}

	@Override
	public TimelineComposite getControl() {
		return fControl;
	}

	@Override
	protected void doUpdateItem(Widget item, Object element, boolean fullMap) {
		// not needed for this viewer
	}

	private class ModelMap extends HashMap<Object, IFigure> {

		private static final long serialVersionUID = 6568720330224087046L;

		private final Map<IFigure, Object> fReverseMap = new HashMap<>();

		@Override
		public IFigure put(Object key, IFigure value) {
			fReverseMap.put(value, key);

			return super.put(key, value);
		}

		public Object getKey(IFigure value) {
			return fReverseMap.get(value);
		}

		@Override
		public IFigure remove(Object key) {
			final IFigure value = super.remove(key);
			fReverseMap.remove(value);

			return value;
		}

		public IFigure removeValue(Object modelElement) {
			return remove(fReverseMap.get(modelElement));
		}
	}
}
