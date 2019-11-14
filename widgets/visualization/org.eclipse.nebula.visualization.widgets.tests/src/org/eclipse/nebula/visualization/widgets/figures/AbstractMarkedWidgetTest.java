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


/**
 * @author Xihui Chen
 *
 */
public abstract class AbstractMarkedWidgetTest extends AbstractScaledWidgetTest {

	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();	
		
		String[] myProps = new String[]{
				"showMarkers",
				"loloLevel",
				"loLevel",
				"hiLevel",
				"hihiLevel",
				"showLolo",
				"showLo",
				"showHi",
				"showHihi",
				"loloColor",
				"loColor",
				"hiColor",
				"hihiColor"
		};
		return concatenateStringArrays(superProps, myProps);
	}
	
}
