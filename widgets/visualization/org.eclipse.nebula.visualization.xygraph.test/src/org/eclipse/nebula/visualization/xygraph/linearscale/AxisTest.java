/*******************************************************************************
 * Copyright (c) 2021 Diamond Light Source Ltd.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.nebula.visualization.xygraph.linearscale;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AxisTest {

	@Test
	public void testDefaultDecimalFormat() {
		assertEquals("############.##", AbstractScale.createDefaultDecimalFormat(-30, 30));
		assertEquals("##.#####", AbstractScale.createDefaultDecimalFormat(-0.001, 0.001));
	}

}
