/*******************************************************************************
 * Copyright (c) 2020 Christian Pontesegger and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Christian Pontesegger - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.timeline.listeners;

import org.eclipse.nebula.widgets.timeline.ICursor;
import org.eclipse.nebula.widgets.timeline.figures.detail.cursor.CursorFigure;

/**
 * Listener to be notified when cursors are created/deleted in the UI via mouse actions.
 */
public interface ICursorListener {

	/**
	 * A cursor got created.
	 *
	 * @param cursor
	 *            created cursor
	 * @param figure
	 *            figure backing the cursor in detail view
	 */
	void notifyCursorCreated(ICursor cursor, CursorFigure figure);

	/**
	 * A cursor got deleted.
	 *
	 * @param cursor
	 *            deleted cursor
	 */
	void notifyCursorDeleted(ICursor cursor);
}
