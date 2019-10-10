/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.nebula.visualization.widgets.figures;
import org.eclipse.draw2d.Figure;
import org.eclipse.nebula.visualization.widgets.datadefinition.IManualValueChangeListener;
import org.eclipse.nebula.visualization.widgets.figures.KnobFigure;

/**
 * @author Xihui Chen
 *
 */
public class KnobTest extends AbstractRoundRampedWidgetTest{

	@Override
	public Figure createTestWidget() {
		KnobFigure knob = new KnobFigure();
		knob.addManualValueChangeListener(new IManualValueChangeListener() {
			
			public void manualValueChanged(double newValue) {
				System.out.println("Knob Dragged: " + newValue);
			}
		});
		return knob;
	}

	
	
	
	@Override
	public boolean isAutoTest() {
		return true;
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		String[] myProps = new String[]{
				"effect3D",
				"thumbColor",
				"increment"
		};
		
		return concatenateStringArrays(superProps, myProps);
	}
}
