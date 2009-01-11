package org.eclipse.nebula.cwt.v;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class VCanvas extends Canvas implements VWidget {

	private VPanel panel;
	
	public VCanvas(Composite parent, int style) {
		super(parent, style);
		panel = new VPanel(this, SWT.NONE);
	}

	public VPanel getPanel() {
		return panel;
	}

}
