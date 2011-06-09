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

import org.eclipse.nebula.effects.stw.TransitionListener;
import org.eclipse.nebula.effects.stw.TransitionManager;

/**
 * Classes which implement this interface provide methods that handle the transition finished event.<br/>
 *
 * After creating an instance of a class that implements this interface it can be added to a 
 * transition manager using the {@link TransitionManager#addTransitionListener(TransitionListener)} 
 * method and removed using the {@link TransitionManager#removeTransitionListener(TransitionListener)}
 *  method.
 * 
 * @author Ahmed Mahran (ahmahran@gmail.com)
 */
public interface TransitionListener {

    /**
     * This method is called when the transition is finished.
     * 
     * @param transitionManager is the transition manager caller object
     */
    public void transitionFinished(TransitionManager transitionManager);
    
}
