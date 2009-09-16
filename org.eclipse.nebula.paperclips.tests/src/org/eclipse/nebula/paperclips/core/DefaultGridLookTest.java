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

import org.eclipse.nebula.paperclips.core.grid.DefaultGridLook;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

public class DefaultGridLookTest extends TestCase {
	public void testEquals() {
		final DefaultGridLook look1 = new DefaultGridLook();
		final DefaultGridLook look2 = new DefaultGridLook();
		assertEquals(look1, look2);

		look1.setBodyBackground(new RGB(0, 0, 0));
		assertFalse(look1.equals(look2));
		look2.setBodyBackground(new RGB(0, 0, 0));
		assertEquals(look1, look2);

		look1.setBodyBackgroundProvider(new CellBackgroundProviderStub());
		assertFalse(look1.equals(look2));
		look2.setBodyBackgroundProvider(new CellBackgroundProviderStub());
		assertEquals(look1, look2);

		look1.setCellBorder(new BorderStub());
		assertFalse(look1.equals(look2));
		look2.setCellBorder(new BorderStub());
		assertEquals(look1, look2);

		look1.setCellPadding(new Rectangle(1, 2, 3, 4));
		assertFalse(look1.equals(look2));
		look2.setCellPadding(1, 2, 3, 4);
		assertEquals(look1, look2);

		look1.setCellSpacing(new Point(1, 2));
		assertFalse(look1.equals(look2));
		look2.setCellSpacing(new Point(1, 2));
		assertEquals(look1, look2);

		look1.setFooterBackground(new RGB(0, 0, 0));
		assertFalse(look1.equals(look2));
		look2.setFooterBackground(new RGB(0, 0, 0));
		assertEquals(look1, look2);

		look1.setFooterBackgroundProvider(new CellBackgroundProviderStub());
		assertFalse(look1.equals(look2));
		look2.setFooterBackgroundProvider(new CellBackgroundProviderStub());
		assertEquals(look1, look2);

		look1.setFooterGap(1);
		assertFalse(look1.equals(look2));
		look2.setFooterGap(1);
		assertEquals(look1, look2);

		look1.setHeaderBackground(new RGB(0, 0, 0));
		assertFalse(look1.equals(look2));
		look2.setHeaderBackground(new RGB(0, 0, 0));
		assertEquals(look1, look2);

		look1.setHeaderBackgroundProvider(new CellBackgroundProviderStub());
		assertFalse(look1.equals(look2));
		look2.setHeaderBackgroundProvider(new CellBackgroundProviderStub());
		assertEquals(look1, look2);

		look1.setHeaderGap(1);
		assertFalse(look1.equals(look2));
		look2.setHeaderGap(1);
		assertEquals(look1, look2);
	}
}
