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
package org.eclipse.nebula.widgets.xviewer.example.styledExample;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerStyledTextLabelProvider;
import org.eclipse.nebula.widgets.xviewer.example.MyXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.example.images.MyImageCache;
import org.eclipse.nebula.widgets.xviewer.example.model.ISomeTask;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * Label provider for example StyledStringLabelProvider implementation
 * 
 * @author Andrew M. Finkbeiner
 */
public class XViewerTestStyledStringLabelProvider extends XViewerStyledTextLabelProvider {
   private final XViewerStyledStringLableProviderTest xViewerTest;

   public XViewerTestStyledStringLabelProvider(XViewerStyledStringLableProviderTest xViewerTest) {
      super(xViewerTest);
      this.xViewerTest = xViewerTest;
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

   @SuppressWarnings("unused")
   @Override
   public Color getBackground(Object element, XViewerColumn viewerColumn, int columnIndex) throws XViewerException {
      return null;
   }

   @SuppressWarnings("unused")
   @Override
   public Font getFont(Object element, XViewerColumn viewerColumn, int columnIndex) throws XViewerException {
      return null;
   }

   @SuppressWarnings("unused")
   @Override
   public Color getForeground(Object element, XViewerColumn viewerColumn, int columnIndex) throws XViewerException {
      return null;
   }

   @SuppressWarnings("unused")
   @Override
   public StyledString getStyledText(Object element, XViewerColumn xCol, int columnIndex) throws XViewerException {
      if (element instanceof String) {
         if (columnIndex == 1) {
            return new StyledString((String) element);
         } else {
            return new StyledString("");
         }
      }
      ISomeTask task = ((ISomeTask) element);
      if (task == null) {
         return new StyledString("");
      }
      if (xCol.equals(MyXViewerFactory.Run_Col)) {
         return new StyledString(String.valueOf(xViewerTest.isRun(task)), StyledString.COUNTER_STYLER);
      }
      if (xCol.equals(MyXViewerFactory.Name_Col)) {
         return new StyledString(task.getId(), StyledString.DECORATIONS_STYLER);
      }
      if (xCol.equals(MyXViewerFactory.Schedule_Time)) {
         return new StyledString(task.getStartTime(), StyledString.QUALIFIER_STYLER);
      }
      if (xCol.equals(MyXViewerFactory.Run_Db)) {
         return new StyledString(task.getRunDb().name(), StyledString.COUNTER_STYLER);
      }
      if (xCol.equals(MyXViewerFactory.Task_Type)) {
         return new StyledString(task.getTaskType().name(), StyledString.DECORATIONS_STYLER);
      }
      if (xCol.equals(MyXViewerFactory.Description)) {
         return new StyledString(task.getDescription(), StyledString.COUNTER_STYLER);
      }
      if (xCol.equals(MyXViewerFactory.Category)) {
         return new StyledString(task.getCategory(), StyledString.DECORATIONS_STYLER);
      }
      if (xCol.equals(MyXViewerFactory.Notification)) {
         return new StyledString(task.getEmailAddress(), StyledString.QUALIFIER_STYLER);
      }
      return new StyledString("unhandled column");
   }

}
