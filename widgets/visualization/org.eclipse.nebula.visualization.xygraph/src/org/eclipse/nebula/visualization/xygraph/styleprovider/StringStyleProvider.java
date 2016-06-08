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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.nebula.visualization.xygraph.dataprovider.IMetaData;
import org.eclipse.nebula.visualization.xygraph.dataprovider.ISample;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.PointStyle;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.graphics.Color;

/**
 * StyleProvider for a string as formatter...
 * 
 */
public class StringStyleProvider extends BasePointStyleProvider {

	@Override
	public Color getPointColor(ISample sample, Trace trace) {

		if ((sample == null) || !(sample instanceof IMetaData)) {
			return trace.getTraceColor();
		}

		Object object = ((IMetaData) sample).getData();

		if ((object == null) || !(object instanceof String))
			return trace.getTraceColor();

		String format = (String) object;

		for (char ch : format.toCharArray()) {
			switch (ch) {
			case 'r':
				return XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_RED);
			case 'g':
				return XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_GREEN);
			case 'b':
				return XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_BLUE);
			case 'c':
				return XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_CYAN);
			case 'm':
				return XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_PURPLE);
			case 'y':
				return XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_YELLOW);
			case 'k':
				return XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_BLACK);
			case 'w':
				return XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_WHITE);
			}
		}
		return trace.getTraceColor();
	}

	@Override
	public PointStyle getPointStyle(ISample sample, Trace trace) {

		if ((sample == null) || !(sample instanceof IMetaData)) {
			return trace.getPointStyle();
		}

		Object object = ((IMetaData) sample).getData();

		if ((object == null) || !(object instanceof String))
			return trace.getPointStyle();

		String format = (String) object;

		for (char ch : format.toCharArray()) {
			switch (ch) {
			case 'o':
				return PointStyle.CIRCLE;
			case 'x':
				return PointStyle.XCROSS;
			case '+':
				return PointStyle.CROSS;
			case 's':
				return PointStyle.SQUARE;
			case 'f':
				return PointStyle.FILLED_SQUARE;
			case 'd':
				return PointStyle.DIAMOND;
			case 'v':
				return PointStyle.TRIANGLE;
			case 'p':
				return PointStyle.POINT;
			}
		}
		return trace.getPointStyle();
	}

	@Override
	public int getPointSize(ISample sample, Trace trace) {

		if ((sample == null) || !(sample instanceof IMetaData)) {
			return trace.getPointSize();
		}

		Object object = ((IMetaData) sample).getData();

		if ((object == null) || !(object instanceof String))
			return trace.getPointSize();

		String format = (String) object;
		Pattern regex = Pattern.compile("(\\d+)");
		Matcher regexMatcher = regex.matcher(format);
		if (regexMatcher.find()) {
			return Integer.parseInt(regexMatcher.group(1));
		}
		return trace.getPointSize();
	}
}
