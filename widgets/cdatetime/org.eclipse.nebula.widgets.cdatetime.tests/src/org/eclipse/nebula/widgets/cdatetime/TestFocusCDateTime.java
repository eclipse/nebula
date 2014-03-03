/****************************************************************************
 * Copyright (c) 2014 the original authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Baruch Youssin
 *****************************************************************************/
package org.eclipse.nebula.widgets.cdatetime;

import org.eclipse.nebula.cwt.test.AbstractVTestCase;

/**
 * This test fails on Ubuntu 12.04: successFocus = true but hasIndeedFocus = false.
 * Reported as bug 429495.
 */
public class TestFocusCDateTime extends AbstractVTestCase {

	private CdtTester tester;

	/**
	 * @throws java.lang.Exception
	 */
	protected void setUp() throws Exception {
		tester = new CdtTester(getShell(), CDT.DATE_MEDIUM | CDT.TIME_MEDIUM);
	}

	/**
	 * @throws java.lang.Exception
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testFocusCDateTime(){
		Boolean successFocus = tester.setFocus();
		Boolean hasIndeedFocus = hasFocus(tester.getCDateTime());
		assertTrue(successFocus);
		assertTrue(hasIndeedFocus);
	}
}
