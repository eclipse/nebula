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

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.example.images.MyImageCache;
import org.eclipse.nebula.widgets.xviewer.example.model.ISomeTask;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * Example implementation for XViewerTest XViewer
 * 
 * @author Donald G. Dunne
 */
public class MyXViewerLabelProvider extends XViewerLabelProvider {
   private final MyXViewer xViewerTest;

   public MyXViewerLabelProvider(MyXViewer xViewerTest) {
      super(xViewerTest);
      this.xViewerTest = xViewerTest;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) {
      if (element instanceof String) {
         if (columnIndex == 1) {
            return (String) element;
         } else {
            return "";
         }
      }
      ISomeTask task = ((ISomeTask) element);
      if (task == null) {
         return "";
      }
      if (xCol.equals(MyXViewerFactory.Run_Col)) {
         return String.valueOf(xViewerTest.isRun(task));
      }
      if (xCol.equals(MyXViewerFactory.Name_Col)) {
         return task.getId();
      }
      if (xCol.equals(MyXViewerFactory.Schedule_Time)) {
         return task.getStartTime();
      }
      if (xCol.equals(MyXViewerFactory.Run_Db)) {
         return task.getRunDb().name();
      }
      if (xCol.equals(MyXViewerFactory.Task_Type)) {
         return task.getTaskType().name();
      }
      if (xCol.equals(MyXViewerFactory.Description)) {
         return task.getDescription();
      }
      if (xCol.equals(MyXViewerFactory.Category)) {
         return task.getCategory();
      }
      if (xCol.equals(MyXViewerFactory.Notification)) {
         return task.getEmailAddress();
      }
      if (xCol.equals(MyXViewerFactory.Last_Run_Date)) {
         return task.getLastRunDateStr();
      }
      if (xCol.equals(MyXViewerFactory.Completed_Col)) {
         return String.valueOf(task.getPercentComplete());
      }
      if (xCol.equals(MyXViewerFactory.Long_Column)) {
         return String.valueOf(task.getLongValue());
      }
      return "unhandled column";
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   @Override
   public void addListener(ILabelProviderListener listener) {
      // do nothing
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
      // do nothing
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      if (xCol.equals(MyXViewerFactory.Run_Col)) {
         return xViewerTest.isRun((ISomeTask) element) ? MyImageCache.getImage("chkbox_enabled.gif") : MyImageCache.getImage("chkbox_disabled.gif");
      }
      if (xCol.equals(MyXViewerFactory.Name_Col) && xViewerTest.isScheduled((ISomeTask) element)) {
         return MyImageCache.getImage("clock.gif");
      }
      return null;
   }

   @Override
   public Color getBackground(Object element, int columnIndex) {
      return super.getBackground(element, columnIndex);
   }

   @Override
   public int getColumnGradient(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      if (!(element instanceof ISomeTask)) {
         return 0;
      }
      ISomeTask task = ((ISomeTask) element);
      if (xCol.equals(MyXViewerFactory.Completed_Col)) {
         return task.getPercentComplete();
      }
      return 0;
   }

}
