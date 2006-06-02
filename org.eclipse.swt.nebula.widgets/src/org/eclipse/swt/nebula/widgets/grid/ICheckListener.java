/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    chris.gross@us.ibm.com - initial API and implementation
 *******************************************************************************/ 
package org.eclipse.swt.nebula.widgets.grid;

/**
 * Classes which implement this interface provide methods that deal with the
 * events that are generated when a widget is checked or unchecked. After
 * creating an instance of a class that implements this interface it can be
 * added to a control using the {@code addCheckListener} method and removed
 * using the {@code removeSelectionListener} method. When selection occurs in a
 * control the appropriate method will be invoked.
 * 
 * @see CheckEvent
 * @author chris.gross@us.ibm.com
 * @since 2.0.0
 */
public interface ICheckListener
{

    /**
     * Sent when the widget is checked or unchecked.
     * 
     * @param event an event containing information about the check/uncheck.
     */
    void checkChanged(CheckEvent event);
}
