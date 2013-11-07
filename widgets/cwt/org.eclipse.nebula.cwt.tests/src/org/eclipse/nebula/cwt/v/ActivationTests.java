package org.eclipse.nebula.cwt.v;

import org.eclipse.nebula.cwt.test.AbstractVTestCase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;

public class ActivationTests extends AbstractVTestCase {

	public void testMouse() {
		moveTo(10,10);
		
		final VButton[] buttons = new VButton[6];
		syncExec(new Runnable() {
			public void run() {
				Shell shell = getShell();
				shell.setLayout(new GridLayout());
				
				VCanvas comp = new VCanvas(shell, SWT.NONE);
				comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				comp.getPanel().setLayout(new VGridLayout(3, true));
				for(int i = 0; i < buttons.length; i++) {
					if(i == 3) {
						comp = new VCanvas(shell, SWT.NONE);
						comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
						comp.getPanel().setLayout(new VGridLayout(3, true));
					}
					buttons[i] = new VButton(comp.getPanel(), SWT.PUSH);
					buttons[i].setText("VB"+i);
//					buttons[i].setPaintNative(false);
				}
			}
		});
		layoutShell();

		int x = buttons[0].getSize().x / 2 + 2;

		moveTo(buttons[0]);
		assertTrue(buttons[0].hasState(VControl.STATE_ACTIVE));
		
		move(x, 0);
		assertFalse(buttons[0].hasState(VControl.STATE_ACTIVE));
		assertTrue(buttons[0].getParent().hasState(VControl.STATE_ACTIVE));

		move(0, 2*x);
		assertFalse(buttons[0].getParent().hasState(VControl.STATE_ACTIVE));
		assertTrue(buttons[3].getParent().hasState(VControl.STATE_ACTIVE));
		
		move(x, 0);
		assertFalse(buttons[3].getParent().hasState(VControl.STATE_ACTIVE));
		assertTrue(buttons[4].hasState(VControl.STATE_ACTIVE));

		// repeat with native painting
		for(VButton button : buttons) {
			button.setPaintNative(true);
		}
		
		moveTo(buttons[0]);
		assertTrue(buttons[0].hasState(VControl.STATE_ACTIVE));
		
		move(x, 0);
		assertFalse(buttons[0].hasState(VControl.STATE_ACTIVE));
		assertTrue(buttons[0].getParent().hasState(VControl.STATE_ACTIVE));

		move(0, 2*x);
		assertFalse(buttons[0].getParent().hasState(VControl.STATE_ACTIVE));
		assertTrue(buttons[3].getParent().hasState(VControl.STATE_ACTIVE));
		
		move(x, 0);
		assertFalse(buttons[3].getParent().hasState(VControl.STATE_ACTIVE));
		assertTrue(buttons[4].hasState(VControl.STATE_ACTIVE));
	}
}
