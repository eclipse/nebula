/*******************************************************************************
 * Copyright (c) 2009 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     compeople AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.compositetable;

import org.eclipse.nebula.widgets.compositetable.AbstractNativeHeader;
import org.eclipse.nebula.widgets.compositetable.CompositeTable;
import org.eclipse.nebula.widgets.compositetable.ResizableGridRowLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CompositeTable_267316Test {

	private boolean createdDisplay = false;

	private static class Header extends AbstractNativeHeader {
		public Header(Composite parent, int style) {
			super(parent, style);
			setWeights(new int[] { 100 });
			setColumnText(new String[] { "Language" });
		}
	}

	private static final class Row extends Composite {
		private Text text;

		public Row(Composite parent, int style) {
			super(parent, style);
			setLayout(new ResizableGridRowLayout());
			text = new Text(this, SWT.BORDER | SWT.SINGLE);
		}

		public String toString() {
			return text.getText();
		}
	}

	private Display display;
	private Shell shell;

	@Before
	public void setUp() throws Exception {
		display = Display.getCurrent();
		if (display == null) {
			display = new Display();
			createdDisplay = true;
		}
		shell = new Shell(display);
		shell.setLayout(new FillLayout());
	}

	@After
	public void tearDown() throws Exception {
		shell.dispose();
		if (createdDisplay) {
			display.dispose();
		}
	}

	// test methods
	// /////////////
	@Test
	public void testGetHeaderControl() {
		CompositeTable table = new CompositeTable(shell, SWT.NONE);

		Assert.assertNull(table.getHeader());

		new Header(table, SWT.NONE);
		new Row(table, SWT.NONE);

		Assert.assertNull(table.getHeader());

		table.setRunTime(true);

		Assert.assertNotNull(table.getHeader());
		Assert.assertTrue(table.getHeader() instanceof Header);
		Assert.assertTrue(table.getHeaderControl() != table.getHeader());
	}

}
