/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.exampleview;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.nebula.visualization.widgets.datadefinition.IManualValueChangeListener;
import org.eclipse.nebula.visualization.widgets.figures.GaugeFigure;
import org.eclipse.nebula.visualization.widgets.figures.KnobFigure;
import org.eclipse.nebula.visualization.widgets.figures.TankFigure;
import org.eclipse.nebula.visualization.widgets.figures.ThermometerFigure;
import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBufferDataProvider;
import org.eclipse.nebula.visualization.xygraph.dataprovider.CircularBufferDataProvider.UpdateMode;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.XYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.PointStyle;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Xihui Chen
 *
 */
public class WidgetsExampleView extends ViewPart {

	public WidgetsExampleView() {
	}

	@Override
	public void createPartControl(Composite parent) {
	    GridLayout layout = new GridLayout();
	    layout.numColumns=4;
	    GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
	    parent.setLayout(layout);
	    //create canvases to hold the widgets.
	    Canvas knobCanvas = new Canvas(parent, SWT.BORDER);
	    knobCanvas.setLayoutData(gd);
	    Canvas gaugeCanvas = new Canvas(parent, SWT.BORDER);
	    gaugeCanvas.setLayoutData(gd);	 
	    Canvas thermoCanvas = new Canvas(parent, SWT.BORDER);
	    thermoCanvas.setLayoutData(gd);
	    Canvas tankCanvas = new Canvas(parent, SWT.BORDER);
	    tankCanvas.setLayoutData(gd);
	     Canvas xyGraphCanvas = new Canvas(parent, SWT.BORDER);
	    gd = new GridData(SWT.FILL, SWT.FILL, true, true,4,3);
	    xyGraphCanvas.setLayoutData(gd);
	    
	    
	    //use LightweightSystem to create the bridge between SWT and draw2D
		LightweightSystem lws = new LightweightSystem(knobCanvas);		
		//Create widgets
		final KnobFigure knobFigure = new KnobFigure();			
		lws.setContents(knobFigure);	
		
		lws = new LightweightSystem(gaugeCanvas);		
		final GaugeFigure gauge = new GaugeFigure();		
		gauge.setBackgroundColor(
				XYGraphMediaFactory.getInstance().getColor(0, 0, 0));
		gauge.setForegroundColor(
				XYGraphMediaFactory.getInstance().getColor(255, 255, 255));
		lws.setContents(gauge);	
		
		lws = new LightweightSystem(thermoCanvas);		
		final ThermometerFigure thermo = new ThermometerFigure();			
		lws.setContents(thermo);	
		
		lws = new LightweightSystem(tankCanvas);		
		final TankFigure tank = new TankFigure();			
		lws.setContents(tank);
		
		
		
		
	

		 //use LightweightSystem to create the bridge between SWT and draw2D
		lws = new LightweightSystem(xyGraphCanvas);
		
		
		//create a new XY Graph.
		XYGraph xyGraph = new XYGraph();
		xyGraph.primaryXAxis.setAutoScale(true);
		//set it as the content of LightwightSystem
		lws.setContents(xyGraph);
		
		//create a trace data provider, which will provide the data to the trace.
		//Set chronological to true so it can sort the data chronologically.
		final CircularBufferDataProvider traceDataProvider = new CircularBufferDataProvider(true);
		traceDataProvider.setBufferSize(100);		
		//Update the trace whenever any X or Y data changed. So only set Y data can update the trace.
		traceDataProvider.setUpdateMode(UpdateMode.X_OR_Y);
		
		//create the trace
		Trace trace = new Trace("Trace1-XY Plot", 
				xyGraph.primaryXAxis, xyGraph.primaryYAxis, traceDataProvider);			
		
		//set trace property
		trace.setPointStyle(PointStyle.POINT);
		
		//add the trace to xyGraph
		xyGraph.addTrace(trace);		
		

		//Add listener
		knobFigure.addManualValueChangeListener(new IManualValueChangeListener() {			
			@Override
			public void manualValueChanged(double newValue) {
				gauge.setValue(newValue);
				thermo.setValue(newValue);
				tank.setValue(newValue);
				traceDataProvider.setCurrentYData(newValue);
			}
		});
		
		
	}

	@Override
	public void setFocus() {

	}

}
