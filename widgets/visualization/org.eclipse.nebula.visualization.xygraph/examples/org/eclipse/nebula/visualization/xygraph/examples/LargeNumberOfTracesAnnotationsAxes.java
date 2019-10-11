/*******************************************************************************
 * Copyright (c) 2010, 2017 Oak Ridge National Laboratory and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.examples;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBufferDataProvider;
import org.eclipse.nebula.visualization.xygraph.figures.Annotation;
import org.eclipse.nebula.visualization.xygraph.figures.Axis;
import org.eclipse.nebula.visualization.xygraph.figures.IXYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.ToolbarArmedXYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.PointStyle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Demonstrate/test large number of traces/annotation/axes in plot
 *
 */
public class LargeNumberOfTracesAnnotationsAxes {
	private static final int NUMBER_OF_TRACES = 100;
	private static final int NUMBER_OF_ANNOTATIONS = 100;
	private static final int NUMBER_OF_EXTRA_AXES = 100;

	public static void main(String[] args) {
		final Shell shell = new Shell();
		shell.setSize(600, 400);
		shell.open();

		// use LightweightSystem to create the bridge between SWT and draw2D
		final LightweightSystem lws = new LightweightSystem(shell);

		// create a new XY Graph.
		IXYGraph xyGraph = new XYGraph();

		ToolbarArmedXYGraph toolbarArmedXYGraph = new ToolbarArmedXYGraph(xyGraph);

		xyGraph.setTitle("Simple Toolbar Armed XYGraph Example");
		// set it as the content of LightwightSystem
		lws.setContents(toolbarArmedXYGraph);

		xyGraph.getPrimaryXAxis().setShowMajorGrid(true);
		xyGraph.getPrimaryYAxis().setShowMajorGrid(true);
		for (int i = 0; i < NUMBER_OF_EXTRA_AXES; i++) {
			xyGraph.addAxis(new Axis("Extra Axis " + i, false));
		}

		for (int i = 0; i < NUMBER_OF_TRACES; i++) {
			// create a trace data provider, which will provide the data to the
			// trace.
			CircularBufferDataProvider traceDataProvider = new CircularBufferDataProvider(false);
			traceDataProvider.setBufferSize(100);
			traceDataProvider.setCurrentXDataArray(new double[] { 10, 23, 34, 45, 56, 78, 88, 99 });
			traceDataProvider.setCurrentYDataArray(new double[] { 11, 44, 55, 45, 88, 98, 52, 23 });

			// create the trace
			Trace trace = new Trace("Trace" + i + "-XY Plot", xyGraph.getPrimaryXAxis(), xyGraph.getPrimaryYAxis(),
					traceDataProvider);

			// set trace property
			trace.setPointStyle(PointStyle.XCROSS);

			// add the trace to xyGraph
			xyGraph.addTrace(trace);
		}

		for (int i = 0; i < NUMBER_OF_ANNOTATIONS; i++) {
			xyGraph.addAnnotation(new Annotation("Name " + i, xyGraph.getPrimaryXAxis(), xyGraph.getPrimaryYAxis()));
		}

		Display display = Display.getDefault();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

	}
}
