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

import junit.framework.TestCase;

import org.eclipse.nebula.paperclips.core.internal.util.SWTUtil;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

public class SWTUtilTest extends TestCase {
	public void testDeriveRGB() {
		assertEquals(new RGB(255, 0, 0), SWTUtil.deriveRGB(0xFF0000));
		assertEquals(new RGB(0, 255, 0), SWTUtil.deriveRGB(0x00FF00));
		assertEquals(new RGB(0, 0, 255), SWTUtil.deriveRGB(0x0000FF));
	}

	public void testEqualImageData_opaque() {
		ImageData imageData = createImageData(100, 0, (byte) 0xFF);
		assertTrue(SWTUtil.equal(imageData,
				createImageData(100, 0, (byte) 0xFF)));
		assertFalse(SWTUtil.equal(imageData, createImageData(101, 0,
				(byte) 0xFF)));
		assertFalse(SWTUtil.equal(imageData, createImageData(100, 1,
				(byte) 0xFF)));
	}

	public void testEqualImageData_transparent() {
		assertTrue(SWTUtil.equal(createImageData(100, 0xFFFFFF, (byte) 0x7F),
				createImageData(100, 0xFFFFFF, (byte) 0x7F)));
		assertFalse(SWTUtil.equal(createImageData(99, 0xFFFFFF, (byte) 0x7F),
				createImageData(99, 0xFFFFFF, (byte) 0xFF)));
		assertTrue(SWTUtil.equal(createImageData(100, 0x000000, (byte) 0x00),
				createImageData(100, 0xFFFFFF, (byte) 0x00)));
	}

	private ImageData createImageData(int size, int color, byte alpha) {
		ImageData imageData = new ImageData(size, size, 24, new PaletteData(
				0xFF0000, 0x00FF00, 0x0000FF));
		int[] pixels = new int[size];
		byte[] alphas = new byte[size];
		for (int x = 0; x < size; x++) {
			pixels[x] = color;
			alphas[x] = alpha;
		}
		for (int y = 0; y < size; y++) {
			imageData.setPixels(0, y, size, pixels, 0);
			imageData.setAlphas(0, y, size, alphas, 0);
		}
		return imageData;
	}
}
