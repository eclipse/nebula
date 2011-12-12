package org.eclipse.nebula.widgets.cdatetime;

import org.eclipse.nebula.cwt.test.VTestCase;
import org.eclipse.swt.SWT;

public class Bug364171 extends VTestCase {

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
	
	public void testStartTyping() throws Exception {
		cdt.setFocus();
		
		keyPress('3');
		keyPress('1');
		keyPress('1');
		keyPress('2');
		keyPress('2');
		keyPress('0');
		keyPress('1');
		keyPress('1');
		keyPress('\t');
		
		assertEquals("31-12-11", cdt.getText());

		//	assertTrue(cdt.getCDateTime().getText()Text().equals("31122011"));
	}
}
