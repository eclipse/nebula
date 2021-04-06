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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.nebula.visualization.widgets.figures.MeterFigure;

/**
 * @author Xihui Chen
 *
 */
public class MeterTest extends AbstractRoundRampedWidgetTest{

	@Override
	public Figure createTestWidget() {
		return new MeterFigure();
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		List<String> superPropList = new ArrayList<String>();
		for(String p : superProps){
			if(!p.equals("transparent"))
				superPropList.add(p);
		}
		String[] myProps = new String[]{
				"needleColor"
		};
		
		return concatenateStringArrays(superPropList.toArray(new String[]{}), myProps);
	}
	
	@Override
	public boolean isAutoTest() {
		return true;
	}


		
}
