package org.eclipse.nebula.widgets.cdatetime.css;

import java.util.Date;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CdtTester;

public class ThemingButtonPropertiesTests extends BaseCSSThemingTest {
	/**
	 * @throws java.lang.Exception
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		tester = new CdtTester(getShell(), CDT.BORDER | CDT.COMPACT | CDT.DROP_DOWN | CDT.DATE_LONG | CDT.TIME_MEDIUM);
		tester.setSelection(new Date());
	}

	public void testCdtButtonHoverBackgroundColor() throws Exception {
		checkColor("cdt-button-hover-background-color", cdt -> cdt.getButtonHoverBackgroundColor());
	}

	public void testCdtButtonHoverBorderColor() throws Exception {
		checkColor("cdt-button-hover-border-color", cdt -> cdt.getButtonHoverBorderColor());
	}

	public void testCdtButtonSelectedBackgroundColor() throws Exception {
		checkColor("cdt-button-selected-background-color", cdt -> cdt.getButtonSelectedBackgroundColor());
	}

	public void testCdtButtonSelectedBorderColor() throws Exception {
		checkColor("cdt-button-selected-border-color", cdt -> cdt.getButtonSelectedBorderColor());
	}

}
