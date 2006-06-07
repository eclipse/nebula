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

import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

/**
 * Instances of this class are sent as a result of widgets being checked or
 * unchecked.
 * 
 * @author chris.gross@us.ibm.com
 */
public class CheckEvent extends TypedEvent
{

    /**
     * The item that was checked.
     */
    public Widget item;

    /**
     * The column where the checkbox resides.
     */
    public int column;

    /**
     * Constructs a new instance of this class based on the information in the
     * given untyped event.
     * 
     * @param e untyped event.
     */
    public CheckEvent(Event e)
    {
        super(e);
        item = e.item;
    }

}
