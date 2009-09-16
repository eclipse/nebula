/*
 * Copyright (c) 2007-2008 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 */
package org.eclipse.nebula.paperclips.core.internal;

import org.eclipse.nebula.paperclips.core.internal.util.Util;

import junit.framework.TestCase;

public class UtilTest extends TestCase {
	public void testSameClass_same() {
		Object o1 = new Object();
		Object o2 = new Object();
		assertTrue(Util.sameClass(o1, o2));
	}

	public void testSameClass_different() {
		Object o1 = new Object();
		Object o2 = new Object() {
		}; // subclass
		assertFalse(Util.sameClass(o1, o2));

		assertFalse(Util.sameClass(null, o2));
		assertFalse(Util.sameClass(o1, null));
	}

	public void testEqual_equivalent() {
		Object o1 = new Stub();
		Object o2 = new Stub();
		assertTrue(Util.equal(o1, o2));
	}

	public void testEqual_different() {
		Object o1 = new Object();
		Object o2 = new Object();
		assertFalse(Util.equal(o1, o2));
		assertFalse(Util.equal(null, o2));
		assertFalse(Util.equal(o1, null));
	}

	public void testEqual_equivalentArray() {
		assertTrue(Util.equal(new byte[] { 0, 1 }, new byte[] { 0, 1 }));
		assertTrue(Util.equal(new short[] { 0, 1 }, new short[] { 0, 1 }));
		assertTrue(Util.equal(new int[] { 0, 1 }, new int[] { 0, 1 }));
		assertTrue(Util.equal(new long[] { 0, 1 }, new long[] { 0, 1 }));
		assertTrue(Util.equal(new char[] { 0, 1 }, new char[] { 0, 1 }));
		assertTrue(Util.equal(new float[] { 0, 1 }, new float[] { 0, 1 }));
		assertTrue(Util.equal(new double[] { 0, 1 }, new double[] { 0, 1 }));
		assertTrue(Util.equal(new boolean[] { false, true }, new boolean[] {
				false, true }));
		assertTrue(Util.equal(new Object[] { new Stub(), new Stub() },
				new Object[] { new Stub(), new Stub() }));
	}

	public void testEqual_differentArray() {
		assertFalse(Util.equal(new byte[] { 0, 1 }, new byte[] { 0, 2 }));
		assertFalse(Util.equal(new short[] { 0, 1 }, new short[] { 0, 2 }));
		assertFalse(Util.equal(new int[] { 0, 1 }, new int[] { 0, 2 }));
		assertFalse(Util.equal(new long[] { 0, 1 }, new long[] { 0, 2 }));
		assertFalse(Util.equal(new char[] { 0, 1 }, new char[] { 0, 2 }));
		assertFalse(Util.equal(new float[] { 0, 1 }, new float[] { 0, 2 }));
		assertFalse(Util.equal(new double[] { 0, 1 }, new double[] { 0, 2 }));
		assertFalse(Util.equal(new boolean[] { false, true }, new boolean[] {
				false, false }));
		assertFalse(Util.equal(new Object[] { new Stub(), new Stub() },
				new Object[] { new Stub(), new Object() }));
	}

	public void testEqual_equivalentNestedArray() {
		assertTrue(Util.equal(new Object[] { new Object[] { new Stub() } },
				new Object[] { new Object[] { new Stub() } }));
		assertTrue(Util.equal(new int[][] { { 0, 1 } },
				new int[][] { { 0, 1 } }));
	}

	public void testEqual_differentNestedArray() {
		assertFalse(Util.equal(new Object[] { new Object[] { new Stub() } },
				new Object[] { new Object[] { new Object() } }));
		assertFalse(Util.equal(new int[][] { { 0, 1 } },
				new int[][] { { 0, 2 } }));
	}

	public void testEqual_double() {
		assertTrue(Util.equal(2.0, 2.0));
		assertFalse(Util.equal(2.0, 1.0));
	}

	public class Stub {
		public boolean equals(Object obj) {
			return Util.sameClass(this, obj);
		}
	}
}
