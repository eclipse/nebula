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

package org.eclipse.nebula.widgets.timeline.listeners;

import org.eclipse.nebula.widgets.timeline.TimelineComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;

public class TimelineScaler implements MouseWheelListener {

	private final TimelineComposite fTimelineComposite;

	public TimelineScaler(TimelineComposite timelineComposite) {
		fTimelineComposite = timelineComposite;
	}

	@Override
	public void mouseScrolled(MouseEvent e) {
		if (e.count > 0)
			fTimelineComposite.getRootFigure().zoomIn(e.x);
		else
			fTimelineComposite.getRootFigure().zoomOut(e.x);
	}
}
