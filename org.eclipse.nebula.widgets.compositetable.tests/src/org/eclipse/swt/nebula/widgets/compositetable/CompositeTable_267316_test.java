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
package org.eclipse.swt.nebula.widgets.compositetable;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CompositeTable_267316_test extends TestCase {

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

    @Override
    protected void setUp() throws Exception {
	display = Display.getDefault();
	shell = new Shell(display);
	shell.setLayout(new FillLayout());
    }

    @Override
    protected void tearDown() throws Exception {
	shell.dispose();
	display.dispose();
    }

    // test methods
    // /////////////

    public void testGetHeaderControl() {
	CompositeTable table = new CompositeTable(shell, SWT.NONE);

	assertNull(table.getHeader());

	new Header(table, SWT.NONE);
	new Row(table, SWT.NONE);

	assertNull(table.getHeader());

	table.setRunTime(true);

	assertNotNull(table.getHeader());
	assertTrue(table.getHeader() instanceof Header);
	assertTrue(table.getHeaderControl() != table.getHeader());
    }

}
