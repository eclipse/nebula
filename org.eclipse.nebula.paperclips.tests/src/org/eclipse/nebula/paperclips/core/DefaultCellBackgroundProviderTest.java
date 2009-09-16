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

import junit.framework.TestCase;

import org.eclipse.nebula.paperclips.core.grid.DefaultCellBackgroundProvider;
import org.eclipse.swt.graphics.RGB;

public class DefaultCellBackgroundProviderTest extends TestCase {
	public void testEquals_equivalent() {
		DefaultCellBackgroundProvider provider1 = new DefaultCellBackgroundProvider();
		DefaultCellBackgroundProvider provider2 = new DefaultCellBackgroundProvider();
		assertEquals(provider1, provider2);

		provider1 = new DefaultCellBackgroundProvider(
				new CellBackgroundProviderStub());
		assertFalse(provider1.equals(provider2));
		provider2 = new DefaultCellBackgroundProvider(
				new CellBackgroundProviderStub());
		assertEquals(provider1, provider2);

		provider1.setBackground(new RGB(0, 0, 0));
		assertFalse(provider1.equals(provider2));
		provider2.setBackground(new RGB(0, 0, 0));
		assertEquals(provider1, provider2);
	}
}
