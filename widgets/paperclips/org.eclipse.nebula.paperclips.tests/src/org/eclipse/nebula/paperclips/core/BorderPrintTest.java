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

import org.eclipse.nebula.paperclips.core.border.BorderPrint;

import junit.framework.TestCase;

public class BorderPrintTest extends TestCase {
	public void testConstructor_nullArguments() {
		try {
			new BorderPrint(null, new BorderStub());
			fail();
		} catch (IllegalArgumentException expected) {
		}

		try {
			new BorderPrint(new PrintStub(), null);
			fail();
		} catch (IllegalArgumentException expected) {
		}
	}

	public void testEquals_equivalent() {
		assertEquals(new BorderPrint(new PrintStub(), new BorderStub()),
				new BorderPrint(new PrintStub(), new BorderStub()));
	}

	public void testEquals_different() {
		BorderPrint borderPrint = new BorderPrint(new PrintStub(0),
				new BorderStub());
		assertFalse(borderPrint.equals(new BorderPrint(new PrintStub(1),
				new BorderStub())));
		assertFalse(borderPrint.equals(new BorderPrint(new PrintStub(0),
				new BorderStub() {
				})));
	}
}
