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
package org.eclipse.nebula.widgets.xviewer.customize.dialog;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeDataLabelProvider;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLib;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * Provides dialog for saving table customizations.
 *
 * @author Donald G. Dunne
 */
public class CustomizationDataSelectionDialog extends ListDialog {

   private Text custText;
   private String enteredName;
   private boolean saveShared = false;
   private Button saveSharedCheck;
   private CustomizeData selectedCustData = null;
   private final XViewer xViewer;

   public CustomizationDataSelectionDialog(XViewer xViewer, List<CustomizeData> custDatas) {
      this(Display.getCurrent().getActiveShell(), xViewer, custDatas);
   }

   public CustomizationDataSelectionDialog(Shell parent, XViewer xViewer, List<CustomizeData> custDatas) {
      super(parent);
      this.xViewer = xViewer;
      setContentProvider(new ArrayContentProvider());
      setLabelProvider(new CustomizeDataLabelProvider(xViewer));
      setInput(custDatas);
      setShellStyle(getShellStyle() | SWT.RESIZE);
      setTitle(XViewerText.get("CustomizationDataSelectionDialog.title")); //$NON-NLS-1$
      setMessage(XViewerText.get("CustomizationDataSelectionDialog.message")); //$NON-NLS-1$
   }

   @Override
   protected void okPressed() {
      if (custText.getText().equals("") && getSelectedCustData() == null) { //$NON-NLS-1$
         XViewerLib.popup(XViewerText.get("error"), XViewerText.get("error.no_cust_selection")); //$NON-NLS-1$ //$NON-NLS-2$
         return;
      }
      super.okPressed();
   }

   @Override
   protected Control createDialogArea(Composite container) {

      Control c = super.createDialogArea(container);

      Composite comp = new Composite(container, SWT.NONE);
      comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      comp.setLayout(new GridLayout(2, true));

      Label custTextLabel = new Label(comp, SWT.None);
      custTextLabel.setText(XViewerText.get("CustomizationDataSelectionDialog.prompt")); //$NON-NLS-1$

      custText = new Text(comp, SWT.BORDER);
      custText.setFocus();
      custText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      custText.addListener(SWT.Modify, e -> enteredName = custText.getText());

      if (xViewer.getXViewerFactory().isAdmin()) {
         comp = new Composite(container, SWT.NONE);
         comp.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
         comp.setLayout(new GridLayout(2, false));

         Label saveSharedCheckLabel = new Label(comp, SWT.None);
         saveSharedCheckLabel.setText(XViewerText.get("checkbox.shared")); //$NON-NLS-1$

         saveSharedCheck = new Button(comp, SWT.CHECK);
         saveSharedCheck.addListener(SWT.Selection, e -> saveShared = saveSharedCheck.getSelection());
      }

      getTableViewer().addSelectionChangedListener(event -> {
            selectedCustData = getSelectedCustomizeData();
            if (saveSharedCheck != null) {
               saveSharedCheck.setSelection(!selectedCustData.isPersonal());
               saveShared = !selectedCustData.isPersonal();
            }
      });
      return c;
   }

   private CustomizeData getSelectedCustomizeData() {
      IStructuredSelection selection = (IStructuredSelection) getTableViewer().getSelection();
      if (selection.isEmpty()) {
         return null;
      }
      Iterator<?> i = selection.iterator();
      return (CustomizeData) i.next();
   }

   public CustomizeData getSelectedCustData() {
      return selectedCustData;
   }

   public String getEnteredName() {
      return enteredName;
   }

   public boolean isSaveShared() {
      return saveShared;
   }

   public void setSaveShared(boolean saveShared) {
      this.saveShared = saveShared;
   }

}
