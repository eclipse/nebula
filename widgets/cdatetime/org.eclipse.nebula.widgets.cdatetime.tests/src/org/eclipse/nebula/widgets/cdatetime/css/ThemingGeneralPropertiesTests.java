package org.eclipse.nebula.widgets.cdatetime.css;

import java.util.Date;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CdtTester;

public class ThemingGeneralPropertiesTests extends BaseCSSThemingTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		tester = new CdtTester(getShell(), CDT.DATE_MEDIUM | CDT.TIME_MEDIUM);
		tester.setSelection(new Date());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCdtBackgroundColor() throws Exception {
		checkColor("cdt-background-color", cdt -> cdt.getBackground());

	}

	public void testCdtColor() throws Exception {
		checkColor("cdt-color", cdt -> cdt.getForeground());
	}

	public void testCdtFont() throws Exception {
		checkFont("cdt-font", cdt -> cdt.getFont());
	}

	public void testCdtFontStyle() throws Exception {
		checkFontStyle("cdt-font-style", cdt -> cdt.getFont());
	}

	public void testCdtFontSize() throws Exception {
		checkFontSize("cdt-font-size", cdt -> cdt.getFont());
	}

	public void testCdtFontWeight() throws Exception {
		checkFontWeight("cdt-font-weight", cdt -> cdt.getFont());
	}

	public void testCdtFontFamily() throws Exception {
		checkFontFamily("cdt-font-family", cdt -> cdt.getFont());
	}
}
