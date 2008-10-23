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
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class CustomButton extends Composite {

	public static final int BUTTON_HEIGHT = 31;

	private Image mImage;
	private String mText;
	private Rectangle mBounds;
	private Image mToolBarImage;
	private String mToolTip;
		
	private boolean mHover;
	private boolean mSelected;
	private ISettings mSettings;
	private CollapsibleButtons mParent;
	private IColorManager mColorManager;
	
	private int mOrderNumber;
	
	/**
	 * Creates a new CustomButton.
	 * 
	 * @param parent ButtonComposite parent
	 * @param style Widget style
	 * @param text Label text
	 * @param image Image to show, null if none
	 * @param toolBarImage Tooolbar image, null if none
	 * @param toolTip Tooltip text
	 * @param settings Button painter class that decides look and feel of button
	 */
	public CustomButton(CollapsibleButtons parent, int style, String text, Image image, Image toolBarImage, String toolTip, ISettings settings) {
		super(parent, style);
		mImage = image;
		mText = text;
		mToolTip = toolTip;
		mToolBarImage = toolBarImage;
		mSettings = settings;
		mParent = parent;
		mColorManager = mParent.getColorManager();
		
		setToolTipText(toolTip);
	
		init();
	}
	
	private void init() {		
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent pe) {
				repaint(pe.gc);
			}			
		});
		
		addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event e) {
				redraw();
			}			
		});
	}
	
	/**
	 * Updates the hover state.
	 * 
	 * @param hover true for hover, false for off
	 */
	public void updateHover(boolean hover) {
		if (isDisposed())
			return;
		
		if (hover && mHover)
			return;
		
		if (!hover && !mHover)
			return;

		mHover = hover;
		redraw();
	}
	
	/**
	 * Updates the selection state.
	 * 
	 * @param selected true for selected, false for not
	 */
	public void updateSelection(boolean selected) {
		if (isDisposed())
			return;
		
		if (selected && mSelected)
			return;
		
		if (!selected && !mSelected)
			return;
		
		mSelected = selected;
		redraw();
	}
		
	/**
	 * Returns the button label text
	 * 
	 * @return Button text
	 */
	public String getText() {
		return mText;
	}
	
	/**
	 * Returns the tooltip text
	 * 
	 * @return Tooltip text
	 */
	public String getToolTip() {
		return mToolTip;
	}
	
	/**
	 * Returns the toolbar image
	 * 
	 * @return Toolbar image
	 */
	public Image getToolBarImage() {
		return mToolBarImage;
	}
	
	/**
	 * Sets the visible text 
	 * 
	 * @param text
	 */
	public void setText(String text) {
		mText = text;
	}

	/**
	 * Sets the toolbar image.
	 * 
	 * @param toolBarImage
	 */
	public void setToolBarImage(Image toolBarImage) {
		mToolBarImage = toolBarImage;
	}

	/**
	 * Sets the tooltip text.
	 * 
	 * @param toolTip
	 */
	public void setToolTip(String toolTip) {
		mToolTip = toolTip;
	}
	
	/**
	 * Returns the big image.
	 *  
	 * @return Image
	 */
	public Image getImage() {
		return mImage;
	}

	/**
	 * Sets the big image.
	 * 
	 * @param image to set
	 */
	public void setImage(Image image) {
		mImage = image;
	}

	private void repaint(GC gc) {
		mBounds = new Rectangle(0, 0, super.getBounds().width, BUTTON_HEIGHT);

		IButtonPainter bp = mSettings.getButtonPainter();
		
		bp.paintBackground(gc, mColorManager, mSettings, mBounds, mHover, mSelected);
		bp.paintImage(gc, mColorManager, mSettings, mBounds, mHover, mSelected, mImage);
		bp.paintText(gc, mColorManager, mSettings, mBounds, (mImage == null ? null : mImage.getBounds()), mHover, mSelected, mText);
	}
	
	/**
	 * Internal function.
	 * This is used to keep a list of numbered buttons in memory via an ever-increasing integer value for setting the
	 * order of buttons back to their original position when buttons are permanently hidden/shown.
	 * 
	 * Should you wish to use this, for some reason, then make sure that there is no gap in numbers in the buttons and that they
	 * start at 0.
	 * 
	 * @param number 
	 */
	public void setNumber(int number) {
		mOrderNumber = number;
	}
	
	/**
	 * Internal function.
	 * Returns the current number for this button. The number reflects what position in the list the button has - visually.
	 * 
	 * @return Number
	 */
	public int getNumber() {
		return mOrderNumber;
	}
	
	public String toString() {
		return "[CustomButton '"+mText+"']";
	}
	
	/**
	 * Disposes this button and removes it from the control.
	 */
	public void dispose() {
		mParent.remove(this, false);
		super.dispose();
	}
	
}
