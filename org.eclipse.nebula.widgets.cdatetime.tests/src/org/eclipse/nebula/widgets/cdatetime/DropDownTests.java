package org.eclipse.nebula.widgets.cdatetime;

import org.eclipse.nebula.cwt.test.VTestCase;
import org.eclipse.swt.SWT;

public class DropDownTests extends VTestCase {

	private CdtTester cdt;
	private boolean running;
	
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

		Runnable callback = new Runnable() {
			public void run() {
				running = false;
			}
		};

		running = true;
		cdt.setOpen(true, callback);

		while(running) {
			try {
				Thread.sleep(100);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}

		assertTrue(cdt.isOpen());

		running = true;
		cdt.setOpen(false, callback);
		
		while(running) {
			try {
				Thread.sleep(100);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}

		assertFalse(cdt.isOpen());
	}

}
