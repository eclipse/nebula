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

import org.eclipse.nebula.paperclips.core.border.GapBorder;

import junit.framework.TestCase;

public class GapBorderTest extends TestCase {
	public void testEquals() {
		GapBorder border1 = new GapBorder();
		GapBorder border2 = new GapBorder();
		assertEquals(border1, border2);

		border1.top = 2;
		assertFalse(border1.equals(border2));
		border2.top = 2;
		assertEquals(border1, border2);

		border1.left = 2;
		assertFalse(border1.equals(border2));
		border2.left = 2;
		assertEquals(border1, border2);

		border1.right = 2;
		assertFalse(border1.equals(border2));
		border2.right = 2;
		assertEquals(border1, border2);

		border1.bottom = 2;
		assertFalse(border1.equals(border2));
		border2.bottom = 2;
		assertEquals(border1, border2);

		border1.openTop = 2;
		assertFalse(border1.equals(border2));
		border2.openTop = 2;
		assertEquals(border1, border2);

		border1.openBottom = 2;
		assertFalse(border1.equals(border2));
		border2.openBottom = 2;
		assertEquals(border1, border2);

		border1.setGap(1);
		assertFalse(border1.equals(border2));
		border2.setGap(1);
		assertEquals(border1, border2);
	}
}
