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
package org.eclipse.nebula.widgets.xviewer.example.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Donald G. Dunne
 */
public class MyLog {

   public static Logger logger = Logger.getLogger("XViewer Log");

   public static void logAndPopup(Class<?> activatorClass, Level level, String message) {
      log(activatorClass, level, message);
      MyLib.popup("ERROR", message);
   }

   public static void log(Class<?> activatorClass, Level level, String message) {
      if (level.intValue() >= Level.SEVERE.intValue()) {
         logger.log(level, message, new Exception("used to get a stack trace"));
      } else {
         logger.log(level, message);
      }
   }

   public static void logAndPopup(Class<?> activatorClass, Level level, Throwable th) {
      log(activatorClass, level, th.getLocalizedMessage(), th);
      MyLib.popup("ERROR", th.getLocalizedMessage());
   }

   public static void log(Class<?> activatorClass, Level level, Throwable th) {
      logger.log(level, th.getLocalizedMessage(), th);
   }

   public static void log(Class<?> activatorClass, Level level, String message, Throwable th) {
      logger.log(level, message, th);
   }

}
