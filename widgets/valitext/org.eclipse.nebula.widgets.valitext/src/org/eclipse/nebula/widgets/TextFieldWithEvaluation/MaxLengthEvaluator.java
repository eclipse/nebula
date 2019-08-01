package org.eclipse.nebula.widgets.TextFieldWithEvaluation;

import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

@SuppressWarnings("restriction")
public class MaxLengthEvaluator implements IEvaluator {
	
	private static final String E4_DARK_THEME_ID = "org.eclipse.e4.ui.css.theme.e4_dark";
	IThemeEngine engine = (IThemeEngine) Display.getDefault().getData("org.eclipse.e4.ui.css.swt.theme");

	private int maxLength;

	private final Color dark_gray = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
	private final Color dark_red = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED);
	private final Color gray = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
	private final Color red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);

	public MaxLengthEvaluator(int maxLength) {
		this.maxLength = maxLength;
	}

	@Override
	public Evaluation evaluate(String text) {
		int length = text.length();

		return new Evaluation(length + " / " + maxLength, length > maxLength ? getInvalidColor() : getValidColor());
	}

	private Color getValidColor() {
		return isDarkTheme() ? gray : dark_gray;
	}

	private Color getInvalidColor() {
		return isDarkTheme() ? red : dark_red;
	}
	
	private boolean isDarkTheme() {
		if (engine != null) {
			String activeThemeId = engine.getActiveTheme().getId();
			return E4_DARK_THEME_ID.equals(activeThemeId);
		}
		return false;
	}

	@Override
	public boolean isValid(String text) {
		return text.length() <= maxLength;
	}
}
