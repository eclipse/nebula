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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class AbstractSettings implements ISettings {

	private AbstractButtonPainter mButtonPainter;
	
	public boolean allowButtonResizing() {
		return true;
	}

	public boolean drawBorder() {
		return false;
	}

	public int getButtonHeight() {
		return 31;
	}

	public Image getOutlook2005ArrowImage() {
		return ImageCache.getImage("icons/arrows.gif");
	}

	public Image getOutlook2007ArrowImage() {
		return ImageCache.getImage("icons/o2007arrow.gif");
	}

	public int getOutlook2005ResizeBarSize() {
		return 7;
	}

	public int getOutlook2007ResizeBarSize() {
		return 9;
	}

	public boolean showToolBar() {
		return true;
	}

	public int getOutlook2005ResizeDotNumber() {
		return 9;
	}

	public int getOutlook2007ResizeDotNumber() {
		return 5;
	}

	public IButtonPainter getButtonPainter() {
		if (mButtonPainter == null) 
			mButtonPainter = new AbstractButtonPainter();
		
		return mButtonPainter;
	}

	public Font getButtonTextFont(Font currentFont) {
		FontData[] old = currentFont.getFontData();
		old[0].setStyle(SWT.BOLD);
		Font f = new Font(Display.getDefault(), old);
		return f;
	}

	public Color getButtonTextColor() {
		return ColorCache.getColor(32, 77, 137);
	}

	public int getToolBarLeftSpacer() {
		return 3;
	}

	public int getToolBarRightSpacer() {
		return 8;
	}

	public int getToolBarSpacing() {
		return 8;
	}

	public int getButtonTextImageSpacing() {
		return 5;
	}

	public int getLeftButtonTextSpacing() {
		return 4;
	}
		
}
