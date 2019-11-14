/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.nebula.visualization.internal.xygraph.undo;

import org.eclipse.nebula.visualization.xygraph.figures.Axis;
import org.eclipse.nebula.visualization.xygraph.linearscale.Range;

/**
 * The undo command for panning or zooming one axis.
 * 
 * @author Xihui Chen
 * @author Kay Kasemir (changed from AxisPanningCommand)
 */
public class AxisPanOrZoomCommand extends SaveStateCommand {
	final private Axis axis;

	final private Range beforeRange;

	private Range afterRange;

	public AxisPanOrZoomCommand(final String name, final Axis axis) {
		super(name);
		this.axis = axis;
		beforeRange = axis.getRange();
	}

	public void redo() {
		axis.setRange(afterRange);
	}

	public void undo() {
		axis.setRange(beforeRange);
	}

	@Override
	public void saveState() {
		afterRange = axis.getRange();
	}
}
