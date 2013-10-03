package org.eclipse.nebula.visualization.widgets.examples;
/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.nebula.visualization.widgets.datadefinition.IManualValueChangeListener;
import org.eclipse.nebula.visualization.widgets.figures.KnobFigure;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


/**
 * A live updated Gauge Example.
 * @author Xihui Chen
 *
 */
public class KnobExample {
	public static void main(String[] args) {
		final Shell shell = new Shell();
		shell.setSize(300, 250);
	    shell.open();
	    
	    //use LightweightSystem to create the bridge between SWT and draw2D
		final LightweightSystem lws = new LightweightSystem(shell);		
		
		//Create Knob
		final KnobFigure knobFigure = new KnobFigure();
		
		//Init Knob
		knobFigure.setRange(-100, 100);
		knobFigure.setLoLevel(-50);
		knobFigure.setLoloLevel(-80);
		knobFigure.setHiLevel(60);
		knobFigure.setHihiLevel(80);
		knobFigure.setMajorTickMarkStepHint(50);
		knobFigure.setThumbColor(ColorConstants.gray);
		knobFigure.addManualValueChangeListener(new IManualValueChangeListener() {			
			@Override
			public void manualValueChanged(double newValue) {
				System.out.println("You set value to: " + newValue);
			}
		});
		
		lws.setContents(knobFigure);		
		
		
	    Display display = Display.getDefault();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch())
	        display.sleep();
	    }

	   
	}
}
