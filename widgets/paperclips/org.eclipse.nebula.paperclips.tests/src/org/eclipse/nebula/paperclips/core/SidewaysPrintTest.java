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

import org.eclipse.nebula.paperclips.core.SidewaysPrint;

import junit.framework.TestCase;

public class SidewaysPrintTest extends TestCase {
	public void testEquals() {
		SidewaysPrint sideways1 = new SidewaysPrint(new PrintStub(0), 90);
		SidewaysPrint sideways2 = new SidewaysPrint(new PrintStub(0), 90);
		assertEquals(sideways1, sideways2);

		sideways1 = new SidewaysPrint(new PrintStub(1), 90);
		assertFalse(sideways1.equals(sideways2));
		sideways2 = new SidewaysPrint(new PrintStub(1), 90);
		assertEquals(sideways1, sideways2);

		sideways1 = new SidewaysPrint(new PrintStub(1), 180);
		assertFalse(sideways1.equals(sideways2));
		sideways2 = new SidewaysPrint(new PrintStub(1), 180);
		assertEquals(sideways1, sideways2);
	}
}
