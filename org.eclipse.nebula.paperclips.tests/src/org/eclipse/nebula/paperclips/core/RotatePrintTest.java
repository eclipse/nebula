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

import org.eclipse.nebula.paperclips.core.RotatePrint;

import junit.framework.TestCase;

public class RotatePrintTest extends TestCase {
	public void testEquals() {
		RotatePrint rotate1 = new RotatePrint(new PrintStub(0), 90);
		RotatePrint rotate2 = new RotatePrint(new PrintStub(0), 90);
		assertEquals(rotate1, rotate2);

		rotate1 = new RotatePrint(new PrintStub(1), 90);
		assertFalse(rotate1.equals(rotate2));
		rotate2 = new RotatePrint(new PrintStub(1), 90);
		assertEquals(rotate1, rotate2);

		rotate1 = new RotatePrint(new PrintStub(1), 180);
		assertFalse(rotate1.equals(rotate2));
		rotate2 = new RotatePrint(new PrintStub(1), 180);
		assertEquals(rotate1, rotate2);
	}
}
