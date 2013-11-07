package org.eclipse.nebula.cwt.v;

import org.eclipse.nebula.cwt.test.AbstractVTestCase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class DisposalTests extends AbstractVTestCase {

	private VCanvas comp;
	
	@Override
	protected void setUp() throws Exception {
		comp = new VCanvas(getShell(), SWT.BORDER);
	}
	
	public void testDisposeWithListeners() throws Exception {
		asyncExec(new Runnable() {
			public void run() {
				comp.addListener(SWT.Dispose, new Listener() {
					public void handleEvent(Event event) {
						assertFalse(comp.isDisposed());
					}
				});
			}
		});
		
		asyncExec(new Runnable() {
			public void run() {
				getShell().dispose();
			}
		});
		
		while(getDisplay() != null && !getDisplay().isDisposed()) {
			Thread.sleep(100);
		}
	}
	
}
