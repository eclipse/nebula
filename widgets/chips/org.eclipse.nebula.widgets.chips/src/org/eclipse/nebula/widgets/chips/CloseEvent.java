/*******************************************************************************
 * Copyright (c) 2020 Laurent CARON.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API and
 * implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.chips;

import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.Event;

/**
 * Instances of this class are sent as a result of
 * widgets being closed.
 * <p>
 * Note: The fields that are filled in depend on the widget.
 * </p>
 *
 * @see CloseListener
 */
public class CloseEvent extends TypedEvent {

	private static final long serialVersionUID = 5028668219476966408L;

	/**
	 * A flag indicating whether the operation should be allowed.
	 * Setting this field to <code>false</code> will cancel the
	 * operation, depending on the widget.
	 */
	public boolean doit;

	/**
	 * Constructs a new instance of this class based on the
	 * information in the given untyped event.
	 *
	 * @param e the untyped event containing the information
	 */
	public CloseEvent(final Event e) {
		super(e);
	}
}
