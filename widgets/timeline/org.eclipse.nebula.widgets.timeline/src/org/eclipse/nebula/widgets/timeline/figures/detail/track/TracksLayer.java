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

package org.eclipse.nebula.widgets.timeline.figures.detail.track;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;

public class TracksLayer extends FreeformLayer {

	public TracksLayer() {
		final ToolbarLayout layout = new ToolbarLayout(false);
		layout.setStretchMinorAxis(true);
		setLayoutManager(layout);
	}

	@Override
	protected boolean useLocalCoordinates() {
		return true;
	}

	@Override
	protected void layout() {
		super.layout();

		RootFigure.getTimeViewDetails(this).setScreenWidth(getBounds().width());
	}
}
