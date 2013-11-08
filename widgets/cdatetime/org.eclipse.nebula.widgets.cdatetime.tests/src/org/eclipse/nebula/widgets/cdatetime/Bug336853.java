/****************************************************************************
 * Copyright (c) 2010 Remain Software, Industrial-TSI and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Wim Jongman - Initial Implementation
 *****************************************************************************/
package org.eclipse.nebula.widgets.cdatetime;

import java.util.Locale;

import org.eclipse.nebula.cwt.test.AbstractVTestCase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * Test for https://bugs.eclipse.org/bugs/show_bug.cgi?id=336853
 *
 * @author Wim Jongman
 *
 */
public class Bug336853 extends AbstractVTestCase {

	private CdtTester cdt;
	private boolean running;
	private Runnable callback = new Runnable() {
		public void run() {
			running = false;
		}
	};

	public void setUp() throws Exception {
		cdt = new CdtTester(getShell(), CDT.BORDER | CDT.DROP_DOWN);
		cdt.setLocale(Locale.US);
		cdt.setFormat(CDT.DATE_SHORT);
	}

	public void testOpenAndCloseByMouse() throws Exception {
		assertFalse(cdt.isOpen());
		click(cdt.getButton());
		assertTrue(cdt.isOpen());
		click(cdt.getButton());
		assertFalse(cdt.isOpen());
	}



	@Override
	public Shell createShell() {
		return new Shell(getDisplay(), SWT.RIGHT_TO_LEFT | SWT.SHELL_TRIM);
	}

	public void testStartTyping() throws Exception {
		cdt.setFocus();

		keyPress('1');
		keyPress('2');
		keyPress('3');
		keyPress('1');
		keyPress('2');
		keyPress('0');
		keyPress('1');
		keyPress('2');
		keyPress('\t');

		assertEquals("12/31/12", cdt.getText());
	}
}
