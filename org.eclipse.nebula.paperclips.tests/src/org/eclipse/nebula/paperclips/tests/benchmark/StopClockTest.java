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
package org.eclipse.nebula.paperclips.tests.benchmark;

import junit.framework.TestCase;

public class StopClockTest extends TestCase {
	public void testConstructor() {
		assertEquals(0, new StopClock().getTime());
		assertEquals(0, new StopClock(0).getTime());
		assertEquals(10, new StopClock(10).getTime());
	}

	public void testGetTime() {
		StopClock clock = new StopClock();
		clock.time = 0;
		assertEquals(0, clock.getTime());
		clock.time = 50;
		assertEquals(50, clock.getTime());
	}
}
