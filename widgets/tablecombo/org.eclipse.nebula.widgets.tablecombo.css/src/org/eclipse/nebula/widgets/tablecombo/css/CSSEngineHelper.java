/*******************************************************************************
 * Copyright (c) 2020 Laurent Caron.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent Caron <laurent dot caron at gmail dot com> - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.tablecombo.css;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Control;

public class CSSEngineHelper {
	public static FontData getFontData(final Control control) {
		final Font font = control.getFont();
		if (font == null || font.isDisposed()) {
			return null;
		}
		final FontData[] fontDatas = !font.isDisposed() ? font.getFontData() : null;
		if (fontDatas == null || fontDatas.length < 1) {
			return null;
		}
		return fontDatas[0];
	}
}
