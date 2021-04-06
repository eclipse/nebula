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
