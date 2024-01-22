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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

/**
 * Interface that describe the graphical configuration of the NebulaSlider widget
 */
public interface NebulaSliderGraphicConfiguration {

	Color getBarInsideColor();

	Color getBarBorderColor();

	Color getBarSelectionColor();

	Color getSelectorColor();

	Color getSelectorColorBorder();

	Color getSelectorTextColor();

	Color getArrowColor();

	int getArrowLineWidth();

	Font getTextFont();

	int getHorizontalMargin();

	int getSelectorWidth();

	int getSelectorHeight();

	int getBarHeight();
}
