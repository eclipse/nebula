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
package org.eclipse.nebula.widgets.xviewer;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * This class provides XViewerColumns another mechanism to providing text, background color and foreground color instead
 * of through the XViewerLabelProvider. XViewerLabelProvider will call the provided routines to try to populate the
 * column and only if null/empty string will default to the normal label provider methods. This allows for XViwerColumns
 * to be shared between XViewers and/or have XViewerColumns provide their own values through the same class that defines
 * the column.
 *
 * @author Donald G. Dunne
 */
public class XViewerValueColumn extends XViewerColumn implements IXViewerValueColumn {

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public XViewerValueColumn copy() {
      XViewerValueColumn copyColumn = new XViewerValueColumn(getId(), getName(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable(), getDescription());
      copyColumn.setSortForward(isSortForward());
      return copyColumn;
   }

   public XViewerValueColumn(String id, String name, int width, XViewerAlign align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   public XViewerValueColumn(XViewer viewer, String xml) {
      super(viewer, xml);
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn column, int columnIndex) throws XViewerException {
      return null;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) throws XViewerException {
      return "unhandled";
   }

   @Override
   public Color getBackground(Object element, XViewerColumn xCol, int columnIndex) throws XViewerException {
      return null;
   }

   @Override
   public Color getForeground(Object element, XViewerColumn xCol, int columnIndex) throws XViewerException {
      return null;
   }

   //This method will only be called be the XViewerStyledTextLabelProvider
   @Override
   public StyledString getStyledText(Object element, XViewerColumn viewerColumn, int columnIndex) throws XViewerException {
      return new StyledString(getColumnText(element, viewerColumn, columnIndex));
   }

   //This method will only be called be the XViewerStyledTextLabelProvider
   @Override
   public Font getFont(Object element, XViewerColumn viewerColumn, int columnIndex) throws XViewerException {
      return null;
   }

   @Override
   public Object getBackingData(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      return null;
   }

}
