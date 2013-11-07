package org.eclipse.nebula.widgets.cdatetime;

import org.eclipse.nebula.cwt.test.AbstractVTestCase;
import org.eclipse.swt.SWT;

public class DropDownTests extends AbstractVTestCase {

	private CdtTester cdt;
	private boolean running;
	private Runnable callback = new Runnable() {
		public void run() {
			running = false;
		}
	};

	public void setUp() throws Exception {
		cdt = new CdtTester(getShell(), CDT.BORDER | CDT.DROP_DOWN);
	}

	public void testOpenAndCloseByKeys() throws Exception {
		cdt.setFocus();
		assertTrue(hasFocus(cdt.getTextWidget()));

		keyDown(SWT.CTRL);
		keyPress(' ');
		keyUp(SWT.CTRL);

		assertTrue(cdt.isOpen());

		// TODO should not need pauses!
		pause(1000);

		keyPress(SWT.ESC);

		assertFalse(cdt.isOpen());
	}

	public void testOpenAndCloseByMouse() throws Exception {
		assertFalse(cdt.isOpen());
		click(cdt.getButton());
		assertTrue(cdt.isOpen());
		click(cdt.getButton());
		assertFalse(cdt.isOpen());
	}

	public void testOpenAndCloseByCode() throws Exception {
		assertFalse(cdt.isOpen());
		cdt.setOpen(true);
		assertTrue(cdt.isOpen());
		cdt.setOpen(false);
		assertFalse(cdt.isOpen());
	}


	public void testOpenAndCloseByCodeWithCallbacks() throws Exception {
		assertFalse(cdt.isOpen());

		running = true;
		cdt.setOpen(true, callback);
		waitForCallback();

		assertTrue(cdt.isOpen());

		running = true;
		cdt.setOpen(false, callback);
		waitForCallback();

		assertFalse(cdt.isOpen());
	}

	private void waitForCallback() {
		while(running) {
			try {
				Thread.sleep(100);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
