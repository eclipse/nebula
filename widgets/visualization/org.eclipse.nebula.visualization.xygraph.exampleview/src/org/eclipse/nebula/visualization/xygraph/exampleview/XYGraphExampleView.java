/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.eclipse.nebula.visualization.xygraph.exampleview;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBufferDataProvider;
import org.eclipse.nebula.visualization.xygraph.figures.ToolbarArmedXYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.PointStyle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Xihui Chen
 *
 */
public class XYGraphExampleView extends ViewPart {

	public XYGraphExampleView() {
	}

	@Override
	public void createPartControl(Composite parent) {

		 //use LightweightSystem to create the bridge between SWT and draw2D
		final LightweightSystem lws = new LightweightSystem(new Canvas(parent, SWT.NONE));
		
		
		//create a new XY Graph.
		XYGraph xyGraph = new XYGraph();
		
		ToolbarArmedXYGraph toolbarArmedXYGraph = new ToolbarArmedXYGraph(xyGraph);

		xyGraph.setTitle("Simple Example");
		//set it as the content of LightwightSystem
		lws.setContents(toolbarArmedXYGraph);
		
		//create a trace data provider, which will provide the data to the trace.
		CircularBufferDataProvider traceDataProvider = new CircularBufferDataProvider(false);
		traceDataProvider.setBufferSize(100);		
		traceDataProvider.setCurrentXDataArray(new double[]{10, 23, 34, 45, 56, 78, 88, 99});
		traceDataProvider.setCurrentYDataArray(new double[]{11, 44, 55, 45, 88, 98, 52, 23});	
		
		//create the trace
		Trace trace = new Trace("Trace1-XY Plot", 
				xyGraph.primaryXAxis, xyGraph.primaryYAxis, traceDataProvider);			
		
		//set trace property
		trace.setPointStyle(PointStyle.XCROSS);
		
		//add the trace to xyGraph
		xyGraph.addTrace(trace);			
		
	}

	@Override
	public void setFocus() {

	}

}
