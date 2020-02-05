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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.nebula.widgets.timeline.figures.RootFigure;
import org.eclipse.nebula.widgets.timeline.listeners.TimelineScaler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class TimelineComposite extends Composite {

	private final RootFigure fRootFigure;
	private final LocalResourceManager fResourceManager;

	public TimelineComposite(Composite parent, int style) {
		super(parent, style);

		fResourceManager = new LocalResourceManager(JFaceResources.getResources(), this);

		final FillLayout layout = new FillLayout();
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		setLayout(layout);

		setBackground(ColorConstants.black);

		final Canvas canvas = new Canvas(this, SWT.DOUBLE_BUFFERED);
		canvas.setBackground(ColorConstants.black);
		final LightweightSystem lightWeightSystem = new LightweightSystem(canvas);

		fRootFigure = new RootFigure(fResourceManager);
		fRootFigure.setFont(parent.getFont());
		lightWeightSystem.setContents(fRootFigure);

		// draw2d does not directly support mouseWheelEvents, so register on canvas
		canvas.addMouseWheelListener(new TimelineScaler(this));
	}

	public RootFigure getRootFigure() {
		return fRootFigure;
	}

	@Override
	public void dispose() {
		fRootFigure.getStyleProvider().dispose();
		fResourceManager.dispose();

		super.dispose();
	}
}
