/*
 * Copyright (c) 2007 Matthew Hall and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.tests.benchmark;

import junit.framework.TestCase;

public class SystemClockTest extends TestCase {
	public void testGetTime() {
		final Clock clock = new SystemClock();

		long before = System.currentTimeMillis();
		long clockTime = clock.getTime();
		long after = System.currentTimeMillis();
		assertTrue(before <= clockTime && clockTime <= after);
	}
}
