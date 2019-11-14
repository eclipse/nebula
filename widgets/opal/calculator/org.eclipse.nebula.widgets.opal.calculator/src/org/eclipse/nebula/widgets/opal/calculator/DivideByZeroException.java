/*******************************************************************************
 * Copyright (c) 2012 Laurent CARON
 * All rights reserved.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Laurent CARON (laurent.caron at gmail dot com) - Initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.calculator;

class DivideByZeroException extends Exception {
	private static final long serialVersionUID = 1764265506499117961L;

	/**
	 * Constructor
	 */
	public DivideByZeroException() {
		super();
	}

	/**
	 * Constructor
	 *
	 * @param errorMessage error message
	 */
	public DivideByZeroException(final String errorMessage) {
		super(errorMessage);
	}
}