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

package org.eclipse.nebula.visualization.xygraph.exampleview;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBufferDataProvider;
import org.eclipse.nebula.visualization.xygraph.dataprovider.Sample;
import org.eclipse.nebula.visualization.xygraph.figures.IXYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.ToolbarArmedXYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.PointStyle;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.TraceType;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;
import org.eclipse.nebula.visualization.xygraph.styleprovider.BooleanStyleProvider;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class XYGraphStyledBoolExampleView extends ViewPart {

	public XYGraphStyledBoolExampleView() {
	}

	@Override
	public void createPartControl(Composite parent) {

		// use LightweightSystem to create the bridge between SWT and draw2D
		final LightweightSystem lws = new LightweightSystem(new Canvas(parent, SWT.NONE));

		// create a new XY Graph.
		IXYGraph xyGraph = new XYGraph();

		ToolbarArmedXYGraph toolbarArmedXYGraph = new ToolbarArmedXYGraph(xyGraph);

		xyGraph.setTitle("Simple Styled Example");
		// set it as the content of LightwightSystem
		lws.setContents(toolbarArmedXYGraph);

		// create a trace data provider, which will provide the data to the
		// trace.
		CircularBufferDataProvider traceDataProvider = new CircularBufferDataProvider(false);

		for (int i = 0; i < 20; i++) {
			Sample sample = new Sample(Math.random(), Math.random());
			sample.setData(Math.random()<0.5);
			traceDataProvider.addSample(sample);
		}

		// create the trace
		Trace trace = new Trace("Trace-Styled XY Plot", xyGraph.getPrimaryXAxis(), xyGraph.getPrimaryYAxis(),
				traceDataProvider);

		// set trace property
		trace.setTraceColor(XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_GRAY));
		trace.setTraceType(TraceType.POINT);

		// set point properties
		trace.setPointStyle(PointStyle.FILLED_SQUARE);
		trace.setPointSize(40);
		trace.setPointStyleProvider(new BooleanStyleProvider());

		// add the trace to xyGraph
		xyGraph.addTrace(trace);

		// perform AutoScale
		xyGraph.performAutoScale();

	}

	@Override
	public void setFocus() {

	}

}