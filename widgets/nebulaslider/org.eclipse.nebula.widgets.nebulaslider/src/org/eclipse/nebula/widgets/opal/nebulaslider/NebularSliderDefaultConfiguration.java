/*******************************************************************************
 * Copyright (c) 2024 Laurent CARON
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - Initial
 * implementation and API
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.nebulaslider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

/**
 * Default graphic configuration for the NebularSlider widget
 */
public class NebularSliderDefaultConfiguration implements NebulaSliderGraphicConfiguration {
	private static final int H_MARGIN = 5;
	private static final int SELECTOR_WIDTH = 78;
	private static final int BAR_HEIGHT = 12;
	private static final int SELECTOR_HEIGHT = 32;
	protected final NebulaSlider parentSlider;

	public NebularSliderDefaultConfiguration(final NebulaSlider parentSlider) {
		this.parentSlider = parentSlider;
	}

	@Override
	public Color getBarInsideColor() {
		return getAndDisposeColor(225, 225, 225);
	}

	@Override
	public Color getBarBorderColor() {
		return getAndDisposeColor(211, 211, 211);
	}

	@Override
	public Color getBarSelectionColor() {
		return getAndDisposeColor(41, 128, 185);
	}

	@Override
	public Color getSelectorColor() {
		return getAndDisposeColor(52, 152, 219);
	}

	@Override
	public Color getSelectorColorBorder() {
		return getAndDisposeColor(224, 237, 245);
	}

	@Override
	public Color getSelectorTextColor() {
		return getAndDisposeColor(255, 255, 255);
	}

	@Override
	public Color getArrowColor() {
		return getAndDisposeColor(153, 203, 237);
	}

	@Override
	public Font getTextFont() {
		final FontData fontData = parentSlider.getFont().getFontData()[0];
		final Font newFont = new Font(parentSlider.getDisplay(), fontData.getName(), Math.max(fontData.getHeight(), 14), SWT.BOLD);
		parentSlider.addDisposeListener(e -> {
			if (!newFont.isDisposed()) {
				newFont.dispose();
			}
		});
		return newFont;
	}

	@Override
	public int getHorizontalMargin() {
		return H_MARGIN;
	}

	@Override
	public int getSelectorWidth() {
		return SELECTOR_WIDTH;
	}

	@Override
	public int getSelectorHeight() {
		return SELECTOR_HEIGHT;
	}

	@Override
	public int getBarHeight() {
		return BAR_HEIGHT;
	}

	protected Color getAndDisposeColor(final int r, final int g, final int b) {
		final Color color = new Color(parentSlider.getDisplay(), r, g, b);
		parentSlider.addDisposeListener(e -> {
			if (!color.isDisposed()) {
				color.dispose();
			}
		});
		return color;
	}

	@Override
	public int getArrowLineWidth() {
		return 3;
	}

}
