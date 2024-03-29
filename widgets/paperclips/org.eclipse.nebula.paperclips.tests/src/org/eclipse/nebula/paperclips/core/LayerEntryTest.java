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
package org.eclipse.nebula.paperclips.core;

import junit.framework.TestCase;

import org.eclipse.nebula.paperclips.core.internal.LayerEntryImpl;
import org.eclipse.nebula.paperclips.core.internal.LayerEntryImpl;
import org.eclipse.swt.SWT;

public class LayerEntryTest extends TestCase {
	public void testConstructor_invalidArguments() {
		try {
			new LayerEntryImpl(null, SWT.LEFT);
			fail();
		} catch (IllegalArgumentException expected) {
		}

		assertEquals(SWT.LEFT,
				new LayerEntryImpl(new PrintStub(), 0).getHorizontalAlignment());
	}

	public void testEquals() {
		LayerEntry entry = new LayerEntryImpl(new PrintStub(0), SWT.LEFT);
		assertEquals(entry, new LayerEntryImpl(new PrintStub(0), SWT.LEFT));
		assertFalse(entry
				.equals(new LayerEntryImpl(new PrintStub(1), SWT.LEFT)));
		assertFalse(entry.equals(new LayerEntryImpl(new PrintStub(0),
				SWT.CENTER)));
	}
}
