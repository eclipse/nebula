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

package org.eclipse.swt.nebula.widgets.cdatetime;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * A CButton is a class wich wraps an SWT Button to create a widget that acts as much like a
 * native Button as possible while adding the following features:
 * <ul>
 *  <li>The appearance of Label when the mouse is not over it and it does not have the focus or selection
 *  	(if style is SWT.TOGGLE).</li>
 *  <li>Can fit seemlessly into a larger visual piece - simple set the image to that of its background and 
 *  adjust the image's offset if necessary.</li>
 *  <li>Can draw polygons and ovals.</li>
 *  <li>Can center or otherwise align its visual display (text, image, polygon or oval).</li>
 * </ul>
 * @see CDateTime for some example uses
 */
public class CButton extends Composite {

	public static final boolean carbon = "carbon".equals(SWT.getPlatform());
	
	public static final int[] Points_OK 	= { 2,6, 5,9, 10,3, 9,2, 5,7, 3,5 };
	public static final int[] Points_Cancel = { 0,1, 3,4, 0,7, 1,8, 4,5, 7,8, 8,7, 5,4, 8,1, 7,0, 4,3, 1,0 };
	public static final int[] Points_Left 	= { 9,0, 4,5, 9,10 };
	public static final int[] Points_Right 	= { 2,0, 7,5, 2,10 };
	public static final int[] Points_Up		= { 10,8, 5,3, 0, 8 };
	public static final int[] Points_Down	= { 10,2, 5,7, 0, 2 };
	public static final int[] Points_Add 	= { 2,4, 4,4, 4,2, 5,2, 5,4, 7,4, 7,5, 5,5, 5,7, 4,7, 4,5, 2,5 };
//	{ 2,4, 6,4, 4,4, 4,2, 4,6, 4,4 };
	public static final int[] Points_Subtract 	= { 2,4, 7,4, 7,5, 2,5 };
//	{ 2,4, 6,4 };
	
	private Button button;
	private String text;
	private Image image;
	private int[] points;
	private Color fillColor;
	
	private int marginTop = 5;
	private int marginBottom = 5;
	private int marginLeft = 5;
	private int marginRight = 5;
	private int xAlign;
	private int yAlign;

	private boolean square = false;
	
	private Listener filter = new Listener() {
		public void handleEvent(Event event) {
			if(isDisposed()) return;
			if(CButton.this.getShell() == ((Control)event.widget).getShell()) {
				if(SWT.MouseMove == event.type) {
					if(event.widget.equals(CButton.this)) {
						button.setVisible(true);
					} else if(button.isVisible() && !button.getSelection() && !event.widget.equals(button)) {
						button.setVisible(false);
					}
				}
			}
		}
	};

	/**
	 * @param parent
	 * @param style
	 * the composite will ignore all style bits and be constructed with SWT.NONE
	 * <p>
	 * the button will only recognize types of either SWT.PUSH or SWT.TOGGLE
	 * <p>
	 * other styles:
	 * 	SWT.OK 		draws a check mark - green if fillColor is not given
	 * 	SWT.CANCEL 	draws an "X" - red if fillColor is not given
	 * 	SWT.ARROW with either SWT.LEFT or SWT.RIGHT draws an arrow (duh)
	 * </p>
	 */
	public CButton(Composite parent, int style) {
		this(parent, style, null);
	}
	public CButton(Composite parent, int style, Color fillColor) {
		super(parent, SWT.DOUBLE_BUFFERED);
		setLayout(new FillLayout());

		int bStyle;
		if(carbon || (style & SWT.TOGGLE) != 0) bStyle = SWT.TOGGLE;
		else bStyle = SWT.PUSH;
		
		button = new Button(this, bStyle | SWT.DOUBLE_BUFFERED);
		button.setVisible(false);

		if((style & SWT.OK) != 0) {
			setPolygon(Points_OK, (fillColor != null) ? fillColor : (fillColor = getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN)));
			setForeground(fillColor);
		} else if((style & SWT.CANCEL) != 0) {
			setPolygon(Points_Cancel, (fillColor != null) ? fillColor : (fillColor = getDisplay().getSystemColor(SWT.COLOR_DARK_RED)));
			setForeground(fillColor);
		} else if((style & SWT.ARROW) != 0) {
			if((style & SWT.DOWN) != 0) {
				setPolygon(Points_Down, (fillColor != null) ? fillColor : getForeground());
			} else if((style & SWT.LEFT) != 0) {
				setPolygon(Points_Left, (fillColor != null) ? fillColor : getForeground());
			} else if((style & SWT.RIGHT) != 0) {
				setPolygon(Points_Right, (fillColor != null) ? fillColor : getForeground());
			} else if((style & SWT.UP) != 0) {
				setPolygon(Points_Up, (fillColor != null) ? fillColor : getForeground());
			}
		} else if((style & SWT.UP) != 0) {
			setPolygon(Points_Add, (fillColor != null) ? fillColor : getForeground());
		} else if((style & SWT.DOWN) != 0) {
			setPolygon(Points_Subtract, (fillColor != null) ? fillColor : getForeground());
		}
		
		button.addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event event) {
				if(SWT.Paint == event.type && button.isVisible()) {
					paintControl(event);
				}
			}
		});

		if(carbon && (style & SWT.TOGGLE) == 0) {
			button.addListener(SWT.MouseUp, new Listener() {
				public void handleEvent(Event event) {
					if(SWT.MouseUp == event.type) {
						Display.getCurrent().asyncExec(new Runnable() {
							public void run() {
								if(!button.isDisposed()) button.setSelection(false);
							}
						});
					}
				}
			});
		}
		
		addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event event) {
				if(SWT.Paint == event.type && !button.isVisible()) {
					paintControl(event);
				}
			}
		});
		
		getDisplay().addFilter(SWT.MouseMove, filter);
		
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if(!getDisplay().isDisposed()) {
					getDisplay().removeFilter(SWT.MouseMove, filter);
				}
				disposeImage();
			}
		});
	}

	public void addListener(int eventType, Listener listener) {
		button.addListener(eventType, listener);
		super.addListener(eventType, listener);
	}
	
	public void addSelectionListener(SelectionListener listener) {
		button.addSelectionListener(listener);
	}
	
	public Point computeSize(int wHint, int hHint, boolean changed) {
		checkWidget ();
		if (wHint != SWT.DEFAULT && wHint < 0) wHint = 0;
		if (hHint != SWT.DEFAULT && hHint < 0) hHint = 0;

		Point size;
		
		if(text != null) {
			GC gc = new GC(this);
			Point tSize = gc.stringExtent(text);
			gc.dispose();
			size = new Point(tSize.x, tSize.y);
		} else if(image != null){
			Rectangle r = image.getBounds();
			size = new Point(r.width, r.height);
		} else if(points != null) {
			if(points.length > 2) {
				int minX = points[0];
				int maxX = points[0];
				int minY = points[1];
				int maxY = points[1];
				for(int i = 2; i < (points.length-1); i++) {
					minX = Math.min(minX, points[i]);
					maxX = Math.max(maxX, points[i]);
					minY = Math.min(minY, points[i+1]);
					maxY = Math.max(maxY, points[i+1]);
				}
				size = new Point(maxX-minX, maxY-minY);
			} else {
				size = new Point(points[0], points[1]);
			}
		} else {
			size = new Point(10,10);
		}
		
		size.x += (marginLeft+marginRight);
		size.y += (marginTop+marginBottom);
		
		if(wHint != SWT.DEFAULT) {
			size.x = Math.min(size.x, wHint);
		}
		if(hHint != SWT.DEFAULT) {
			size.y = Math.min(size.y, hHint);
		}
		
		if(square) {
			size.x = size.y = Math.max(size.x, size.y);
		}
		return size;
	}
	
	private void disposeImage() {
		if(image != null && !image.isDisposed()) image.dispose();
		image = null;
	}

	private void drawControl(Event e) {
		if(image != null) {
			Rectangle r = getClientArea();
			Rectangle size = image.getBounds();
			if(e.widget instanceof Button) {
				int x = r.width-marginLeft-marginRight;
				int y = r.height-marginTop-marginBottom;
				Image img = new Image(getDisplay(), x, y);
				GC gc = new GC(img);
				gc.drawImage(image,
						-(size.width-r.width) / 2 - marginLeft - ix,
						-(size.height-r.height) / 2 - marginTop - iy
						);
				e.gc.drawImage(img, marginLeft, marginTop);
				gc.dispose();
				img.dispose();
			} else {
				e.gc.drawImage(
						image,
						(r.width-size.width) / 2 - ix,
						(r.height-size.height) / 2 - iy
						);
			}
		} 
		
		if(points != null && points.length > 0) {
			e.gc.setAntialias(SWT.ON);
			Rectangle r = getClientArea();
			int minX = (points.length > 2) ? points[0] : 0;
			int maxX = points[0];
			int minY = (points.length > 2) ? points[1] : 0;
			int maxY = points[1];
			for(int i = 2; i < (points.length-1); i++) {
				minX = Math.min(minX, points[i]);
				maxX = Math.max(maxX, points[i]);
				minY = Math.min(minY, points[i+1]);
				maxY = Math.max(maxY, points[i+1]);
			}
			double x;
			if(xAlign == SWT.LEFT) {
				x = marginLeft;
			} else if(xAlign == SWT.RIGHT) {
				x = r.width - maxX - marginRight;
			} else { // CENTERED / Default
				x = ((r.width - (maxX-minX)) / 2);
			}
			double y;
			if(yAlign == SWT.TOP) {
				y = marginTop;
			} else if(yAlign == SWT.BOTTOM) {
				y = r.height - maxY - marginBottom;
			} else { // CENTERED / Default
				y = ((r.height - (maxY-minY)) / 2);
			}

			int[] data = new int[points.length];
			for (int i = 0; i < points.length; i += 2) {
				data[i] = points[i] + (int) x - minX;
			}
			for (int i = 1; i < data.length; i += 2) {
				data[i] = points[i] + (int) y - minY;
			}
			if(fillColor != null && !fillColor.isDisposed()) {
				e.gc.setBackground(fillColor);
				if(points.length > 2) {
					e.gc.fillPolygon(data);
				} else {
					e.gc.fillOval((int)x, (int)y, points[0], points[1]);
				}
			}
			if(points.length > 2) {
				e.gc.drawPolygon(data);
			} else {
				e.gc.drawOval((int)x, (int)y, points[0], points[1]);
			}
		}

		if(text != null) {
			e.gc.setTextAntialias(SWT.ON);

			Rectangle r = getClientArea();
			Point size = e.gc.textExtent(text);
			int x;
			if(xAlign == SWT.LEFT) {
				x = marginLeft;
			} else if(xAlign == SWT.RIGHT) {
				x = r.width - size.x - marginRight;
			} else { // CENTERED / Default
				x = (r.width-size.x) / 2;
			}
			int y;
			if(yAlign == SWT.TOP) {
				y = marginTop;
			} else if(yAlign == SWT.BOTTOM) {
				y = r.height - size.y - marginBottom;
			} else { // CENTERED / Default
				y = (r.height-size.y) / 2;
			}

//			if(ACW.win32 && e.widget == button){
//				Image img = new Image(e.display, e.width, e.height);
//				e.gc.copyArea(img, e.x, e.y);
//				Pattern p = new Pattern(e.display, img);
//
//				e.gc.setBackgroundPattern(p);
//				e.gc.drawText(text, x, y);
//
//				img.dispose();
//				p.dispose();
//			} else {
				e.gc.drawText(text, x, y, true);
//			}
		} 
	}
	
	public Button getButton() {
		return button;
	}
	
	public boolean getSelection() {
		return button.getSelection();
	}
	
	public String getText() {
		return text;
	}

	public boolean isDisposed() {
		return (super.isDisposed() || ((button != null) && button.isDisposed()));
	}
	
	public boolean isSquare() {
		return square;
	}
	
	private void paintControl(Event e) {
		if(e.widget == button) {
			Point mloc = button.toControl(getDisplay().getCursorLocation());
			if(button.getSelection() || button.getBounds().contains(mloc)) {
				if(!button.isVisible()) button.setVisible(true);
				drawControl(e);
			} else {
				if(button.isVisible()) button.setVisible(false);
				redraw();
			}
		} else {
			drawControl(e);
		}
	}

	public void removeListener(int eventType, Listener handler) {
		super.removeListener(eventType, handler);
		button.removeListener(eventType, handler);
	}

	public void removeSelectionListener(SelectionListener listener) {
		button.removeSelectionListener(listener);
	}

	public void setAlignment(int x, int y) {
		xAlign = x;
		yAlign = y;
	}
	
	public void setBackground(Color color) {
		button.setBackground(color);
		super.setBackground(color);
	}

	public void setData(Object data) {
		button.setData(data);
		super.setData(data);
	}
	
	public void setData(String key, Object value) {
		button.setData(key, value);
		super.setData(key, value);
	}
	
	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor; //new Color(getDisplay(), fillColor.getRGB());
	}
	
	public void setForeground(Color color) {
		button.setForeground(color);
		super.setForeground(color);
	}
	
	private int ix = 0;
	private int iy = 0;;
	public void setImage(Image image, int x, int y) {
		ix = x;
		iy = y;
		setImage(image);
	}
	public void setImage(Image image) {
		disposeImage();
		this.image = new Image(getDisplay(), image.getImageData());
		redraw();
	}

	public void setMargins(int marginWidth, int marginHeight) {
		setMargins(marginWidth, marginWidth, marginHeight, marginHeight);
	}
	
	public void setMargins(int left, int right, int top, int bottom) {
		if(left >= 0) marginLeft = left;
		if(right >= 0) marginRight = right;
		if(top >= 0) marginTop = top;
		if(bottom >= 0) marginBottom = bottom;
	}
	
	public void setPolygon(int[] points) {
		setPolygon(points, fillColor != null ? fillColor : getForeground());
	}

	public void setPolygon(int[] points, Color fillColor) {
		if(points.length < 2 || points.length % 2 != 0) return;
		this.points = points;
		this.fillColor = fillColor;
		redraw();
	}

	public void setSelection(boolean selected) {
		if(button.getVisible() != selected) button.setVisible(selected);
		if(button.getSelection() != selected) button.setSelection(selected);
	}

	/**
	 * if param equal is true, the x and y sizes of this CButton will be forced equal,
	 * thus drawing a square button
	 * @param equal
	 */
	public void setSquare(boolean equal) {
		square = equal;
	}

	public void setText(String text) {
		this.text = text;
		if(button.isVisible()) {
			button.redraw();
		} else {
			redraw();
		}
	}

	public void setToolTipText(String string) {
		button.setToolTipText(string);
		super.setToolTipText(string);
	}
	
	public void widgetDefaultSelected(SelectionEvent e) {
		if(!button.isVisible()) {
			button.setVisible(true);
		}
	}
	
	public void widgetSelected(SelectionEvent e) {
		if(!button.isVisible()) {
			button.setVisible(true);
		}
	}
}
