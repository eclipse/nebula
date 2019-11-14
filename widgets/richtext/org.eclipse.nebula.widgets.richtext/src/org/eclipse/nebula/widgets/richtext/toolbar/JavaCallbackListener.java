/*****************************************************************************
 * Copyright (c) 2016 Dirk Fauth.
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
 *		Dirk Fauth <dirk.fauth@googlemail.com> - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.nebula.widgets.richtext.toolbar;

/**
 * Interface for creating a listener that is called on Java callback execution triggered via custom toolbar
 * button.
 */
public interface JavaCallbackListener {

	/**
	 * Method that is triggered <b>before</b> a Java callback is executed via custom toolbar button.
	 */
	void javaExecutionStarted();

	/**
	 * Method that is triggered <b>after</b> a Java callback is executed via custom toolbar button.
	 */
	void javaExecutionFinished();

}
