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

import org.eclipse.jface.viewers.IStructuredContentProvider;

/**
 * Content provider for {@link TimelineViewer}. The provider links between the model and the SWT component. As cursors may
 */
public interface ITimelineContentProvider extends IStructuredContentProvider {

	/**
	 * Get tracks for the provided input. Tracks contain lanes to display events.
	 *
	 * @param input
	 *            viewer input root element
	 * @return tracks to display
	 */
	Object[] getTracks(Object input);

	/**
	 * Get lanes for a given track. Lanes contain display events.
	 *
	 * @param track
	 *            track to get lanes for
	 * @return lanes to display
	 */
	Object[] getLanes(Object track);

	/**
	 * Get events for a given lane.
	 *
	 * @param lane
	 *            lane to fetch events for
	 * @return timing events
	 */
	Object[] getEvents(Object lane);

	/**
	 * Get cursors for the provided input
	 *
	 * @param input
	 *            viewer input root element
	 * @return cursors to display
	 */
	Object[] getCursors(Object input);
}
