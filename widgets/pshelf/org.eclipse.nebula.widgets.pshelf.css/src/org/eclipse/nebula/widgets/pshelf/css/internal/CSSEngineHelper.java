/*******************************************************************************
 * Copyright (c) 2013 Tom Schindl. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: Tom Schindl <tom.schindl@bestsolution.at> - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.pshelf.css.internal;

import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.nebula.widgets.pshelf.css.CSSShelfRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Control;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.RGBColor;

public class CSSEngineHelper {
	Control control;
	IStylingEngine engine;
	CSSStyleDeclaration style;
	CSSShelfRenderer renderer;

	public CSSEngineHelper(IStylingEngine engine, Control control, CSSShelfRenderer renderer) {
		this.engine = engine;
		this.control = control;
		this.renderer = renderer;
		initialize();
	}

	private void initialize() {
		if (engine != null) {
			style = engine.getStyle(control);
		}
	}
	
	public Color getBaseColorSelected() {
		if( style == null ) {
			return control.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT);
		}
		return getColor(style.getPropertyCSSValue("pshelf-basecolor-selected"), control.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
	}

	public Color getBaseColorUnselected() {
		if( style == null ) {
			return control.getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION);
		}
		return getColor(style.getPropertyCSSValue("pshelf-basecolor-unselected"), control.getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION));
	}
	
	public Color getBaseColorHover() {
		if( style == null ) {
			return control.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND);
		}
		return getColor(style.getPropertyCSSValue("pshelf-basecolor-hover"), control.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
	}
	
	private Color getColor(CSSValue value, Color defaultValue) {
		if( value instanceof RGBColor ) {
			RGBColor rgbValue = (RGBColor) value;
			RGB rgb = new RGB(
					Integer.parseInt(rgbValue.getRed().getCssText()), 
					Integer.parseInt(rgbValue.getGreen().getCssText()),
					Integer.parseInt(rgbValue.getBlue().getCssText()));
			return new Color(control.getDisplay(), rgb);
		}
		
		
		return defaultValue;
	}
}
