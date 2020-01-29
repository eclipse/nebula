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

package org.eclipse.nebula.widgets.xviewer.util.internal.dialog;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class DateSelectionDialog extends MessageDialog {

   private Date initialDate, selectedDate;

   private final String dialogMessage;

   public DateSelectionDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex, Date selectedDate) {
      super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
         defaultIndex);
      this.initialDate = selectedDate;
      this.dialogMessage = dialogMessage;
   }

   public DateSelectionDialog(String dialogTitle, String dialogMessage, Date selectedDate) {
      this(Display.getCurrent().getActiveShell(), dialogTitle, null, dialogMessage, MessageDialog.NONE, new String[] {
    	  XViewerText.get("button.ok"), XViewerText.get("button.cancel")}, 0, selectedDate); //$NON-NLS-1$ //$NON-NLS-2$
   }

   @Override
   protected Control createDialogArea(Composite container) {

      Composite filterComp = new Composite(container, SWT.NONE);

      filterComp.setLayout(new GridLayout(1, false));
      filterComp.setLayoutData(new GridData(GridData.FILL_BOTH));

      new Label(filterComp, SWT.None).setText(dialogMessage);

      final DateTime dp = new DateTime(filterComp, SWT.CALENDAR);
      dp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      if (initialDate != null) {
         Calendar cal = Calendar.getInstance();
         cal.setTime(initialDate);

         dp.setYear(cal.get(Calendar.YEAR));
         dp.setMonth(cal.get(Calendar.MONTH));
         dp.setDay(cal.get(Calendar.DAY_OF_YEAR));
      }
      dp.addListener(SWT.Selection, e->  {
            Calendar cal = Calendar.getInstance();
            cal.set(dp.getYear(), dp.getMonth(), dp.getDay());
            selectedDate = cal.getTime();
      });

      Button clearButton = new Button(filterComp, SWT.PUSH);
      clearButton.setText(XViewerText.get("button.clear")); //$NON-NLS-1$
      clearButton.addListener(SWT.Selection, e-> selectedDate = null);

      // set selected date if != null
      return filterComp;
   }

   /**
    * @return the selectedDate
    */
   public Date getSelectedDate() {
      return selectedDate;
   }

   /**
    * @param selectedDate the selectedDate to set
    */
   public void setSelectedDate(Date initialDate) {
      this.initialDate = initialDate;
   }

   /**
    * @return the noneSelected
    */
   public boolean isNoneSelected() {
      return selectedDate == null;
   }

}
