/*******************************************************************************
* Copyright (c) 2011 EBM WebSourcing (PetalsLink)
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Mickael Istria, EBM WebSourcing (PetalsLink) - initial API and implementation
*******************************************************************************/
package org.eclipse.nebula.widgets.treemapper;

import org.eclipse.swt.graphics.Color;

/**
 * @author Mickael Istria (EBM WebSourcing (PetalsLink))
 * @serial 0.1.0
 */
public final class TreeMapperUIConfigProvider {
	//
	// Configuration area
	//

	private Color selectedColor;
	private int selectedWidth;
	private int defaultWidth;
	private Color defaultColor;

	public TreeMapperUIConfigProvider(Color defaultColor, int defaultWidth, Color selectedColor, int selectedWidth) {
		this.selectedColor = selectedColor;
		this.selectedWidth = selectedWidth;
		this.defaultColor = defaultColor;
		this.defaultWidth = defaultWidth;
	}
	
	/**
	 * @return
	 */
	public Color getSelectedMappingColor() {
		return selectedColor;
	}

	/**
	 * @return
	 */
	public int getDefaultArrowWidth() {
		return defaultWidth;
	}
	
	/**
	 * @return
	 */
	public Color getDefaultMappingColor() {
		return defaultColor;
	}
	
	/**
	 * @return
	 */
	public int getHoverArrowWidth() {
		return selectedWidth;
	}
	
}
