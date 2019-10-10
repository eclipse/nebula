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

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.nebula.visualization.xygraph.dataprovider.IMetaData;
import org.eclipse.nebula.visualization.xygraph.dataprovider.ISample;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class DoubleStyleProvider extends BasePointStyleProvider {

	static TreeMap<Double, RGB> COLOR_MAP_DEFAULT;
	private TreeMap<Double, RGB> fColorMap;

	static {
		COLOR_MAP_DEFAULT = new TreeMap<Double, RGB>();
		COLOR_MAP_DEFAULT.put(0.5, XYGraphMediaFactory.COLOR_RED);
		COLOR_MAP_DEFAULT.put(0.75, XYGraphMediaFactory.COLOR_ORANGE);
		COLOR_MAP_DEFAULT.put(0.5, XYGraphMediaFactory.COLOR_YELLOW);
		COLOR_MAP_DEFAULT.put(1.0, XYGraphMediaFactory.COLOR_GREEN);
	}

	public DoubleStyleProvider(Map<Double, RGB> colorMap) {
		fColorMap = new TreeMap<Double, RGB>(colorMap);
	}

	public DoubleStyleProvider() {
		fColorMap = COLOR_MAP_DEFAULT;
	}

	@Override
	public Color getPointColor(ISample sample, Trace trace) {

		if ((sample == null) || !(sample instanceof IMetaData) || (fColorMap == null)) {
			return trace.getTraceColor();
		}

		Object object = ((IMetaData) sample).getData();
		if ((object == null) || !(object instanceof Double))
			return trace.getTraceColor();

		double value = (Double) object;
		for (Entry<Double, RGB> upperLimit : fColorMap.entrySet()) {
			if (value < upperLimit.getKey())
				return XYGraphMediaFactory.getInstance().getColor(upperLimit.getValue());
		}

		return XYGraphMediaFactory.getInstance().getColor(fColorMap.lastEntry().getValue());
	}
}
