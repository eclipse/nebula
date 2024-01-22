package org.eclipse.nebula.widgets.opal.nebulaslider.snippets;

import org.eclipse.nebula.widgets.opal.nebulaslider.NebulaSlider;
import org.eclipse.nebula.widgets.opal.nebulaslider.NebularDefaultSliderRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

public class NebulaCustomSliderRenderer extends NebularDefaultSliderRenderer {

	public NebulaCustomSliderRenderer(final NebulaSlider parentSlider) {
		super(parentSlider);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.nebulaslider.NebularDefaultSliderRenderer#getBarInsideColor()
	 */
	@Override
	public Color getBarInsideColor() {
		return getAndDisposeColor(231, 225, 219);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.nebulaslider.NebularDefaultSliderRenderer#getBarBorderColor()
	 */
	@Override
	public Color getBarBorderColor() {
		return getAndDisposeColor(219, 211, 203);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.nebulaslider.NebularDefaultSliderRenderer#getBarSelectionColor()
	 */
	@Override
	public Color getBarSelectionColor() {
		return getAndDisposeColor(129, 108, 91);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.nebulaslider.NebularDefaultSliderRenderer#getSelectorColor()
	 */
	@Override
	public Color getSelectorColor() {
		return getAndDisposeColor(148, 130, 113);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.nebulaslider.NebularDefaultSliderRenderer#getSelectorColorBorder()
	 */
	@Override
	public Color getSelectorColorBorder() {
		return getAndDisposeColor(238, 234, 230);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.nebulaslider.NebularDefaultSliderRenderer#getSelectorTextColor()
	 */
	@Override
	public Color getSelectorTextColor() {
		return getAndDisposeColor(255, 255, 204);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.nebulaslider.NebularDefaultSliderRenderer#getArrowColor()
	 */
	@Override
	public Color getArrowColor() {
		return getAndDisposeColor(203, 192, 181);
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.nebulaslider.NebularDefaultSliderRenderer#getTextFont()
	 */
	@Override
	public Font getTextFont() {
		final FontData fontData = parentSlider.getFont().getFontData()[0];
		final Font newFont = new Font(parentSlider.getDisplay(), "Arial", Math.max(fontData.getHeight(), 14), SWT.ITALIC);
		parentSlider.addDisposeListener(e -> {
			if (!newFont.isDisposed()) {
				newFont.dispose();
			}
		});
		return newFont;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.nebulaslider.NebularDefaultSliderRenderer#getHorizontalMargin()
	 */
	@Override
	public int getHorizontalMargin() {
		// TODO Auto-generated method stub
		return super.getHorizontalMargin();
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.nebulaslider.NebularDefaultSliderRenderer#getSelectorWidth()
	 */
	@Override
	public int getSelectorWidth() {
		return 100;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.nebulaslider.NebularDefaultSliderRenderer#getSelectorHeight()
	 */
	@Override
	public int getSelectorHeight() {
		return 40;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.nebulaslider.NebularDefaultSliderRenderer#getBarHeight()
	 */
	@Override
	public int getBarHeight() {
		return 18;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.nebulaslider.NebularDefaultSliderRenderer#getArrowLineWidth()
	 */
	@Override
	public int getArrowLineWidth() {
		return 5;
	}

}
