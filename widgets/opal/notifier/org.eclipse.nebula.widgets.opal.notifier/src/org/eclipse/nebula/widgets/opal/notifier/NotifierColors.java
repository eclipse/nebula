/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API
 * and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.notifier;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.swt.graphics.Color;

/**
 * This class is a simple POJO that holds colors used by the Notifier widget
 *
 */
class NotifierColors {
	Color titleColor;
	Color textColor;
	Color borderColor;
	Color leftColor;
	Color rightColor;

	void dispose() {
		SWTGraphicUtil.safeDispose(titleColor);
		SWTGraphicUtil.safeDispose(borderColor);
		SWTGraphicUtil.safeDispose(leftColor);
		SWTGraphicUtil.safeDispose(rightColor);
		SWTGraphicUtil.safeDispose(textColor);
	}
}
