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

/**
 * @author Ahmed Mahran (ahmahran@gmail.com)
 */
public interface TransitionListener {

    /**
     * This method is called when the most recent transition is finished.
     * 
     * @param transition is the transition object used to make the transition effect
     */
    public void transitionFinished(TransitionManager transition);
    
}
