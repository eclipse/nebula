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
package org.eclipse.nebula.widgets.xviewer.util;

import java.util.Collection;
import org.eclipse.nebula.widgets.xviewer.util.internal.ArrayTreeContentProvider;
import org.eclipse.nebula.widgets.xviewer.util.internal.StringLabelProvider;
import org.eclipse.nebula.widgets.xviewer.util.internal.StringViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;

/**
 * @author Donald G. Dunne
 */
public class EnumStringMultiSelectionDialog extends CheckedTreeSelectionDialog {

   private Button addSelectedRadioButton;
   private Button replaceAllRadioButton;
   private Button deleteSelectedRadioButton;
   public static enum Selection {
      AddSelection, ReplaceAll, DeleteSelected
   };
   private Selection selected = Selection.AddSelection;
   private boolean enableReplace = false;
   private boolean enableDelete = false;

   /**
    * @param parent
    * @param artifacts
    */
   public EnumStringMultiSelectionDialog(String displayName, Collection<String> enums, Collection<String> selEnums) {
      super(Display.getCurrent().getActiveShell(), new StringLabelProvider(), new ArrayTreeContentProvider());
      setTitle("Select " + displayName);
      setMessage("Select " + displayName + " to add, delete or replace.");
      setInput(enums);
      setComparator(new StringViewerSorter());
      setInitialSelections(selEnums.toArray());
   }

   public EnumStringMultiSelectionDialog(String displayName, Collection<String> enums, Collection<String> selEnums, boolean enableReplace, boolean enableDelete) {
      this(displayName, enums, selEnums);
      this.enableDelete = enableDelete;
      this.enableReplace = enableReplace;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control c = super.createDialogArea(container);

      Composite comp = new Composite(container, SWT.NONE);
      comp.setLayout(new GridLayout(2, false));

      (new Label(comp, SWT.None)).setText("Add selected item(s) to existing if not already chosen.");

      addSelectedRadioButton = new Button(comp, SWT.CHECK);
      addSelectedRadioButton.setSelection(true);
      addSelectedRadioButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            super.widgetSelected(e);
            if (addSelectedRadioButton.getSelection()) selected = Selection.AddSelection;
         }
      });

      if (enableReplace) {
         (new Label(comp, SWT.None)).setText("Replace all existing with selected item(s).");

         replaceAllRadioButton = new Button(comp, SWT.CHECK);
         replaceAllRadioButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);
               if (replaceAllRadioButton.getSelection()) selected = Selection.ReplaceAll;
            }
         });
      }

      if (enableDelete) {
         (new Label(comp, SWT.None)).setText("Remove selected item(s) if already chosen.");

         deleteSelectedRadioButton = new Button(comp, SWT.CHECK);
         deleteSelectedRadioButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);
               if (deleteSelectedRadioButton.getSelection()) selected = Selection.DeleteSelected;
            }
         });
      }
      return c;
   }

   public Selection getSelected() {
      return selected;
   }

}
