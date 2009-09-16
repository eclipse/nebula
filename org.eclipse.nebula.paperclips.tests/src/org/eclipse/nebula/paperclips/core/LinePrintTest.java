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
package org.eclipse.nebula.paperclips.core;

import junit.framework.TestCase;

import org.eclipse.nebula.paperclips.core.LinePrint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

public class LinePrintTest extends TestCase {
	public void testConstructor_invalidArgument() {
		assertEquals(SWT.HORIZONTAL, new LinePrint(0).getOrientation());
	}

	public void testEquals() {
		LinePrint line1 = new LinePrint(SWT.HORIZONTAL);
		LinePrint line2 = new LinePrint(SWT.HORIZONTAL);
		assertEquals(line1, line2);

		line1 = new LinePrint(SWT.VERTICAL);
		assertFalse(line1.equals(line2));
		line2 = new LinePrint(SWT.VERTICAL);
		assertEquals(line1, line2);

		line1.setRGB(new RGB(127, 127, 127));
		assertFalse(line1.equals(line2));
		line2.setRGB(new RGB(127, 127, 127));
		assertEquals(line1, line2);

		line1.setThickness(2.3);
		assertFalse(line1.equals(line2));
		line2.setThickness(2.3);
		assertEquals(line1, line2);
	}
}
