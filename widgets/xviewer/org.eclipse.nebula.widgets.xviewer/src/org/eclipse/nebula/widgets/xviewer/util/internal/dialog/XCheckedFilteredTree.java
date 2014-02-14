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
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.nebula.widgets.xviewer.util.internal.PatternFilter;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class XCheckedFilteredTree extends XFilteredTree {

   private final Set<Object> checked = new HashSet<Object>();

   public XCheckedFilteredTree(Composite parent, int treeStyle, PatternFilter filter) {
      super(parent, treeStyle, filter, true);
      setInitialText(""); //$NON-NLS-1$
   }

   @Override
   protected Control createTreeControl(Composite parent, int style) {
      Control control = super.createTreeControl(parent, style);
      getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            storeResults(treeViewer.getTree().getItems());
         }
      });
      getFilterControl().addModifyListener(new ModifyListener() {
         @Override
         public void modifyText(ModifyEvent e) {
            restoreChecked(treeViewer.getTree().getItems());
         }
      });
      getViewer().getTree().addPaintListener(new PaintListener() {

         @Override
         public void paintControl(PaintEvent e) {
            restoreChecked(treeViewer.getTree().getItems());
         }
      });
      return control;
   }

   public void setInitalChecked(Collection<? extends Object> checked) {
      this.checked.addAll(checked);
      restoreChecked(treeViewer.getTree().getItems());
      for (Object obj : checked) {
         treeViewer.reveal(obj);
      }
   }

   public void clearChecked() {
      this.checked.clear();
      restoreChecked(treeViewer.getTree().getItems());
   }

   private void restoreChecked(TreeItem treeItems[]) {
      for (TreeItem treeItem : treeItems) {
         if (treeItem.getChecked() && !checked.contains(treeItem.getData())) {
            //            System.out.println("Unchecked " + treeItem.getData());
            treeItem.setChecked(false);
         } else if (!treeItem.getChecked() && checked.contains(treeItem.getData())) {
            //            System.out.println("Checked " + treeItem.getData());
            treeItem.setChecked(true);
         }
         restoreChecked(treeItem.getItems());
      }
   }

   public Object[] getResult() {
      return getChecked().toArray(new Object[getChecked().size()]);
   }

   private void storeResults(TreeItem treeItems[]) {
      for (TreeItem treeItem : treeItems) {
         if (treeItem.getChecked() && !checked.contains(treeItem.getData())) {
            //            System.out.println("Added " + treeItem.getData());
            checked.add(treeItem.getData());
         } else if (!treeItem.getChecked() && checked.contains(treeItem.getData())) {
            //            System.out.println("Removed " + treeItem.getData());
            checked.remove(treeItem.getData());
         }
         storeResults(treeItem.getItems());
      }
   }

   public Set<Object> getChecked() {
      return checked;
   }

}
