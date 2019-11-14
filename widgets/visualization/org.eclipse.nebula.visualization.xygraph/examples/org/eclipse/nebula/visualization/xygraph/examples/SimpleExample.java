package org.eclipse.nebula.visualization.xygraph.examples;

import org.eclipse.draw2d.ColorConstants;
/*******************************************************************************
 * Copyright (c) 2010, 2017 Oak Ridge National Laboratory.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBufferDataProvider;
import org.eclipse.nebula.visualization.xygraph.figures.Annotation;
import org.eclipse.nebula.visualization.xygraph.figures.IXYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.Legend;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.Annotation.CursorLineStyle;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.PointStyle;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * A very simple example.
 * 
 * @author Xihui Chen
 *
 */
public class SimpleExample {
	public static void main(String[] args) {
		final Shell shell = new Shell();
		shell.setSize(300, 250);
		shell.open();

		// use LightweightSystem to create the bridge between SWT and draw2D
		final LightweightSystem lws = new LightweightSystem(shell);

		// create a new XY Graph.
		IXYGraph xyGraph = new XYGraph();
		xyGraph.setTitle("Simple Example");
		// set it as the content of LightwightSystem
		lws.setContents(xyGraph);

		// create a trace data provider, which will provide the data to the
		// trace.
		CircularBufferDataProvider traceDataProvider = new CircularBufferDataProvider(false);
		traceDataProvider.setBufferSize(100);
		traceDataProvider.setCurrentXDataArray(new double[] { 10, 23, 34, 45, 56, 78, 88, 99 });
		traceDataProvider.setCurrentYDataArray(new double[] { 11, 44, 55, 45, 88, 98, 52, 23 });

		// create the trace
		Trace trace = new Trace("Trace1-XY Plot", xyGraph.getPrimaryXAxis(), xyGraph.getPrimaryYAxis(),
				traceDataProvider);

		// set trace property
		trace.setPointStyle(PointStyle.XCROSS);

		// Create an annotation on Primary axis, pName is a String
		Annotation lAnnotation = new Annotation("Point1", xyGraph.getPrimaryXAxis(), xyGraph.getPrimaryYAxis());

		// Set the value as an X
		lAnnotation.setValues(10, 11);
		lAnnotation.setEnabled(true); // the annotation can be moved on the graph
		lAnnotation.setShowPosition(true);
		lAnnotation.setShowName(true);
		lAnnotation.setShowSampleInfo(false);

		lAnnotation.setCursorLineStyle(CursorLineStyle.NONE);
		lAnnotation.setAnnotationColor(ColorConstants.darkGray);
		xyGraph.addAnnotation(lAnnotation);

		// add the trace to xyGraph
		xyGraph.addTrace(trace);
		
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
