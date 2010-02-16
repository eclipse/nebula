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
package org.eclipse.nebula.widgets.xviewer.customize.dialog;

import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeDataLabelProvider;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLib;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
   private Label custTextLabel;
   private String enteredName;
   private boolean saveGlobal = false;
   private Button saveGlobalCheck;
   private Label saveGlobalCheckLabel;
   private CustomizeData selectedCustData = null;
   private final XViewer xViewer;

   public CustomizationDataSelectionDialog(XViewer xViewer, List<CustomizeData> custDatas) {
      this(Display.getCurrent().getActiveShell(), xViewer, custDatas);
   }

   public CustomizationDataSelectionDialog(Shell parent, XViewer xViewer, List<CustomizeData> custDatas) {
      super(Display.getCurrent().getActiveShell());
      this.xViewer = xViewer;
      setContentProvider(new ArrayContentProvider());
      setLabelProvider(new CustomizeDataLabelProvider(xViewer));
      setInput(custDatas);
      setShellStyle(getShellStyle() | SWT.RESIZE);
      setTitle("Save Customization");
      setMessage("Enter name or select customization.");
   }

   @Override
   protected void okPressed() {
      if (custText.getText().equals("") && getSelectedCustData() == null) {
         XViewerLib.popup("ERROR", "Must select customization or enter new customization name.");
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

      custTextLabel = new Label(comp, SWT.None);
      custTextLabel.setText("Enter New Customization Name");

      custText = new Text(comp, SWT.BORDER);
      custText.setFocus();
      custText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
      custText.addModifyListener(new ModifyListener() {
         public void modifyText(ModifyEvent e) {
            enteredName = custText.getText();
         }
      });

      if (xViewer.getXViewerFactory().isAdmin()) {
         comp = new Composite(container, SWT.NONE);
         comp.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
         comp.setLayout(new GridLayout(2, false));

         saveGlobalCheckLabel = new Label(comp, SWT.None);
         saveGlobalCheckLabel.setText("Save Global");

         saveGlobalCheck = new Button(comp, SWT.CHECK);
         saveGlobalCheck.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               saveGlobal = saveGlobalCheck.getSelection();
            }
         });
      }

      getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
         public void selectionChanged(SelectionChangedEvent event) {
            selectedCustData = getSelectedCustomizeData();
            if (saveGlobalCheck != null) {
               saveGlobalCheck.setSelection(!selectedCustData.isPersonal());
               saveGlobal = !selectedCustData.isPersonal();
            }
         }
      });
      return c;
   }

   private CustomizeData getSelectedCustomizeData() {
      IStructuredSelection selection = (IStructuredSelection) getTableViewer().getSelection();
      if (selection.size() == 0) return null;
      Iterator<?> i = selection.iterator();
      return (CustomizeData) i.next();
   }

   /**
    * @return the selectedCustData
    */
   public CustomizeData getSelectedCustData() {
      return selectedCustData;
   }

   public String getEnteredName() {
      return enteredName;
   }

   public boolean isSaveGlobal() {
      return saveGlobal;
   }

   public void setSaveGlobal(boolean saveGlobal) {
      this.saveGlobal = saveGlobal;
   }

}
