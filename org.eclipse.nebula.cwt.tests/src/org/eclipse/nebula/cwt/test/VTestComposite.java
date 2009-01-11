package org.eclipse.nebula.cwt.test;

import org.eclipse.nebula.cwt.v.VWidget;
import org.eclipse.nebula.cwt.v.VPanel;
import org.eclipse.swt.widgets.Composite;

public class VTestComposite extends Composite implements VWidget {

	private VPanel panel;
	
	public VTestComposite(Composite parent, int style) {
		super(parent, style);
		panel = new VPanel(this, style);
	}

	public VPanel getPanel() {
		return panel;
	}
	
}
