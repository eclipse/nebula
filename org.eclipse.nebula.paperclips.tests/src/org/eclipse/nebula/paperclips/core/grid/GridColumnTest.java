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

import org.eclipse.nebula.paperclips.core.grid.GridColumn;
import org.eclipse.nebula.paperclips.core.grid.GridPrint;
import org.eclipse.swt.SWT;

public class GridColumnTest extends TestCase {
	public void testEquals_equivalent() {
		GridColumn c1 = GridColumn.parse("l:p:g");
		GridColumn c2 = new GridColumn(SWT.LEFT, GridPrint.PREFERRED, 1);
		assertEquals(c1, c2);
	}

	public void testEquals_different() {
		GridColumn gc = new GridColumn(SWT.LEFT, SWT.DEFAULT, 0);
		assertFalse(gc.equals(new GridColumn(SWT.CENTER, SWT.DEFAULT, 0)));
		assertFalse(gc.equals(new GridColumn(SWT.LEFT, GridPrint.PREFERRED, 0)));
		assertFalse(gc.equals(new GridColumn(SWT.LEFT, SWT.DEFAULT, 1)));
	}

	public void testInchConversion() {
		assertEquals(72, GridColumn.parse("1 inch").size);
		assertEquals(90, GridColumn.parse("1.25in").size);
		assertEquals(108, GridColumn.parse("1.5INCH").size);
		assertEquals(126, GridColumn.parse("1.75 IN").size);
	}

	public void testCentimeterConversion() {
		assertEquals(72, GridColumn.parse("2.54cm").size);
		assertEquals(284, GridColumn.parse("10cm").size); // ceil(283.464)
	}

	public void testMillimeterConversion() {
		assertEquals(72, GridColumn.parse("25.4mm").size);
		assertEquals(284, GridColumn.parse("100mm").size); // ceil(2834.64)
	}
}
