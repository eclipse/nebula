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

package org.eclipse.nebula.widgets.xviewer.util.internal;

/**
 * @author Don Dunne
 */
public class Strings {
   private final static String EMPTY_STRING = "";

   private Strings() {
      // Utility class
   }

   public static boolean isValid(String value) {
      return value != null && value.length() > 0;
   }

   public static String emptyString() {
      return EMPTY_STRING;
   }

}
