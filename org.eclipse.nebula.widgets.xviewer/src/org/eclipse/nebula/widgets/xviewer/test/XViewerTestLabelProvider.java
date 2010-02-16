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
package org.eclipse.nebula.widgets.xviewer.test;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLib;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * Example implementation for XViewerTest XViewer
 * 
 * @author Donald G. Dunne
 */
public class XViewerTestLabelProvider extends XViewerLabelProvider {
   Font font = null;
   private final XViewerTest xViewerTest;

   public XViewerTestLabelProvider(XViewerTest xViewerTest) {
      super(xViewerTest);
      this.xViewerTest = xViewerTest;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) {
      if (element instanceof String) {
         if (columnIndex == 1)
            return (String) element;
         else
            return "";
      }
      IXViewerTestTask task = ((IXViewerTestTask) element);
      if (task == null) return "";
      if (xCol.equals(XViewerTestFactory.Run_Col)) return String.valueOf(xViewerTest.isRun(task));
      if (xCol.equals(XViewerTestFactory.Name_Col)) return task.getId();
      if (xCol.equals(XViewerTestFactory.Schedule_Time)) return task.getStartTime();
      if (xCol.equals(XViewerTestFactory.Run_Db)) return task.getRunDb().name();
      if (xCol.equals(XViewerTestFactory.Task_Type)) return task.getTaskType().name();
      if (xCol.equals(XViewerTestFactory.Description)) return task.getDescription();
      if (xCol.equals(XViewerTestFactory.Category)) return task.getCategory();
      if (xCol.equals(XViewerTestFactory.Notification)) return task.getEmailAddress();
      if (xCol.equals(XViewerTestFactory.Last_Run_Date)) return task.getLastRunDateStr();
      if (xCol.equals(XViewerTestFactory.Completed_Col)) return String.valueOf(task.getPercentComplete());
      return "unhandled column";
   }

   public void dispose() {
      if (font != null) font.dispose();
      font = null;
   }

   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   public void addListener(ILabelProviderListener listener) {
   }

   public void removeListener(ILabelProviderListener listener) {
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      if (xCol.equals(XViewerTestFactory.Run_Col)) {
         return xViewerTest.isRun((IXViewerTestTask) element) ? XViewerLib.getImage("chkbox_enabled.gif") : XViewerLib.getImage("chkbox_disabled.gif");
      }
      if (xCol.equals(XViewerTestFactory.Name_Col) && xViewerTest.isScheduled((IXViewerTestTask) element)) {
         return XViewerLib.getImage("clock.gif");
      }
      return null;
   }

   @Override
   public Color getBackground(Object element, int columnIndex) {
      return super.getBackground(element, columnIndex);
   }

   @Override
   public int getColumnGradient(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      if (!(element instanceof IXViewerTestTask)) return 0;
      IXViewerTestTask task = ((IXViewerTestTask) element);
      if (xCol.equals(XViewerTestFactory.Completed_Col)) {
         return task.getPercentComplete();
      }
      return 0;
   }

}
