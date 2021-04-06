/*******************************************************************************
 * Copyright (c) 2016 Bernhard Wedl and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Bernhard Wedl - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.visualization.xygraph.styleprovider;

import org.eclipse.nebula.visualization.xygraph.dataprovider.IMetaData;
import org.eclipse.nebula.visualization.xygraph.dataprovider.ISample;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.graphics.Color;

/**
 * Example StyleProvider for boolean elements...
 * 
 * if SampleObject is true -> color is green <br />
 * if SampleObject is true -> color is red
 */
public class BooleanStyleProvider extends BasePointStyleProvider {

	@Override
	public Color getPointColor(ISample sample, Trace trace) {

		if ((sample == null) || !(sample instanceof IMetaData)) {
			return trace.getTraceColor();
		}

		Object object = ((IMetaData) sample).getData();
		if (object == null || !(object instanceof Boolean))
			return trace.getTraceColor();

		return ((Boolean) object) ? XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_GREEN)
				: XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_RED);
	}
}
