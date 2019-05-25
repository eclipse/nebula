/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    chris.gross@us.ibm.com - initial API and implementation
 *******************************************************************************/ 
package org.eclipse.nebula.widgets.grid.internal;

import org.eclipse.swt.graphics.GC;

/**
 * Utility class to provide common operations on strings not supported by the
 * base java API.
 * 
 * @author chris.gross@us.ibm.com
 * @author Mirko Paturzo <mirko.paturzo@exeura.eu>
 * @author gongguangyong@live.cn
 *
 * Mirko modified the pivot calculation for improve short text provider performance. 
 * The pivot number is calculate starting from the size of the cell provided
 * 
 * @since 2.0.0
 */
public class TextUtils
{

    /**
     * Shortens a supplied string so that it fits within the area specified by
     * the width argument. Strings that have been shorted have an "..." attached
     * to the end of the string. The width is computed using the
     * {@link GC#textExtent(String)}.
     * 
     * @param gc GC used to perform calculation.
     * @param t text to modify.
     * @param width Pixels to display.
     * @return shortened string that fits in area specified.
     */
    public static String getShortText(GC gc, String t, int width)
    {
        return getShortString(gc, t, width);
    }
    
    /**
     * Shortens a supplied string so that it fits within the area specified by
     * the width argument. Strings that have been shorted have an "..." attached
     * to the end of the string. The width is computed using the
     * {@link GC#stringExtent(String)}.
     * 
     * @param gc GC used to perform calculation.
     * @param t text to modify.
     * @param width Pixels to display.
     * @return shortened string that fits in area specified.
     */
    public static String getShortString(GC gc, String t, int width)
    {
        if (t == null || t.equals("")) {
	    return t;
	}

	if (width >= gc.textExtent(t).x) {
	    return t;
	}
	
	char[] chars = t.toCharArray();
	int length = chars.length;
	int left = 0;
	int right = length - 1;
	int calcWidth = gc.textExtent("...").x;;

	while (left < right) {
	    int step = gc.getCharWidth(chars[left]);
	    calcWidth += step;
	    if(calcWidth >= width) {
	        break;
	    }
	    left++;
			
	    step = gc.getCharWidth(chars[right]);
	    calcWidth += step;
	    if (calcWidth >= width) {
	        break;
	    }
	    right--;
	}
	if (left >= right) {
	    return t;
	}
	StringBuilder builder = new StringBuilder(left + length - right + 4);
	if(left == 0 || right == length - 1) {
	    builder.append(chars[0]).append("...").append(chars[length-1]);
	} else {
	    int leftLen = left == 1 ? left : left - 1;
	    builder.append(chars, 0, leftLen).append("...").append(chars, right+1, length - right - 1);
	}
	return builder.toString();
    }

    /**
     * private constructor to prevent instantiation.
     */
    private TextUtils()
    {
    	//is empty
    }
}
