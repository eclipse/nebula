package org.eclipse.nebula.cwt.base;

import org.eclipse.nebula.cwt.test.VTestCase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ReparentComboTest extends VTestCase {

	private Shell shell2;
	private BaseCombo combo;

	@Override
	protected void setUp() throws Exception {
		combo = new BaseCombo(getShell(), SWT.BORDER | SWT.DROP_DOWN) {
			@Override
			protected boolean setContentFocus() {
				return true;
			}
		};
		Label lbl = new Label(combo, 0);
		lbl.setText("hello world");
		combo.setContent(lbl);
		combo.button.setPaintInactive(true);
		
		shell2 = new Shell();
		shell2.setText("Shell2");
		shell2.setLayout(new FillLayout());
	}
	
	public void testReparentCombo() {
		syncExec(new Runnable() {
			public void run() {
				Rectangle bounds = getShell().getBounds();
				bounds.y -= 100;
				shell2.setBounds(bounds);
				shell2.open();
			}
		});
		pause(500);
		syncExec(new Runnable() {
			public void run() {
				combo.setOpen(true, new Runnable() {
					public void run() {
						combo.setOpen(false);
					}
				});
			}
		});
		pause(500);
		syncExec(new Runnable() {
			public void run() {
				combo.setParent(shell2);
				shell2.layout(true);
			}
		});
		pause(500);
		syncExec(new Runnable() {
			public void run() {
				combo.setOpen(true, new Runnable() {
					public void run() {
						combo.setOpen(false);
					}
				});
			}
		});
		pause(500);
	}
	
}
