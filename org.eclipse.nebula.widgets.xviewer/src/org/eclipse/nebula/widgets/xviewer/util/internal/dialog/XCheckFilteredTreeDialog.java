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
package org.eclipse.nebula.widgets.xviewer.util.internal.dialog;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.nebula.widgets.xviewer.util.internal.Result;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLib;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PatternFilter;

public class XCheckFilteredTreeDialog extends MessageDialog {

   protected Label statusLabel;
   private Button okButton;
   private XCheckedFilteredTree treeViewer;
   private final PatternFilter patternFilter;
   private Object input;
   private final IContentProvider contentProvider;
   private final IBaseLabelProvider labelProvider;
   private Collection<? extends Object> initialSelections;
   private final ViewerSorter viewerSorter;

   public XCheckFilteredTreeDialog(String dialogTitle, String dialogMessage, PatternFilter patternFilter, IContentProvider contentProvider, IBaseLabelProvider labelProvider, ViewerSorter viewerSorter) {
      super(Display.getCurrent().getActiveShell(), dialogTitle, null, dialogMessage, MessageDialog.NONE, new String[] {
            "OK", "Cancel"}, 0);
      this.contentProvider = contentProvider;
      this.labelProvider = labelProvider;
      this.patternFilter = patternFilter;
      this.viewerSorter = viewerSorter;
      setShellStyle(getShellStyle() | SWT.RESIZE);
   }

   protected void createPreCustomArea(Composite parent) {
   }

   public Set<Object> getChecked() {
      if (getTreeViewer() == null) return Collections.emptySet();
      return getTreeViewer().getChecked();
   }

   /**
    * Sets the input. Convenience method.
    * 
    * @param object the input.
    */
   public final void setInput(Object input) {
      this.input = input;
      if (treeViewer != null) treeViewer.getViewer().setInput(input);
   }

   /**
    * Sets the initial selection. Convenience method.
    * 
    * @param object the initial selection.
    */
   public void setInitialSelections(Collection<? extends Object> initialSelections) {
      this.initialSelections = initialSelections;
      if (treeViewer != null) {
         treeViewer.setInitalChecked(initialSelections);
      }
   }

   public Object[] getResult() {
      if (treeViewer == null) return new Object[] {};
      return treeViewer.getResult();
   }

   @Override
   protected Control createCustomArea(Composite parent) {

      statusLabel = new Label(parent, SWT.NONE);
      statusLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
      updateStatusLabel();

      createPreCustomArea(parent);

      Composite aiComp = new Composite(parent, SWT.NONE);
      aiComp.setLayout(XViewerLib.getZeroMarginLayout());
      aiComp.setLayoutData(new GridData(GridData.FILL_BOTH));

      treeViewer =
            new XCheckedFilteredTree(aiComp,
                  SWT.MULTI | SWT.CHECK | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, patternFilter);
      treeViewer.getViewer().getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      treeViewer.getViewer().setContentProvider(contentProvider);
      treeViewer.getViewer().setLabelProvider(labelProvider);
      treeViewer.getViewer().setSorter(viewerSorter);
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 400;
      treeViewer.getViewer().getTree().setLayoutData(gd);
      treeViewer.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
         public void selectionChanged(SelectionChangedEvent event) {
            updateStatusLabel();
         }
      });
      if (input != null) treeViewer.getViewer().setInput(input);
      if (initialSelections != null) treeViewer.setInitalChecked(initialSelections);
      return parent;
   }

   protected void updateStatusLabel() {
      Result result = isComplete();
      if (result.isFalse())
         statusLabel.setText(result.getText());
      else
         statusLabel.setText("");
      statusLabel.getParent().layout();
      updateButtons();
   }

   @Override
   protected Control createButtonBar(Composite parent) {
      Control c = super.createButtonBar(parent);
      okButton = getButton(0);
      okButton.setEnabled(false);
      return c;
   }

   protected Result isComplete() {
      return Result.TrueResult;
   }

   private void updateButtons() {
      if (okButton != null) okButton.setEnabled(isComplete().isTrue());
   }

   /**
    * @return the treeViewer
    */
   public XCheckedFilteredTree getTreeViewer() {
      return treeViewer;
   }

}
