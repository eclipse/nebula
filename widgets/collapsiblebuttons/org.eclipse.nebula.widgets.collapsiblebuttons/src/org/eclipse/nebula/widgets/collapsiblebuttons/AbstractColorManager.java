/*******************************************************************************
 * Copyright (c) Emil Crumhorn - Hexapixel.com - emil.crumhorn@gmail.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    emil.crumhorn@gmail.com - initial API and implementation
 *******************************************************************************/ 

package org.eclipse.nebula.widgets.collapsiblebuttons;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class AbstractColorManager implements IColorManager {

	private int mTheme;
	
	public AbstractColorManager() {
		this(SKIN_AUTO_DETECT);
	}
	
	public AbstractColorManager(int theme) {
		mTheme = theme;
		
		if (mTheme == SKIN_AUTO_DETECT)
			autoDetect();
	}

	
	public Color getHoverButtonBackgroundColorBottom() {
		switch (mTheme) {
			case SKIN_OFFICE_2007:
				return o2007orangeHoveredBot;
		    case SKIN_BLUE:
	        case SKIN_OLIVE:
	        case SKIN_SILVER:
	            return lightBrownColor[2];
		}
		
		return black;
	}

	public Color getHoverButtonBackgroundColorMiddle() {
		switch (mTheme) {
			case SKIN_OFFICE_2007:
				return o2007orangeHoveredMid;
		    case SKIN_BLUE:
	        case SKIN_OLIVE:
	        case SKIN_SILVER:
	            return lightBrownColor[1];
		}
		
		return black;
	}

	public Color getHoverButtonBackgroundColorTop() {
			switch (mTheme) {
			case SKIN_OFFICE_2007:
				return o2007orangeHoveredTop;
		    case SKIN_BLUE:
	        case SKIN_OLIVE:
	        case SKIN_SILVER:
	            return lightBrownColor[0];
		}
		
		return black;
	}

	public Color getHoverSelectedButtonBackgroundColorBottom() {
		switch (mTheme) {
			case SKIN_OFFICE_2007:
				return o2007orangeHoverSelectedBot;
		    case SKIN_BLUE:
	        case SKIN_OLIVE:
	        case SKIN_SILVER:
	            return darkBrownColor[2];
		}
		
		return black;
	}

	public Color getHoverSelectedButtonBackgroundColorMiddle() {
		switch (mTheme) {
			case SKIN_OFFICE_2007:
				return o2007orangeHoverSelectedMid;
		    case SKIN_BLUE:
	        case SKIN_OLIVE:
	        case SKIN_SILVER:
	            return darkBrownColor[1];
		}
		
		return black;
	}

	public Color getHoverSelectedButtonBackgroundColorTop() {
		switch (mTheme) {
			case SKIN_OFFICE_2007:
				return o2007orangeHoverSelectedTop;
		    case SKIN_BLUE:
	        case SKIN_OLIVE:
	        case SKIN_SILVER:
	            return darkBrownColor[0];
		}
		
		return black;
	}

	public Color getSelectedButtonBackgroundColorBottom() {
		switch (mTheme) {
			case SKIN_OFFICE_2007:
				return o2007orangeSelectedBot;
		    case SKIN_BLUE:
	        case SKIN_OLIVE:
	        case SKIN_SILVER:
	            return darkBrownColor[2];
		}
		
		return black;
	}

	public Color getSelectedButtonBackgroundColorMiddle() {
		switch (mTheme) {
			case SKIN_OFFICE_2007:
				return o2007orangeSelectedMid;
		    case SKIN_BLUE:
	        case SKIN_OLIVE:
	        case SKIN_SILVER:
	            return darkBrownColor[1];
		}
	
		return black;
	}

	public Color getSelectedButtonBackgroundColorTop() {
		switch (mTheme) {
			case SKIN_OFFICE_2007:
				return o2007orangeSelectedTop;
		    case SKIN_BLUE:
	        case SKIN_OLIVE:
	        case SKIN_SILVER:
	            return darkBrownColor[0];
		}
		
		return black;
	}

	public Color getButtonBackgroundColorTop() {
		switch (mTheme) {
			case SKIN_OFFICE_2007:
				return o2007blueTop;
		    case SKIN_BLUE:
                return lightBlueButtonColor[0];
            case SKIN_OLIVE:
                return lightOliveButtonColor[0];
            case SKIN_SILVER:
                return lightSilverButtonColor[0];
		}
		
		return black;
	}
	
	public Color getButtonBackgroundColorMiddle() {
		switch (mTheme) {
			case SKIN_OFFICE_2007:
				return o2007blueMid;
		    case SKIN_BLUE:
                return lightBlueButtonColor[1];
            case SKIN_OLIVE:
                return lightOliveButtonColor[1];
            case SKIN_SILVER:
                return lightSilverButtonColor[1];
		}
	
		return black;
	}
	
	public Color getButtonBackgroundColorBottom() {
		switch (mTheme) {
			case SKIN_OFFICE_2007:
				return o2007blueBot;
		    case SKIN_BLUE:
                return lightBlueButtonColor[2];
            case SKIN_OLIVE:
                return lightOliveButtonColor[2];
            case SKIN_SILVER:
                return lightSilverButtonColor[2];
		}
		
		return black;	
	}

	public Color getDarkResizeColor() {
		switch (mTheme) {
			case SKIN_OFFICE_2007:
				return o2007darkResizeColor;
			case SKIN_BLUE:
                return ColorCache.getColor(0, 45, 150);
            case SKIN_OLIVE:
                return ColorCache.getColor(73, 91, 67);
            case SKIN_SILVER:
                return ColorCache.getColor(119, 118, 151);
		}
		
		return black;
	}

	public Color getLightResizeColor() {
		switch (mTheme) {
			case SKIN_OFFICE_2007:
				return o2007lightResizeColor;
		    case SKIN_BLUE:
                return ColorCache.getColor(89, 135, 214);
            case SKIN_OLIVE:
                return ColorCache.getColor(120, 142, 111);
            case SKIN_SILVER:
                return ColorCache.getColor(168, 167, 191);		
        }
		
		return black;
	}

	public Color getBorderColor() {
		switch (mTheme) {
			case SKIN_OFFICE_2007:
				return o2007borderColor;
			case SKIN_BLUE:
                return blueButtonBackground;
            case SKIN_OLIVE:
                return oliveButtonBackground;
            case SKIN_SILVER:
                return silverButtonBackground;
		}
	
		return black;		
	}

	public Color getDotDarkColor() {		
		switch (mTheme) {
			case SKIN_OFFICE_2007:
				return ColorCache.getColor(101, 147, 207);
			case SKIN_BLUE:
			case SKIN_OLIVE:
			case SKIN_SILVER:
				return darkBlue;
		}
		
		return black;
	}

	public Color getDotLightColor() {
		switch (mTheme) {
			case SKIN_OFFICE_2007:
				return ColorCache.getColor(173, 209, 255);
			case SKIN_BLUE:
			case SKIN_OLIVE:
			case SKIN_SILVER:
				return lightBlue;
		}
		
		return black;
	}

	public Color getDotMiddleColor() {
		switch (mTheme) {
			case SKIN_OFFICE_2007:
				return white;
			case SKIN_BLUE:
			case SKIN_OLIVE:
			case SKIN_SILVER:
				return white;
		}
		
		return black;
	}

	public void setTheme(int theme) {
		mTheme = theme;
	}

	public int getTheme() {
		return mTheme;
	}
	
	private void autoDetect() {
		RGB bgGradient = Display.getDefault().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT).getRGB();

		int r = bgGradient.red;
		int g = bgGradient.green;
		int b = bgGradient.blue;

		int style = SKIN_NONE;

		if (r == 200 && g == 200 && b == 200) {
			style = SKIN_SILVER;
		} else if (r == 198 && g == 210 && b == 162) {
			style = SKIN_OLIVE;
		} else if (r == 61 && g == 149 && b == 255) {
			style = SKIN_BLUE;
		}

		if (style == SKIN_NONE) {
			style = SKIN_BLUE;
		}

		mTheme = style;
	}
	
	public void dispose() {
	}

	
		
}
