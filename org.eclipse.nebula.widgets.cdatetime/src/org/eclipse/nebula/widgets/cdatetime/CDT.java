/****************************************************************************
* Copyright (c) 2006 Jeremy Dowdall
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
*****************************************************************************/
package org.eclipse.nebula.widgets.cdatetime;

import org.eclipse.swt.SWT;


/**
 * This class provides access to the the public constants provided
 * by the CDateTime widget.  This class is analogous to the SWT class
 * of the Standard Widget Toolkit (SWT) and has been created separately
 * to avoid collisions.
 * <p>
 * Note that, unlike the SWT class, these constants apply for all platforms
 * (upon which CDateTime has been tested) because the CDateTime is a 
 * custom widget.
 * </p>
 */
public class CDT {
	
	/**
	 * true if the platform is carbon, false otherwise
	 */
	public static final boolean carbon = "carbon".equals(SWT.getPlatform());
	/**
	 * true if the platform is gtk, false otherwise
	 */
	public static final boolean gtk = "gtk".equals(SWT.getPlatform());
	/**
	 * true if the platform is win32, false otherwise
	 */
	public static final boolean win32 = "win32".equals(SWT.getPlatform());
	
	/**
	 * Style constant indicating no style (value is 0).
	 */
	public static final int NONE				= 0;

	/**
	 * Style constant requesting a border.  This value will be converted to its
	 * SWT equivalent and passed to the SWT super (value is 1&lt;&lt;0).
	 * @see SWT#BORDER
	 */
	public static final int BORDER			= 1 << 0;

	/**
	 * Style constant for drop down combo behavior (value is 1&lt;&lt;1).
	 */
	public static final int DROP_DOWN 		= 1 << 1;

	/**
	 * Style constant for simple combo behavior (value is 1&lt;&lt;1).
	 */
	public static final int SIMPLE			= 1 << 2;
	
	/**
	 * Style constant for a DropCombo whose button is always visible (value is 1&lt;&lt;10).
	 * @see #BUTTON_AUTO
	 * @see #BUTTON_MANUAL
	 * @see #BUTTON_NEVER
	 * @see AbstractCombo#setButtonVisibility(int)
	 */
	public static final int BUTTON_ALWAYS	= 1 << 3;

	/**
	 * Style constant for a DropCombo whose button is automatically set to be
	 * visible or not depending on its focus state (value is 1&lt;&lt;11).
	 * @see #BUTTON_AUTO
	 * @see #BUTTON_MANUAL
	 * @see #BUTTON_NEVER
	 * @see AbstractCombo#setButtonVisibility(int)
	 */
	public static final int BUTTON_AUTO		= 1 << 4;

	/**
	 * Style constant for a DropCombo whose button is never visible (value is 1&lt;&lt;12).
	 * @see #BUTTON_ALWAYS
	 * @see #BUTTON_AUTO
	 * @see #BUTTON_NEVER
	 * @see AbstractCombo#setButtonVisibility(int)
	 */
	public static final int BUTTON_MANUAL	= 1 << 5;

	/**
	 * Style constant for a DropCombo whose button is never visible.
	 * The difference between this and BUTTON_MANUAL is that the drop contents
	 * are never created (value is 1&lt;&lt;13).
	 * @see #BUTTON_ALWAYS
	 * @see #BUTTON_AUTO
	 * @see #BUTTON_MANUAL
	 * @see AbstractCombo#setButtonVisibility(int)
	 */
	public static final int BUTTON_NEVER 	= 1 << 6;

	/**
	 * Style constant for a DropCombo with its button to the Left of the text (value is 1&lt;&lt;14).
	 * @see #BUTTON_RIGHT
	 */
	public static final int BUTTON_LEFT	 	= 1 << 7;

	/**
	 * Style constant for a DropCombo with its button to the right of the text (value is 1&lt;&lt;15).
	 * @see #BUTTON_LEFT
	 */
	public static final int BUTTON_RIGHT 	= 1 << 8;
	
	/**
	 * Style constant for left aligning the text of a DropCombo (value is 1&lt;&lt;16).
	 * @see #TEXT_RIGHT
	 * @see SWT#LEFT
	 */
	public static final int TEXT_LEFT		 = 1 << 9;

	/**
	 * Style constant for left aligning the text of a DropCombo (value is 1&lt;&lt;16).
	 * @see #TEXT_RIGHT
	 * @see SWT#LEAD
	 */
	public static final int TEXT_LEAD		 = 1 << 9;

	/**
	 * Style constant for right aligning the text of a DropCombo (value is 1&lt;&lt;17).
	 * @see #TEXT_LEFT
	 * @see SWT#RIGHT
	 */
	public static final int TEXT_RIGHT 		= 1 << 10;
	
	/**
	 * Style constant for right aligning the text of a DropCombo (value is 1&lt;&lt;17).
	 * @see #TEXT_LEFT
	 * @see SWT#TRAIL
	 */
	public static final int TEXT_TRAIL 		= 1 << 10;

	public static final int HORIZONTAL		= 1 << 11;
	public static final int VERTICAL		= 1 << 12;
	public static final int READ_ONLY		= 1 << 13;
	
///////////////////////////////////////////////////////////////////////////////////////
// END OF AbstractCombo STYLES
// BEGINNING OF CDateCombo STYLES
///////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Style constant for showing a short date format (value is 1&lt;&lt;2).
	 * @see CDateTime#setPattern(String)
	 * @see CDateTime#setFormat(int)
	 */
	public static final int DATE_SHORT		= 1 << 20;

	/**
	 * Style constant for showing a medium date format (value is 1&lt;&lt;3).
	 * @see CDateTime#setPattern(String)
	 * @see CDateTime#setFormat(int)
	 */
	public static final int DATE_MEDIUM		= 1 << 21;

	/**
	 * Style constant for showing a long date format (value is 1&lt;&lt;4).
	 * @see CDateTime#setPattern(String)
	 * @see CDateTime#setFormat(int)
	 */
	public static final int DATE_LONG		= 1 << 22;

	/**
	 * Style constant for showing a short time format (value is 1&lt;&lt;6).
	 * @see CDateTime#setPattern(String)
	 * @see CDateTime#setFormat(int)
	 */
	public static final int TIME_SHORT		= 1 << 23;

	/**
	 * Style constant for showing a medium time format (value is 1&lt;&lt;7).
	 * @see CDateTime#setPattern(String)
	 * @see CDateTime#setFormat(int)
	 */
	public static final int TIME_MEDIUM		= 1 << 24;

	public static final int COMPACT			= 1 << 15;

	/**
	 * Style constant indicating that the TAB key should be used to traverse
	 * the CDateTime's fields (value is 1&lt;&lt;19).
	 */
	public static final int TAB_FIELDS 		= 1 << 25;

	public static final int SPINNER			= 1 << 26;

	public static final int CLOCK_DISCRETE	= 1 << 27;
	
	public static final int CLOCK_12_HOUR	= 1 << 28;

	public static final int CLOCK_24_HOUR	= 1 << 29;
}
