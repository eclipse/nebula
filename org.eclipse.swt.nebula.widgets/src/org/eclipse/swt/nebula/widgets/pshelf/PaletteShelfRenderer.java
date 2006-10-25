/*******************************************************************************
 * Copyright (c) 2006 Chris Gross. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: schtoo@schtoo.com(Chris Gross) - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.nebula.widgets.pshelf;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 *
 * @author cgross
 */
public class PaletteShelfRenderer extends AbstractRenderer {
	
	private int textMargin = 2;
	private int margin = 2;
	private PShelf parent;
	private int spacing = 4;
	
	private Color shadeColor;


	/** 
     * {@inheritDoc}
     */
    public Point computeSize(GC gc, int wHint, int hHint, Object value)
    {
        PShelfItem item = (PShelfItem)value;
        
		if (item.getImage() == null)
			return new Point(wHint,gc.getFontMetrics().getHeight() + (2*(margin+textMargin)));
		
		int h = Math.max(item.getImage().getBounds().height,gc.getFontMetrics().getHeight() + (2*textMargin)) + (2*margin);
		
		if (h % 2 != 0)
			h ++;
		
		return new Point(wHint,h);
	}
	
    
	/** 
     * {@inheritDoc}
     */
    public void paint(GC gc, Object value)
    {
        PShelfItem item = (PShelfItem)value;
        
		//Color back = parent.getBackground();
		Color fore = parent.getForeground();
		
		gc.fillRectangle(0,getBounds().y,getBounds().width-1,getBounds().height-1);		
		
        gc.setForeground(shadeColor);

		gc.fillGradientRectangle(0,getBounds().y,40,getBounds().height-1,false);
		
		gc.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		gc.drawLine(0,getBounds().y,0,getBounds().y +getBounds().height -1);
		gc.drawLine(0,getBounds().y,getBounds().width -1,getBounds().y);
		
		gc.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		gc.drawLine(0,getBounds().y +getBounds().height -1,getBounds().width-1,getBounds().y +getBounds().height-1);
		gc.drawLine(getBounds().width-1,getBounds().y,getBounds().width-1,getBounds().y +getBounds().height-1);
		
		int x = 6;
		if (item.getImage() != null){
			int y2 = (getBounds().height - item.getImage().getBounds().height)/2;
			if ((getBounds().height - item.getImage().getBounds().height) % 2 != 0)
				y2 ++;
			
			gc.drawImage(item.getImage(),x,getBounds().y + y2);
			
			x += item.getImage().getBounds().width + spacing;
		}
		gc.setForeground(fore);
		
		int y2 = (getBounds().height - gc.getFontMetrics().getHeight())/2;
		if ((getBounds().height - gc.getFontMetrics().getHeight()) % 2 != 0)
			y2 ++;
		
//		if (center){
//			x = (width - gc.stringExtent(item.getText()).x)/2;
//		}
        
        if (isHover() && !isSelected())
            gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_LIST_SELECTION));
		
		gc.drawString(item.getText(),x,getBounds().y +y2,true);
		
		if (isFocus()){
			gc.drawFocus(1,1,getBounds().width-2,getBounds().height-1);
		}
	}
	
	public void initialize(Control control){
		this.parent = (PShelf)control;
		shadeColor = parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
	}

	public Color getShadeColor() {
		return shadeColor;
	}

	public void setShadeColor(Color shadeColor) {
		this.shadeColor = shadeColor;
	}

}
