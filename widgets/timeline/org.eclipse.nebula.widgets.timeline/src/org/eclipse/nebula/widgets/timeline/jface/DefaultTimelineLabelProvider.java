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

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IToolTipProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.nebula.widgets.timeline.IColored;
import org.eclipse.nebula.widgets.timeline.ICursor;
import org.eclipse.nebula.widgets.timeline.ITimelineEvent;
import org.eclipse.nebula.widgets.timeline.Timing;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class DefaultTimelineLabelProvider extends LabelProvider implements ITimelineLabelProvider, IToolTipProvider, IColorProvider {

	private final ResourceManager fResourceManager;

	public DefaultTimelineLabelProvider(ResourceManager resourceManager) {
		fResourceManager = resourceManager;
	}

	@Override
	public String getToolTipText(Object element) {
		if (element instanceof ITimelineEvent)
			return ((ITimelineEvent) element).getMessage();

		return null;
	}

	@Override
	public Timing getTimings(Object element) {
		if (element instanceof ITimelineEvent)
			return new Timing(((ITimelineEvent) element).getStartTimestamp(), ((ITimelineEvent) element).getDuration());

		if (element instanceof ICursor)
			return new Timing(((ICursor) element).getTimestamp());

		return null;
	}

	@Override
	public Color getForeground(Object element) {
		if (element instanceof IColored) {
			final RGB rgb = ((IColored) element).getRgb();
			if (rgb != null)
				return fResourceManager.createColor(rgb);
		}

		return null;
	}

	@Override
	public Color getBackground(Object element) {
		return null;
	}
}
