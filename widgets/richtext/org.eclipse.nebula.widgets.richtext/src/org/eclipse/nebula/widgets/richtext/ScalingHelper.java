/*****************************************************************************
 * Copyright (c) 2020 Dirk Fauth and others.
 *
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *		Dirk Fauth <dirk.fauth@googlemail.com> - Initial API and implementation
 *****************************************************************************/
package org.eclipse.nebula.widgets.richtext;

import org.eclipse.swt.widgets.Display;

/**
 * Helper class to handle display scaling.
 * 
 * @since 1.4
 */
public final class ScalingHelper {

	private ScalingHelper() {
		// private default constructor for helper class
	}

    /**
     * Returns the factor for scaling calculations of pixels regarding the DPI.
     *
     * @param dpi
     *            The DPI for which the factor is requested.
     * @return The factor for dpi scaling calculations.
     */
    public static float getDpiFactor(int dpi) {
        return Math.max(0.1f, Math.round((dpi / 96f) * 100) / 100f);
    }

    /**
     * Converts the given amount of pixels to a DPI scaled value using the
     * factor for the horizontal DPI value.
     *
     * @param pixel
     *            the amount of pixels to convert.
     * @return The converted pixels.
     */
    public static int convertHorizontalPixelToDpi(int pixel) {
        return Math.round(pixel * getDpiFactor(Display.getDefault().getDPI().x));
    }

    /**
     * Converts the given DPI scaled value to a pixel value using the factor for
     * the horizontal DPI.
     *
     * @param dpi
     *            the DPI value to convert.
     * @return The pixel value related to the given DPI
     */
    public static int convertHorizontalDpiToPixel(int dpi) {
        return Math.round(dpi / getDpiFactor(Display.getDefault().getDPI().x));
    }

    /**
     * Converts the given amount of pixels to a DPI scaled value using the
     * factor for the vertical DPI.
     *
     * @param pixel
     *            the amount of pixels to convert.
     * @return The converted pixels.
     */
    public static int convertVerticalPixelToDpi(int pixel) {
        return Math.round(pixel * getDpiFactor(Display.getDefault().getDPI().y));
    }

    /**
     * Converts the given DPI scaled value to a pixel value using the factor for
     * the vertical DPI.
     *
     * @param dpi
     *            the DPI value to convert.
     * @return The pixel value related to the given DPI
     */
    public static int convertVerticalDpiToPixel(int dpi) {
        return Math.round(dpi / getDpiFactor(Display.getDefault().getDPI().y));
    }

}
