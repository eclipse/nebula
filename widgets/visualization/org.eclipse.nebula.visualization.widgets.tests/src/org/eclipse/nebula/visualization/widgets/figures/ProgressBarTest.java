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
import org.eclipse.nebula.visualization.widgets.figures.ProgressBarFigure;

/**
 * @author Xihui Chen
 *
 */
public class ProgressBarTest extends AbstractMarkedWidgetTest{

	@Override
	public Figure createTestWidget() {
		return new ProgressBarFigure();
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		String[] myProps = new String[]{
				"fillColor",
				"fillBackgroundColor",
				"effect3D",
				"horizontal",
				"indicatorMode",
				"origin",
				"originIgnored"
		};
		
		return concatenateStringArrays(superProps, myProps);
	}
	
	@Override
	public boolean isAutoTest() {
		return true;
	}

	
		
}
