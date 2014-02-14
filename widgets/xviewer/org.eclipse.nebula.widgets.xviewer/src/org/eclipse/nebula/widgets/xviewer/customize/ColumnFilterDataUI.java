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

package org.eclipse.nebula.widgets.xviewer.customize;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.nebula.widgets.xviewer.util.internal.dialog.DialogWithEntry;
import org.eclipse.swt.widgets.Display;

/**
 * UI for the display of column filter data
 * 
 * @author Donald G. Dunne
 */
public class ColumnFilterDataUI {

   private final XViewer xViewer;

   public ColumnFilterDataUI(XViewer xViewer) {
      this.xViewer = xViewer;
   }

   public void createWidgets() {
      // provided for subclass implementation
   }

   public void promptSetFilter(String colId) {
      DialogWithEntry ed =
         new DialogWithEntry(Display.getCurrent().getActiveShell(), XViewerText.get("ColumnFilterDataUI.title"), null, //$NON-NLS-1$
            XViewerText.get("ColumnFilterDataUI.prompt", colId), MessageDialog.QUESTION, new String[] { //$NON-NLS-1$
               XViewerText.get("button.ok"), //$NON-NLS-1$
               XViewerText.get("button.clear"), //$NON-NLS-1$
               XViewerText.get("button.clear_all"), //$NON-NLS-1$
               XViewerText.get("button.cancel")}, 0); //$NON-NLS-1$
      String str = xViewer.getCustomizeMgr().getColumnFilterData().getFilterText(colId);
      if (str != null && !str.equals("")) { //$NON-NLS-1$
         ed.setEntry(str);
      }
      int result = ed.open();
      if (result == 0) {
         xViewer.getCustomizeMgr().setColumnFilterText(colId, ed.getEntry());
      } else if (result == 1) {
         xViewer.getCustomizeMgr().setColumnFilterText(colId, null);
      } else if (result == 2) {
         xViewer.getCustomizeMgr().clearAllColumnFilters();
      }
   }

   public void appendToStatusLabel(StringBuffer sb) {
      for (String colId : xViewer.getCustomizeMgr().getColumnFilterData().getColIds()) {
         sb.append("[" + colId + "=" + xViewer.getCustomizeMgr().getColumnFilterData().getFilterText(colId) + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      }
   }

}
