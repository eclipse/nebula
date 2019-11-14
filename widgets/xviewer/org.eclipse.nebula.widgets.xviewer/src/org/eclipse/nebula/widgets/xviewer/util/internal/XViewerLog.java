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
package org.eclipse.nebula.widgets.xviewer.util.internal;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.nebula.widgets.xviewer.XViewerText;

/**
 * @author Donald G. Dunne
 */
public class XViewerLog {

   public static Logger logger = Logger.getLogger("XViewer Log");

   public static void logAndPopup(Class<?> activatorClass, Level level, String message) {
      log(activatorClass, level, message);
      XViewerLib.popup(XViewerText.get("error"), message); //$NON-NLS-1$
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
      XViewerLib.popup(XViewerText.get("error"), th.getLocalizedMessage()); //$NON-NLS-1$
   }

   public static void log(Class<?> activatorClass, Level level, Throwable th) {
      logger.log(level, th.getLocalizedMessage(), th);
   }

   public static void log(Class<?> activatorClass, Level level, String message, Throwable th) {
      logger.log(level, message, th);
   }

}
