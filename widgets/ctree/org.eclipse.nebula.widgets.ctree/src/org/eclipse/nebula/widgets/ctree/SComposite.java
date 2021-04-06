/****************************************************************************
 * Copyright (c) 2005-2006 Jeremy Dowdall
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.nebula.widgets.ctree;

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

	/**
	 * Style setting indicating that Border should NOT be drawn
	 */
	public static int NONE		= 0;
	/**
	 * Style setting indicating that a Border should be drawn
	 */
	public static int BORDER 	= 1 << 0;
	/**
	 * Style setting indicating that the Border should be drawn with a Flat appearance,
	 * otherwise the border will have a subtle raised appearance.
	 */
	public static int FLAT		= 1 << 1;
	/**
	 * Style setting indicating that the Border should be drawn with Square corners,
	 * otherwise the corners will be rounded.
	 */
	public static int SQUARE	= 1 << 2;

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
		borderWidth = 0;

		updateBackground();
		setBackground(parent.getBackground());

		// setup graphics support
		GC gc = new GC(Display.getCurrent());
		gc.setAdvanced(true);
		advancedGraphics = gc.getAdvanced();
		gc.dispose();

		setLayout(new GridLayout());
		setMargins(0,0);

		addListener(SWT.Paint, this);
	}

	public int getBorderStyle() {
		return borderStyle;
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	public void handleEvent(Event event) {
		if(event.type == SWT.Paint) {
			paintControl(event.gc);
		}
	}

	private void paintControl(GC gc) {
		if((borderStyle & BORDER) != 0) {
			if(advancedGraphics) {
				gc.setAntialias(SWT.ON);
			}

			gc.setLineWidth(borderWidth);

			Rectangle r = getClientArea();

			if((borderStyle & SQUARE) != 0) {
				if((borderStyle & FLAT) == 0) {
					gc.setForeground(WHITE);
					gc.drawLine(r.x+1,r.y+1, r.x+1,r.y+r.height-3);
					gc.drawLine(r.x+1,r.y+1, r.x+r.width-3,r.y+1);
				}
				gc.setForeground(borderColor);
				gc.drawRectangle(r.x,r.y, r.width-1,r.height-1);
			} else {
				if((borderStyle & FLAT) == 0) {
					gc.setForeground(WHITE);
					gc.drawLine(r.x+1,r.y+1, r.x+1,r.y+r.height-3);
					gc.drawLine(r.x+1,r.y+1, r.x+r.width-3,r.y+1);
				}
				gc.setForeground(borderColor);
				gc.drawRoundRectangle(r.x+(borderWidth/2),r.y+(borderWidth/2), r.width-borderWidth,r.height-borderWidth, borderWidth*5, borderWidth*5);
			}
		}
	}

	/**
	 * The setBackground method of an SComposite does not actually set its own
	 * background color, as a typical Composite does, but rather the color of its border.
	 * This enables its parent to set the background color on all child controls with an 
	 * identical call: <code>((Control) child).setBackground(color)</code>, and have the
	 * SComposite do its job of drawing a color coordinated border.<br />
	 * To set the actual background of the SComposite, see the <code>updateBackground</code>
	 * method. 
	 * @see #updateBackground()
	 * {@inheritDoc}
	 */
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

	/**
	 * Convenience method; simply calls setBorderStyle(style) and then setBorderWidth(width).
	 * @param style
	 * @param width
	 * @see #setBorderStyle(int)
	 * @see #setBorderWidth(int)
	 */
	public void setBorder(int style, int width) {
		setBorderStyle(style);
		setBorderWidth(width);
	}

	/**
	 * Set the style of border to be drawn.<br />
	 * If turning the border OFF after it has been ON, the margins may need to be reset.<br />
	 * Valid Styles:
	 * <ul>
	 * 	<li>NONE (default)</li>
	 * 	<li>BORDER</li>
	 * 	<li>FLAT</li>
	 * 	<li>SQUARE</li>
	 * </ul>
	 * @param style
	 * @see #setMargins(int, int)
	 */
	public void setBorderStyle(int style) {
		if(borderStyle != style) {
			borderStyle = style;
			updateLayout();
		}
	}

	/**
	 * Set the line width of the border to be drawn.
	 * @param width
	 */
	public void setBorderWidth(int width) {
		if(borderWidth != width) {
			borderWidth = width;
			updateLayout();
		}
	}

	/**
	 * Sets the layout ONLY if layout is a GridLayout
	 * and makes sure that there is room for the borders by setting the margins as
	 * shown below:
	 * <ol>
	 *  <li>BORDER:  (borderWidth+1) pixels all around (arc gets cut otherwise)</li>
	 *  <li>BORDER | FLAT:  (borderWidth+1) pixels all around</li>
	 *  <li>BORDER | SQUARE: (borderWidth+1) pixels left & top, borderWidth pixels right & bottom</li>
	 * 	<li>BORDER | SQUARE | FLAT: borderWidth pixels all around</li>
	 * </ol>
	 */
	public void setLayout(Layout layout) {
		if((layout != null) && (layout instanceof GridLayout)) {
			super.setLayout(layout);
			updateLayout();
		}
	}

	/**
	 * Set the margin for the layout.  Internally this method calls setLayout(layout)
	 * so that the border is guaranteed to show properly.
	 * Call this method AFTER setting the border style.
	 * @param width
	 * @param height
	 * @see #setLayout(Layout)
	 */
	public void setMargins(int width, int height) {
		GridLayout layout = (GridLayout) getLayout();
		layout.marginWidth = width;
		layout.marginHeight = height;
		updateLayout();
	}

	/**
	 * Set the color of the border to be drawn when the cell is NOT selected.<br />
	 * @param color
	 */
	public void setNormalBorderColor(Color color) {
		normalBorderColor = color;
	}

	/**
	 * This method to sets the actual background of this SComposite
	 * to match that of its parent.  Use when the parent's background has been changed.
	 */
	public void updateBackground() {
		super.setBackground(getParent().getBackground());
	}

	private void updateLayout() {
		GridLayout gridlayout = (GridLayout) getLayout();
		if((borderStyle & BORDER) != 0) {
			if((borderStyle & SQUARE) != 0) {
				if(gridlayout.marginHeight < (borderWidth+1)) {
					if(gridlayout.marginTop 	< (borderWidth+1)) gridlayout.marginTop    = (borderWidth+1); 
					if(gridlayout.marginBottom 	< (borderWidth+1)) gridlayout.marginBottom = (borderWidth+1); 
				}
				if(gridlayout.marginWidth < (borderWidth+1)) {
					if(gridlayout.marginLeft  < (borderWidth+1)) gridlayout.marginLeft  = (borderWidth+1); 
					if(gridlayout.marginRight < (borderWidth+1)) gridlayout.marginRight = (borderWidth+1); 
				}
			} else {
				if((borderStyle & FLAT) != 0) {
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
		}
	}
}
