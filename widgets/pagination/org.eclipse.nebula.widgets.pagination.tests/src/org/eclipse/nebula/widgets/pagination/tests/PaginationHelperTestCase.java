/*******************************************************************************
 * Copyright (C) 2011 Angelo Zerr <angelo.zerr@gmail.com>, Pascal Leclercq <pascal.leclercq@gmail.com>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo ZERR - initial API and implementation
 *     Pascal Leclercq - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.pagination.tests;

import org.eclipse.nebula.widgets.pagination.PaginationHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * {@link PaginationHelper#getPageIndexes(int, int, int)} test cases.
 * 
 */
public class PaginationHelperTestCase {

	@Test
	public void testname() throws Exception {
		int[] indexes = PaginationHelper.getPageIndexes(0, 403, 10);
		// System.err.println(display(indexes));

		int[] expected = { 0, 1, 2, 3, 4, 5, 6, 7, -1, 402 };
		Assert.assertEquals(display(expected), display(indexes));
	}

	@Test
	public void test2name() throws Exception {
		int[] indexes = PaginationHelper.getPageIndexes(10, 403, 10);
		// System.err.println(display(indexes));

		int[] expected = { 0, -1, 7, 8, 9, 10, 11, 12, -1, 402 };
		Assert.assertEquals(display(expected), display(indexes));
	}

	@Test
	public void test22name() throws Exception {
		int[] indexes = PaginationHelper.getPageIndexes(402, 403, 10);
		// System.err.println(display(indexes));

		int[] expected = { 0, -1, 395, 396, 397, 398, 399, 400, 401, 402 };
		Assert.assertEquals(display(expected), display(indexes));
	}

	@Test
	public void test23name() throws Exception {
		int[] indexes = PaginationHelper.getPageIndexes(396, 403, 10);
		// System.err.println(display(indexes));

		int[] expected = { 0, -1, 393, 394, 395, 396, 397, 398, -1, 402 };
		Assert.assertEquals(display(expected), display(indexes));
	}

	@Test
	public void test24name() throws Exception {
		int[] indexes = PaginationHelper.getPageIndexes(395, 403, 10);
		// System.err.println(display(indexes));

		int[] expected = { 0, -1, 392, 393, 394, 395, 396, 397, -1, 402 };
		Assert.assertEquals(display(expected), display(indexes));
	}

	@Test
	public void test3name() throws Exception {
		int[] indexes = PaginationHelper.getPageIndexes(0, 4, 10);
		// System.err.println(display(indexes));

		int[] expected = { 0, 1, 2, 3 };
		Assert.assertEquals(display(expected), display(indexes));
	}

	@Test
	public void test4name() throws Exception {
		int[] indexes = PaginationHelper.getPageIndexes(1, 4, 10);
		// System.err.println(display(indexes));

		int[] expected = { 0, 1, 2, 3 };
		Assert.assertEquals(display(expected), display(indexes));
	}

	@Test
	public void test5name() throws Exception {
		int[] indexes = PaginationHelper.getPageIndexes(2, 4, 10);
		// System.err.println(display(indexes));

		int[] expected = { 0, 1, 2, 3 };
		Assert.assertEquals(display(expected), display(indexes));
	}

	@Test
	public void test6name() throws Exception {
		int[] indexes = PaginationHelper.getPageIndexes(3, 4, 10);
		// System.err.println(display(indexes));

		int[] expected = { 0, 1, 2, 3 };
		Assert.assertEquals(display(expected), display(indexes));
	}

	@Test
	public void test7name() throws Exception {
		int[] indexes = PaginationHelper.getPageIndexes(7, 20, 10);
		// System.err.println(display(indexes));

		int[] expected = { 0, 1, 2, 3, 4, 5, 6, 7, -1, 19 };
		Assert.assertEquals(display(expected), display(indexes));
	}

	@Test
	public void test8name() throws Exception {
		int[] indexes = PaginationHelper.getPageIndexes(8, 20, 10);
		// System.err.println(display(indexes));

		int[] expected = { 0, -1, 5, 6, 7, 8, 9, 10, -1, 19 };
		Assert.assertEquals(display(expected), display(indexes));
	}

	private static String display(int[] indexes) {
		StringBuilder result = new StringBuilder();
		result.append("[");
		for (int i = 0; i < indexes.length; i++) {
			if (i > 0) {
				result.append(",");
			}
			result.append(indexes[i]);
		}
		result.append("]");
		return result.toString();
	}
}
