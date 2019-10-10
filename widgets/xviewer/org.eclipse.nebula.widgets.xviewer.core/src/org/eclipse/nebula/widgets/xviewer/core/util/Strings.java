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

package org.eclipse.nebula.widgets.xviewer.core.util;

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
