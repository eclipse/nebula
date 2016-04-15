/*****************************************************************************
 * Copyright (c) 2016 Dirk Fauth.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
