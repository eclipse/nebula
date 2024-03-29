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

import org.eclipse.swt.widgets.Control;

/**
 * Interface IRowContentProvider.  An interface for objects that are able to initialize an
 * arbitrary row control with values on demand.
 * @author djo
 */
@FunctionalInterface
public interface IRowContentProvider {

	/**
	 * Method refresh.  Requests receiver to refresh the currentRowInTable with data
	 * to edit.
	 * 
	 * @param sender The CompositeTable sending the message.
	 * @param currentObjectOffset The 0-based row number that is offset in the data structure of 
	 * the table's top row.
	 * @param row The row control to fill with data.  This will be a copy of your prototype 
	 * row object.
	 */
	void refresh(CompositeTable sender, int currentObjectOffset, Control row);

}
