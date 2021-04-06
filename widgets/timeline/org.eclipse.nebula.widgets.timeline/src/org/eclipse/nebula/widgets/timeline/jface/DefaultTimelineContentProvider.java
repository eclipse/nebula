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

import org.eclipse.nebula.widgets.timeline.ILane;
import org.eclipse.nebula.widgets.timeline.ITimeline;
import org.eclipse.nebula.widgets.timeline.ITrack;

/**
 * Default content provider for timeline viewer. Expects the input to be of type {@link ITimeline}.
 */
public class DefaultTimelineContentProvider implements ITimelineContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		return getTracks(inputElement);
	}

	@Override
	public Object[] getTracks(Object input) {
		if (input instanceof ITimeline)
			return ((ITimeline) input).getTracks().toArray();

		return new Object[0];
	}

	@Override
	public Object[] getLanes(Object track) {
		return ((ITrack) track).getLanes().toArray();
	}

	@Override
	public Object[] getEvents(Object lane) {
		return ((ILane) lane).getTimeEvents().toArray();
	}

	@Override
	public Object[] getCursors(Object input) {
		if (input instanceof ITimeline)
			return ((ITimeline) input).getCursors().toArray();

		return new Object[0];
	}
}
