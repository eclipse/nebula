package org.eclipse.nebula.widgets.opal.nebulaslider;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

public interface NebulaSliderRenderer {

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
