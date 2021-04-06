/*******************************************************************************
 *  Copyright (c) 2010 Weltevree Beheer BV, Remain Software & Industrial-TSI
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
 *   Wim S. Jongman - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.oscilloscope.multichannel;

/**
 * Listener for an empty stack. It enables you to input more values only if the
 * stack runs out of values.
 * 
 * @author Wim.Jongman (@remainsoftware.com)
 * 
 */
public abstract class OscilloscopeStackAdapter {

	/**
	 * Is called when the stack runs out of values.
	 * 
	 * @param scope
	 */
	public void stackEmpty(Oscilloscope scope, int channel) {
	}
}
