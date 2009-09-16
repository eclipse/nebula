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

import org.eclipse.nebula.paperclips.core.border.LineBorder;
import org.eclipse.swt.graphics.RGB;

public class LineBorderTest extends TestCase {
	public void testEquals() {
		LineBorder border1 = new LineBorder();
		LineBorder border2 = new LineBorder();
		assertEquals(border1, border2);

		border1.setGapSize(10);
		assertFalse(border1.equals(border2));
		border2.setGapSize(10);
		assertEquals(border1, border2);

		border1.setLineWidth(10);
		assertFalse(border1.equals(border2));
		border2.setLineWidth(10);
		assertEquals(border1, border2);

		border1.setRGB(new RGB(127, 127, 127));
		assertFalse(border1.equals(border2));
		border2.setRGB(new RGB(127, 127, 127));
		assertEquals(border1, border2);
	}
}
