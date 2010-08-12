/*******************************************************************************
 * Copyright (c) 2010 Ahmed Mahran and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html 
 *
 * Contributors:
 *     Ahmed Mahran - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.effects.stw;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Ahmed Mahran (ahmahran@gmail.com)
 */
public interface Transitionable{

    public void addSelectionListener(SelectionListener listener);

    /**
     * returns the control at index <i>index</i>
     * 
     * @param index the index of the control to return
     * @return the control at the specified index
     */
    public Control getControl(int index);
    
    /**
     * returns the composite at which the transition should
     * be shown. It could be considered the composite that 
     * contains all controls.
     * 
     * @return the composite at which the transition should be shown
     */
    public Composite getComposite();
    
    /**
     * @return the current selected control
     */
    public int getSelection();
    
    /**
     * sets the current selected control
     * @param index the index of the control to be set as the current selection
     */
    public void setSelection(int index);
    
    /**
     * should compare toIndex with fromIndex and returns 
     * the required direction of the transition.
     * 
     * @param toIndex index of the control to make transition to
     * @param fromIndex index of the control to make transition from
     * @return the required direction
     */
    public double getDirection(int toIndex, int fromIndex);
    
}
