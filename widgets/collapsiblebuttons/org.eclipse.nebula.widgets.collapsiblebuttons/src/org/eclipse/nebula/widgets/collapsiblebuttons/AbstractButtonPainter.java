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

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class AbstractButtonPainter implements IButtonPainter {
	
	public void paintBackground(GC gc, IColorManager colorManager, ISettings settings, Rectangle bounds, boolean hover, boolean selected) {		
		switch (colorManager.getTheme()) {
			case IColorManager.SKIN_OFFICE_2007:		
			{
				if (!selected && !hover) {
					gc.setBackground(colorManager.getButtonBackgroundColorMiddle());
					gc.setForeground(colorManager.getButtonBackgroundColorTop());
					gc.fillGradientRectangle(bounds.x, 0, bounds.width, 19, true);
					gc.setBackground(colorManager.getButtonBackgroundColorBottom());
					gc.setForeground(colorManager.getButtonBackgroundColorMiddle());
					gc.fillGradientRectangle(bounds.x, 12, bounds.width, CustomButton.BUTTON_HEIGHT-12, true);		
				}
				else {
					if (selected && hover) {
						gc.setBackground(colorManager.getHoverSelectedButtonBackgroundColorMiddle());
						gc.setForeground(colorManager.getHoverSelectedButtonBackgroundColorTop());
						gc.fillGradientRectangle(bounds.x, 0, bounds.width, 19, true);
						gc.setBackground(colorManager.getHoverSelectedButtonBackgroundColorBottom());
						gc.setForeground(colorManager.getHoverSelectedButtonBackgroundColorMiddle());
						gc.fillGradientRectangle(bounds.x, 12, bounds.width, CustomButton.BUTTON_HEIGHT-12, true);		
					}
					else {
						if (hover) {
							gc.setBackground(colorManager.getHoverButtonBackgroundColorMiddle());
							gc.setForeground(colorManager.getHoverButtonBackgroundColorTop());
							gc.fillGradientRectangle(bounds.x, 0, bounds.width, 19, true);
							gc.setBackground(colorManager.getHoverButtonBackgroundColorBottom());
							gc.setForeground(colorManager.getHoverButtonBackgroundColorMiddle());
							gc.fillGradientRectangle(bounds.x, 12, bounds.width, CustomButton.BUTTON_HEIGHT-12, true);									
						}
						else {
							gc.setBackground(colorManager.getSelectedButtonBackgroundColorMiddle());
							gc.setForeground(colorManager.getSelectedButtonBackgroundColorTop());
							gc.fillGradientRectangle(bounds.x, 0, bounds.width, 19, true);
							gc.setBackground(colorManager.getSelectedButtonBackgroundColorBottom());
							gc.setForeground(colorManager.getSelectedButtonBackgroundColorMiddle());
							gc.fillGradientRectangle(bounds.x, 12, bounds.width, CustomButton.BUTTON_HEIGHT-12, true);									
						}
					}
				}
				break;
			}
			case IColorManager.SKIN_BLUE:
			case IColorManager.SKIN_OLIVE:
			case IColorManager.SKIN_SILVER:
			{
				if (!selected && !hover) {
					gc.setBackground(colorManager.getButtonBackgroundColorMiddle());
					gc.setForeground(colorManager.getButtonBackgroundColorBottom());
					gc.fillGradientRectangle(bounds.x, 0, bounds.width, CustomButton.BUTTON_HEIGHT, true);
				}
				else {
					if (selected && hover) {
						gc.setBackground(colorManager.getHoverSelectedButtonBackgroundColorMiddle());
						gc.setForeground(colorManager.getHoverSelectedButtonBackgroundColorBottom());
						gc.fillGradientRectangle(bounds.x, 0, bounds.width, CustomButton.BUTTON_HEIGHT, true);							
					}
					else {
						if (hover) {
							gc.setBackground(colorManager.getHoverButtonBackgroundColorMiddle());
							gc.setForeground(colorManager.getHoverButtonBackgroundColorBottom());
							gc.fillGradientRectangle(bounds.x, 0, bounds.width, CustomButton.BUTTON_HEIGHT, true);							
						}
						else {
							gc.setBackground(colorManager.getSelectedButtonBackgroundColorMiddle());
							gc.setForeground(colorManager.getSelectedButtonBackgroundColorBottom());
							gc.fillGradientRectangle(bounds.x, 0, bounds.width, CustomButton.BUTTON_HEIGHT, true);
						}
					}
				}
				
				break;
			}
		}		
	}
		
	public void paintImage(GC gc, IColorManager colorManager, ISettings settings, Rectangle bounds, boolean hover, boolean selected, Image image) {
		if (image == null)
			return;
		
		int imgHeight = image.getBounds().height;

		// draw image
		int topSpacer = (CustomButton.BUTTON_HEIGHT - imgHeight) / 2;		
		gc.drawImage(image, settings.getLeftButtonTextSpacing(), topSpacer);
	}

	public void paintText(GC gc, IColorManager colorManager, ISettings settings, Rectangle bounds, Rectangle imageBounds, boolean hover, boolean selected, String text) {
		if (text == null || text.length() == 0)
			return;
		
		int imgWidth = 0;
		if (imageBounds != null)
			imgWidth = imageBounds.width;		

		Point se = gc.stringExtent(text);
		int textTop = (CustomButton.BUTTON_HEIGHT - se.y) / 2;
		//int textWidth = se.x;
		
		Font oldFont = gc.getFont();
		Font newFont = settings.getButtonTextFont(oldFont);
		gc.setFont(newFont);
		
		// skip image if image is null (TODO: feature to let users align imageless button text with image button text?
		//int spaceNeeded = (imgWidth > 0 ? mLeftSpacer + imgWidth : 0) + mRightSpacer + textWidth;
		
		String textToUse = text;
		
		// out of bounds?
/*		if (spaceNeeded > bounds.width) {
			//TODO: truncate text
			char[] total = mText.toCharArray();
			StringBuffer fin = new StringBuffer();
			int loop = 0;
			while (gc.stringExtent(fin.toString()).x < (bounds.width - gc.stringExtent("...").x)) {
				fin.append(total[loop]);
				loop++;
			}
			fin.append("...");
			textToUse = fin.toString();
		}
*/				
		gc.setForeground(settings.getButtonTextColor());
		gc.drawString(textToUse, (imgWidth > 0 ? settings.getLeftButtonTextSpacing() + imgWidth : 0) + settings.getButtonTextImageSpacing(), textTop-1, true);
		
		// reset fonts
		newFont.dispose();
		gc.setFont(oldFont);
	}

}
