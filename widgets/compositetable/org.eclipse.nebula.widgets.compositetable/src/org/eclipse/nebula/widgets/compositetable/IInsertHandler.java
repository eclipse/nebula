/*
 * Copyright (C) 2005 David Orme <djo@coconut-palm-software.com>
 * 
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Orme     - Initial API and implementation
 */
package org.eclipse.nebula.widgets.compositetable;

/**
 * Interface IInsertHandler.  An interface for objects that can handle requests to insert a new
 * object into a collection being edited by a CompositeTable.
 * 
 * @author djo
 */
public interface IInsertHandler {
	/**
	 * Method insert.  Requests that the receiver insert object(s) making up a new row at 
	 * the specified position.  The receiver returns the actual position where the insert
	 * occured (that doesn't have to be the same as the requested position).
	 * 
	 * @param positionHint The user's current position in the user interface relative to the 
	 * beginning of the collection (0-based).
	 * 
	 * @return the actual position of the new object or -1 if a new object could
	 * not be inserted.
	 */
	public int insert(int positionHint);
}
