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

import org.eclipse.nebula.paperclips.core.LayerPrint;
import org.eclipse.swt.SWT;

public class LayerPrintTest extends TestCase {
	public void testEquals() {
		LayerPrint lp1 = new LayerPrint();
		LayerPrint lp2 = new LayerPrint();
		assertEquals(lp1, lp2);

		lp1.add(new PrintStub());
		assertFalse(lp1.equals(lp2));
		lp2.add(new PrintStub());
		assertEquals(lp1, lp2);

		lp1.add(new PrintStub(), SWT.CENTER);
		assertFalse(lp1.equals(lp2));
		lp2.add(new PrintStub(), SWT.CENTER);
		assertEquals(lp1, lp2);
	}
}
