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

import org.eclipse.nebula.paperclips.core.Margins;

import junit.framework.TestCase;

public class MarginsTest extends TestCase {
	public void testEquals() {
		Margins m1 = new Margins(0);
		Margins m2 = new Margins(0);
		assertEquals(m1, m2);

		m1.top = 1;
		assertFalse(m1.equals(m2));
		m2.top = 1;
		assertEquals(m1, m2);

		m1.left = 1;
		assertFalse(m1.equals(m2));
		m2.left = 1;
		assertEquals(m1, m2);

		m1.right = 1;
		assertFalse(m1.equals(m2));
		m2.right = 1;
		assertEquals(m1, m2);

		m1.bottom = 1;
		assertFalse(m1.equals(m2));
		m2.bottom = 1;
		assertEquals(m1, m2);
	}
}
