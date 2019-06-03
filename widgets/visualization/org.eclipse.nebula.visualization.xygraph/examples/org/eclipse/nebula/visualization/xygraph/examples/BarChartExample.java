package org.eclipse.nebula.visualization.xygraph.examples;

/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBufferDataProvider;
import org.eclipse.nebula.visualization.xygraph.figures.IXYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.Legend;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.TraceType;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Bar Chart Example
 * 
 * @author Xihui Chen
 *
 */
public class BarChartExample {
	public static void main(String[] args) {
		final Shell shell = new Shell();
		shell.setSize(300, 250);
		shell.open();

		// use LightweightSystem to create the bridge between SWT and draw2D
		final LightweightSystem lws = new LightweightSystem(shell);

		// create a new XY Graph.
		IXYGraph xyGraph = new XYGraph();
		xyGraph.setTitle("Bar and Area Chart");
		// set it as the content of LightwightSystem
		lws.setContents(xyGraph);

		// Configure XYGraph
		xyGraph.getPrimaryXAxis().setShowMajorGrid(true);
		xyGraph.getPrimaryYAxis().setShowMajorGrid(true);

		// create a trace data provider, which will provide the data to the
		// trace.
		CircularBufferDataProvider traceDataProvider = new CircularBufferDataProvider(false);
		traceDataProvider.setBufferSize(100);
		traceDataProvider.setCurrentXDataArray(new double[] { 0, 20, 30, 40, 50, 60, 70, 80, 100 });
		traceDataProvider.setCurrentYDataArray(new double[] { 11, 44, 55, 45, 88, 98, 52, 23, 78 });

		// create the trace
		Trace trace = new Trace("Trace1-XY Plot", xyGraph.getPrimaryXAxis(), xyGraph.getPrimaryYAxis(),
				traceDataProvider);

		// set trace property
		trace.setTraceType(TraceType.BAR);
		trace.setLineWidth(15);
		trace.setAreaAlpha(200);
		trace.setTraceColor(XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_BLUE));
		// add the trace to xyGraph
		xyGraph.addTrace(trace);

		// create a trace data provider, which will provide the data to the
		// trace.
		CircularBufferDataProvider traceDataProvider2 = new CircularBufferDataProvider(false);
		traceDataProvider2.setBufferSize(100);
		traceDataProvider2.setCurrentXDataArray(new double[] { 0, 20, 30, 40, 50, 60, 70, 80, 100 });
		traceDataProvider2.setCurrentYDataArray(new double[] { 15, 60, 40, 60, 70, 80, 65, 70, 23 });

		// create the trace
		Trace trace2 = new Trace("Trace1-XY Plot", xyGraph.getPrimaryXAxis(), xyGraph.getPrimaryYAxis(),
				traceDataProvider2);

		// set trace property
		trace2.setPointSize(6);
		trace2.setAreaAlpha(150);
		trace2.setTraceType(TraceType.AREA);
		trace2.setTraceColor(XYGraphMediaFactory.getInstance().getColor(XYGraphMediaFactory.COLOR_RED));

		// trace2.setLineWidth(5);
		// add the trace to xyGraph
		xyGraph.addTrace(trace2);
		
		Font LEGEND_FONT = XYGraphMediaFactory.getInstance().getFont(new FontData("Lucida Sans", 11, SWT.BOLD));
		
		Legend legend = xyGraph.getLegend(trace);
		legend.setDrawBorder(true);
		legend.setPreferredHeight(100);
		legend.setTextFont(LEGEND_FONT);
		
		Display display = Display.getDefault();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

	}
}
