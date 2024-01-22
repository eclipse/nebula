/*******************************************************************************
 * Copyright (c) 2016 Boeing.
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

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author Donald G. Dunne
 */
public class XViewerUtil {

   static Random random = new SecureRandom();

   public static String intern(String str) {
      return (str == null) ? null : str.intern();
   }

   public static String doubleToI18nString(double d) {
      return doubleToI18nString(d, false);
   }

   public static String doubleToI18nString(double d, boolean blankIfZero) {
      if (blankIfZero && d == 0) {
         return "";
      } else {
         return String.format("%4.2f", d);
      }
   }

   public static String generateGuidStr() {
      long rand = (random.nextLong() & 0x7FFFFFFFFFFFFFFFL) | 0x4000000000000000L;
      return Long.toString(rand, 32) + Long.toString(System.currentTimeMillis() & 0xFFFFFFFFFFFFFL, 32);
   }

}
