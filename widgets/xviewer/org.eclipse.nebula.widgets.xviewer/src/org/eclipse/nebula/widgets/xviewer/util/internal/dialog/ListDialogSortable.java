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

import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.nebula.widgets.xviewer.XViewerColumnSorter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public class ListDialogSortable extends ListDialog {

   private ViewerSorter viewerSorter;

   public ListDialogSortable(Shell parent) {
      super(parent);
   }

   public ListDialogSortable(ViewerSorter viewerSorter, Shell parent) {
      super(parent);
      this.viewerSorter = viewerSorter;
   }

   public void setSorter(ViewerSorter viewerSorter) {
      this.viewerSorter = viewerSorter;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control control = super.createDialogArea(container);
      if (viewerSorter != null) {
         getTableViewer().setSorter(new XViewerColumnSorter());
      }
      return control;
   }

}
