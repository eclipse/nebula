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

import org.eclipse.nebula.paperclips.core.BigPrint;
import org.eclipse.nebula.paperclips.core.Print;

import junit.framework.TestCase;

public class BigPrintTest extends TestCase {
	public void testConstructor_nullArgument() {
		try {
			new BigPrint(null);
			fail();
		} catch (IllegalArgumentException expected) {
		}
	}

	public void testEquals() {
		Print print = new BigPrint(new PrintStub(0));
		assertEquals(print, new BigPrint(new PrintStub(0)));
		assertFalse(print.equals(new BigPrint(new PrintStub(1))));
	}
}
