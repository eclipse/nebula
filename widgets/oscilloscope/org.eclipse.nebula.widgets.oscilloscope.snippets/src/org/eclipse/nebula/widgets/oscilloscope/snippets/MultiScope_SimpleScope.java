/*******************************************************************************
 *  Copyright (c) 2010 Weltevree Beheer BV, Remain Software & Industrial-TSI
 * 
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Wim S. Jongman - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.oscilloscope.snippets;

import org.eclipse.nebula.widgets.oscilloscope.multichannel.Oscilloscope;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This snippet demonstrates how to run the dispatcher in simple mode.
 * 
 */
public class MultiScope_SimpleScope {

	protected static Shell shell;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected static void createContents() {
		shell = new Shell();
		shell.setText("Nebula Oscilloscope");
		shell.setLayout(new FillLayout());

		// Create a single channel scope
		Oscilloscope scope = new Oscilloscope(shell, SWT.NONE);
		scope.getDispatcher(0).dispatch();
		
	}
}
