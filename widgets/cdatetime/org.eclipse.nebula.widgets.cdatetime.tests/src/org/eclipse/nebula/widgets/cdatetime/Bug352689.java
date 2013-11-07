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

import org.eclipse.nebula.cwt.test.AbstractVTestCase;

/**
 * Test for https://bugs.eclipse.org/bugs/show_bug.cgi?id=352689
 *
 * @author Wim Jongman
 *
 */
public class Bug352689 extends AbstractVTestCase {

	private CdtTester cdt;
	private boolean running;
	private Runnable callback = new Runnable() {
		public void run() {
			running = false;
		}
	};

	public void setUp() throws Exception {
		cdt = new CdtTester(getShell(), CDT.BUTTON_AUTO| CDT.BORDER | CDT.DROP_DOWN);
	}

	public void testDisposeException() throws Exception {
//		cdt.setFocus();
//
//		keyPress('3');
//		keyPress('1');
//		keyPress('1');
//		keyPress('2');
//		keyPress('2');
//		keyPress('0');
//		keyPress('1');
//		keyPress('1');
//		keyPress('\t');

//		assertEquals("31-12-11", cdt.getText());

//		getComposite(cdt.getBodyPanel()).getShell().close();

		//	assertTrue(cdt.getCDateTime().getText()Text().equals("31122011"));
	}
}
