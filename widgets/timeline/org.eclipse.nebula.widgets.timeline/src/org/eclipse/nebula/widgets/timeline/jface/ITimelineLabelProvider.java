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

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.nebula.widgets.timeline.ICursor;
import org.eclipse.nebula.widgets.timeline.ITimelineEvent;
import org.eclipse.nebula.widgets.timeline.Timing;

public interface ITimelineLabelProvider extends ILabelProvider {

	/**
	 * Get event timings. Timings are defined for {@link ICursor} ans {@link ITimelineEvent} objects. For all other objects the return value is ignored.
	 *
	 * @param element
	 *            element to get timings for
	 * @return timings of event or cursor in elementary time units (nanoseconds)
	 */
	Timing getTimings(Object element);
}
