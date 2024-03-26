package org.eclipse.nebula.widgets.cdatetime.css;

import java.util.Date;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.nebula.widgets.cdatetime.CdtTester;
import org.eclipse.swt.graphics.Color;

public class ThemingPickerPartPropertiesTests extends BaseCSSThemingTest {
	
	/**
	 * @throws java.lang.Exception
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		tester = new CdtTester(getShell(), CDT.BORDER | CDT.COMPACT | CDT.DROP_DOWN | CDT.DATE_LONG | CDT.TIME_MEDIUM);
		tester.setSelection(new Date());
	}
	
	public void testCdtPickerBackgroundColor() throws Exception {
		checkColor("cdt-picker-background-color", cdt -> cdt.getPickerBackgroundColor());
	}

	public void testCdtPickerColor() throws Exception {
		checkColor("cdt-picker-color", cdt -> cdt.getPickerForegroundColor());
	}

	public void testCdtPickerFont() throws Exception {
		checkFont("cdt-picker-font", cdt -> cdt.getPickerFont());
	}

	public void testCdtPickerFontStyle() throws Exception {
		checkFontStyle("cdt-picker-font-style", cdt -> cdt.getPickerFont());
	}

	public void testCdtPickerFontSize() throws Exception {
		checkFontSize("cdt-picker-font-size", cdt -> cdt.getPickerFont());
	}

	public void testCdtPickerFontWeight() throws Exception {
		checkFontWeight("cdt-picker-font-weight", cdt -> cdt.getPickerFont());
	}

	public void testCdtPickerFontFamily() throws Exception {
		checkFontFamily("cdt-picker-font-family", cdt -> cdt.getPickerFont());
	}
	
	public void testCdtPickerActiveDayColor() throws Exception {
		checkColor("cdt-picker-active-day-color", new ColorGetter() {
			public Color getColor(CDateTime cdt) {
				return cdt.getPickerActiveDayColor();
			}
		});
	}
	public void testCdtPickerInactiveDayColor() throws Exception {
		checkColor("cdt-picker-inactive-day-color", cdt -> cdt.getPickerInactiveDayColor());
	}
	
	public void testCdtPickerTodayColor() throws Exception {
		checkColor("cdt-picker-today-color", cdt -> cdt.getPickerTodayColor());
	}

	public void testCdtPickerMinutesColor() throws Exception {
		checkColor("cdt-picker-minutes-color", cdt -> cdt.getPickerMinutesColor());
	}
	public void testCdtPickerMinutesBackgroundColor() throws Exception {
		checkColor("cdt-picker-minutes-background-color", cdt -> cdt.getPickerMinutesBackgroundColor());
	}
}
