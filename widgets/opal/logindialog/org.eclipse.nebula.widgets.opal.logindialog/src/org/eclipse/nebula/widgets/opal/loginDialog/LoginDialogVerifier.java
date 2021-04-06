/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation 
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.loginDialog;

/**
 * This interface describes a verifier for the LoginDialogWidget
 */
@FunctionalInterface
public interface LoginDialogVerifier {
	/**
	 * Check if the couple login/password is correct
	 * 
	 * @param login login entered by the user
	 * @param password password entered by the user
	 * @throws Exception if the couple login/password is wrong. The description
	 *             of the exception contains the error message that is gonna be
	 *             displayed. For instance, an implementation can throw the
	 *             exception *
	 *             <code>new Exception("Unable to connect to the LDAP Server")</code>
	 */
	void authenticate(String login, String password) throws Exception;
}
