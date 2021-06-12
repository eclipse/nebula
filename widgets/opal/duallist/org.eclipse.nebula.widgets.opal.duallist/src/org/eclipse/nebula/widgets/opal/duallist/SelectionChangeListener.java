/*******************************************************************************
 * Copyright (c) 2014-2021 Laurent CARON
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
package org.eclipse.nebula.widgets.opal.duallist;

import org.eclipse.swt.internal.SWTEventListener;

/**
 * Classes which implement this interface provide methods that deal with the
 * events that are generated when selection occurs in a control.
 * <p>
 * After creating an instance of a class that implements this interface it can
 * be added to a control using the <code>addSelectionChangeListener</code>
 * method and removed using the <code>removeSelectionChangeListener</code>
 * method. When selection occurs in a control the appropriate method will be
 * invoked.
 * </p>
 *
 * @see SelectionChangeEvent
 */
@SuppressWarnings("restriction")
@FunctionalInterface
public interface SelectionChangeListener extends SWTEventListener {

	/**
	 * Sent when selection occurs in the control.
	 *
	 * @param e an event containing information about the selection
	 */
	public void widgetSelected(SelectionChangeEvent e);

}
