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

package org.eclipse.nebula.widgets.xviewer;

import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLog;

/**
 * @author Donald G. Dunne
 */
public class XViewerCells {

   public static String getCellExceptionString(String message) {
      return CELL_ERROR_PREFIX + " - " + message;
   }

   public static String getCellExceptionString(Exception ex) {
      XViewerLog.log(Activator.class, Level.SEVERE, ex);
      return CELL_ERROR_PREFIX + " - " + ex.getLocalizedMessage();
   }

   public static final String CELL_ERROR_PREFIX = "!Error";

}
