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
package org.eclipse.nebula.widgets.xviewer.column;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.Activator;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerComputedColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.nebula.widgets.xviewer.util.XViewerLib;
import org.eclipse.nebula.widgets.xviewer.util.XViewerLog;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class XViewerDaysTillTodayColumn extends XViewerComputedColumn {

   private final static SimpleDateFormat format10 = new SimpleDateFormat("MM/dd/yyyy");
   private final static String ID = "ats.computed.daysTillToday";

   public XViewerDaysTillTodayColumn() {
      super(ID, "Days Till Today", 30, SWT.LEFT, false, SortDataType.Integer, false,
         "Shows number of days till today for selected column");
   }

   private XViewerDaysTillTodayColumn(String id) {
      super(id, "Days Till Today", 30, SWT.LEFT, false, SortDataType.Integer, false,
         "Shows number of days till today for selected column");
   }

   @SuppressWarnings("unused")
   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) throws XViewerException {
      if (sourceXViewerColumn == null) {
         return String.format("Source column not found for " + id + ".  Delete column and re-create.");
      }
      try {
         String dateStr =
            ((XViewerLabelProvider) xViewer.getLabelProvider()).getColumnText(element, sourceXViewerColumn, columnIndex);
         if (dateStr == null || dateStr.equals("")) {
            return "";
         }
         DateFormat format;
         if (dateStr.length() == 10) {
            format = format10;
         } else {
            format = DateFormat.getInstance();
         }
         Date date1Date = null;
         try {
            date1Date = format.parse(dateStr);
         } catch (ParseException ex) {
            try {
               date1Date = DateFormat.getInstance().parse(dateStr);
            } catch (ParseException ex2) {
               XViewerLog.log(Activator.class, Level.SEVERE, ex2);
            }
         }
         if (date1Date == null) {
            return "Can't parse date";
         }
         return String.valueOf(XViewerLib.daysTillToday(date1Date));
      } catch (Exception ex) {
         return ex.getLocalizedMessage();
      }
   }

   @Override
   public boolean isApplicableFor(XViewerColumn xViewerColumn) {
      return xViewerColumn.getSortDataType() == SortDataType.Date;
   }

   @Override
   public String getName() {
      if (sourceXViewerColumn == null) {
         return "Days Till Today";
      }
      return "Days Till Today from " + sourceXViewerColumn.getName() + "";
   }

   @Override
   public XViewerDaysTillTodayColumn copy() {
      XViewerDaysTillTodayColumn col = new XViewerDaysTillTodayColumn();
      col.setXViewer(getXViewer());
      col.setSourceXViewerColumn(getSourceXViewerColumn());
      return col;
   }

   @Override
   public String getId() {
      if (sourceXViewerColumn == null) {
         return ID;
      }
      return ID + "(" + sourceXViewerColumn.getId() + ")";
   }

   @Override
   public boolean isApplicableFor(String storedId) {
      return storedId.startsWith(ID);
   }

   @Override
   public XViewerComputedColumn createFromStored(XViewerColumn storedColumn) {
      return new XViewerDaysTillTodayColumn(storedColumn.getId());
   }
}
