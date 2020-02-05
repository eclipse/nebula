/*******************************************************************************
 * Copyright (c) 2020 Christian Pontesegger and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Christian Pontesegger - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.timeline;

import static org.junit.Assert.*;

import org.junit.Test;

import junit.framework.TestCase;

public class TimingTest {

	@Test
	public void createTiming() {
		Timing timing = new Timing(123, 5);

		assertEquals(123, timing.getTimestamp(), 0.1);
		assertEquals(5, timing.getDuration(), 0.1);
	}

	@Test
	public void createTimestamp() {
		Timing timing = new Timing(123);

		assertEquals(123, timing.getTimestamp(), 0.1);
		assertEquals(0, timing.getDuration(), 0.1);
	}

	@Test
	public void left() {
		Timing timing = new Timing(123, 5);

		assertEquals(123, timing.left(), 0.1);
	}

	@Test
	public void right() {
		Timing timing = new Timing(123, 5);

		assertEquals(timing.getTimestamp() + timing.getDuration(), timing.right(), 0.1);
	}

	@Test
	public void unionContained() {
		Timing timing = new Timing(123, 5);
		timing.union(new Timing(124, 2));

		assertEquals(123, timing.getTimestamp(), 0.1);
		assertEquals(5, timing.getDuration(), 0.1);
	}

	@Test
	public void unionNotContained() {
		Timing timing = new Timing(123, 5);
		timing.union(new Timing(1, 55));
		
		assertEquals(1, timing.getTimestamp(), 0.1);
		assertEquals(123+5-1, timing.getDuration(), 0.1);
	}
	
	@Test
	public void scale() {
		Timing timing = new Timing(100, 5);
		timing.scale(2);
		
		assertEquals(200, timing.getTimestamp(), 0.1);
		assertEquals(10, timing.getDuration(), 0.1);
	}

	@Test
	public void translate() {
		Timing timing = new Timing(100, 5);
		timing.translate(10);
		
		assertEquals(110, timing.getTimestamp(), 0.1);
		assertEquals(5, timing.getDuration(), 0.1);
	}
	
	@Test
	public void isEmpty() {
		assertTrue(new Timing(100, 0).isEmpty());
		assertFalse(new Timing(100, 1).isEmpty());
	}
}
