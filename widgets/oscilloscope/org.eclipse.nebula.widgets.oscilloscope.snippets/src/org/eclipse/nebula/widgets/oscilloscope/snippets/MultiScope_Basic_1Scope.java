/*******************************************************************************
 *  Copyright (c) 2010, 2012 Weltevree Beheer BV, Remain Software & Industrial-TSI
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
package org.eclipse.nebula.widgets.oscilloscope.snippets;

/**
 * @author Wim.Jongman (@remainsoftware.com)
 *
 */
public class MultiScope_Basic_1Scope extends MultiScope_Basic {

	public MultiScope_Basic_1Scope(int counter) {
		super(counter);
	}

	/**
	 * Launch the application.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MultiScope_Basic_1Scope window = new MultiScope_Basic_1Scope(1);
			window.open();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}