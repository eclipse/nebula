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

import java.util.Collection;
import java.util.Date;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.xviewer.util.EnumStringMultiSelectionDialog;
import org.eclipse.nebula.widgets.xviewer.util.EnumStringSingleSelectionDialog;
import org.eclipse.nebula.widgets.xviewer.util.internal.dialog.DateSelectionDialog;
import org.eclipse.nebula.widgets.xviewer.util.internal.dialog.DialogWithEntry;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class XPromptChange {

   private final static String VALID_FLOAT_REG_EX = "^[0-9\\.]+$"; //$NON-NLS-1$
   private final static String VALID_INTEGER_REG_EX = "^[0-9]+$"; //$NON-NLS-1$
   private final static String VALID_PERCENT_REG_EX =
      "^(0*100{1,1}\\.?((?<=\\.)0*)?%?$)|(^0*\\d{0,2}\\.?((?<=\\.)\\d*)?%?)$"; //$NON-NLS-1$
   public static enum Option {
      SINGLE_LINE,
      MULTI_LINE
   };

   public static Date promptChangeDate(String displayName, Date currDate) {
      // prompt that current release is (get from attribute); want to change
      DateSelectionDialog diag =
         new DateSelectionDialog(
            XViewerText.get("XPromptChange.dialog.date_selection") + " " + displayName, XViewerText.get("XPromptChange.dialog.date_selection") + " " + displayName, currDate != null ? currDate : null); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      if (diag.open() == 0) {
         return diag.getSelectedDate();
      }
      return null;
   }

   public static EnumStringSingleSelectionDialog promptChangeSingleSelectEnumeration(String displayName, Collection<String> enums, String currSelected) {
      final EnumStringSingleSelectionDialog diag =
         new EnumStringSingleSelectionDialog(displayName, displayName, enums, currSelected);
      if (diag.open() == 0) {
         return diag;
      }
      return null;
   }

   public static EnumStringMultiSelectionDialog promptChangeMultiSelectEnumeration(String displayName, Collection<String> enums, Collection<String> currEnums) {
      final EnumStringMultiSelectionDialog diag = new EnumStringMultiSelectionDialog(displayName, enums, currEnums);
      if (diag.open() == 0) {
         return diag;
      }
      return null;
   }

   public static String promptChangeInteger(String displayName, int currEntry) {
      return promptChangeInteger(displayName, currEntry == 0 ? "" : String.valueOf(currEntry)); //$NON-NLS-1$
   }

   public static String promptChangeInteger(String displayName, String currEntry) {
      return promptChangeString(displayName, currEntry, VALID_INTEGER_REG_EX);
   }

   public static String promptChangePercent(String displayName, String currEntry) {
      return promptChangeString(displayName, currEntry, VALID_PERCENT_REG_EX);
   }

   public static String promptChangeFloat(String displayName, double currEntry) {
      return promptChangeFloat(displayName, currEntry == 0 ? "" : String.valueOf(currEntry)); //$NON-NLS-1$
   }

   public static String promptChangeFloat(String displayName, String currEntry) {
      return promptChangeString(displayName, currEntry, VALID_FLOAT_REG_EX);
   }

   public static String promptChangeString(String displayName, String currEntry, String validationRegEx) {
      return promptChangeString(displayName, currEntry, validationRegEx, Option.SINGLE_LINE);
   }

   public static String promptChangeString(String displayName, String currEntry, String validationRegEx, Option option) {
      DialogWithEntry ed =
         new DialogWithEntry(
            XViewerText.get("XPromptChange.dialog.entry") + " " + displayName, XViewerText.get("XPromptChange.dialog.entry") + " " + displayName); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      if (option == Option.MULTI_LINE) {
         ed.setFillVertically(true);
      }
      if (currEntry != null && !currEntry.equals("")) { //$NON-NLS-1$
         ed.setEntry(currEntry);
      }
      if (validationRegEx != null) {
         ed.setValidationRegularExpression(validationRegEx);
      }
      int result = ed.open();
      if (result == 0) {
         return ed.getEntry();
      }
      return null;
   }

   public static Boolean promptChangeBoolean(String displayName, String toggleMessage, boolean currSelection) {
      MessageDialogWithToggle md =
         new MessageDialogWithToggle(Display.getCurrent().getActiveShell(), displayName, null, displayName,
            MessageDialog.QUESTION,
            new String[] {XViewerText.get("button.ok"), XViewerText.get("button.cancel")}, Window.OK, //$NON-NLS-1$ //$NON-NLS-2$
            (toggleMessage != null ? toggleMessage : displayName), currSelection);
      int result = md.open();
      if (result == Window.OK) {
         return md.getToggleState();
      }
      return null;
   }

}
