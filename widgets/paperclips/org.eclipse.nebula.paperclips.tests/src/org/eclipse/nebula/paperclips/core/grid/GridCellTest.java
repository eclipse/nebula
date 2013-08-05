/*
 * Copyright (c) 2007 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.core.grid;

import junit.framework.TestCase;

import org.eclipse.nebula.paperclips.core.PrintStub;
import org.eclipse.nebula.paperclips.core.grid.internal.GridCellImpl;
import org.eclipse.swt.SWT;

public class GridCellTest extends TestCase {
	public void testEquals() {
		GridCell cell = new GridCellImpl(SWT.DEFAULT, SWT.DEFAULT,
				new PrintStub(0), 1);
		assertTrue(cell.equals(new GridCellImpl(SWT.DEFAULT, SWT.DEFAULT,
				new PrintStub(0), 1)));
		assertFalse(cell.equals(new GridCellImpl(SWT.CENTER, SWT.DEFAULT,
				new PrintStub(0), 1)));
		assertFalse(cell.equals(new GridCellImpl(SWT.DEFAULT, SWT.CENTER,
				new PrintStub(0), 1)));
		assertFalse(cell.equals(new GridCellImpl(SWT.DEFAULT, SWT.DEFAULT,
				new PrintStub(1), 1)));
		assertFalse(cell.equals(new GridCellImpl(SWT.DEFAULT, SWT.DEFAULT,
				new PrintStub(0), 2)));
	}
}
