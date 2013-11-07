/****************************************************************************
 * Copyright (c) 2008 Jeremy Dowdall
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/

package org.eclipse.nebula.widgets.cdatetime;

import java.util.Locale;

import org.eclipse.nebula.cwt.test.AbstractVTestCase;
import org.eclipse.nebula.cwt.v.VButton;

public class LocaleTests extends AbstractVTestCase {

	private CdtTester tester;

	@Override
	protected void setUp() throws Exception {
		tester = new CdtTester(getShell(), CDT.BORDER | CDT.DROP_DOWN);
	}

	public void testTextUpdatedAfterSetLocale() {
		String clearTextE = Resources.getString("clear.text", Locale.ENGLISH);
		String clearTextG = Resources.getString("clear.text", Locale.GERMAN);
		String nullTextE = Resources.getString("null_text.date", Locale.ENGLISH);
		String nullTextG = Resources.getString("null_text.date", Locale.GERMAN);

		assertFalse("clear.text does not exist in English", clearTextE.equals("!clear.text!"));
		assertFalse("clear.text does not exist in German", clearTextE.equals("!clear.text!") || clearTextE.equals(clearTextG));
		assertFalse("null_text.date does not exist in English", nullTextE.equals("!null_text.date!"));
		assertFalse("null_text.date does not exist in German", nullTextE.equals("!null_text.date!") || nullTextE.equals(nullTextG));

		// Set to English and check texts
		tester.setLocale(Locale.ENGLISH);

		System.out.println(tester.getText());
		assertEquals(nullTextE, tester.getText());

		tester.setOpen(true);
		VButton clearButton = tester.getClearButton();

		System.out.println(clearButton.getText());
		assertEquals(clearTextE, clearButton.getText());

		tester.setOpen(false);

		// Set to German and repeat checks
		tester.setLocale(Locale.GERMAN);

		System.out.println(tester.getText());
		assertEquals(nullTextG, tester.getText());

		tester.setOpen(true);
		clearButton = tester.getClearButton();

		System.out.println(clearButton.getText());
		assertEquals(clearTextG, clearButton.getText());
	}

}
