/****************************************************************************
 * Copyright (c) 2021 the original authors
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Matthias Paul Scholz
 *****************************************************************************/

package org.eclipse.nebula.widgets.cdatetime;

import java.util.Date;

import org.eclipse.nebula.cwt.test.AbstractVTestCase;
import org.eclipse.nebula.cwt.v.VPanel;

public class CDateTimeDiscreteClockTest extends AbstractVTestCase {

	private CdtTester tester;

	@Override
	protected void setUp() throws Exception {
		tester = new CdtTester(getShell(), CDT.SIMPLE | CDT.DATE_MEDIUM | CDT.TIME_SHORT | CDT.CLOCK_DISCRETE);
		tester.setSelection(new Date());
	}

	/*
	 * Regression test for #572035. Ensures that CDateTime displays a discrete clock
	 * when a date style and a time style have been configured and the clock has been configured with style CDT.CLOCK_DISCRETE.
	 */
	public void testDiscreteClockWithDateMedium() throws Exception {

		getDisplay().syncExec(new Runnable() {
			public void run() {
				VPanel timePicker = tester.getTimePicker();
				assertNotNull(
						"Expected the timepicker not to be null since the CDateTime has been configured with style CDT.TIME_SHORT",timePicker);
				assertEquals(
						"Expected the timepicker to be an instance of DiscreteTimePicker since the CDateTime has been configured with style CDT.CLOCK_DISCRETE",
						DiscreteTimePicker.class, timePicker.getClass());
			}
		});

	}

}