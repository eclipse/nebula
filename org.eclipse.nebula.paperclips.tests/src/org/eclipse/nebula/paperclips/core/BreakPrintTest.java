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

import org.eclipse.nebula.paperclips.core.BreakPrint;

import junit.framework.TestCase;

public class BreakPrintTest extends TestCase {
	public void testEquals_equivalent() {
		assertEquals(new BreakPrint(), new BreakPrint());
	}

	public void testEquals_different() {
		assertFalse(new BreakPrint().equals(new PrintStub()));
	}
}
