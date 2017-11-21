/****************************************************************************
 * Copyright (c) 2013 the original authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Wim Jongman
 *****************************************************************************/

package org.eclipse.nebula.widgets.cdatetime;

import java.util.Date;

import org.eclipse.nebula.cwt.test.AbstractVTestCase;
import org.eclipse.nebula.cwt.v.VNative;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Spinner;

public class Bug320656 extends AbstractVTestCase {

	private CdtTester tester;

	@Override
	protected void setUp() throws Exception {
		tester = new CdtTester(getShell(), CDT.BORDER | CDT.COMPACT | CDT.TIME_MEDIUM | CDT.SPINNER);
		tester.setSelection(new Date());
	}

	public void testSpinnerSelection() throws Exception {
		VNative<Spinner> spinner = tester.getSpinner();
		assertNotNull(spinner);

		long original = tester.getSelection().getTime();

		moveToEdge(tester.getCDateTime(), SWT.RIGHT | SWT.TOP);
		// sleep(5000);
		// System.out.println(tester.getSelection().getTime());
		moveX(-15);
		moveY(5);
		// sleep(5000);
		click();
		// System.out.println(tester.getSelection().getTime());
		long time = tester.getSelection().getTime();
		assertTrue("Difference = " + (original - time), time == original + (60 * 60 * 1000));

		keyPress(SWT.ARROW_RIGHT);
		moveToEdge(tester.getCDateTime(), SWT.RIGHT | SWT.BOTTOM);
		moveX(-15);
		moveY(-8);
		click();
		original = time;
		time = tester.getSelection().getTime();
		assertTrue("Difference = " + (original - time), time == original - (60 * 1000));

	}
}