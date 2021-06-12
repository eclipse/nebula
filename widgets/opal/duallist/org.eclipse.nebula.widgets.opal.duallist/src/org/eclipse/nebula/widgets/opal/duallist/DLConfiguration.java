/*******************************************************************************
 * Copyright (c) 2021 Laurent CARON
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API
 * and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.duallist;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * Configuration class for the DualList widget
 */
public class DLConfiguration {
	private Color itemsBackgroundColor, itemsOddLinesColor, itemsForegroundColor, selectionBackgroundColor, selectionOddLinesColor, selectionForegroundColor;
	private int itemsTextAlignment = SWT.LEFT, selectionTextAlignment = SWT.LEFT;
	private Image doubleDownImage, doubleUpImage, doubleLeftImage, doubleRightImage, //
			downImage, leftImage, upImage, rightImage;

	private boolean doubleRightVisible = true;
	private boolean doubleLeftVisible = true;
	private boolean doubleUpVisible = true;
	private boolean upVisible = true;
	private boolean doubleDownVisible = true;
	private boolean downVisible = true;

	/**
	 * @return the background color of the items panel
	 */
	public Color getItemsBackgroundColor() {
		return itemsBackgroundColor;
	}

	/**
	 * @param color the background color of the items panel to set
	 */
	public DLConfiguration setItemsBackgroundColor(Color color) {
		if (color != null && color.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		this.itemsBackgroundColor = color;
		return this;
	}

	/**
	 * @return the background color of the odd lines for the unselected items list
	 */
	public Color getItemsOddLinesColor() {
		return itemsOddLinesColor;
	}

	/**
	 * @param color the background color of the odd lines for the unselected items list to set
	 */
	public DLConfiguration setItemsOddLinesColor(Color color) {
		if (color != null && color.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		this.itemsOddLinesColor = color;
		return this;
	}

	/**
	 * @return the background color of the selected items panel
	 */
	public Color getSelectionBackgroundColor() {
		return selectionBackgroundColor;
	}

	/**
	 * @param color the background color of the items panel to set
	 */
	public DLConfiguration setSelectionBackgroundColor(Color color) {
		if (color != null && color.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		this.selectionBackgroundColor = color;
		return this;
	}

	/**
	 * @return the background color of the odd lines for the selected items list
	 */
	public Color getSelectionOddLinesColor() {
		return selectionOddLinesColor;
	}

	/**
	 * @param color the background color of the odd lines for the selected items list to set
	 */
	public DLConfiguration setSelectionOddLinesColor(Color color) {
		if (color != null && color.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		this.selectionOddLinesColor = color;
		return this;
	}

	/**
	 * @return the text alignment (SWT.RIGHT, SWT.CENTER, SWT.LEFT) for the unselected items
	 */
	public int getItemsTextAlignment() {
		return itemsTextAlignment;
	}

	/**
	 * @param alignment the text alignment (SWT.RIGHT, SWT.CENTER, SWT.LEFT) for the unselected items to set
	 */
	public DLConfiguration setItemsTextAlignment(int alignment) {
		if (alignment != SWT.NONE && alignment != SWT.LEFT && alignment != SWT.RIGHT && alignment != SWT.CENTER) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		this.itemsTextAlignment = alignment;
		return this;
	}

	/**
	 * @return the text alignment (SWT.RIGHT, SWT.CENTER, SWT.LEFT) for the selected items
	 */
	public int getSelectionTextAlignment() {
		return selectionTextAlignment;
	}

	/**
	 * @param alignment the text alignment (SWT.RIGHT, SWT.CENTER, SWT.LEFT) for the unselected items to set
	 */
	public DLConfiguration setSelectionTextAlignment(int alignment) {
		if (alignment != SWT.NONE && alignment != SWT.LEFT && alignment != SWT.RIGHT && alignment != SWT.CENTER) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		this.selectionTextAlignment = alignment;
		return this;
	}

	/**
	 * @return the image for the "double down" button
	 */
	public Image getDoubleDownImage() {
		return doubleDownImage;
	}

	/**
	 * @param image the image for the "double down" button to set
	 */
	public DLConfiguration setDoubleDownImage(Image image) {
		this.doubleDownImage = image;
		return this;
	}

	/**
	 * @return the image for the "double up" button
	 */
	public Image getDoubleUpImage() {
		return doubleUpImage;
	}

	/**
	 * @param image the image for the "double up" button to set
	 */
	public DLConfiguration setDoubleUpImage(Image image) {
		this.doubleUpImage = image;
		return this;
	}

	/**
	 * @return the image for the "double left" button
	 */
	public Image getDoubleLeftImage() {
		return doubleLeftImage;
	}

	/**
	 * @param image the image for the "double left" button to set
	 */
	public DLConfiguration setDoubleLeftImage(Image image) {
		this.doubleLeftImage = image;
		return this;
	}

	/**
	 * @return the image for the "double right" button
	 */
	public Image getDoubleRightImage() {
		return doubleRightImage;
	}

	/**
	 * @param image the image for the "double right" button to set
	 */
	public DLConfiguration setDoubleRightImage(Image image) {
		this.doubleRightImage = image;
		return this;
	}

	/**
	 * @return the image for the "down" button
	 */
	public Image getDownImage() {
		return downImage;
	}

	/**
	 * @param image the image for the "down" button to set
	 */
	public DLConfiguration setDownImage(Image image) {
		this.downImage = image;
		return this;
	}

	/**
	 * @return the image for the "left" button
	 */
	public Image getLeftImage() {
		return leftImage;
	}

	/**
	 * @param image the image for the "left" button to set
	 */
	public DLConfiguration setLeftImage(Image image) {
		this.leftImage = image;
		return this;
	}

	/**
	 * @return the image for the "up" button
	 */
	public Image getUpImage() {
		return upImage;
	}

	/**
	 * @param image the image for the "up" button to set
	 */
	public DLConfiguration setUpImage(Image image) {
		this.upImage = image;
		return this;
	}

	/**
	 * @return the image for the "right" button
	 */
	public Image getRightImage() {
		return rightImage;
	}

	/**
	 * @param image the image for the "right" button to set
	 */
	public DLConfiguration setRightImage(Image image) {
		this.rightImage = image;
		return this;
	}

	/**
	 * @return <code>true</code> if the "double right" button is visible, <code>false</code> otherwise
	 */
	public boolean isDoubleRightVisible() {
		return doubleRightVisible;
	}

	/**
	 * @param visible the visibility of the "double right" button
	 */
	public DLConfiguration setDoubleRightVisible(boolean visible) {
		this.doubleRightVisible = visible;
		return this;
	}

	/**
	 * @return <code>true</code> if the "double left" button is visible, <code>false</code> otherwise
	 */
	public boolean isDoubleLeftVisible() {
		return doubleLeftVisible;
	}

	/**
	 * @param visible the visibility of the "double left" button
	 */
	public DLConfiguration setDoubleLeftVisible(boolean visible) {
		this.doubleLeftVisible = visible;
		return this;
	}

	/**
	 * @return <code>true</code> if the "double up" button is visible, <code>false</code> otherwise
	 */
	public boolean isDoubleUpVisible() {
		return doubleUpVisible;
	}

	/**
	 * @param visible the visibility of the "double up" button
	 */
	public DLConfiguration setDoubleUpVisible(boolean visible) {
		this.doubleUpVisible = visible;
		return this;
	}

	/**
	 * @return <code>true</code> if the "up" button is visible, <code>false</code> otherwise
	 */
	public boolean isUpVisible() {
		return upVisible;
	}

	/**
	 * @param visible the visibility of the "up" button
	 */
	public DLConfiguration setUpVisible(boolean visible) {
		this.upVisible = visible;
		return this;
	}

	/**
	 * @return <code>true</code> if the "double down" button is visible, <code>false</code> otherwise
	 */
	public boolean isDoubleDownVisible() {
		return doubleDownVisible;
	}

	/**
	 * @param visible the visibility of the "double down" button
	 */
	public DLConfiguration setDoubleDownVisible(boolean visible) {
		this.doubleDownVisible = visible;
		return this;
	}

	/**
	 * @return <code>true</code> if the "down" button is visible, <code>false</code> otherwise
	 */
	public boolean isDownVisible() {
		return downVisible;
	}

	/**
	 * @param visible the visibility of the "down" button
	 */
	public DLConfiguration setDownVisible(boolean visible) {
		this.downVisible = visible;
		return this;
	}

	/**
	 * @return the foreground color of the items panel
	 */
	public Color getItemsForegroundColor() {
		return itemsForegroundColor;
	}

	/**
	 * @param color the foreground color of the items panel to set
	 * @return
	 */
	public DLConfiguration setItemsForegroundColor(Color color) {
		if (color != null && color.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		this.itemsForegroundColor = color;
		return this;
	}

	/**
	 * @return the foreground color of the items panel
	 */
	public Color getSelectionForegroundColor() {
		return selectionForegroundColor;
	}

	/**
	 * @param color the foreground color of the selection panel to set
	 * @return
	 */
	public DLConfiguration setSelectionForegroundColor(Color color) {
		if (color != null && color.isDisposed()) {
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		}
		this.selectionForegroundColor = color;
		return this;
	}

}
