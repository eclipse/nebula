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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public interface ISettings {

	/**
	 * Returns the height of a button.
	 * 
	 * @return Button height. Default is 31.
	 */
	public int getButtonHeight();
	
	/**
	 * Whether to draw a 1 pixel border on the left, right and top side of the ButtonComposite.
	 * 
	 * @return true if draw border. Default is false.
	 */	
	public boolean drawBorder();
	
	/**
	 * Whether to show the toolbar at the bottom or not.
	 * 
	 * @return true if to show toolbar. Default is true.
	 */
	public boolean showToolBar();
	
	/**
	 * Whether buttons can be resized via the resize bar.
	 * 
	 * @return true if to allow button resizing. Default is true.
	 */
	public boolean allowButtonResizing();
	
	/**
	 * Returns the image for the Outlook 2005 styled arrow.
	 *   
	 * @return Image
	 */
	public Image getOutlook2005ArrowImage();
	
	/**
	 * Returns the image for the Outlook 2007 styled arrow.
	 * 
	 * @return Image
	 */
	public Image getOutlook2007ArrowImage();
	
	/**
	 * Returns the pixel height of the Outlook 2005 resize bar.
	 * 
	 * @return Height in pixels. Default is 7.
	 */
	public int getOutlook2005ResizeBarSize();
	
	/**
	 * Returns the pixel height of the Outlook 2007 resize bar.
	 * 
	 * @return Height in pixels. Default is 9.
	 */
	public int getOutlook2007ResizeBarSize();
	
	/**
	 * Returns the IButtonPainter to use to paint buttons. 
	 * 
	 * @return IButtonPainter. Default is DefaultButtonPainter.
	 */
	public IButtonPainter getButtonPainter();
	
	/**
	 * Returns the number of dots shown on the resize bar for the Outlook 2005 styled resize bar. 
	 * 
	 * @return Number of dots to show. Default is 9.
	 */
	public int getOutlook2005ResizeDotNumber();
	
	/**
	 * Returns the number of dots shown on the resize bar for the Outlook 2007 styled resize bar.
	 *  
	 * @return Number of dots to show. Default is 5.
	 */
	public int getOutlook2007ResizeDotNumber();
	
	/**
	 * Returns the Font to use when drawing text on the buttons. Font will be disposed after use.
	 * 
	 * @param currentFont The current font that it is recommended to modify and return
	 * @return Font to use. Default is a bold font.
	 */
	public Font getButtonTextFont(Font currentFont);
	
	/**
	 * Returns the Color used for the text on the buttons.
	 * 
	 * @return Color. Default is R32 G77 B137.
	 */
	public Color getButtonTextColor();
	
	/**
	 * Returns the number of pixels between each item on the toolbar.
	 * 
	 * @return Pixel spacing. Default is 8.
	 */
	public int getToolBarSpacing();
	
	/**
	 * Returns the number of pixels left of an item on the toolbar that the selection area should cover.
	 * 
	 * @return Pixel number.
	 */
	public int getToolBarLeftSpacer();
	
	/**
	 * Returns the number of pixels right of an item on the toolbar that the selection area should cover.
	 * 
	 * @return Pixel number.
	 */
	public int getToolBarRightSpacer();
	
	/**
	 * Returns the spacing between the left side border and the text on a button.
	 * 
	 * @return pixels spacing. Default is 4.
	 */
	public int getLeftButtonTextSpacing();
	
	/**
	 * Returns the spacing between the image and the text on a button.
	 * 
	 * @return pixels spacing. Default is 5.
	 */
	public int getButtonTextImageSpacing();
	
}
