/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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