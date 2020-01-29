/****************************************************************************
 * Copyright (c) 2008, 2009 Jeremy Dowdall
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Jeremy Dowdall <jeremyd@aspencloud.com> - initial API and implementation
 *****************************************************************************/
package org.eclipse.nebula.cwt.v;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class VCanvas extends Canvas {

	private VPanel panel;
	
	public VCanvas(Composite parent, int style) {
		super(parent, style);
		panel = new VPanel(this, SWT.NONE);
	}

	public VPanel getPanel() {
		return panel;
	}

}
