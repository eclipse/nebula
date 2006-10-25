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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.nebula.widgets.grid.internal.TextUtils;
import org.eclipse.swt.nebula.widgets.pgroup.internal.GraphicUtils;
import org.eclipse.swt.widgets.Control;

public class RedmondShelfRenderer extends AbstractRenderer {

	private int textMargin = 2;
	private int margin = 4;
	private PShelf parent;
	private int spacing = 8;
	
	private Font initialFont;
	private Font initialOpenFont;
	
	private Color gradient1;
	private Color gradient2;
	
	private Font font;
	private Font selectedFont;
	
	private Color selectedGradient1;
	private Color selectedGradient2;
	
	private Color hoverGradient1;
	private Color hoverGradient2;
	
	private Color lineColor;
	
	private Color selectedForeground;

	/** 
     * {@inheritDoc}
     */
    public Point computeSize(GC gc, int wHint, int hHint, Object value)
    {
        PShelfItem item = (PShelfItem)value;
        
		int h = 0;
		
		gc.setFont(font);
		
		if (item.getImage() == null){
			h = gc.getFontMetrics().getHeight() + (2*(textMargin));
		} else {
			h = Math.max(item.getImage().getBounds().height,gc.getFontMetrics().getHeight() + (2*textMargin));
		}
		
		gc.setFont(selectedFont);
		
		h = Math.max(h,gc.getFontMetrics().getHeight() + (2*textMargin));
		
		h += 2*margin;
		
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
        
        if ((parent.getStyle() & SWT.SIMPLE) == 0)
        {
            if (isSelected()){
                gc.setForeground(gradient1);
                gc.setBackground(gradient2);
            } else {
                if (isHover()){
                    gc.setForeground(hoverGradient1);
                    gc.setBackground(hoverGradient2);
                } else {
                    gc.setForeground(selectedGradient1);
                    gc.setBackground(selectedGradient2);
                }
            }
        }
        else
        {
            if (isSelected()){
                gc.setForeground(selectedGradient1);
                gc.setBackground(selectedGradient2);
            } else {
                if (isHover()){
                    gc.setForeground(hoverGradient1);
                    gc.setBackground(hoverGradient2);
                } else {
                    gc.setForeground(gradient1);
                    gc.setBackground(gradient2);
                }
            }
        }
		

        
		gc.fillGradientRectangle(getBounds().x,getBounds().y,getBounds().width,getBounds().height,true);		

        if ((parent.getStyle() & SWT.SIMPLE) != 0)
        {
    		if (!isSelected()){
    			gc.setForeground(lineColor);
    			gc.drawLine(0,getBounds().y,getBounds().width -1,getBounds().y);
    		}	
        }
		
        boolean imageLeft = true;
        
        if ((parent.getStyle() & SWT.SIMPLE) != 0)
        {
            imageLeft = !isSelected();
        }
        
		int x = 6;
		if (item.getImage() != null && imageLeft){
			int y2 = (getBounds().height - item.getImage().getBounds().height)/2;
			if ((getBounds().height - item.getImage().getBounds().height) % 2 != 0)
				y2 ++;
			
			gc.drawImage(item.getImage(),x,getBounds().y + y2);
			
			x += item.getImage().getBounds().width + spacing;
		}
		gc.setForeground(fore);
		
        if ((parent.getStyle() & SWT.SIMPLE) == 0)
        {
            gc.setFont(font);
            if (isSelected()){
                //gc.setForeground(selectedForeground);
            } else {
                gc.setForeground(selectedForeground);
            }
        }
        else
        {
    		if (isSelected()){
    			gc.setFont(selectedFont);
    			gc.setForeground(selectedForeground);
    		} else {
    			gc.setFont(font);
    		}
        }
		
		int y2 = (getBounds().height - gc.getFontMetrics().getHeight())/2;
		if ((getBounds().height - gc.getFontMetrics().getHeight()) % 2 != 0)
			y2 ++;
		
		int textWidth = getBounds().width - 12;
		if (item.getImage() != null){
			textWidth -= item.getImage().getBounds().width;
			textWidth -= 6;
		}
		
		gc.drawString(TextUtils.getShortString(gc,item.getText(),textWidth),x,getBounds().y +y2,true);
		
		if (item.getImage() != null && !imageLeft){
			int y3 = (getBounds().height - item.getImage().getBounds().height)/2;
			if ((getBounds().height - item.getImage().getBounds().height) % 2 != 0)
				y3 ++;
			
			gc.drawImage(item.getImage(),getBounds().width - 6 - item.getImage().getBounds().width,getBounds().y + y3);
		}
		
		if (isFocus()){
			gc.drawFocus(1,1,getBounds().width-2,getBounds().height-1);
		}
	}

	public void initialize(Control control) {
		this.parent = (PShelf)control;
		
        FontData fd = parent.getFont().getFontData()[0];
        initialFont = new Font(parent.getDisplay(), fd.getName(), fd.getHeight(),SWT.BOLD);
		//parent.setFont(initialFont);
		
		Color baseColor = parent.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT);
		
		gradient1 = GraphicUtils.createNewBlendedColor(baseColor,parent.getDisplay().getSystemColor(SWT.COLOR_WHITE),30);
		
		baseColor = GraphicUtils.createNewBlendedColor(parent.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),parent.getDisplay().getSystemColor(SWT.COLOR_WHITE),80);
		
		gradient2 = GraphicUtils.createNewSaturatedColor(baseColor,.01f);
		
		baseColor.dispose();
		
		lineColor = GraphicUtils.createNewSaturatedColor(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION),.02f);

		
		baseColor = parent.getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION);
		
		selectedGradient1 = GraphicUtils.createNewBlendedColor(baseColor,parent.getDisplay().getSystemColor(SWT.COLOR_WHITE),70);
		
		baseColor = GraphicUtils.createNewBlendedColor(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION),parent.getDisplay().getSystemColor(SWT.COLOR_BLACK),80);
		
		selectedGradient2 = GraphicUtils.createNewSaturatedColor(baseColor,.02f);
		
		baseColor.dispose();

		//initialOpenFont = FontUtils.createFont(parent.getFont(),4,SWT.BOLD);
		initialOpenFont = new Font(parent.getDisplay(),"Arial",12,SWT.BOLD);
		
		font = initialFont;
		selectedFont = initialOpenFont;
		
		selectedForeground = parent.getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT);
		
		baseColor = GraphicUtils.createNewReverseColor(parent.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND));
		
		hoverGradient1 = GraphicUtils.createNewBlendedColor(baseColor,parent.getDisplay().getSystemColor(SWT.COLOR_WHITE),30);
		
				
		Color baseColor2 = GraphicUtils.createNewBlendedColor(baseColor,parent.getDisplay().getSystemColor(SWT.COLOR_WHITE),99);
		
		hoverGradient2 = GraphicUtils.createNewSaturatedColor(baseColor2,.00f);
		
		baseColor2.dispose();
		baseColor.dispose();
	}

	public void dispose() {
		initialFont.dispose();
		gradient1.dispose();
		gradient2.dispose();
		lineColor.dispose();
		
		selectedGradient1.dispose();
		selectedGradient2.dispose();
		
		initialFont.dispose();
		initialOpenFont.dispose();
		
		hoverGradient1.dispose();
		hoverGradient2.dispose();
        
        super.dispose();
	}

	public Color getLineColor() {
		return lineColor;
	}

	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public Color getGradient1() {
		return gradient1;
	}

	public void setGradient1(Color gradient1) {
		this.gradient1 = gradient1;
	}

	public Color getGradient2() {
		return gradient2;
	}

	public void setGradient2(Color gradient2) {
		this.gradient2 = gradient2;
	}

	public Color getHoverGradient1() {
		return hoverGradient1;
	}

	public void setHoverGradient1(Color hoverGradient1) {
		this.hoverGradient1 = hoverGradient1;
	}

	public Color getHoverGradient2() {
		return hoverGradient2;
	}

	public void setHoverGradient2(Color hoverGradient2) {
		this.hoverGradient2 = hoverGradient2;
	}

	public Font getSelectedFont() {
		return selectedFont;
	}

	public void setSelectedFont(Font selectedFont) {
		this.selectedFont = selectedFont;
	}

	public Color getSelectedForeground() {
		return selectedForeground;
	}

	public void setSelectedForeground(Color selectedForeground) {
		this.selectedForeground = selectedForeground;
	}

	public Color getSelectedGradient1() {
		return selectedGradient1;
	}

	public void setSelectedGradient1(Color selectedGradient1) {
		this.selectedGradient1 = selectedGradient1;
	}

	public Color getSelectedGradient2() {
		return selectedGradient2;
	}

	public void setSelectedGradient2(Color selectedGradient2) {
		this.selectedGradient2 = selectedGradient2;
	}

}
