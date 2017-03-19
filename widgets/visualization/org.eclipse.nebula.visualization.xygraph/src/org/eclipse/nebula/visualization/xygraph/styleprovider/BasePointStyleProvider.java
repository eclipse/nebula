/*******************************************************************************
 * Copyright (c) 2016 Bernhard Wedl and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bernhard Wedl - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.visualization.xygraph.styleprovider;

import org.eclipse.nebula.visualization.xygraph.dataprovider.ISample;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.PointStyle;
import org.eclipse.swt.graphics.Color;

public abstract class BasePointStyleProvider implements IPointStyleProvider {

	@Override
	public Color getPointColor(ISample sample, Trace trace) {
		return trace.getTraceColor();
	}

	@Override
	public int getPointSize(ISample sample, Trace trace) {
		return trace.getPointSize();
	}

	@Override
	public PointStyle getPointStyle(ISample sample, Trace trace) {
		return trace.getPointStyle();
	}

}
