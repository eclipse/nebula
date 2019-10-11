/*
 * Copyright (c) 2007 Matthew Hall and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.core.grid;

import org.eclipse.nebula.paperclips.core.PrintStub;
import org.eclipse.nebula.paperclips.core.grid.internal.GridCellImpl;
import org.eclipse.swt.SWT;

import junit.framework.TestCase;

public class GridCellTest extends TestCase {
	@SuppressWarnings("restriction")
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
