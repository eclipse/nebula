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
package org.eclipse.nebula.widgets.xviewer.action;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumnSorter;
import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLib;
import org.eclipse.nebula.widgets.xviewer.util.internal.dialog.ListDialogSortable;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Megumi Telles
 */
public class ColumnMultiEditAction extends Action {

   private final XViewer xViewer;

   public ColumnMultiEditAction(XViewer xViewer) {
      super(XViewerText.get("ColumnMultiEditAction.text")); //$NON-NLS-1$
      this.xViewer = xViewer;
   }

   public static Set<TreeColumn> getEditableTreeColumns(XViewer xViewer, Collection<TreeItem> selectedTreeItems) {
      Set<TreeColumn> editableColumns = new HashSet<TreeColumn>();
      for (TreeColumn treeCol : xViewer.getTree().getColumns()) {
         if (xViewer.isColumnMultiEditable(treeCol, selectedTreeItems)) {
            editableColumns.add(treeCol);
         }
      }
      return editableColumns;
   }

   @Override
   public void run() {
      Collection<TreeItem> selectedTreeItems = Arrays.asList(xViewer.getTree().getSelection());
      Set<TreeColumn> editableColumns = getEditableTreeColumns(xViewer, selectedTreeItems);
      if (editableColumns.isEmpty()) {
         XViewerLib.popup(XViewerText.get("error"), XViewerText.get("error.no_columns.multi_editable")); //$NON-NLS-1$ //$NON-NLS-2$
         return;
      }
      ListDialogSortable ld = new ListDialogSortable(new XViewerColumnSorter(), xViewer.getTree().getShell());
      ld.setMessage(XViewerText.get("ColumnMultiEditAction.title")); //$NON-NLS-1$
      ld.setInput(editableColumns);
      ld.setLabelProvider(treeColumnLabelProvider);
      ld.setContentProvider(new ArrayContentProvider());
      ld.setTitle(XViewerText.get("ColumnMultiEditAction.title")); //$NON-NLS-1$
      int result = ld.open();
      if (result != 0) {
         return;
      }
      xViewer.handleColumnMultiEdit((TreeColumn) ld.getResult()[0], selectedTreeItems);
   }

   static LabelProvider treeColumnLabelProvider = new LabelProvider() {
      @Override
      public String getText(Object element) {
         if (element instanceof TreeColumn) {
            return ((TreeColumn) element).getText();
         }
         return "Unknown element type"; //$NON-NLS-1$
      }
   };

}
