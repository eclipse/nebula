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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerTextWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Donald G. Dunne
 */
public class DialogWithEntry extends MessageDialog {

   XViewerTextWidget text;
   Composite comp;
   String entryText = ""; //$NON-NLS-1$
   String validationRegularExpression = null;
   String validationErrorString = ""; //$NON-NLS-1$
   Button ok;
   MouseMoveListener listener;
   Label errorLabel;
   boolean fillVertically = false;
   private static Font font = null;
   private Button fontButton;

   public DialogWithEntry(String dialogTitle, String dialogMessage) {
      super(Display.getCurrent().getActiveShell(), dialogTitle, null, dialogMessage, MessageDialog.QUESTION,
         new String[] {XViewerText.get("button.ok"), XViewerText.get("button.cancel")}, 0); //$NON-NLS-1$ //$NON-NLS-2$
   }

   public DialogWithEntry(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
      super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
         defaultIndex);
   }

   @Override
   protected Control createCustomArea(Composite parent) {

      comp = new Composite(parent, SWT.NONE);
      comp.setLayout(new GridLayout(2, false));
      comp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

      listener = new MouseMoveListener() {

         @Override
         public void mouseMove(MouseEvent e) {
            setInitialButtonState();
         }
      };
      comp.addMouseMoveListener(listener);

      Composite headerComp = new Composite(comp, SWT.NONE);
      headerComp.setLayout(new GridLayout(3, false));
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.horizontalSpan = 2;
      headerComp.setLayoutData(gd);

      if (fillVertically) { // Create error label
         Button button = new Button(headerComp, SWT.PUSH);
         button.setText(XViewerText.get("button.clear")); //$NON-NLS-1$
         button.addListener(SWT.Selection, e-> text.setText(""));
         // Create error label
         fontButton = new Button(headerComp, SWT.CHECK);
         fontButton.setText(XViewerText.get("DialogWithEntry.button.font")); //$NON-NLS-1$
         fontButton.addListener(SWT.Selection, e-> {
               if (fontButton.getSelection()) {
                  if (font == null) {
                     font = new Font(Display.getCurrent(), "Courier New", 8, SWT.NORMAL); //$NON-NLS-1$
                  }
                  text.setFont(font);
               } else {
                  text.setFont(null);
               }
         });
      }

      // Create error label
      errorLabel = new Label(headerComp, SWT.NONE);
      errorLabel.setSize(errorLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      errorLabel.setText(""); //$NON-NLS-1$
      if (!fillVertically) {
         gd = new GridData();
         gd.horizontalSpan = 3;
         errorLabel.setLayoutData(gd);
      }

      createExtendedAreaPre(comp);

      text = new XViewerTextWidget();
      text.setFillHorizontally(true);
      text.setFocus();
      text.setDisplayLabel(false);
      if (fillVertically) {
         text.setFillVertically(true);
         text.setHeight(200);
         text.setFont(font);
      }
      text.createWidgets(comp, 2);
      if (!entryText.equals("")) { //$NON-NLS-1$
         text.set(entryText);
      }

      ModifyListener modifyListener = new ModifyListener() {

         @Override
         public void modifyText(ModifyEvent e) {
            handleModified();
         }
      };
      text.addModifyListener(modifyListener);
      createExtendedArea(comp);
      comp.layout();
      parent.layout();
      return comp;
   }

   /**
    * Override to provide other widgets before entry
    */
   protected void createExtendedAreaPre(Composite parent) {
      // provided for subclass implementation
   }

   @Override
   protected boolean isResizable() {
      return true;
   }

   /**
    * Override to provide other widgets
    */
   protected void createExtendedArea(Composite parent) {
      // provided for subclass implementation
   }

   public void setInitialButtonState() {
      if (ok == null) {
         ok = getButton(0);
         handleModified();
      }
      comp.removeMouseMoveListener(listener);
   }

   public void handleModified() {
      if (text != null) {
         entryText = text.get();
         if (!isEntryValid()) {
            getButton(getDefaultButtonIndex()).setEnabled(false);
            errorLabel.setText(validationErrorString);
            errorLabel.update();
            comp.layout();
         } else {
            getButton(getDefaultButtonIndex()).setEnabled(true);
            errorLabel.setText(""); //$NON-NLS-1$
            errorLabel.update();
            comp.layout();
         }
      }
   }

   public String getEntry() {
      return entryText;
   }

   public void setEntry(String entry) {
      if (text != null) {
         text.set(entry);
      }
      this.entryText = entry;
   }

   /**
    * override this method to make own checks on entry this will be called with every keystroke
    *
    * @return true if entry is valid
    */
   public boolean isEntryValid() {
      if (validationRegularExpression == null) {
         return true;
      }
      // verify title is alpha-numeric with spaces and dashes
      Matcher m = Pattern.compile(validationRegularExpression).matcher(text.get());
      return m.find();
   }

   public void setValidationRegularExpression(String regExp) {
      validationRegularExpression = regExp;
   }

   public void setValidationErrorString(String errorText) {
      validationErrorString = errorText;
   }

   /**
    * Calling will enable dialog to loose focus
    */
   public void setModeless() {
      setShellStyle(SWT.DIALOG_TRIM | SWT.MODELESS);
      setBlockOnOpen(false);
   }

   public void setSelectionListener(SelectionListener listener) {
      for (int i = 0; i < getButtonLabels().length; i++) {
         Button button = getButton(i);
         button.addSelectionListener(listener);
      }
   }

   public boolean isFillVertically() {
      return fillVertically;
   }

   public void setFillVertically(boolean fillVertically) {
      this.fillVertically = fillVertically;
   }

}
