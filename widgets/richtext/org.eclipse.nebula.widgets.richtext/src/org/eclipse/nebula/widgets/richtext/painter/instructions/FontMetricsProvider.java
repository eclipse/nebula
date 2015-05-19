/*****************************************************************************
 * Copyright (c) 2015 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *		Dirk Fauth <dirk.fauth@googlemail.com> - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.nebula.widgets.richtext.painter.instructions;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;

/**
 * Implementations of this interface typically perform modifications to a {@link Font} and therefore
 * need to be able to supply the {@link FontMetrics} of the font after the modifications are done.
 */
public interface FontMetricsProvider {

	/**
	 * Calculates the {@link FontMetrics} based on the font information of this
	 * {@link FontMetricsProvider} in conjunction with the current set font information set to the
	 * given {@link GC}.
	 * <p>
	 * <b>Note:</b> To retrieve the {@link FontMetrics} it is necessary to set the {@link Font}
	 * based on the local font information and the current applied font. You should not reset the
	 * {@link Font} on the {@link GC} in this method again, because the font information might be
	 * used by other {@link FontMetricsProvider}.
	 * </p>
	 * 
	 * @param gc
	 *            The {@link GC} that should be used to retrieve the {@link FontMetrics}
	 * @return The {@link FontMetrics} based on the font information of this
	 *         {@link FontMetricsProvider} in conjunction with the current set font information set
	 *         to the given {@link GC}.
	 */
	FontMetrics getFontMetrics(GC gc);
}
