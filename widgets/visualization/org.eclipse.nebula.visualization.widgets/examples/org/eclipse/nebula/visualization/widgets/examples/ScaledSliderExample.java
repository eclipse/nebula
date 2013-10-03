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
import org.eclipse.nebula.visualization.widgets.figures.ScaledSliderFigure;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


/**
 * A live Scaled Slider Example.
 * @author Xihui Chen
 *
 */
public class ScaledSliderExample {
	public static void main(String[] args) {
		final Shell shell = new Shell();
		shell.setSize(300, 250);
	    shell.open();
	    
	    //use LightweightSystem to create the bridge between SWT and draw2D
		final LightweightSystem lws = new LightweightSystem(shell);		
		
		//Create Scaled Slider
		final ScaledSliderFigure slider = new ScaledSliderFigure();
		
		//Init Scaled Slider
		slider.setRange(-100, 100);
		slider.setLoLevel(-50);
		slider.setLoloLevel(-80);
		slider.setHiLevel(60);
		slider.setHihiLevel(80);
		slider.setMajorTickMarkStepHint(50);
		slider.setThumbColor(ColorConstants.gray);
		slider.addManualValueChangeListener(new IManualValueChangeListener() {			
			@Override
			public void manualValueChanged(double newValue) {
				System.out.println("You set value to: " + newValue);
			}
		});
		
		lws.setContents(slider);		
		
		
	    Display display = Display.getDefault();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch())
	        display.sleep();
	    }

	   
	}
}
