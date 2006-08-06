/****************************************************************************
* Copyright (c) 2005-2006 Jeremy Dowdall
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
*****************************************************************************/

package org.eclipse.swt.nebula.widgets.ctabletree.ccontainer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;


/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p>
 */
public class SComposite extends Canvas implements Listener {

	private static Color WHITE = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	
	private Color	borderColor;
	private int		borderStyle;
	private int		borderWidth;
	private Color	normalBorderColor;
	private boolean advancedGraphics;

	/**
	 * styles:
	 *  default is a Raised Rounded Corner border
	 *  SWT.FLAT - flat
	 *  SWT.SIMPLE - rectangular corners
	 * @param parent
	 * @param style
	 */
	public SComposite(Composite parent) {
		this(parent, SWT.NONE);
	}
	public SComposite(Composite parent, int style) {
		super(parent, SWT.NONE);

		borderStyle = style;

		setTrueBackground(parent.getBackground());
		setBackground(parent.getBackground());
		
		// setup graphics support
		GC gc = new GC(Display.getCurrent());
		gc.setAdvanced(true);
		advancedGraphics = gc.getAdvanced();
		gc.dispose();
		
		setLayout(new GridLayout());
		
		addListener(SWT.Paint, this);
	}
	
	private void paintControl(GC gc) {
		if((borderStyle & SWT.BORDER) != 0) {
			if(advancedGraphics) {
				gc.setAntialias(SWT.ON);
			}

			gc.setLineWidth(borderWidth);
			
			Rectangle r = getClientArea();
			
			if((borderStyle & SWT.SIMPLE) != 0) {
				if((borderStyle & SWT.FLAT) == 0) {
					gc.setForeground(WHITE);
					gc.drawLine(r.x+1,r.y+1, r.x+1,r.y+r.height-3);
					gc.drawLine(r.x+1,r.y+1, r.x+r.width-3,r.y+1);
				}
				gc.setForeground(borderColor);
				gc.drawRectangle(r.x,r.y, r.width-1,r.height-1);
			} else {
				if((borderStyle & SWT.FLAT) == 0) {
					gc.setForeground(WHITE);
					gc.drawLine(r.x+1,r.y+1, r.x+1,r.y+r.height-3);
					gc.drawLine(r.x+1,r.y+1, r.x+r.width-3,r.y+1);
				}
				gc.setForeground(borderColor);
				gc.drawRoundRectangle(r.x+(borderWidth/2),r.y+(borderWidth/2), r.width-borderWidth,r.height-borderWidth, borderWidth*5, borderWidth*5);
			}
		}
	}

	public void setBackground(Color color) {
		if(getBackground().getRGB().equals(color.getRGB())) {
			if(normalBorderColor == null) {
				normalBorderColor = getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
			}
			borderColor = normalBorderColor;
		} else {
			borderColor = color;
		}
	}

	public void setNormaBorderColor(Color color) {
		normalBorderColor = color;
	}
	
	public void setTrueBackground(Color color) {
		super.setBackground(color);
	}
	
	public int getBorderStyle() {
		return borderStyle;
	}
	
	public void setBorderStyle(int style) {
		if(borderStyle != style) {
			GridLayout layout = (GridLayout) getLayout();
			setLayout(layout);
			borderStyle = style;
			redraw();
		}
	}

	public int getBorderWidth() {
		return borderWidth;
	}
	
	public void setBorderWidth(int width) {
		if(borderWidth != width) {
			borderWidth = width;
			setLayout(getLayout());
		}
	}
	
	/**
	 * sets the layout ONLY if layout is a GridLayout
	 * and makes sure there's room for the borders
	 *  1. Square: (borderWidth+1) pixels left & top, borderWidth pixels right & bottom
	 *  2. Flat Square: borderWidth pixels all around
	 *  3. Round:  (borderWidth+1) pixels all around (arc gets cut otherwise)
	 *  4. Flat Round:  (borderWidth+1) pixels all around
	 */
	public void setLayout(Layout layout) {
		if((layout != null) && (layout instanceof GridLayout)) {
			GridLayout gridlayout = (GridLayout) layout;

			if((borderStyle & SWT.BORDER) != 0) {
				if((borderStyle & SWT.SIMPLE) != 0) {
					if(gridlayout.marginHeight < (borderWidth+1)) {
						if(gridlayout.marginTop 	< (borderWidth+1)) gridlayout.marginTop    = (borderWidth+1); 
						if(gridlayout.marginBottom 	< (borderWidth+1)) gridlayout.marginBottom = (borderWidth+1); 
					}
					if(gridlayout.marginWidth < (borderWidth+1)) {
						if(gridlayout.marginLeft  < (borderWidth+1)) gridlayout.marginLeft  = (borderWidth+1); 
						if(gridlayout.marginRight < (borderWidth+1)) gridlayout.marginRight = (borderWidth+1); 
					}
				} else 
					if((borderStyle & SWT.FLAT) != 0) {
						if(gridlayout.marginHeight < borderWidth) {
							if(gridlayout.marginTop 	< borderWidth) gridlayout.marginTop    = borderWidth; 
							if(gridlayout.marginBottom 	< borderWidth) gridlayout.marginBottom = borderWidth; 
						}
						if(gridlayout.marginWidth < borderWidth) {
							if(gridlayout.marginLeft  < borderWidth) gridlayout.marginLeft  = borderWidth; 
							if(gridlayout.marginRight < borderWidth) gridlayout.marginRight = borderWidth; 
						}
					} else {
						if(gridlayout.marginHeight < (borderWidth+1)) {
							if(gridlayout.marginTop 	< (borderWidth+1)) gridlayout.marginTop 	= (borderWidth+1); 
							if(gridlayout.marginBottom 	< (borderWidth+1)) gridlayout.marginBottom  = (borderWidth+1); 
						}
						if(gridlayout.marginWidth < (borderWidth+1)) {
							if(gridlayout.marginLeft  < (borderWidth+1)) gridlayout.marginLeft  = (borderWidth+1); 
							if(gridlayout.marginRight < (borderWidth+1)) gridlayout.marginRight = (borderWidth+1); 
						}
					}
			}
			super.setLayout(layout);
			layout(true, true);
		}
	}
	
	public void handleEvent(Event event) {
		if(event.type == SWT.Paint) {
			paintControl(event.gc);
		}
	}
}
