/*******************************************************************************
 * Copyright (c) 2021 Laurent CARON
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Laurent CARON (laurent.caron at gmail dot com) - Initial implementation and API
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.commons;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TypedListener;

public class SelectionListenerUtil {
	/**
	 * Add a <code>SelectionListener</code> to a given Control
	 * 
	 * @param control control on which the selection listener is added
	 * @param listener listener to add
	 */
	public static void addSelectionListener(final Control control, final SelectionListener listener) {
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		TypedListener typedListener = new TypedListener(listener);
		control.addListener(SWT.Selection, typedListener);
	}

	/**
	 * Remove a <code>SelectionListener</code> of a given Control
	 * 
	 * @param control control on which the selection listener is removed
	 * @param listener listener to remove
	 */
	public static void removeSelectionListener(final Control control, final SelectionListener listener) {
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		final Listener[] listeners = control.getListeners(SWT.Selection);
		for (Listener l : listeners) {
			if (l instanceof TypedListener) {
				TypedListener typedListener = (TypedListener) l;
				if (typedListener.getEventListener() == listener) {
					ReflectionUtils.callMethod(control, "removeListener", SWT.Selection, ((TypedListener) l).getEventListener());
					return;
				}
			}
		}
	}

	/**
	 * Fire the selection listeners of a given control
	 *
	 * @param control the control that fires the event
	 * @param sourceEvent mouse event
	 * @return true if the selection could be changed, false otherwise
	 */
	public static boolean fireSelectionListeners(final Control control, final Event sourceEvent) {
		for (final Listener listener : control.getListeners(SWT.Selection)) {
			final Event event = new Event();

			event.button = sourceEvent==null?1:sourceEvent.button;
			event.display = control.getDisplay();
			event.item = null;
			event.widget = control;
			event.data = sourceEvent == null ? null : sourceEvent.data;
			event.time = sourceEvent == null ? 0 : sourceEvent.time;
			event.x = sourceEvent == null ? 0 : sourceEvent.x;
			event.y = sourceEvent == null ? 0 : sourceEvent.y;
			event.type = SWT.Selection;

			listener.handleEvent(event);
			if (!event.doit) {
				return false;
			}
		}
		return true;
	}
}
