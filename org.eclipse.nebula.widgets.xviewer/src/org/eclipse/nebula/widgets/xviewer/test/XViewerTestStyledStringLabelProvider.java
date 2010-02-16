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
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerStyledTextLabelProvider;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLib;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * Label provider for example StyledStringLabelProvider implementation
 * 
 * @author Andrew M. Finkbeiner
 */
public class XViewerTestStyledStringLabelProvider extends XViewerStyledTextLabelProvider {
   Font font = null;
   private final XViewerStyledStringLableProviderTest xViewerTest;

   public XViewerTestStyledStringLabelProvider(XViewerStyledStringLableProviderTest xViewerTest) {
      super(xViewerTest);
      this.xViewerTest = xViewerTest;
   }

   @Override
   public void dispose() {
      if (font != null) font.dispose();
      font = null;
   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   @Override
   public void addListener(ILabelProviderListener listener) {
   }

   @Override
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
   public Color getBackground(Object element, XViewerColumn viewerColumn, int columnIndex) throws XViewerException {
      return null;
   }

   @Override
   public Font getFont(Object element, XViewerColumn viewerColumn, int columnIndex) throws XViewerException {
      return null;
   }

   @Override
   public Color getForeground(Object element, XViewerColumn viewerColumn, int columnIndex) throws XViewerException {
      return null;
   }

   @Override
   public StyledString getStyledText(Object element, XViewerColumn xCol, int columnIndex) throws XViewerException {
      if (element instanceof String) {
         if (columnIndex == 1)
            return new StyledString((String) element);
         else
            return new StyledString("");
      }
      IXViewerTestTask task = ((IXViewerTestTask) element);
      if (task == null) return new StyledString("");
      if (xCol.equals(XViewerTestFactory.Run_Col)) return new StyledString(String.valueOf(xViewerTest.isRun(task)),
            StyledString.COUNTER_STYLER);
      if (xCol.equals(XViewerTestFactory.Name_Col)) return new StyledString(task.getId(),
            StyledString.DECORATIONS_STYLER);
      if (xCol.equals(XViewerTestFactory.Schedule_Time)) return new StyledString(task.getStartTime(),
            StyledString.QUALIFIER_STYLER);
      if (xCol.equals(XViewerTestFactory.Run_Db)) return new StyledString(task.getRunDb().name(),
            StyledString.COUNTER_STYLER);
      if (xCol.equals(XViewerTestFactory.Task_Type)) return new StyledString(task.getTaskType().name(),
            StyledString.DECORATIONS_STYLER);
      if (xCol.equals(XViewerTestFactory.Description)) return new StyledString(task.getDescription(),
            StyledString.COUNTER_STYLER);
      if (xCol.equals(XViewerTestFactory.Category)) return new StyledString(task.getCategory(),
            StyledString.DECORATIONS_STYLER);
      if (xCol.equals(XViewerTestFactory.Notification)) return new StyledString(task.getEmailAddress(),
            StyledString.QUALIFIER_STYLER);
      return new StyledString("unhandled column");
   }

}
