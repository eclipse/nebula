package org.eclipse.nebula.visualization.widgets.examples;
/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.nebula.visualization.widgets.figures.GaugeFigure;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


/**
 * A live updated Gauge Example.
 * @author Xihui Chen
 *
 */
public class GaugeExample {
	private static int counter = 0;
	public static void main(String[] args) {
		final Shell shell = new Shell();
		shell.setSize(300, 250);
		shell.setBackground(XYGraphMediaFactory.getInstance().getColor(255, 255, 255));
	    shell.open();
	    
	    //use LightweightSystem to create the bridge between SWT and draw2D
		final LightweightSystem lws = new LightweightSystem(shell);		
		
		//Create Gauge
		final GaugeFigure gaugeFigure = new GaugeFigure();
		
		//Init gauge
		gaugeFigure.setBackgroundColor(
				XYGraphMediaFactory.getInstance().getColor(0, 0, 0));
		gaugeFigure.setForegroundColor(
				XYGraphMediaFactory.getInstance().getColor(255, 255, 255));
		
		gaugeFigure.setRange(-100, 100);
		gaugeFigure.setLoLevel(-50);
		gaugeFigure.setLoloLevel(-80);
		gaugeFigure.setHiLevel(60);
		gaugeFigure.setHihiLevel(80);
		gaugeFigure.setMajorTickMarkStepHint(50);
		
		lws.setContents(gaugeFigure);		
		
		//Update the gauge in another thread.
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				Display.getDefault().asyncExec(new Runnable() {					
					@Override
					public void run() {
						gaugeFigure.setValue(Math.sin(counter++/10.0)*100);						
					}
				});
			}
		}, 100, 100, TimeUnit.MILLISECONDS);		
		
	    Display display = Display.getDefault();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch())
	        display.sleep();
	    }
	    future.cancel(true);
	    scheduler.shutdown();
	   
	}
}
