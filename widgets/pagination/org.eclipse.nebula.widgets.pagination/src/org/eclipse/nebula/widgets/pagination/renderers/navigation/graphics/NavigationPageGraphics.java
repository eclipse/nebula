/*******************************************************************************
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com>, Pascal Leclercq <pascal.leclercq@gmail.com>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo ZERR - initial API and implementation
 *     Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.pagination.renderers.navigation.graphics;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.nebula.widgets.pagination.PaginationHelper;
import org.eclipse.nebula.widgets.pagination.Resources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/**
 * This canvas draw navigation page with {@link GC} like this :
 * 
 * <pre>
 *  	Previous 1 2 ...10 Next
 * </pre>
 * 
 */
public class NavigationPageGraphics extends Canvas {

	private List<NavigationPageGraphicsItem> items;
	private NavigationPageGraphicsItem selectedItem;
	private Integer round = null;

	private Color selectedItemForeground;
	private Color selectedItemBackground;
	private Color selectedItemBorderColor;

	private Color itemForeground;
	private Color itemBackground;
	private Color itemBorderColor;

	private Color disabledItemForeground;
	private Color disabledItemBackground;
	private Color disabledItemBorderColor;

	private Integer totalWidth;
	private final NavigationPageGraphicsItem previousItem;
	private final NavigationPageGraphicsItem nextItem;

	public NavigationPageGraphics(Composite parent, int style) {
		this(parent, style, BlueNavigationPageGraphicsConfigurator
				.getInstance());
	}

	public NavigationPageGraphics(Composite parent, int style,
			INavigationPageGraphicsConfigurator configurator) {
		super(parent, style);
		// Create previous+next item.
		this.previousItem = new NavigationPageGraphicsItem(this,
				NavigationPageGraphicsItem.PREVIOUS);
		previousItem.setText(Resources.getText(
				Resources.PaginationRenderer_previous, Locale.getDefault()));
		this.nextItem = new NavigationPageGraphicsItem(this,
				NavigationPageGraphicsItem.NEXT);
		nextItem.setText(Resources.getText(Resources.PaginationRenderer_next,
				Locale.getDefault()));

		// Add paint listener to draw the navigation page
		this.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				onPaint(e.gc);
			}
		});

		// Add mouse listener to select a page item.
		this.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
				NavigationPageGraphicsItem selectedItem = getItem(e.x, e.y);
				if (selectedItem != null) {
					select(selectedItem);
				}
			}

			public void mouseDown(MouseEvent e) {
			}

			public void mouseDoubleClick(MouseEvent arg0) {
			}
		});

		if (configurator != null) {
			configurator.configure(this);
		}
	}

	private void onPaint(GC gc) {
		gc.setAdvanced(true);
		if (gc.getAdvanced())
			gc.setTextAntialias(SWT.ON);
		if (items == null) {
			return;
		}
		computeBoundsIfNeeded(gc);

		Color fg = getDisplay().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND);
		Color bg = getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

		boolean separator = false;
		int x, y, width, height = 0;
		boolean selected = false;
		boolean enabled = false;

		for (NavigationPageGraphicsItem pageItem : items) {
			selected = pageItem.equals(selectedItem);
			enabled = pageItem.isEnabled();
			separator = pageItem.isSeparator();

			x = pageItem.getBounds().x;
			y = pageItem.getBounds().y;
			width = pageItem.getBounds().width;
			height = pageItem.getBounds().height;

			// Fill rectangle
			Color filledRectangleColor = getFilledRectangleColor(selected,
					!separator ? enabled : true, bg);
			if (filledRectangleColor != null) {
				gc.setBackground(filledRectangleColor);
				if (round != null) {
					gc.fillRoundRectangle(x, y, width, height, round, round);
				} else {
					gc.fillRectangle(x, y, width, height);
				}
			}

			// Border rectangle
			if (!separator) {
				Color borderRectangleColor = getBorderRectangleColor(selected,
						enabled, bg);
				if (borderRectangleColor != null) {
					gc.setForeground(borderRectangleColor);
					if (round != null) {
						gc.drawRoundRectangle(x, y, width, height, round, round);
					} else {
						gc.drawRectangle(x, y, width, height);
					}
				}
			}

			// Foreground text
			Color textColor = getTextColor(selected, enabled);
			if (textColor != null) {
				gc.setForeground(textColor);
			} else {
				gc.setForeground(fg);
			}
			gc.drawString(pageItem.getText(), x + 3, y, true);
		}
	}

	private Color getFilledRectangleColor(boolean selected, boolean enabled,
			Color bg) {
		if (selected) {
			return selectedItemBackground;
		}
		if (!enabled) {
			return disabledItemBackground;
		}
		return itemBackground;
	}

	private Color getBorderRectangleColor(boolean selected, boolean enabled,
			Color bg) {
		if (selected) {
			return selectedItemBorderColor;
		}
		if (!enabled) {
			return disabledItemBorderColor;
		}
		return itemBorderColor;
	}

	private Color getTextColor(boolean selected, boolean enabled) {
		if (selected) {
			return selectedItemForeground;
		}
		if (!enabled) {
			return disabledItemForeground;
		}
		return itemForeground;
	}

	public void update(int[] pageIndexes, int currentPage) {
		update(pageIndexes, currentPage, Locale.getDefault());
	}
	
	public void update(int[] pageIndexes, int currentPage, Locale locale) {
		// Compute navigation item and updat ethe selected item.
		this.items = new ArrayList<NavigationPageGraphicsItem>(
				pageIndexes.length + 2);
		int index = -1;
		items.add(previousItem);
		NavigationPageGraphicsItem item = null;
		for (int i = 0; i < pageIndexes.length; i++) {
			index = pageIndexes[i];
			item = new NavigationPageGraphicsItem(this, index);
			if (index==PaginationHelper.SEPARATOR) {
				item.setText(Resources.getText(Resources.PaginationRenderer_separator, locale));
			}
			items.add(item);
			if (currentPage == index) {
				selectedItem = item;
			}
		}
		items.add(nextItem);
		// bounds must be recomputed.
		this.totalWidth = null;
		redraw();
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		checkWidget();

		if (wHint == SWT.DEFAULT || hHint == SWT.DEFAULT) {
			computeBoundsIfNeeded(null);
			if (totalWidth != null) {
				return new Point(totalWidth, 15);
			}
			return new Point(wHint, 15);
		}
		return super.computeSize(wHint, hHint, changed);
	}

	/**
	 * This method loop for item and update for each item their bound.
	 * 
	 * @param gc
	 *            graphic context used to compute the size font used in page
	 *            item. If GC is null, a new GC is created
	 */
	private void computeBoundsIfNeeded(GC gc) {
		if (items == null || totalWidth != null) {
			return;
		}
		GC tempGC = null;
		if (gc == null) {
			// GC null, create temporary GC
			tempGC = new GC(this);
			gc = tempGC;
		}
		this.totalWidth = 0;
		int x = 0;
		int y = 0;
		int width = 0;
		int height = 0;
		String text = null;
		// Loop for each page item.
		for (NavigationPageGraphicsItem pageItem : items) {
			text = pageItem.getText();
			// Get the size of the text of the current item.
			Point size = gc.stringExtent(text);

			width = size.x + 4;
			height = size.y;

			pageItem.setBounds(new Rectangle(x, y, width, height));
			x += width + 3;
		}
		// Updat ethe total width of this control
		totalWidth = x + width - 3;
		if (tempGC != null) {
			// Dispose temporary GC.
			tempGC.dispose();
		}
	}

	// NavigationPageGraphicsItem getItem(int index) {
	// for (NavigationPageGraphicsItem pageItem : pageItems) {
	// if (pageItem.getIndex() == index) {
	// return pageItem;
	// }
	// }
	// return null;
	// }

	/**
	 * Returns the item which match the given location point.Null if none item
	 * is selected or if item is disabled.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private NavigationPageGraphicsItem getItem(int x, int y) {
		checkWidget();

		if (items == null) {
			return null;
		}

		for (NavigationPageGraphicsItem pageItem : items) {
			if (pageItem.contains(x, y)) {
				// an item is selected
				if (!pageItem.isEnabled()) {
					// item is not enable, return null.
					return null;
				}
				// return the selected item.
				return pageItem;
			}
		}
		return null;
	}

	public void select(NavigationPageGraphicsItem pageItem) {
		if (!pageItem.isSeparator()) {
			// item selected is not the item '...'
			selectedItem = pageItem;
			redraw();
			getDisplay().update();
		}
		this.handleSelection(pageItem);
	}

	/**
	 * This method is called when page item (Next, Previous or page index) is
	 * selected. By default this method do nothing.
	 * 
	 * @param selectedItem
	 */
	protected void handleSelection(NavigationPageGraphicsItem selectedItem) {
		// Do nothing.
	}

	public void setSelectedItemBackground(Color selectedItemBackground) {
		this.selectedItemBackground = selectedItemBackground;
	}

	public Color getSelectedItemBackground() {
		return selectedItemBackground;
	}

	public void setSelectedItemForeground(Color selectedItemForeground) {
		this.selectedItemForeground = selectedItemForeground;
	}

	public Color getSelectedItemForeground() {
		return selectedItemForeground;
	}

	public Color getItemForeground() {
		return itemForeground;
	}

	public void setItemForeground(Color itemForeground) {
		this.itemForeground = itemForeground;
	}

	public Color getItemBackground() {
		return itemBackground;
	}

	public void setItemBackground(Color itemBackground) {
		this.itemBackground = itemBackground;
	}

	public Color getItemBorderColor() {
		return itemBorderColor;
	}

	public void setItemBorderColor(Color itemBorderColor) {
		this.itemBorderColor = itemBorderColor;
	}

	public Color getSelectedItemBorderColor() {
		return selectedItemBorderColor;
	}

	public void setSelectedItemBorderColor(Color selectedItemBorderColor) {
		this.selectedItemBorderColor = selectedItemBorderColor;
	}

	public Color getDisabledItemForeground() {
		return disabledItemForeground;
	}

	public void setDisabledItemForeground(Color disabledItemForeground) {
		this.disabledItemForeground = disabledItemForeground;
	}

	public Color getDisabledItemBorderColor() {
		return disabledItemBorderColor;
	}

	public void setDisabledItemBorderColor(Color disabledItemBorderColor) {
		this.disabledItemBorderColor = disabledItemBorderColor;
	}

	public void setDisabledItemBackground(Color disabledItemBackground) {
		this.disabledItemBackground = disabledItemBackground;
	}

	public Color getDisabledItemBackground() {
		return disabledItemBackground;
	}

	public void setConfigurator(INavigationPageGraphicsConfigurator configurator) {
		this.round = null;
		configurator.configure(this);
		redraw();
	}

	/**
	 * Set the round height/width.
	 * 
	 * @param round
	 */
	public void setRound(int round) {
		this.round = round;
	}

	/**
	 * Set text for previous, next item.
	 * 
	 * @param previousText
	 * @param nextText
	 */
	public void setText(String previousText, String nextText) {
		previousItem.setText(previousText);
		nextItem.setText(nextText);
		this.totalWidth = null;
		redraw();
	}

	/**
	 * Set enabled for previous, next item.
	 * 
	 * @param hasPreviousPage
	 * @param hasNextPage
	 */
	public void setEnabled(boolean hasPreviousPage, boolean hasNextPage) {
		previousItem.setEnabled(hasPreviousPage);
		nextItem.setEnabled(hasNextPage);
		redraw();
	}

}
