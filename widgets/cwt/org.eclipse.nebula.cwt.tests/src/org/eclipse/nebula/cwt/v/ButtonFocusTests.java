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

package org.eclipse.nebula.cwt.v;

import org.eclipse.nebula.cwt.test.AbstractVTestCase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;



public class ButtonFocusTests extends AbstractVTestCase {

	private Button button1;
	private Button button2;
	private VButton vbutton1;
	private VButton vbutton2;

	public void setUp1() throws Exception {
		Shell shell = getShell();
		shell.setLayout(new GridLayout(2, true));

		// row 1
		button1 = new Button(shell, SWT.PUSH);
		button1.setText("B1");
		button1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		VCanvas c1 = new VCanvas(shell, SWT.NONE);
		c1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		c1.getPanel().setLayout(new VSimpleLayout());
		
		vbutton1 = new VButton(c1.getPanel(), SWT.PUSH);
		vbutton1.setText("VB1");

		// row 2
		button2 = new Button(shell, SWT.PUSH);
		button2.setText("B2");
		button2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		VCanvas c2 = new VCanvas(shell, SWT.NONE);
		c2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		c2.getPanel().setLayout(new VSimpleLayout());

		vbutton2 = new VButton(c2.getPanel(), SWT.PUSH);
		vbutton2.setText("VB2");
	}
	
	public void setUp2() throws Exception {
		Shell shell = getShell();
		shell.setLayout(new GridLayout(2, false));

		// row 1
		VCanvas c1 = new VCanvas(shell, SWT.NONE);
		c1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		c1.getPanel().setLayout(new VSimpleLayout());
		
		vbutton1 = new VButton(c1.getPanel(), SWT.PUSH);
		vbutton1.setText("VB1");

		button1 = new Button(shell, SWT.PUSH);
		button1.setText("B1");
		button1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// row 2
		VCanvas c2 = new VCanvas(shell, SWT.NONE);
		c2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		c2.getPanel().setLayout(new VSimpleLayout());

		vbutton2 = new VButton(c2.getPanel(), SWT.PUSH);
		vbutton2.setText("VB2");

		button2 = new Button(shell, SWT.PUSH);
		button2.setText("B2");
		button2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	public void setUp3() throws Exception {
		Shell shell = getShell();
		shell.setLayout(new GridLayout(2, false));

		VCanvas c1 = new VCanvas(shell, SWT.NONE);
		c1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		c1.getPanel().setLayout(new VGridLayout(2, true));
		
		vbutton1 = new VButton(c1.getPanel(), SWT.PUSH);
		vbutton1.setText("VB1");

		vbutton2 = new VButton(c1.getPanel(), SWT.PUSH);
		vbutton2.setText("VB2");

		moveTo(10, 10);
		move(5, 5);
	}
	
	public void testMouse_1() {
		assertTrue(hasFocus(button1));

		click(vbutton1);
		assertTrue(hasFocus(vbutton1));

		click(button2);
		assertTrue(hasFocus(button2));

		click(vbutton2);
		assertTrue(hasFocus(vbutton2));
		
		click(button1);
		assertTrue(hasFocus(button1));
	}
	
	public void testMouse_3() {
		assertTrue(hasFocus(vbutton1));

		click(vbutton2);
		assertTrue(hasFocus(vbutton2));
		assertFalse(hasFocus(vbutton1));

		click(vbutton1);
		assertTrue(hasFocus(vbutton1));
		assertFalse(hasFocus(vbutton2));
	}
	
	public void testTabKeyForward_1() {
		assertTrue(hasFocus(button1));

		keyPress('\t');
		assertTrue(hasFocus(vbutton1));

		keyPress('\t');
		assertTrue(hasFocus(button2));

		keyPress('\t');
		assertTrue(hasFocus(vbutton2));
		
		keyPress('\t');
		assertTrue(hasFocus(button1));
	}

	public void testTabKeyReverse_1() {
		assertTrue(hasFocus(button1));

		keyDown(SWT.SHIFT);
		
		keyPress('\t');
		assertTrue(hasFocus(vbutton2));

		keyPress('\t');
		assertTrue(hasFocus(button2));

		keyPress('\t');
		assertTrue(hasFocus(vbutton1));
		
		keyPress('\t');
		assertTrue(hasFocus(button1));

		keyUp(SWT.SHIFT);
	}

	public void testTabKeyForward_2() {
		assertTrue(hasFocus(vbutton1));

		keyPress('\t');
		assertTrue(hasFocus(button1));

		keyPress('\t');
		assertTrue(hasFocus(vbutton2));

		keyPress('\t');
		assertTrue(hasFocus(button2));
		
		keyPress('\t');
		assertTrue(hasFocus(vbutton1));
	}

	public void testTabKeyReverse_2() {
		assertTrue(hasFocus(vbutton1));

		keyDown(SWT.SHIFT);
		
		keyPress('\t');
		assertTrue(hasFocus(button1));

		keyPress('\t');
		assertTrue(hasFocus(vbutton2));

		keyPress('\t');
		assertTrue(hasFocus(button2));
		
		keyPress('\t');
		assertTrue(hasFocus(vbutton1));

		keyUp(SWT.SHIFT);
	}

}
