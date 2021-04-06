/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.xviewer.util;

/**
 * @author Ryan D. Brooks
 */
public class XViewerException extends Exception {
   private static final long serialVersionUID = 1L;

   public XViewerException(String message) {
      super(message);
   }

   public XViewerException(String message, Throwable cause) {
      super(message, cause);
   }

   public XViewerException(Throwable cause) {
      super(cause);
   }
}