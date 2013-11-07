package org.eclipse.nebula.cwt.base;

import org.eclipse.nebula.cwt.test.AbstractVTestCase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;

public class DropDownTests extends AbstractVTestCase {

	private BaseCombo combo;
	
	public void setUp() throws Exception {
		combo = new BaseCombo(getShell(), SWT.BORDER | SWT.DROP_DOWN) {
			@Override
			protected boolean setContentFocus() {
				if(content != null && !content.isDisposed()) {
					return content.forceFocus();
				}
				return false;
			}
		};
		Label lbl = new Label(combo, 0);
		lbl.setText("hello world");
		combo.setContent(lbl);
	}

	public void testOpenByKey() throws Exception {
		syncExec(new Runnable() {
			public void run() {
				combo.setFocus();
			}
		});
		assertTrue(hasFocus(combo.text));
		
		keyDown(SWT.CTRL);
		keyPress(' ');
		keyUp(SWT.CTRL);
		
		assertTrue(combo.isOpen());
	}

	public void testOpenAndCloseByMouse() throws Exception {
		assertFalse(combo.isOpen());
		click(combo.button);
		assertTrue(combo.isOpen());
		click(combo.button);
		assertFalse(combo.isOpen());
	}
	
	public void testOpenAndCloseByCode() throws Exception {
		assertFalse(combo.isOpen());
		syncExec(new Runnable() {
			public void run() {
				combo.setOpen(true);
			}
		});
		assertTrue(combo.isOpen());
		syncExec(new Runnable() {
			public void run() {
				combo.setOpen(false);
			}
		});
		assertFalse(combo.isOpen());
	}
	
}
