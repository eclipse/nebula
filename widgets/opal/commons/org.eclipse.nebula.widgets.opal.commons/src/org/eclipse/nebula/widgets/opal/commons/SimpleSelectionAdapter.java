/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.widgets.opal.commons;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

/**
 * This class is an adapter for the SelectionListener. Both behaviours (DefaultSelected and Selected) are doing the same thing
 */
public abstract class SimpleSelectionAdapter implements SelectionListener {

    /**
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     */
    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        this.handle(e);

    }

    /**
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    @Override
    public void widgetSelected(final SelectionEvent e) {
        this.handle(e);

    }

    /**
     * Sent when selection occurs in the control.
     * 
     * @param e - an event containing information about the selection
     */
    public abstract void handle(SelectionEvent e);

}
