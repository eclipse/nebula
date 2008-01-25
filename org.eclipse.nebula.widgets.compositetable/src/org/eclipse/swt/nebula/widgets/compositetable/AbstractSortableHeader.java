/*
 * Copyright (C) 2005 David Orme <djo@coconut-palm-software.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Orme     - Initial API and implementation
 *     The Pampered Chef - Expanded to handle sorting
 */
package org.eclipse.swt.nebula.widgets.compositetable;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Class AbstractSortableHeader. A Header class making it easier to implement a
 * sorted table where clicking on a header column sets or changes the sort
 * order.
 * 
 * @author djo
 */
public abstract class AbstractSortableHeader extends Composite {

    protected List labels;
    private String[] labelStrings;

	private MouseAdapter sortMouseAdapter;
	private Boolean sortDescending = null; // choices: null (no sort); true; false

	private int lastSortColumn = -1;
	protected Image sortUpIndicator = new Image(Display.getDefault(), getUpImageData());
	protected Image sortDownIndicator = new Image(Display.getDefault(), getDownImageData());

	private Image sortIndicator = null;

	final int width = 8;
	final int height = 4;

	private ImageData getEmptyImageData() {
		final RGB whiteRGB = new RGB(255, 255, 255); // The transparancy color
		final RGB blackRGB = new RGB(0, 0, 0);

		PaletteData palette = new PaletteData(new RGB[] { whiteRGB, blackRGB });
		ImageData imageData = new ImageData(width, height, 1, palette);
		int whitePixel = imageData.palette.getPixel(whiteRGB);
		imageData.transparentPixel = whitePixel;
		return imageData;
	}

	private ImageData getUpImageData() {
		ImageData imageData = getEmptyImageData();
		int numTransparentPixels = 0;
		for (int row = height - 1; row >= 0; --row) {
			for (int col = numTransparentPixels; col < width / 2; ++col) {
				imageData.setPixel(col, row, 1);
				imageData.setPixel(width - col - 1, row, 1);
			}
			++numTransparentPixels;
		}
		return imageData;
	}

	private ImageData getDownImageData() {
		ImageData imageData = getEmptyImageData();
		int numTransparentPixels = 0;
		for (int row = 0; row < height; ++row) {
			for (int col = numTransparentPixels; col < width / 2; ++col) {
				imageData.setPixel(col, row, 1);
				imageData.setPixel(width - col - 1, row, 1);
			}
			++numTransparentPixels;
		}
		return imageData;
	}

	/**
     * 
	 * @param parent
	 * @param style
	 */
	public AbstractSortableHeader(Composite parent, int style) {
		super(parent, style);
		makeMouseAdapter();
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent arg0) {
				sortUpIndicator.dispose();
				sortDownIndicator.dispose();
			}
		});
	}

	private boolean toggleSortDirection() {
		if (sortDescending == null) {
			sortDescending = Boolean.TRUE;
			sortIndicator = sortDownIndicator;
			return sortDescending.booleanValue();
		}
		sortDescending = new Boolean(!sortDescending.booleanValue());
		if (sortDescending.booleanValue()) {
			sortIndicator = sortDownIndicator;
		} else {
			sortIndicator = sortUpIndicator;
		}
		return sortDescending.booleanValue();
	}

	private void makeMouseAdapter() {
		this.sortMouseAdapter = new MouseAdapter() {

			public void mouseDown(MouseEvent e) {
				CLabel label = (CLabel) e.widget;
				int c = labels.indexOf(label);
				if (c != lastSortColumn) {
					sortDescending = null;
					sortIndicator = null;
				}
				lastSortColumn = c;
				sortOnColumn(c, toggleSortDirection());

				for (int i = 0; i < labels.size(); i++) {
					CLabel labelToSet = (CLabel) labels.get(i);
					if (i != c) {
						labelToSet.setImage(null);
					} else {
						labelToSet.setImage(sortIndicator);
					}
				}
			}
		};
	}

	/**
	 * Clients must override this method to reset the current sort column.
	 * 
	 * @param column
	 *            The column on which to sort
	 * @param sortDescending
	 *            true if the sort should be in descending order; false for
	 *            ascending order
	 */
	protected abstract void sortOnColumn(int column, boolean sortDescending);

    /**
     * Clients must call this method (normally in the constructor) to set the
     * column names.
     * 
     * @param columnText String[] The text to display in each column
     */
    public void setColumnText(String[] columnText) {
        this.labelStrings = columnText;
        initialize();
    }

    private void initialize() {
        this.labels = new ArrayList();
        String[] fields = labelStrings;
        for (int i = 0; i < fields.length; i++) {
            CLabel label = new CLabel(this, SWT.NONE);
            if (label.isDisposed()) return;
            this.labels.add(label);
            label.setText(fields[i]);
            initializeLabel(label);
        }
    }
    
    protected void initializeLabel(CLabel label) {
        label.addMouseListener(this.sortMouseAdapter);
    }
}
