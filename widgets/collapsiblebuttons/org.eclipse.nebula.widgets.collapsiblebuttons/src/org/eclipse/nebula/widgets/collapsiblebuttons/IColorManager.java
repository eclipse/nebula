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

import org.eclipse.swt.graphics.Color;

public interface IColorManager {

	public static Color white = ColorCache.getColor(255, 255, 255);
    public static Color black = ColorCache.getColor(0, 0, 0);
    
	// Windows XP Theme - Blue
    public static Color [] blueHeaderColor = new Color[]{
            ColorCache.getColor(89, 135, 214),
            ColorCache.getColor(3, 56, 148),
            ColorCache.getColor(89, 135, 214)
    };

    public static Color [] lightBlueButtonColor = new Color[]{
            ColorCache.getColor(203, 225, 252),
            ColorCache.getColor(125, 165, 224),
            ColorCache.getColor(203, 225, 252)
    };

    public static Color blueButtonBackground = ColorCache.getColor(3, 56, 148);

    public static Color blueToolbarColor = ColorCache.getColor(77, 124, 205);
    public static Color lightBlueToolbarcolor = ColorCache.getColor(170, 199, 246);
    // -- end blue skin

	// Windows XP Theme - Olive
    public static Color [] oliveHeaderColor = new Color[]{
            ColorCache.getColor(175, 192, 130), // light
            ColorCache.getColor(99, 122, 68), // dark
            ColorCache.getColor(175, 192, 130)       // light
    };

    public static Color [] lightOliveButtonColor = new Color[]{
            ColorCache.getColor(232, 238, 204),
            ColorCache.getColor(177, 192, 140),
            ColorCache.getColor(232, 238, 204)
    };

    public static Color oliveButtonBackground = ColorCache.getColor(99, 122, 68);

    public static Color oliveToolbarColor = ColorCache.getColor(230, 230, 200);
    public static Color lightOliveToolbarcolor = ColorCache.getColor(232, 232, 206);
    // -- end olive skin

	// Windows XP Theme - Silver
    public static Color [] silverHeaderColor = new Color[]{
            ColorCache.getColor(168, 167, 191), // light
            ColorCache.getColor(124, 124, 148), // dark
            ColorCache.getColor(168, 167, 191)       // light
    };

    public static Color [] lightSilverButtonColor = new Color[]{
            ColorCache.getColor(225, 226, 236),
            ColorCache.getColor(149, 147, 177),
            ColorCache.getColor(225, 226, 236)
    };

    public static Color silverButtonBackground = ColorCache.getColor(124, 124, 148);

    public static Color silverToolbarColor = ColorCache.getColor(164, 163, 187);
    public static Color lightSilverToolbarcolor = ColorCache.getColor(231, 231, 239);
    // -- end silver skin

    
    public static Color [] lightBrownColor = new Color[]{
            ColorCache.getColor(254, 252, 215),
            ColorCache.getColor(247, 192, 91),
            ColorCache.getColor(254, 252, 215)
    };

    public static Color [] lightBrownColorReverse = new Color[]{
            ColorCache.getColor(247, 192, 91),
            ColorCache.getColor(254, 252, 215),
            ColorCache.getColor(247, 192, 91)
    };

    public static Color [] darkBrownColor = new Color[]{
            ColorCache.getColor(232, 127, 8),
            ColorCache.getColor(247, 218, 124),
            ColorCache.getColor(232, 127, 8)
    };

    // office 2007 does chrome gradients, top color goes 12 pixels down
    public static Color o2007blueTop = ColorCache.getColor(227, 239, 255);
    public static Color o2007blueMid = ColorCache.getColor(173, 209, 255);
    public static Color o2007blueBot = ColorCache.getColor(192, 219, 255);
    
    public static Color o2007orangeSelectedTop = ColorCache.getColor(255, 217, 170);
    public static Color o2007orangeSelectedMid = ColorCache.getColor(255, 187, 110);
    public static Color o2007orangeSelectedBot = ColorCache.getColor(254, 225, 122);

    public static Color o2007orangeHoverSelectedTop = ColorCache.getColor(255, 189, 105);
    public static Color o2007orangeHoverSelectedMid = ColorCache.getColor(251, 140, 60);
    public static Color o2007orangeHoverSelectedBot = ColorCache.getColor(254, 211, 100);

    public static Color o2007orangeHoveredTop = ColorCache.getColor(255, 254, 228);
    public static Color o2007orangeHoveredMid = ColorCache.getColor(255, 232, 167);
    public static Color o2007orangeHoveredBot = ColorCache.getColor(255, 230, 158);
    
    public static Color o2007buttonBackgroundColor = ColorCache.getColor(101, 147, 207); 
    
    public static Color o2007borderColor = ColorCache.getColor(94, 136, 192);
    
    public static Color o2007lightResizeColor = ColorCache.getColor(227, 239, 255);
    public static Color o2007darkResizeColor = ColorCache.getColor(182, 214, 255);
    	
    public static final Color darkBlue = ColorCache.getColor(40, 50, 71);
    public static final Color lightBlue = ColorCache.getColor(97, 116, 152);
    
	public static final int SKIN_NONE = -1;
    public static final int SKIN_AUTO_DETECT = 0; // auto detect, but only blue olive or silver
    public static final int SKIN_BLUE = 1;
    public static final int SKIN_OLIVE = 2;
    public static final int SKIN_SILVER = 3;
    public static final int SKIN_OFFICE_2007 = 4;
    
	public Color getDarkResizeColor();
	public Color getLightResizeColor();
	public Color getBorderColor();	
	public Color getDotDarkColor();
	public Color getDotLightColor();
	public Color getDotMiddleColor();
	
	public Color getButtonBackgroundColorTop();
	public Color getButtonBackgroundColorMiddle();
	public Color getButtonBackgroundColorBottom();

	public Color getSelectedButtonBackgroundColorTop();
	public Color getSelectedButtonBackgroundColorMiddle();
	public Color getSelectedButtonBackgroundColorBottom();

	public Color getHoverButtonBackgroundColorTop();
	public Color getHoverButtonBackgroundColorMiddle();
	public Color getHoverButtonBackgroundColorBottom();

	public Color getHoverSelectedButtonBackgroundColorTop();
	public Color getHoverSelectedButtonBackgroundColorMiddle();
	public Color getHoverSelectedButtonBackgroundColorBottom();

	public void setTheme(int theme);
	public int getTheme();
	public void dispose();
	
}
