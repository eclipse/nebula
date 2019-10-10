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

package org.eclipse.nebula.widgets.xviewer.customize;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.nebula.widgets.xviewer.core.model.ColumnDateFilter;
import org.eclipse.nebula.widgets.xviewer.core.model.DateRangeType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.core.util.Strings;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLib;
import org.eclipse.nebula.widgets.xviewer.util.internal.dialog.ColumnFilterDialog;
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

   public void promptSetFilter(XViewerColumn column) {
      String columnName = column.getId().replaceFirst(".*\\.", "");
      ColumnFilterDialog ed =
         new ColumnFilterDialog(Display.getCurrent().getActiveShell(), XViewerText.get("ColumnFilterDataUI.title"), //$NON-NLS-1$
            XViewerText.get("ColumnFilterDataUI.prompt", columnName), MessageDialog.QUESTION, //$NON-NLS-1$
            new String[] {
               XViewerText.get("button.ok"), //$NON-NLS-1$
               XViewerText.get("button.clear"), //$NON-NLS-1$
               XViewerText.get("button.clear_all"), //$NON-NLS-1$
               XViewerText.get("button.cancel")}, //$NON-NLS-1$
            0, column);
      String str = xViewer.getCustomizeMgr().getColumnFilterData().getFilterText(column.getId());
      if (str != null && !str.equals("")) { //$NON-NLS-1$
         ed.setEntry(str);
      }
      int result = ed.open();
      if (result == 0) {
         xViewer.getCustomizeMgr().setColumnFilterText(column.getId(), ed.getEntry());
         xViewer.getCustomizeMgr().setColumnDateFilter(column.getId(), ed.getDateRangeType(), ed.getDate1(),
            ed.getDate2());
      } else if (result == 1) {
         xViewer.getCustomizeMgr().setColumnFilterText(column.getId(), null);
         xViewer.getCustomizeMgr().setColumnDateFilter(column.getId(), DateRangeType.None, null, null);
      } else if (result == 2) {
         xViewer.getCustomizeMgr().clearAllColumnFilters();
      }
   }

   public void appendToStatusLabel(StringBuilder sb) {
      for (String colId : xViewer.getCustomizeMgr().getColumnFilterData().getColIds()) {
         String filterText = xViewer.getCustomizeMgr().getColumnFilterData().getFilterText(colId);
         if (Strings.isValid(filterText)) {
            sb.append("[" + colId + " = " + filterText + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
         }
         ColumnDateFilter dateFilter = xViewer.getCustomizeMgr().getColumnFilterData().getDateFilter(colId);
         if (dateFilter != null) {
            sb.append("[" + colId + " = " + dateFilter.getType().name() + " ");
            sb.append(XViewerLib.getDateFromPattern(dateFilter.getDate1(), XViewerLib.MMDDYYHHMM));
            if (dateFilter.getType() == DateRangeType.Between_Dates) {
               sb.append(" and " + XViewerLib.getDateFromPattern(dateFilter.getDate2(), XViewerLib.MMDDYYHHMM));
            }
            sb.append("]");
         }
      }
   }

}
