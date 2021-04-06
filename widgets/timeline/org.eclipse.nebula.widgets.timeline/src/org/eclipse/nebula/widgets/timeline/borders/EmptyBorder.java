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

package org.eclipse.nebula.widgets.timeline.borders;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;

public class EmptyBorder extends AbstractBorder {

	private final Insets fInsets;

	public EmptyBorder(Insets insets) {
		fInsets = insets;
	}

	public EmptyBorder(int indent) {
		this(new Insets(indent));
	}

	@Override
	public Insets getInsets(IFigure figure) {
		return fInsets;
	}

	@Override
	public void paint(IFigure figure, Graphics graphics, Insets insets) {
	}
}
