/*******************************************************************************
* Copyright (c) 2011 EBM WebSourcing (PetalsLink)
*
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
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
