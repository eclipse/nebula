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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class ToolbarComposite extends Composite implements MouseListener, MouseMoveListener, MouseTrackListener {

	private List					mToolBarItems;
	private TBItem					mLastHover;
	private CustomButton			mSelectedItem;
	private CollapsibleButtons		mButtonComposite;

	private boolean					mEnableDoubleBuffering	= true;
	private boolean					mCreated				= false;

	private static Image			mOutlook2005ArrowsImage	= ImageCache.getImage("icons/arrows.gif");
	private static Image			mOutlook2007ArrowImage	= ImageCache.getImage("icons/o2007arrow.gif");
	private Rectangle				mArrowsBounds;
	private boolean					mArrowHover				= false;

	private IColorManager			mColorManager;
	private AbstractButtonPainter	mButtonPainter;

	private Image					mArrowImage;
	private ISettings				mSettings;
	private ILanguageSettings		mLanguage;

	/**
	 * Creates a new toolbar composite.
	 * 
	 * @param bc ButtonComposite parent
	 * @param style Composite style
	 */
	public ToolbarComposite(CollapsibleButtons bc, int style) {
		super(bc, style | SWT.NO_BACKGROUND);

		this.mLanguage = bc.getLanguageSettings();
		this.mButtonPainter = new AbstractButtonPainter();
		this.mButtonComposite = bc;
		this.mColorManager = bc.getColorManager();
		this.mSettings = bc.getSettings();

		if (mColorManager.getTheme() == IColorManager.SKIN_OFFICE_2007)
			mArrowImage = mOutlook2007ArrowImage;
		else
			mArrowImage = mOutlook2005ArrowsImage;

		mToolBarItems = new ArrayList();

		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				repaint(event);
			}
		});

		addMouseListener(this);
		addMouseTrackListener(this);
		addMouseMoveListener(this);
	}

	private void repaint(PaintEvent event) {
		GC gc = event.gc;
		if (mCreated && mEnableDoubleBuffering) {
			try {
				Image buffer = new Image(Display.getDefault(), super.getBounds());
				GC gc2 = new GC(buffer);
				drawOntoGC(gc2);

				// transfer the image buffer onto this canvas
				// just drawImage(buffer, w, h) didn't work, so we do the whole
				// source transfer call
				Rectangle b = getBounds();
				gc.drawImage(buffer, 0, 0, b.width, b.height, 0, 0, b.width, b.height);

				// dispose the buffer, very important or we'll run out of
				// address space for buffered images
				buffer.dispose();
				gc2.dispose();
			} catch (IllegalArgumentException iea) {
				// seems to come here for some reason when we switch phases
				// while the gantt chart is being viewed, I'm not sure why
				// but no time to figure it out for the demo.. so instead of
				// buffering, just draw it onto the GC
				drawOntoGC(gc);
			}
		} else {
			drawOntoGC(gc);
			mCreated = true;
		}

		gc.dispose();
	}

	private void drawOntoGC(GC gc) {
		Rectangle rect = getClientArea();
		Rectangle imageBounds = (mArrowImage != null ? mArrowImage.getBounds() : new Rectangle(0, 0, 0, 0));
		int verticalLoc = (getBounds().height / 2) - (mArrowImage == null ? 0 : mArrowImage.getBounds().height / 2);
		// move it down just a smidge, the human eye percieves things
		// mis-aligned when exactly centered
		verticalLoc += 1;
		mButtonPainter.paintBackground(gc, mColorManager, mSettings, rect, false, false);

		int right = rect.width;

		if (mArrowImage != null)
			gc.drawImage(mArrowImage, rect.width - imageBounds.width, verticalLoc);
		mArrowsBounds = new Rectangle(rect.width - imageBounds.width, verticalLoc, imageBounds.width, imageBounds.height);
		right -= imageBounds.width + mSettings.getToolBarSpacing();

		// reorganize items if stuff have been hidden/shown (permanently)
		orderItems();

		for (int i = 0; i < mToolBarItems.size(); i++) {
			TBItem tb = (TBItem) mToolBarItems.get(i);
			if (tb.getHidden())
				continue;

			if (tb.getButton() == mSelectedItem) {
				Rectangle cur = tb.getBounds();
				// TODO: Clean up code-repeat
				if (cur == null) {
					if (tb.getButton().getToolBarImage() != null) {
						Rectangle imBounds = tb.getButton().getToolBarImage().getBounds();
						cur = new Rectangle(right - imBounds.width, verticalLoc, imBounds.width, imBounds.width);
						tb.setBounds(cur);
					} else {
						// basically a non-existent button, but no image = no
						// button, so that's fine
						tb.setBounds(new Rectangle(right, verticalLoc, 0, 0));
						continue;
					}
				}

				Rectangle bounds = new Rectangle(cur.x - mSettings.getToolBarLeftSpacer(), cur.y, cur.width + mSettings.getToolBarRightSpacer(), cur.height);
				mButtonPainter.paintBackground(gc, mColorManager, mSettings, bounds, false, true);
			}

			Rectangle imBounds = null;
			if (tb.getButton().getToolBarImage() != null) {
				imBounds = tb.getButton().getToolBarImage().getBounds();
				gc.drawImage(tb.getButton().getToolBarImage(), right - imBounds.width, verticalLoc);
				tb.setBounds(new Rectangle(right - imBounds.width, verticalLoc, imBounds.width, imBounds.width));
			}

			right -= (imBounds == null ? 0 : imBounds.width) + mSettings.getToolBarSpacing();
		}
	}

	private void orderItems() {
		if (mToolBarItems.size() == 0)
			return;

		Collections.sort(mToolBarItems);
	}

	public Point getSize() {
		checkWidget();
		return new Point(super.getSize().x, CustomButton.BUTTON_HEIGHT);
	}

	public void addItem(CustomButton button) {
		checkWidget();
		mToolBarItems.add(new TBItem(button));
	}
	
	public void removeAll() {
		checkWidget();
		mToolBarItems.clear();		
	}

	public void removeItem(CustomButton button) {
		checkWidget();
		for (int i = 0; i < mToolBarItems.size(); i++) {
			TBItem item = (TBItem) mToolBarItems.get(i);
			if (item.getButton() == button) {
				mToolBarItems.remove(item);
				// redraw();
				break;
			}
		}
	}

	public void hideButton(CustomButton button) {
		checkWidget();
		for (int i = 0; i < mToolBarItems.size(); i++) {
			TBItem item = (TBItem) mToolBarItems.get(i);
			if (item.getButton() == button) {
				item.setHidden(true);
				break;
			}
		}
	}

	public void setSelectedItem(CustomButton button) {
		checkWidget();
		clearHover();
		clearArrowsHover();
		clearSelection();
		mSelectedItem = button;

		for (int i = 0; i < mToolBarItems.size(); i++) {
			TBItem item = (TBItem) mToolBarItems.get(i);

			if (item.getButton() == mSelectedItem) {
				Rectangle lb = item.getBounds();
				GC gc = new GC(this);
				Rectangle rect = new Rectangle(lb.x - mSettings.getToolBarLeftSpacer(), 0, lb.width + mSettings.getToolBarRightSpacer(), CustomButton.BUTTON_HEIGHT);
				mButtonPainter.paintBackground(gc, mColorManager, mSettings, rect, false, true);
				gc.drawImage(item.getButton().getToolBarImage(), lb.x, lb.y);
				gc.dispose();
			}
		}

	}

	public void mouseDoubleClick(MouseEvent event) {
		checkWidget();
	}

	public void mouseDown(MouseEvent event) {
		checkWidget();
		Rectangle bigArrowsBounds = new Rectangle(mArrowsBounds.x, 0, mArrowsBounds.width, CustomButton.BUTTON_HEIGHT);
		if (isInside(event.x, event.y, bigArrowsBounds)) {
			GC gc = new GC(this);
			Rectangle rect = new Rectangle(mArrowsBounds.x - mSettings.getToolBarLeftSpacer(), 0, mArrowsBounds.width + mSettings.getToolBarRightSpacer(),
					CustomButton.BUTTON_HEIGHT);
			mButtonPainter.paintBackground(gc, mColorManager, mSettings, rect, false, true);

			gc.drawImage(mArrowImage, mArrowsBounds.x, mArrowsBounds.y);

			gc.dispose();

			Menu mainMenu = new Menu(Display.getDefault().getActiveShell(), SWT.POP_UP);
			
			List menuListeners = mButtonComposite.getMenuListeners();
			for (int i = 0; i < menuListeners.size(); i++) {
				((IMenuListener)menuListeners.get(i)).preMenuItemsCreated(mainMenu);
			}
			
			MenuItem menuShowMoreButtons = new MenuItem(mainMenu, SWT.PUSH);
			MenuItem menuShowFewerButtons = new MenuItem(mainMenu, SWT.PUSH);
			menuShowFewerButtons.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					mButtonComposite.hideNextButton();
				}
			});
			menuShowMoreButtons.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					mButtonComposite.showNextButton();
				}
			});

			menuShowMoreButtons.setText(mLanguage.getShowMoreButtonsText());
			menuShowFewerButtons.setText(mLanguage.getShowFewerButtonsText());

			new MenuItem(mainMenu, SWT.SEPARATOR);
			MenuItem more = new MenuItem(mainMenu, SWT.CASCADE);
			more.setText(mLanguage.getAddOrRemoveButtonsText());
			Menu moreMenu = new Menu(more);
			more.setMenu(moreMenu);

			List cbs = mButtonComposite.getItems();
			for (int i = 0; i < cbs.size(); i++) {
				final CustomButton cb = (CustomButton) cbs.get(i);
				final MenuItem temp = new MenuItem(moreMenu, SWT.CHECK);
				temp.setText(cb.getText());
				temp.setImage(cb.getToolBarImage());
				temp.setSelection(mButtonComposite.isVisible(cb));
				temp.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event) {
						if (mButtonComposite.isVisible(cb)) {
							mButtonComposite.permanentlyHideButton(cb);
							temp.setSelection(false);
						} else {
							mButtonComposite.permanentlyShowButton(cb);
							temp.setSelection(true);
						}
					}
				});
			}

			for (int i = 0; i < menuListeners.size(); i++) {
				((IMenuListener)menuListeners.get(i)).postMenuItemsCreated(mainMenu);
			}
			
			mainMenu.setVisible(true);
			return;
		}

		for (int i = 0; i < mToolBarItems.size(); i++) {
			TBItem item = (TBItem) mToolBarItems.get(i);
			if (item.getBounds() != null) {
				if (isInside(event.x, event.y, item.getBounds())) {
					mButtonComposite.selectItemAndLoad(item.getButton());
					break;
				}
			}
		}
	}

	public void mouseUp(MouseEvent event) {
		checkWidget();
	}

	public void mouseMove(MouseEvent event) {
		checkWidget();
		TBItem found = null;

		Rectangle bigArrowsBounds = new Rectangle(mArrowsBounds.x, 0, mArrowsBounds.width, CustomButton.BUTTON_HEIGHT);
		if (isInside(event.x, event.y, bigArrowsBounds)) {
			setToolTipText(null);
			GC gc = new GC(this);
			Rectangle rect = new Rectangle(mArrowsBounds.x - mSettings.getToolBarLeftSpacer(), 0, mArrowsBounds.width + mSettings.getToolBarRightSpacer(),
					CustomButton.BUTTON_HEIGHT);
			mButtonPainter.paintBackground(gc, mColorManager, mSettings, rect, true, false);

			gc.drawImage(mArrowImage, mArrowsBounds.x, mArrowsBounds.y);
			gc.dispose();
			mArrowHover = true;
			return;
		}

		clearArrowsHover();

		for (int i = 0; i < mToolBarItems.size(); i++) {
			TBItem item = (TBItem) mToolBarItems.get(i);
			if (item.getBounds() != null) {
				if (isInside(event.x, event.y, item.getBounds())) {
					found = item;
					break;
				}
			}
		}

		if (found == null) {
			clearHover();
			return;
		}

		setToolTipText(found.getButton().getToolTip());

		if (found.isHovered()) {
			return;
		}

		if (found.getButton() == mSelectedItem) {
			return;
		}

		clearHover();

		GC gc = new GC(this);
		Rectangle tbBounds = found.getBounds();
		Rectangle toUse = new Rectangle(tbBounds.x - mSettings.getToolBarLeftSpacer(), 0, tbBounds.width + mSettings.getToolBarRightSpacer(), CustomButton.BUTTON_HEIGHT);
		mButtonPainter.paintBackground(gc, mColorManager, mSettings, toUse, true, false);
		gc.drawImage(found.getButton().getToolBarImage(), tbBounds.x, tbBounds.y);
		gc.dispose();
		found.setHovered(true);
		mLastHover = found;
	}

	public void mouseEnter(MouseEvent event) {

	}

	public void mouseExit(MouseEvent event) {
		checkWidget();
		clearHover();
		clearArrowsHover();
	}

	public void mouseHover(MouseEvent event) {

	}

	private void clearSelection() {
		for (int i = 0; i < mToolBarItems.size(); i++) {
			TBItem item = (TBItem) mToolBarItems.get(i);
			if (item.getButton() == mSelectedItem) {
				GC gc = new GC(this);
				Rectangle lb = item.getBounds();
				redraw(lb.x - mSettings.getToolBarLeftSpacer(), 0, lb.width + mSettings.getToolBarRightSpacer(), CustomButton.BUTTON_HEIGHT, false);
				if (item.getButton().getToolBarImage() != null)
					gc.drawImage(item.getButton().getToolBarImage(), lb.x, lb.y);
				gc.dispose();
			}
		}
	}

	private void clearHover() {
		if (mLastHover != null) {
			GC gc = new GC(this);
			Rectangle lb = mLastHover.getBounds();
			redraw(lb.x - mSettings.getToolBarLeftSpacer(), 0, lb.width + mSettings.getToolBarRightSpacer(), CustomButton.BUTTON_HEIGHT, false);
			gc.drawImage(mLastHover.getButton().getToolBarImage(), lb.x, lb.y);
			gc.dispose();
			mLastHover.setHovered(false);
			mLastHover = null;
			setToolTipText(null);
		}
	}

	private void clearArrowsHover() {
		if (mArrowHover) {
			GC gc = new GC(this);
			redraw(mArrowsBounds.x - mSettings.getToolBarLeftSpacer(), 0, mArrowsBounds.width + mSettings.getToolBarRightSpacer(), CustomButton.BUTTON_HEIGHT, false);
			gc.drawImage(mArrowImage, mArrowsBounds.x, mArrowsBounds.y);
			gc.dispose();
			mArrowHover = false;
		}
	}

	private boolean isInside(int x, int y, Rectangle rect) {
		if (rect == null) {
			return false;
		}

		if (x >= rect.x && y >= rect.y && x <= (rect.x + rect.width) && y <= (rect.y + rect.height)) {
			return true;
		}

		return false;
	}

	class TBItem implements Comparable {
		private Rectangle		bounds;
		private CustomButton	button;
		private boolean			hovered;
		private boolean			hidden;

		public TBItem(CustomButton button) {
			this.button = button;
		}

		public Rectangle getBounds() {
			return bounds;
		}

		public void setBounds(Rectangle bounds) {
			this.bounds = bounds;
		}

		public CustomButton getButton() {
			return button;
		}

		public boolean isHovered() {
			return hovered;
		}

		public void setHovered(boolean hovered) {
			this.hovered = hovered;
		}

		public void setHidden(boolean hidden) {
			this.hidden = hidden;
		}

		public boolean getHidden() {
			return this.hidden;
		}

		public String toString() {
			return "[TBItem " + button.getNumber() + "]";
		}

		public int compareTo(Object item) {
			if (!(item instanceof TBItem))
				return 0;

			TBItem tbitem = (TBItem) item;

			Integer one = new Integer(tbitem.getButton().getNumber());
			Integer two = new Integer(getButton().getNumber());
			return one.compareTo(two);
		}
	}

}
