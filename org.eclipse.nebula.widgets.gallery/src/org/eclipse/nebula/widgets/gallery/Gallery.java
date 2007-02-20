/*******************************************************************************
 * Copyright (c) 2006-2007 Nicolas Richeton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors :
 *    Nicolas Richeton (nicolas.richeton@gmail.com) - initial API and implementation
 *******************************************************************************/ 
 package org.eclipse.nebula.widgets.gallery;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.TypedListener;

/**
 * SWT Widget that displays a picture gallery<br/> see
 * http://nicolas.richeton.free.fr/swtgallery This class must be compatible with
 * jdk-1.4
 * 
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 *
 * @author Nicolas Richeton (nicolas.richeton@gmail.com)
 */

public class Gallery extends Canvas {

	static final int DEFAULT_SIZE = 96;

	protected static boolean DEBUG = false;

	Item[] items = null;

	ArrayList selection = new ArrayList();

	boolean virtual = false;

	boolean autoMargins = true;

	int userMargin = 10;

	boolean vertical = true;

	int count = 0;

	int lastIndexOf = 0;

	int itemWidth = DEFAULT_SIZE;

	int itemHeight = DEFAULT_SIZE;

	int interpolation = SWT.LOW;

	int antialias = SWT.NONE;

	// Internals
	private int hCount = 0;

	private int vCount = 0;

	private int gHeight = 0;

	private int gWidth = 0;

	private int realMargin = 0;

	private int lastSingleClick = -1;

	private Color backgroundColor;

	private int translateY = 0;

	private int translateX = 0;

	AbstractGalleryItemRenderer itemRenderer;

	AbstractGalleryGroupRenderer groupRenderer;

	// Optimisation
	int itemWidthPlusMargin = itemWidth + realMargin;

	int itemHeightPlusMargin = itemHeight + realMargin;

	/**
	 * Return item count
	 * 
	 * @return
	 */
	public int getItemCount() {
		checkWidget();
		if (virtual)
			return count;

		if (items == null)
			return 0;

		return items.length;
	}

	/**
	 * Set item count. Only work in VIRTUAL mode.
	 * 
	 * @return
	 */
	public void setItemCount(int count) {
		checkWidget();

		if (DEBUG)
			System.out.println("setCount" + count);

		if (virtual) {
			this.count = count;
			// TODO: do not clear the table here
			if (count == 0)
				items = null;
			else
				items = new Item[count];

			updateStructuralValues();
			syncScrollBars();
			redraw();
		}
	}

	public AbstractGalleryItemRenderer getItemRenderer() {
		checkWidget();
		return itemRenderer;
	}

	public void setItemRenderer(AbstractGalleryItemRenderer itemRenderer) {
		checkWidget();

		if (this.itemRenderer != null)
			this.itemRenderer.dispose();

		this.itemRenderer = itemRenderer;
	}

	/**
	 * Add selection listener
	 * 
	 * @param listener
	 */
	public void addSelectionListener(SelectionListener listener) {
		checkWidget();
		addListener(SWT.Selection, new TypedListener(listener));
	}

	/**
	 * Remove selection listener
	 * 
	 * @param listener
	 */
	public void removeSelectionListener(SelectionListener listener) {
		checkWidget();
		removeListener(SWT.Selection, listener);
	}

	private boolean isSelected(int index) {
		return selection.contains(new Integer(index));
	}

	/**
	 * Toggle item selection status
	 * 
	 * @param i
	 * @param selected
	 */
	private void setSelected(int i, boolean selected) {
		if (selected) {
			if (!isSelected(i)) {
				selection.add(new Integer(i));
				notifySelectionListeners(getItem(i), i);
			}

		} else {
			if (isSelected(i)) {
				selection.remove(new Integer(i));
			}
		}
	}

	/**
	 * Send a selection event for a gallery item
	 * 
	 * @param item
	 */
	private void notifySelectionListeners(GalleryItem item, int index) {

		Event e = new Event();
		e.widget = this;
		e.item = item;
		e.data = item.getData();
		// e.index = index;
		try {
			notifyListeners(SWT.Selection, e);
		} catch (RuntimeException e1) {
		}
	}

	/**
	 * Select all items
	 */
	private void selectAll() {
		for (int i = 0; i < getItemCount(); i++) {
			setSelected(i, true);
		}
		redraw();
	}

	/**
	 * Create a Gallery
	 * 
	 * 
	 * @param parent
	 * @param style -
	 *            SWT.VIRTUAL switches in virtual mode.
	 */
	public Gallery(Composite parent, int style) {
		super(parent, style | SWT.V_SCROLL | SWT.H_SCROLL | SWT.NO_BACKGROUND | SWT.DOUBLE_BUFFERED);
		virtual = (style & SWT.VIRTUAL) > 0;

		backgroundColor = Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND);

		itemRenderer = new DefaultGalleryItemRenderer();

		this.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				if (itemRenderer != null)
					itemRenderer.dispose();
			}
		});

		updateStructuralValues();
		initScrollBars();
		syncScrollBars();

		// resize listener
		addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent event) {
				updateStructuralValues();
				syncScrollBars();
			}
		});

		// paint listener
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {

				if (DEBUG) {
					System.out.println("Paint :  " + event.x + " " + event.y + " " + event.width + " " + event.height);
				}
				paint(event.gc);
			}
		});

		// Key listener
		addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if (DEBUG) {
					System.out.println("keyPressed , statemask :" + e.stateMask);
					System.out.println("keyPressed , statemask :" + (e.stateMask & SWT.MOD4));
					System.out.println("keyPressed , statemask :" + (e.stateMask & SWT.MOD3));
					System.out.println("keyPressed , statemask :" + (e.stateMask & SWT.MOD2));
					System.out.println("keyPressed , statemask :" + (e.stateMask & SWT.MOD1));
				}

				if ((e.stateMask & SWT.CTRL) != 0 && (e.keyCode == 'a' || e.keyCode == 'A')) {
					// TODO: Handle Apple+A on MacOS
					if (DEBUG)
						System.out.println("Crtl+A");
					selectAll();
				}

				if (e.keyCode == SWT.ARROW_DOWN) {
					if (selection.size() == 1) {

						Integer nb = (Integer) selection.get(0);
						if (nb + hCount < count) {
							setSelected(nb, false);
							setSelected(nb + hCount, true);
							showIndex(nb + hCount);
							redraw();
						}
					}
				}

				if (e.keyCode == SWT.ARROW_UP) {
					if (selection.size() == 1) {
						Integer nb = (Integer) selection.get(0);
						if (nb - hCount >= 0) {
							setSelected(nb, false);
							setSelected(nb - hCount, true);
							showIndex(nb - hCount);
							redraw();
						}
					}
				}

				if (e.keyCode == SWT.ARROW_LEFT) {
					if (selection.size() == 1) {
						Integer nb = (Integer) selection.get(0);
						Point pos = getItemPosition(nb);
						if (pos.x > 0) {
							setSelected(nb, false);
							setSelected(nb - 1, true);
							showIndex(nb - 1);
							redraw();
						}
					}
				}

				if (e.keyCode == SWT.ARROW_RIGHT) {
					if (selection.size() == 1) {
						Integer nb = (Integer) selection.get(0);
						Point pos = getItemPosition(nb);
						if (pos.x + 1 < hCount && nb + 1 < count) {
							setSelected(nb, false);
							setSelected(nb + 1, true);
							showIndex(nb + 1);
							redraw();
						}
					}
				}

			}

			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
			}
		});

		// Mouse listener
		addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {
				GalleryItem item = getItem(new Point(e.x, e.y));
				if (item != null) {
					// TODO: Handle double click.

				}

			}

			public void mouseDown(MouseEvent e) {
				if (DEBUG)
					System.out.println("Mouse down ");
				if (e.button == 1) {
					GalleryItem item = getItem(new Point(e.x, e.y));

					if (item != null) {
						if ((e.stateMask & SWT.CTRL) == 0 && (e.stateMask & SWT.SHIFT) == 0) {

							if (!isSelected(indexOf(item))) {
								if (DEBUG)
									System.out.println("clear");
								selection.clear();

								if (DEBUG)
									System.out.println("setSelected");
								setSelected(indexOf(item), true);

								lastSingleClick = indexOf(item);
								redraw();
							}
						}
					}
				} else if (e.button == 3) {
					if (DEBUG)
						System.out.println("right clic");
					GalleryItem item = getItem(new Point(e.x, e.y));
					if (!isSelected(indexOf(item))) {
						selection.clear();
						setSelected(indexOf(item), true);
						redraw();
					}

				}

			}

			public void mouseUp(MouseEvent e) {
				if (DEBUG)
					System.out.println("Mouse Up ");
				if (e.button == 1) {
					GalleryItem item = getItem(new Point(e.x, e.y));
					int itemIndex = indexOf(item);

					if ((e.stateMask & SWT.CTRL) > 0) {
						if (item != null) {
							if (DEBUG)
								System.out.println("setSelected : inverse");
							setSelected(itemIndex, !isSelected(itemIndex));
							lastSingleClick = itemIndex;
							redraw();
						}
					} else if ((e.stateMask & SWT.SHIFT) > 0) {
						selection.clear();
						int newIndex = itemIndex;

						int min;
						int max;
						if (lastSingleClick <= newIndex) {
							min = lastSingleClick;
							max = newIndex;
						} else {
							min = newIndex;
							max = lastSingleClick;

						}

						for (int i = min; i <= max; i++) {
							setSelected(i, true);
						}
						redraw();
					} else {
						if (item == null) {
							if (DEBUG)
								System.out.println("clear");
							selection.clear();
						} else {
							if (DEBUG)
								System.out.println("clear");
							selection.clear();
							if (DEBUG)
								System.out.println("setSelected");
							setSelected(itemIndex, true);
							lastSingleClick = itemIndex;

						}
						redraw();
					}
				}

			}

		});

	}

	/**
	 * Returns item position in the gallery.
	 * 
	 * @param nb
	 * @return
	 */
	private Point getItemPosition(int nb) {
		int posX, posY;
		if (vertical) {
			posX = nb % this.hCount;
			posY = (nb - posX) / hCount;
		} else {
			posY = nb % this.vCount;
			posX = (nb - posY) / vCount;

		}
		return new Point(posX, posY);
	}

	/**
	 * Returns Pixel position of item
	 * 
	 * @param nb
	 * @return
	 */
	private Point getItemCoords(int nb) {
		Point pos = getItemPosition(nb);
		// userMagin is usded here, because we don't want to increase the
		// borders

		int x = pos.x * itemWidthPlusMargin + getInitialMarginX();
		int y = pos.y * itemHeightPlusMargin + getInitialMarginY();
		return new Point(x, y);
	}

	private int getInitialMarginX() {
		return (vertical ? realMargin : userMargin);
	}

	private int getInitialMarginY() {
		return (vertical ? userMargin : realMargin);
	}

	private int getNextMarginY() {
		return (vertical ? userMargin : realMargin);
	}

	/**
	 * Get item at pixel position
	 * 
	 * @param coords
	 * @return
	 */
	public GalleryItem getItem(Point coords) {
		checkWidget();
		if (DEBUG)
			System.out.println("getitem " + coords.x + " " + coords.y);

		// Calculate the "might be" position
		int posX = (coords.x - getInitialMarginX()) / (itemWidthPlusMargin);

		// Check if the users clicked on the X margin.
		if (((coords.x - getInitialMarginX()) % itemWidthPlusMargin) > itemWidth) {
			return null;
		}

		if (posX >= this.hCount) // Nothing there
			return null;

		int posY = (coords.y + this.translateY - getInitialMarginY()) / (itemHeightPlusMargin);

		// Check if the users clicked on the Y margin.
		if (((coords.y + this.translateY - getInitialMarginY()) % itemHeightPlusMargin) > itemHeight) {
			return null;
		}
		int itemNb = posX + posY * hCount;

		if (DEBUG)
			System.out.println("Item found : " + itemNb);

		if (itemNb < getItemCount()) {
			return getItem(itemNb);
		}

		return null;
	}

	private void initScrollBars() {
		ScrollBar verticalLocal = getVerticalBar();

		verticalLocal.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {
				scrollVertically((ScrollBar) event.widget);
			}
		});
	}

	private void paint(GC gc) {
		// long startTime = System.currentTimeMillis();

		// long startStepTime = System.currentTimeMillis();

		GC newGC = gc;
		// newGC.setAntialias(SWT.HIGH);
		newGC.setInterpolation(interpolation);
		// newGC.setClipping(clientRect);
		newGC.setBackground(backgroundColor);
		newGC.fillRectangle(gc.getClipping());
		// logger.warn( "init: " +(System.currentTimeMillis() - startStepTime));
		// startStepTime = System.currentTimeMillis();

		int[] indexes = getVisibleItems(gc.getClipping());
		// logger.warn( "visible: " +(System.currentTimeMillis() -
		// startStepTime));
		// startStepTime = System.currentTimeMillis();

		if (indexes.length > 0) {

			for (int i = indexes.length - 1; i >= 0; i--) {
				drawItem(newGC, indexes[i], isSelected(indexes[i]));
			}
		}

		// logger.warn( "draw: " +(System.currentTimeMillis() - startStepTime));

		// logger.warn( "paint: " +(System.currentTimeMillis() - startTime));

	}

	public void refresh(int nb) {
		checkWidget();
		if (nb < getItemCount()) {
			// System.out.println("Drawing item " + nb);
			int posX = nb % this.hCount;
			int posY = (nb - posX) / hCount;

			this.redraw(posX * (itemWidthPlusMargin) + getInitialMarginX(), posY * (itemHeightPlusMargin) - translateY + getInitialMarginY(), itemWidthPlusMargin, itemHeightPlusMargin, false);
		}
	}

	private int[] getVisibleItems(Rectangle clientRect) {
		// int width = getSize().x;
		int height = clientRect.height;

		int firstLine = (translateY + clientRect.y - userMargin) / itemHeightPlusMargin;
		int firstItem = firstLine * hCount;
		if (DEBUG)
			System.out.println("First line : " + firstLine);

		int lastLine = (translateY + clientRect.y + height - userMargin) / itemHeightPlusMargin;

		if (DEBUG)
			System.out.println("Last line : " + lastLine);

		int lastItem = (lastLine + 1) * hCount;

		int[] indexes = new int[lastItem - firstItem];
		for (int i = 0; i < (lastItem - firstItem); i++) {
			indexes[i] = firstItem + i;
		}

		return indexes;
	}

	private void sendPaintItemEvent(Item item, int index, GC gc, int x, int y) {
		Event e = new Event();
		e.item = item;
		e.type = SWT.PaintItem;
		e.index = index;
		gc.setClipping(x, y, itemWidth, itemHeight);
		e.gc = gc;
		e.x = x;
		e.y = y;
		e.width = this.itemWidth;
		e.height = this.itemHeight;
		this.notifyListeners(SWT.PaintItem, e);
	}

	private void drawItem(GC gc, int nb, boolean selected) {
		// long startTime = System.currentTimeMillis();
		if (DEBUG)
			System.out.println("Draw item ? " + nb);

		if (nb < getItemCount()) {
			// if (DEBUG)
			// System.out.println("Drawing item " + nb + " ty : " + ty);
			int posX = nb % hCount;
			int posY = (nb - posX) / hCount;

			Item item = getItem(nb);

			// No item ? return
			if (item == null)
				return;

			int xPixelPos = posX * itemWidthPlusMargin + getInitialMarginX();
			int yPixelPos = posY * itemHeightPlusMargin - translateY + getInitialMarginY();

			sendPaintItemEvent(item, nb, gc, xPixelPos, yPixelPos);

			if (itemRenderer != null) {
				gc.setClipping(xPixelPos, yPixelPos, itemWidth, itemHeight);
				this.itemRenderer.setSelected(selected);
				if (DEBUG)
					System.out.println("itemRender.draw");
				this.itemRenderer.draw(gc, (GalleryItem) item, xPixelPos, yPixelPos, itemWidth, itemHeight);
				if (DEBUG)
					System.out.println("itemRender done");
			}

		}
		// logger.warn( "Draw item: " +(System.currentTimeMillis() -
		// startTime));
	}

	/**
	 * If table is virtual and item at pos i has not been set, call the callback
	 * listener to set its value.
	 * 
	 * @return
	 */
	private void updateItem(int i) {
		GalleryItem galleryItem = (GalleryItem) items[i];

		if (galleryItem == null && this.virtual) {
			if (DEBUG) {
				System.out.println("Virtual/creating item ");
			}

			galleryItem = new GalleryItem(this, SWT.NONE);
			items[i] = galleryItem;
			setData(galleryItem, i);
		}
	}

	private void setData(GalleryItem galleryItem, int index) {
		Item item = galleryItem;
		Event e = new Event();
		e.item = item;
		e.type = SWT.SetData;
		e.index = index;
		this.notifyListeners(SWT.SetData, e);
	}

	private int calculateMargins(int size, int count, int itemSize) {
		int margin = this.userMargin;
		margin += (size - this.userMargin - getVerticalBar().getSize().x - (count * (itemSize + this.userMargin))) / (count + 1);
		return margin;
	}

	private void updateStructuralValues() {
		if (DEBUG)
			System.out.println("Gallery size : " + this.getSize().x + " " + getSize().y);

		int countLocal = getItemCount();
		Point size = getSize();
		int sizeX = size.x;
		int sizeY = size.y;

		int minMargin = userMargin;
		realMargin = minMargin;

		if (vertical) {
			hCount = (sizeX - minMargin - getVerticalBar().getSize().x) / (itemWidth + minMargin);
			if (DEBUG)
				System.out.println("Hnb" + hCount);

			if (hCount != 0) {

				if (this.autoMargins) {
					// Calculate best margins
					realMargin = calculateMargins(sizeX, hCount, itemWidth);
				}
				vCount = (int) Math.ceil((double) countLocal / (double) hCount);
			} else {
				// Show at least one item;
				vCount = countLocal;
				if (countLocal > 0) {
					hCount = 1;
				}
			}

		} else {
			vCount = (sizeY - minMargin - getHorizontalBar().getSize().y) / (itemHeight + minMargin);
			if (DEBUG)
				System.out.println("Hnb" + vCount);

			if (vCount != 0) {
				if (autoMargins) {
					// Calculate best margins
					realMargin = calculateMargins(sizeY, vCount, itemHeight);
				}
				hCount = (int) Math.ceil((double) countLocal / (double) vCount);
			} else {
				// Show at least one item;
				hCount = countLocal;
				if (countLocal > 0) {
					vCount = 1;
				}
			}
		}
		if (DEBUG)
			System.out.println("Vnb" + vCount);

		gWidth = sizeX;
		itemWidthPlusMargin = itemWidth + realMargin;
		itemHeightPlusMargin = itemHeight + getNextMarginY();
		gHeight = vCount * itemHeightPlusMargin + getInitialMarginY();

		updateScrollBarsProperties();
		redraw();
	}

	private void updateScrollBarsProperties() {

		int ch = getClientArea().height;

		ScrollBar verticalLocal = getVerticalBar();
		verticalLocal.setMinimum(0);

		verticalLocal.setIncrement(16);
		verticalLocal.setPageIncrement(getClientArea().height);
		if (gHeight > ch) { /* image is higher than client area */
			verticalLocal.setMaximum(gHeight);
			verticalLocal.setEnabled(true);

		} else { /* image is less higher than client area */
			verticalLocal.setEnabled(false);
			verticalLocal.setSelection(0);
			translateY = 0;
		}
		verticalLocal.setThumb(getClientArea().height);

	}

	/* Scroll vertically */
	private void scrollVertically(ScrollBar scrollBar) {
		syncScrollBars();
	}

	protected void syncScrollBars() {

		int ch = getClientArea().height;

		if (gHeight > ch) { /* image is higher than client area */
			ScrollBar verticalLocal = getVerticalBar();
			scroll(0, translateY - verticalLocal.getSelection(), 0, 0, getClientArea().width, ch, true);
			translateY = verticalLocal.getSelection();
		} else {
			translateY = 0;
		}

		return;
	}

	protected void addItem(Item i) {
		checkWidget();
		if (!virtual) {

			if (items == null) {
				items = new Item[1];
			} else {
				Item[] oldItems = items;
				items = new Item[oldItems.length + 1];
				System.arraycopy(oldItems, 0, items, 0, oldItems.length);
			}
			items[items.length - 1] = i;
			updateStructuralValues();
			syncScrollBars();
		}
	}

	public GalleryItem getItem(int index) {
		checkWidget();
		if (index < getItemCount()) {
			// System.out.println( "getItem " + index);

			// Refresh item if it is not set yet
			updateItem(index);
			return (GalleryItem) items[index];
		}

		return null;
	}

	public void clear() {
		checkWidget();
		if (virtual) {
			setItemCount(0);
		} else {
			items = null;
		}

		updateStructuralValues();
		syncScrollBars();
	}

	public void clearAll() {
		checkWidget();
		if (virtual) {
			for (int i = 0; i < items.length; i++)
				items[i] = null;

			// TODO: I'm clearing selection here
			// but we have to check that Table has the same behavior
			selection.clear();

			updateStructuralValues();
			syncScrollBars();
		}
	}

	public void clear(int i) {
		checkWidget();
		if (virtual) {
			items[i] = null;

			updateStructuralValues();
			syncScrollBars();
		}
	}

	public int[] getSelectionIndices() {
		checkWidget();
		int[] result = new int[selection.size()];
		for (int i = 0; i < selection.size(); i++) {
			result[i] = ((Integer) selection.get(i)).intValue();
		}
		return result;
	}

	public GalleryItem[] getSelection() {
		checkWidget();
		int[] selectionLocal = getSelectionIndices();
		GalleryItem[] result = new GalleryItem[selectionLocal.length];

		for (int i = 0; i < selectionLocal.length; i++) {
			result[i] = getItem(selectionLocal[i]);
		}

		return result;
	}

	public int getSelectionCount() {
		checkWidget();
		return selection.size();
	}

	public int indexOf(GalleryItem item) {
		checkWidget();
		if (item == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		if (1 <= lastIndexOf && lastIndexOf < count - 1) {
			if (items [lastIndexOf] == item) return lastIndexOf;
			if (items [lastIndexOf + 1] == item) return ++lastIndexOf;
			if (items [lastIndexOf - 1] == item) return --lastIndexOf;
		}
		if (lastIndexOf < count / 2) {
			for (int i=0; i<count; i++) {
				if (items [i] == item) return lastIndexOf = i;
			}
		} else {
			for (int i=count - 1; i>=0; --i) {
				if (items [i] == item) return lastIndexOf = i;
			}
		}
		return -1;
	}

	public int getItemWidth() {
		checkWidget();
		return itemWidth;
	}

	public int getItemHeight() {
		checkWidget();
		return itemHeight;
	}

	public void setItemSize(int width, int height) {
		checkWidget();
		this.itemWidth = width;
		this.itemHeight = height;

		updateStructuralValuesKeepSelection();

	}

	public void setItemSize(int thumbnailSize) {
		// No need to checkWidget here, this method calls directly
		// setItemSize(int width, int height) which do the job
		setItemSize(thumbnailSize, thumbnailSize);
	}

	private void updateStructuralValuesKeepSelection() {
		// Keep the vertical bar at the same position, even
		// if max value is changing.
		int s = getVerticalBar().getSelection();
		int max = getVerticalBar().getMaximum();
		float ratio = 0;
		if (max != 0) {
			ratio = (float) s / (float) max;
		}

		// Rebuild the gallery
		updateStructuralValues();

		// Now that the new maximum value is set
		// restore the selection
		getVerticalBar().setSelection(Math.round(ratio * getVerticalBar().getMaximum()));

		// Update content position and redraw
		syncScrollBars();
		redraw();
	}

	public void showItem(GalleryItem item) {
		checkWidget();
		// TODO: showItem(GalleryItem item)
	}

	public void showItem(GalleryGroup item) {
		checkWidget();
		// TODO: showItem(GalleryGroup item)
	}

	/**
	 * Scroll the gallery in order to display this item. <br/> This method is
	 * poorly implemented, but works. Rewriting will be necessary at some point.
	 * 
	 * @param index
	 */
	public void showIndex(int index) {
		checkWidget();

		int[] visible = getVisibleIndices();
		if (visible.length > 0) {

			int indexBottom = index - hCount;
			if (indexBottom < 0)
				indexBottom = 0;

			int indexTop = index + hCount;
			if (indexTop < 0)
				indexTop = 0;

			// Check if item is already visible
			if (indexBottom >= visible[0] && indexTop <= visible[visible.length - 1]) {
				return;
			}

			// Check if item is located before the first visible item
			if (indexBottom < visible[0]) {
				int currentSelection = this.getVerticalBar().getSelection();
				int step = this.getVerticalBar().getIncrement();

				this.getVerticalBar().setSelection(currentSelection - step >= 0 ? currentSelection - step : 0);
				this.syncScrollBars();
			}

			// Check if item is located after the last visible item
			if (indexTop > visible[visible.length - 1]) {
				int currentSelection = this.getVerticalBar().getSelection();
				int step = this.getVerticalBar().getIncrement();
				int max = this.getVerticalBar().getMaximum();
				this.getVerticalBar().setSelection(currentSelection + step >= max ? max : currentSelection + step);
				this.syncScrollBars();

			}
			int oldTranslateY;

			// This is a bad algorithm. The method is called until there is no
			// more need to move the vertical bar.
			// A better implementation (todo) should calculate the right
			// translation and do it only one.
			do {
				oldTranslateY = translateY;
				this.showIndex(index);
				if (DEBUG)
					System.out.println("oldTranslateY " + oldTranslateY + "  translateY" + translateY);
			} while (oldTranslateY != translateY);

		}
		// TODO: showIndex(int index)
	}

	public int getMargin() {
		checkWidget();
		return userMargin;
	}

	public void setMargin(int margin) {
		checkWidget();
		this.userMargin = margin;
		updateStructuralValues();
		syncScrollBars();
	}

	public GalleryItem[] getItems() {
		checkWidget();
		GalleryItem[] itemsLocal = new GalleryItem[this.items.length];
		System.arraycopy(items, 0, itemsLocal, 0, this.items.length);
	
		return itemsLocal;
	}

	public int[] getVisibleIndices() {
		checkWidget();
		return getVisibleItems(getBounds());
	}

	public boolean isAutoMargins() {
		checkWidget();
		return autoMargins;
	}

	public void setAutoMargins(boolean autoMargins) {
		checkWidget();
		this.autoMargins = autoMargins;
		this.updateStructuralValues();
	}

	public boolean isVertical() {
		checkWidget();
		return vertical;
	}

	public void setVertical(boolean vertical) {
		checkWidget();
		this.vertical = vertical;
		this.updateStructuralValues();
		redraw();
	}

}
