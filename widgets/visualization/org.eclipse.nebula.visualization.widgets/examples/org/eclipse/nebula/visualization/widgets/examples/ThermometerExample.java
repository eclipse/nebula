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
import org.eclipse.draw2d.SchemeBorder;
import org.eclipse.nebula.visualization.widgets.figures.ThermometerFigure;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


/**
 * A live updated Thermometer Example.
 * @author Xihui Chen
 *
 */
public class ThermometerExample {
	private static int counter = 0;
	public static void main(String[] args) {
		final Shell shell = new Shell();
		shell.setSize(300, 250);
	    shell.open();
	    
	    //use LightweightSystem to create the bridge between SWT and draw2D
		final LightweightSystem lws = new LightweightSystem(shell);		
		
		//Create widget
		final ThermometerFigure thermo = new ThermometerFigure();
		
		//Init widget
		thermo.setBackgroundColor(
				XYGraphMediaFactory.getInstance().getColor(255, 255, 255));
		
		thermo.setBorder(new SchemeBorder(SchemeBorder.SCHEMES.ETCHED));
		
		thermo.setRange(-100, 100);
		thermo.setLoLevel(-50);
		thermo.setLoloLevel(-80);
		thermo.setHiLevel(60);
		thermo.setHihiLevel(80);
		thermo.setShowHi(false);
		thermo.setMajorTickMarkStepHint(50);
		
		lws.setContents(thermo);		
		
		//Update the widget in another thread.
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				Display.getDefault().asyncExec(new Runnable() {					
					@Override
					public void run() {
						thermo.setValue(Math.sin(counter++/10.0)*100);						
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
