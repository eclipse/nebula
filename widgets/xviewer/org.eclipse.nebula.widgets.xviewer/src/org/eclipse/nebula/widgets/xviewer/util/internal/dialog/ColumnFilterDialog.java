/*******************************************************************************
 * Copyright (c) 2015 Boeing.
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
package org.eclipse.nebula.widgets.xviewer.util.internal.dialog;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.DateRangeType;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Donald G. Dunne
 */
public class ColumnFilterDialog extends DialogWithEntry {

   private final XViewerColumn column;
   private ComboViewer dateRangeTypeCombo;
   private DateTime date1Widget;
   private Date date1, date2;
   private DateRangeType dateRangeType = null;
   private DateTime date2Widget;
   private DateTime time1Widget;
   private DateTime time2Widget;
   private Composite widgetComp;

   public ColumnFilterDialog(Shell shell, String dialogTitle, String dialogMessage, int question, String[] strings, int i, XViewerColumn column) {
      super(shell, dialogTitle, null, dialogMessage, MessageDialog.QUESTION, strings, 0);
      this.column = column;
   }

   @Override
   protected void createExtendedArea(Composite parent) {
      super.createExtendedArea(parent);
      if (column.getSortDataType() == SortDataType.Date) {

         widgetComp = new Composite(parent, SWT.NONE);
         widgetComp.setLayout(new GridLayout(6, false));
         GridData gd = new GridData(GridData.FILL_HORIZONTAL);
         gd.horizontalSpan = 2;
         widgetComp.setLayoutData(gd);

         Label label = new Label(widgetComp, SWT.NONE);
         label.setText("Date Match: ");

         dateRangeTypeCombo = new ComboViewer(widgetComp, SWT.NONE);
         dateRangeTypeCombo.setContentProvider(new ArrayContentProvider());
         dateRangeTypeCombo.setLabelProvider(new LabelProvider() {

            @Override
            public String getText(Object element) {
               return ((DateRangeType) element).getDisplayName();
            }

         });
         dateRangeTypeCombo.setInput(DateRangeType.values());
         dateRangeTypeCombo.addSelectionChangedListener(event -> {
               String text2 = dateRangeTypeCombo.getCombo().getText();
               dateRangeType = DateRangeType.get(text2);
               updateDate2Composite();
         });

         date1Widget = new DateTime(widgetComp, SWT.CALENDAR);
         date1Widget.addListener(SWT.Selection, e-> setDate1Selection());

         // set initial date
         Calendar cal = Calendar.getInstance();
         cal.set(date1Widget.getYear(), date1Widget.getMonth(), date1Widget.getDay(), 0, 0);
         date1 = cal.getTime();

         time1Widget = new DateTime(widgetComp, SWT.TIME);
         time1Widget.addListener(SWT.Selection, e-> setDate1Selection());
         time1Widget.setHours(0);
         time1Widget.setMinutes(0);
         time1Widget.setSeconds(0);

      }
   }

   private boolean isBetweenDates() {
      dateRangeType = DateRangeType.get(dateRangeTypeCombo.getCombo().getText());
      return dateRangeType == DateRangeType.Between_Dates;
   }

   public void updateDate2Composite() {
      if (isBetweenDates()) {

         date2Widget = new DateTime(widgetComp, SWT.CALENDAR);
         date2Widget.addListener(SWT.Selection, e-> setDate2Selection());

         time2Widget = new DateTime(widgetComp, SWT.TIME);
         time2Widget.addListener(SWT.Selection, e-> setDate2Selection());
         time2Widget.setHours(0);
         time2Widget.setMinutes(0);
         time2Widget.setSeconds(0);
      } else {
         if (date2Widget != null) {
            date2Widget.dispose();
            date2Widget = null;
            time2Widget.dispose();
            time2Widget = null;
         }
      }
      widgetComp.layout(true, true);
      final Point newSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
      getShell().setSize(newSize);
   }

   private void setDate1Selection() {
      Calendar cal = Calendar.getInstance();
      cal.set(date1Widget.getYear(), date1Widget.getMonth(), date1Widget.getDay(), time1Widget.getHours(),
         time1Widget.getMinutes());
      date1 = cal.getTime();
   }

   private void setDate2Selection() {
      if (date2Widget.isEnabled()) {
         Calendar cal = Calendar.getInstance();
         cal.set(date2Widget.getYear(), date2Widget.getMonth(), date2Widget.getDay(), date2Widget.getHours(),
            date2Widget.getMinutes());
         date2 = cal.getTime();
      } else {
         date2 = null;
      }
   }

   public Date getDate1() {
      return date1;
   }

   public void setDate1(Date date1) {
      this.date1 = date1;
   }

   public Date getDate2() {
      return date2;
   }

   public void setDate2(Date date2) {
      this.date2 = date2;
   }

   public DateRangeType getDateRangeType() {
      return dateRangeType;
   }

   public void setDateRangeType(DateRangeType dateRangeType) {
      this.dateRangeType = dateRangeType;
   }

}
