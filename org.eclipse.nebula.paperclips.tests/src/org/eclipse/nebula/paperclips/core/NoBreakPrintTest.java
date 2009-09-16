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

import org.eclipse.nebula.paperclips.core.NoBreakPrint;

import junit.framework.TestCase;

public class NoBreakPrintTest extends TestCase {
	public void testConstructor() {
		try {
			new NoBreakPrint(null);
			fail();
		} catch (IllegalArgumentException expected) {
		}
	}

	public void testEquals() {
		NoBreakPrint noBreak = new NoBreakPrint(new PrintStub(0));
		assertEquals(noBreak, new NoBreakPrint(new PrintStub(0)));
		assertFalse(noBreak.equals(new NoBreakPrint(new PrintStub(1))));
	}
}
