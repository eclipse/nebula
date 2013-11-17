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

import java.util.Calendar;
import java.util.Date;

import org.eclipse.nebula.cwt.test.AbstractVTestCase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class FocusTests extends AbstractVTestCase {

	private CdtTester tester;
	private Button button;
	private Button button1;
	private Button button2;
	private CDateTime cdt1;
	private CDateTime cdt2;

	public void setUp1() throws Exception {
		Shell shell = getShell();
		shell.setLayout(new GridLayout(4, false));

		button1 = new Button(shell, SWT.TOGGLE);
		button1.setText("B1");
		button1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		cdt1 = new CDateTime(shell, CDT.BORDER | CDT.DROP_DOWN);
		cdt1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		button2 = new Button(shell, SWT.PUSH);
		button2.setText("B2");
		button2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		cdt2 = new CDateTime(shell, CDT.BORDER | CDT.SIMPLE);
		cdt2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
	}

	public void setUp2() throws Exception {
		Shell shell = getShell();
		shell.setLayout(new GridLayout(4, false));

		cdt1 = new CDateTime(shell, CDT.BORDER | CDT.SIMPLE);
		cdt1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		button1 = new Button(shell, SWT.TOGGLE);
		button1.setText("B1");
		button1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		cdt2 = new CDateTime(shell, CDT.BORDER | CDT.DROP_DOWN);
		cdt2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		button2 = new Button(shell, SWT.PUSH);
		button2.setText("B2");
		button2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
	}

	public void setUp3() throws Exception {
		Shell shell = getShell();
		shell.setLayout(new GridLayout(4, false));

		button1 = new Button(shell, SWT.TOGGLE);
		button1.setText("B1");
		button1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		cdt1 = new CDateTime(shell, CDT.BORDER | CDT.SIMPLE);
		cdt1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		button2 = new Button(shell, SWT.PUSH);
		button2.setText("B2");
		button2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		cdt2 = new CDateTime(shell, CDT.BORDER | CDT.DROP_DOWN);
		cdt2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
	}

	public void setUp4() throws Exception {
		Shell shell = getShell();
		shell.setLayout(new GridLayout(4, false));

		cdt1 = new CDateTime(shell, CDT.BORDER | CDT.DROP_DOWN);
		cdt1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		button1 = new Button(shell, SWT.TOGGLE);
		button1.setText("B1");
		button1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		cdt2 = new CDateTime(shell, CDT.BORDER | CDT.SIMPLE);
		cdt2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		button2 = new Button(shell, SWT.PUSH);
		button2.setText("B2");
		button2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
	}

	public void setUp5() throws Exception {
		Shell shell = getShell();
		shell.setLayout(new GridLayout(4, false));

		button1 = new Button(shell, SWT.TOGGLE);
		button1.setText("B1");
		button1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		cdt1 = new CDateTime(shell, CDT.BORDER | CDT.DROP_DOWN | CDT.TAB_FIELDS);
		cdt1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		button2 = new Button(shell, SWT.PUSH);
		button2.setText("B2");
		button2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		cdt2 = new CDateTime(shell, CDT.BORDER | CDT.SIMPLE | CDT.TAB_FIELDS);
		cdt2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
	}

	public void setUp6() throws Exception {
		Shell shell = getShell();
		shell.setLayout(new GridLayout(4, false));

		button1 = new Button(shell, SWT.TOGGLE);
		button1.setText("B1");
		button1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		cdt1 = new CDateTime(shell, CDT.BORDER | CDT.DROP_DOWN | CDT.TAB_FIELDS);
		cdt1.setPattern("z MM/dd/yyyy");
		cdt1.setSelection(new Date());
		cdt1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		button2 = new Button(shell, SWT.PUSH);
		button2.setText("B2");
		button2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		cdt2 = new CDateTime(shell, CDT.BORDER | CDT.SIMPLE | CDT.TAB_FIELDS);
		cdt2.setPattern("MM/dd/yyyy");
		cdt2.setSelection(new Date());
		cdt2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
	}

	public void setUp7() throws Exception {
		Shell shell = getShell();
		shell.setLayout(new GridLayout(4, false));

		button1 = new Button(shell, SWT.TOGGLE);
		button1.setText("B1");
		button1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		cdt1 = new CDateTime(shell, CDT.BORDER | CDT.DROP_DOWN | CDT.TAB_FIELDS);
		cdt1.setPattern("MM/dd/yyyy z");
		cdt1.setSelection(new Date());
		cdt1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		button2 = new Button(shell, SWT.PUSH);
		button2.setText("B2");
		button2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

		cdt2 = new CDateTime(shell, CDT.BORDER | CDT.SIMPLE | CDT.TAB_FIELDS);
		cdt2.setPattern("MM/dd/yyyy");
		cdt2.setSelection(new Date());
		cdt2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
	}

	private Listener listener;
	public void focusListenersSetup() {
		listener = new Listener() {
			public void handleEvent(Event event) {
				if(SWT.FocusIn == event.type) {
					System.out.println("FocusIn: " + event.widget);
				} else if(SWT.FocusOut == event.type) {
					System.out.println("FocusOut: " + event.widget);
				}
			}
		};

		cdt1 = new CDateTime(getShell(), CDT.BORDER | CDT.SIMPLE);
		cdt1.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				System.out.println("FocusGained: cdt1");
			}
			public void focusLost(FocusEvent e) {
				System.out.println("FocusLost: cdt1");
			}
		});
		cdt1.addListener(SWT.FocusIn, listener);
		cdt1.addListener(SWT.FocusOut, listener);

		cdt2 = new CDateTime(getShell(), CDT.BORDER | CDT.SIMPLE);
		cdt2.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				System.out.println("FocusGained: cd2");
			}
			public void focusLost(FocusEvent e) {
				System.out.println("FocusLost: cd2");
			}
		});
		cdt2.addListener(SWT.FocusIn, listener);
		cdt2.addListener(SWT.FocusOut, listener);
	}

	public void testFocusListeners() {
		assertTrue(hasFocus(cdt1));

		System.out.println("click cdt1");
		click(cdt1);

		System.out.println("click cdt2");
		click(cdt2);
	}

	public void testMouse_1() {
		assertTrue(hasFocus(button1));

		click(button1);
		assertTrue(hasFocus(button1));

		click(cdt1);
		assertTrue(hasFocus(cdt1.getTextWidget()));

		click(button2);
		assertTrue(hasFocus(button2));

		click(cdt2);
		assertTrue(hasFocus(cdt2));
	}

	public void testTabFieldsForward_5() {
		assertTrue(hasFocus(button1));

		keyPress('\t');
		assertTrue(hasFocus(cdt1.getTextWidget()));

		keyPress('\t');
		assertTrue(hasFocus(button2));

		keyPress('\t');
		assertTrue(hasFocus(cdt2));

		keyPress('\t');
		assertTrue(hasFocus(button1));
	}

	public void testTabFieldsForward_6() {
		syncExec(new Runnable() {
			public void run() {
				button1.setFocus();
			}
		});
		assertTrue(hasFocus(button1));

		keyPress('\t');
		assertTrue(hasFocus(cdt1.getTextWidget()));

		keyPress('\t');
		assertTrue(hasFocus(cdt1.getTextWidget()));

		keyPress('\t');
		assertTrue(hasFocus(cdt1.getTextWidget()));

		keyPress('\t');
		assertTrue(hasFocus(button2));

		keyPress('\t');
		assertTrue(hasFocus(cdt2));

		keyPress('\t');
		assertTrue(hasFocus(button1));
	}

	public void testTabFieldsReverse_5() {
		assertTrue(hasFocus(button1));

		keyDown(SWT.SHIFT);

		keyPress('\t');
		assertTrue(hasFocus(cdt2));

		keyPress('\t');
		assertTrue(hasFocus(button2));

		keyPress('\t');
		assertTrue(hasFocus(cdt1.getTextWidget()));

		keyPress('\t');
		assertTrue(hasFocus(button1));

		keyUp(SWT.SHIFT);
	}

	public void testTabFieldsReverse_7() {
		syncExec(new Runnable() {
			public void run() {
				button1.setFocus();
			}
		});

		assertTrue(hasFocus(button1));

		keyDown(SWT.SHIFT);

		keyPress('\t');
		assertTrue(hasFocus(cdt2));

		keyPress('\t');
		assertTrue(hasFocus(button2));

		keyPress('\t');
		assertTrue(hasFocus(cdt1.getTextWidget()));

		keyPress('\t');
		assertTrue(hasFocus(cdt1.getTextWidget()));

		keyPress('\t');
		assertTrue(hasFocus(cdt1.getTextWidget()));

		keyPress('\t');
		assertTrue(hasFocus(button1));

		keyUp(SWT.SHIFT);
	}

	public void testTabKeyForward_1() {
		assertTrue(hasFocus(button1));

		keyPress('\t');
		assertTrue(hasFocus(cdt1.getTextWidget()));

		keyPress('\t');
		assertTrue(hasFocus(button2));

		keyPress('\t');
		assertTrue(hasFocus(cdt2));

		keyPress('\t');
		assertTrue(hasFocus(button1));
	}

	public void testTabKeyForward_2() {

		assertTrue(hasFocus(getComposite(getPanel(cdt1))));

		keyPress('\t');
		assertTrue(hasFocus(button1));

		keyPress('\t');
		assertTrue(hasFocus(cdt2.getTextWidget()));

		keyPress('\t');
		assertTrue(hasFocus(button2));

		keyPress('\t');
		assertTrue(hasFocus(getPanel(cdt1).getComposite()));
	}

	public void testTabKeyForward_3() {
		assertTrue(hasFocus(button1));

		keyPress('\t');
		assertTrue(hasFocus(getPanel(cdt1).getComposite()));

		keyPress('\t');
		assertTrue(hasFocus(button2));

		keyPress('\t');
		assertTrue(hasFocus(cdt2.getTextWidget()));

		keyPress('\t');
		assertTrue(hasFocus(button1));
	}

	public void testTabKeyForward_4() {
		assertTrue(hasFocus(cdt1.getTextWidget()));

		keyPress('\t');
		assertTrue(hasFocus(button1));

		keyPress('\t');
		assertTrue(hasFocus(cdt2));

		keyPress('\t');
		assertTrue(hasFocus(button2));

		keyPress('\t');
		assertTrue(hasFocus(cdt1.getTextWidget()));
	}

	public void testTabKeyReverse_1() {
		assertTrue(hasFocus(button1));

		keyDown(SWT.SHIFT);

		keyPress('\t');
		assertTrue(hasFocus(cdt2));

		keyPress('\t');
		assertTrue(hasFocus(button2));

		keyPress('\t');
		assertTrue(hasFocus(cdt1.getTextWidget()));

		keyPress('\t');
		assertTrue(hasFocus(button1));

		keyUp(SWT.SHIFT);
	}

	public void testTabKeyReverse_2() {
		assertTrue(hasFocus(getPanel(cdt1).getComposite()));

		keyDown(SWT.SHIFT);

		keyPress('\t');
		assertTrue(hasFocus(button2));

		keyPress('\t');
		assertTrue(hasFocus(cdt2.getTextWidget()));

		keyPress('\t');
		assertTrue(hasFocus(button1));

		keyPress('\t');
		assertTrue(hasFocus(getPanel(cdt1).getComposite()));

		keyUp(SWT.SHIFT);
	}

	public void testTabKeyReverse_3() {
		assertTrue(hasFocus(button1));

		keyDown(SWT.SHIFT);

		keyPress('\t');
		assertTrue(hasFocus(cdt2.getTextWidget()));

		keyPress('\t');
		assertTrue(hasFocus(button2));

		keyPress('\t');
		assertTrue(hasFocus(getPanel(cdt1).getComposite()));

		keyPress('\t');
		assertTrue(hasFocus(button1));

		keyUp(SWT.SHIFT);
	}

	public void testTabKeyReverse_4() {
		assertTrue(hasFocus(cdt1.getTextWidget()));

		keyDown(SWT.SHIFT);

		keyPress('\t');
		assertTrue(hasFocus(button2));

		keyPress('\t');
		assertTrue(hasFocus(cdt2));

		keyPress('\t');
		assertTrue(hasFocus(button1));

		keyPress('\t');
		assertTrue(hasFocus(cdt1.getTextWidget()));

		keyUp(SWT.SHIFT);
	}

	public void dateUpdatedAfterFocusLostSetup() {
		tester = new CdtTester(getShell(), CDT.BORDER | CDT.DROP_DOWN | CDT.COMPACT);
		tester.setPattern("dd");
		Calendar cal = tester.getCalendarInstance();
		cal.set(Calendar.DATE, 1);
		tester.setSelection(cal.getTime());

		button = new Button(getShell(), SWT.PUSH);
		button.setText("test");
	}

	public void testDateUpdatedAfterFocusLost() {
		assertEquals("01", tester.getText());

		// test focusOut from mouse click
		Date date = tester.getSelection();
		delay(1000);

		click(tester.getCDateTime().getTextWidget());
		delay(1000);
		keyPress('0');
		keyPress('3');
		delay(1000);
		click(button);
		delay(1000);
		System.out.println(tester.getSelection());
		System.out.println(date);
		
		
		assertEquals("03", tester.getText());
		assertFalse("Dates should not equal", date.equals(tester.getSelection()));

		// test focusOut from tab key
		date = tester.getCalendarTime();

		click(tester.getCDateTime().getTextWidget());
		keyPress('0');
		keyPress('5');
		keyPress('\t');

		assertEquals("05", tester.getText());
		assertFalse("Dates should not equal", date.equals(tester.getCalendarTime()));
	}

	private void delay(int i) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}
}
