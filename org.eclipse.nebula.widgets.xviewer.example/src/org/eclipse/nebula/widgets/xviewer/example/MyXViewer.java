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
package org.eclipse.nebula.widgets.xviewer.example;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.example.model.ISomeTask;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Example extension of XViewer.
 * 
 * @author Donald G. Dunne
 */
public class MyXViewer extends XViewer {
   private final Set<ISomeTask> runList = new HashSet<ISomeTask>();

   public MyXViewer(Composite parent, int style) {
      super(parent, style, new MyXViewerFactory());
   }

   public boolean isScheduled(ISomeTask autoRunTask) {
      return true;
   }

   public boolean isRun(ISomeTask autoRunTask) {
      return runList.contains(autoRunTask);
   }

   public void setRun(ISomeTask autoRunTask, boolean run) {
      if (run) {
         runList.add(autoRunTask);
      } else {
         runList.remove(autoRunTask);
      }
   }

   @Override
   public boolean handleLeftClickInIconArea(TreeColumn treeColumn, TreeItem treeItem) {
      if (treeColumn.getData().equals(MyXViewerFactory.Run_Col)) {
         setRun((ISomeTask) treeItem.getData(), !isRun((ISomeTask) treeItem.getData()));
         update(treeItem.getData(), null);
         return true;
      } else {
         return super.handleLeftClickInIconArea(treeColumn, treeItem);
      }
   }

}
