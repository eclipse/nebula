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
package org.eclipse.nebula.widgets.xviewer.column;

import org.eclipse.nebula.widgets.xviewer.IXViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.XViewerComputedColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;

/**
 * Show if cells from two columns (selected and one to right) are different.
 *
 * @author Donald G. Dunne
 */
public class XViewerDiffsBetweenColumnsColumn extends XViewerComputedColumn {

   private final static String ID = "ats.computed.diffsBetweenColumns"; //$NON-NLS-1$

   public XViewerDiffsBetweenColumnsColumn() {
      this(ID);
   }

   private XViewerDiffsBetweenColumnsColumn(String id) {
      super(id, XViewerText.get("column.diffsBetweenColumns.name"), 30, XViewerAlign.Left, false, SortDataType.String, //$NON-NLS-1$
         false,
         XViewerText.get("column.diffsBetweenColumns.description")); //$NON-NLS-1$
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      if (sourceXViewerColumn == null) {
         return String.format(XViewerText.get("error.no_source_column"), id); //$NON-NLS-1$
      }
      try {
         int sourceColumnNum = xViewer.getCustomizeMgr().getColumnNumFromXViewerColumn(sourceXViewerColumn);
         String sourceColumnStr =
            ((IXViewerLabelProvider) xViewer.getLabelProvider()).getColumnText(element, sourceColumnNum);
         int nextColumnNum = sourceColumnNum + 1;
         String nextColumnStr =
            ((IXViewerLabelProvider) xViewer.getLabelProvider()).getColumnText(element, nextColumnNum);
         if (sourceColumnStr == null && nextColumnStr == null) {
            return XViewerText.get("same"); //$NON-NLS-1$
         } else if (sourceColumnStr == null) {
            return XViewerText.get("different") + " - " + XViewerText.get("column.diffsBetweenColumns.leftNull"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
         } else if (nextColumnStr == null) {
            return XViewerText.get("different") + " - " + XViewerText.get("column.diffsBetweenColumns.rightNull"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
         }
         return (sourceColumnStr.equals(nextColumnStr)) ? XViewerText.get("same") : String.format( //$NON-NLS-1$
            XViewerText.get("different") + " [%s][%s]", sourceColumnStr, //$NON-NLS-1$ //$NON-NLS-2$
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
         return XViewerText.get("column.diffsBetweenColumns.name"); //$NON-NLS-1$
      }
      return String.format(XViewerText.get("column.diffsBetweenColumns.name2"), sourceXViewerColumn.getName()); //$NON-NLS-1$
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
      return ID + "(" + sourceXViewerColumn.getId() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
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
