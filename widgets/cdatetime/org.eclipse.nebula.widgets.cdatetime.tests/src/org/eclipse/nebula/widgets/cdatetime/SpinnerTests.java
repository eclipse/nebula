package org.eclipse.nebula.widgets.cdatetime;

import java.util.Date;

import org.eclipse.nebula.cwt.test.AbstractVTestCase;
import org.eclipse.nebula.cwt.v.VNative;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Spinner;

public class SpinnerTests extends AbstractVTestCase {

	private CdtTester tester;

	@Override
	protected void setUp() throws Exception {
		tester = new CdtTester(getShell(), CDT.BORDER | CDT.SPINNER);
		tester.setSelection(new Date());
	}

	public void testSpinnerSelection() throws Exception {
		VNative<Spinner> spinner = tester.getSpinner();
		assertNotNull(spinner);

		long original = tester.getSelection().getTime();
		System.out.println(original);

		moveToEdge(tester.getCDateTime(), SWT.RIGHT);
		moveX(-10);

		moveY(-5);
		click();

		long time = tester.getSelection().getTime();
		System.out.println(time);

		assertTrue(time > original);

		moveY(10);
		click();

		time = tester.getSelection().getTime();
		System.out.println(time);

		assertTrue(time == original);

		click();

		time = tester.getSelection().getTime();
		System.out.println(time);

		assertTrue(time < original);
	}

	public void testSpinnerFieldNext() throws Exception {
		VNative<Spinner> spinner = tester.getSpinner();
		assertNotNull(spinner);

		int original = tester.getCDateTime().activeField;
		System.out.println(original);

		moveToEdge(tester.getCDateTime(), SWT.RIGHT);
		moveX(-10);

		click(2);

		int active = tester.getCDateTime().activeField;
		System.out.println(active);

		assertTrue(active > original);
	}

}
