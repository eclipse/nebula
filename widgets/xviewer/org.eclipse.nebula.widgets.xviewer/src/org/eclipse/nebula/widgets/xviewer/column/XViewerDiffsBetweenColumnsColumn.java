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
package org.eclipse.nebula.widgets.xviewer.column;

import org.eclipse.nebula.widgets.xviewer.IXViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerComputedColumn;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.swt.SWT;

/**
 * Show if cells from two columns (selected and one to right) are different.
 * 
 * @author Donald G. Dunne
 */
public class XViewerDiffsBetweenColumnsColumn extends XViewerComputedColumn {

   private final static String ID = "ats.computed.diffsBetweenColumns";

   public XViewerDiffsBetweenColumnsColumn() {
      super(ID, "Diffs Between Columns", 30, SWT.LEFT, false, SortDataType.String, false,
         "Shows if cells of selected column and one to right are different");
   }

   private XViewerDiffsBetweenColumnsColumn(String id) {
      super(id, "Diffs Between Columns", 30, SWT.LEFT, false, SortDataType.String, false,
         "Shows if cells of two columns and one to right are different");
   }

   @SuppressWarnings("unused")
   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) throws XViewerException {
      if (sourceXViewerColumn == null) {
         return String.format("Source column not found for " + id + ".  Delete column and re-create.");
      }
      try {
         int sourceColumnNum = xViewer.getCustomizeMgr().getColumnNumFromXViewerColumn(sourceXViewerColumn);
         String sourceColumnStr =
            ((IXViewerLabelProvider) xViewer.getLabelProvider()).getColumnText(element, sourceColumnNum);
         int nextColumnNum = sourceColumnNum + 1;
         String nextColumnStr =
            ((IXViewerLabelProvider) xViewer.getLabelProvider()).getColumnText(element, nextColumnNum);
         if (sourceColumnStr == null && nextColumnStr == null) {
            return "same";
         } else if (sourceColumnStr == null) {
            return "different - left == null";
         } else if (nextColumnStr == null) {
            return "different - right == null";
         }
         return (sourceColumnStr.equals(nextColumnStr)) ? "same" : String.format("different [%s][%s]", sourceColumnStr,
            nextColumnStr);
      } catch (Exception ex) {
         return ex.getLocalizedMessage();
      }
   }

   @Override
   public boolean isApplicableFor(XViewerColumn xViewerColumn) {
      return true;
   }

   @Override
   public String getName() {
      if (sourceXViewerColumn == null) {
         return "Diffs Between Columns";
      }
      return "Diffs Between Column " + sourceXViewerColumn.getName() + " and one to right";
   }

   @Override
   public XViewerDiffsBetweenColumnsColumn copy() {
      XViewerDiffsBetweenColumnsColumn col = new XViewerDiffsBetweenColumnsColumn();
      col.setXViewer(getXViewer());
      col.setSourceXViewerColumn(getSourceXViewerColumn());
      return col;
   }

   @Override
   public String getId() {
      if (sourceXViewerColumn == null) {
         return ID;
      }
      return ID + "(" + sourceXViewerColumn.getId() + ")";
   }

   @Override
   public boolean isApplicableFor(String storedId) {
      return storedId.startsWith(ID);
   }

   @Override
   public XViewerComputedColumn createFromStored(XViewerColumn storedColumn) {
      return new XViewerDiffsBetweenColumnsColumn(storedColumn.getId());
   }
}
