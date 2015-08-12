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

package org.eclipse.nebula.widgets.xviewer;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLog;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public abstract class XViewerLabelProvider implements ITableLabelProvider, ITableColorProvider, IXViewerLabelProvider {

   private final XViewer viewer;

   // Store index of columnIndex to XViewerColumns to speed up label providing
   private final Map<Integer, XViewerColumn> indexToXViewerColumnMap = new HashMap<Integer, XViewerColumn>();

   @Override
   public XViewerColumn getTreeColumnOffIndex(int columnIndex) {
      if (!indexToXViewerColumnMap.containsKey(columnIndex)) {
         XViewerColumn xViewerColumn = viewer.getXTreeColumn(columnIndex);
         if (xViewerColumn != null) {
            indexToXViewerColumnMap.put(columnIndex, xViewerColumn);
         }
      }
      return indexToXViewerColumnMap.get(columnIndex);
   }

   // When columns get re-ordered, need to clear out this cache so indexing can be re-computed
   @Override
   public void clearXViewerColumnIndexCache() {
      indexToXViewerColumnMap.clear();
   }

   public XViewerLabelProvider(final XViewer viewer) {
      super();
      this.viewer = viewer;
   }

   @Override
   public Image getColumnImage(Object element, int columnIndex) {
      try {
         XViewerColumn xViewerColumn = getTreeColumnOffIndex(columnIndex);
         // If not shown, don't process any further
         if (!xViewerColumn.isShow()) {
            return null;
         }
         if (xViewerColumn instanceof IXViewerValueColumn) {
            Image image = ((IXViewerValueColumn) xViewerColumn).getColumnImage(element, xViewerColumn, columnIndex);
            if (image != null) {
               return image;
            }
         }
         return getColumnImage(element, xViewerColumn, columnIndex);
      } catch (Exception ex) {
         XViewerLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   @Override
   public String getColumnText(Object element, int columnIndex) {
      try {
         XViewerColumn xViewerColumn = getTreeColumnOffIndex(columnIndex);
         // If not shown, don't process any further
         if (!xViewerColumn.isShow()) {
            return "";
         }
         // Check if Pre Computed column
         if (xViewerColumn instanceof IXViewerPreComputedColumn) {
            IXViewerPreComputedColumn preComputedColumn = (IXViewerPreComputedColumn) xViewerColumn;
            Long key = preComputedColumn.getKey(element);
            String cachedValue = xViewerColumn.getPreComputedValue(key);
            String result = ((IXViewerPreComputedColumn) xViewerColumn).getText(element, key, cachedValue);
            return result;
         }
         // First check value column's methods
         if (xViewerColumn instanceof IXViewerValueColumn) {
            String str = ((IXViewerValueColumn) xViewerColumn).getColumnText(element, xViewerColumn, columnIndex);
            if (str != null) {
               return str;
            }
            return "";
         }
         // Return label provider's value
         return getColumnText(element, xViewerColumn, columnIndex);
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   @Override
   public Color getBackground(Object element, int columnIndex) {
      try {
         if (viewer.isSearch()) {
            String text = getColumnText(element, columnIndex);
            if (viewer.searchMatch(text)) {
               return viewer.getSearchMatchColor();
            }
         }

         XViewerColumn xViewerColumn = getTreeColumnOffIndex(columnIndex);
         // If not shown, don't process any further
         if (!xViewerColumn.isShow()) {
            return null;
         }
         if (xViewerColumn instanceof IXViewerValueColumn) {
            Color color = ((IXViewerValueColumn) xViewerColumn).getBackground(element, xViewerColumn, columnIndex);
            if (color != null) {
               return color;
            }
         }
         return getBackground(element, xViewerColumn, columnIndex);
      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }

   @Override
   public Color getForeground(Object element, int columnIndex) {
      try {
         XViewerColumn xViewerColumn = getTreeColumnOffIndex(columnIndex);
         // If not shown, don't process any further
         if (!xViewerColumn.isShow()) {
            return null;
         }
         if (xViewerColumn instanceof IXViewerValueColumn) {
            Color color = ((IXViewerValueColumn) xViewerColumn).getForeground(element, xViewerColumn, columnIndex);
            if (color != null) {
               return color;
            }
         }
         return getForeground(element, xViewerColumn, columnIndex);
      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }

   public Color getBackground(Object element, XViewerColumn xCol, int columnIndex) {
      return null;
   }

   public Color getForeground(Object element, XViewerColumn xCol, int columnIndex) {
      return null;
   }

   public abstract Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) throws Exception;

   @Override
   public abstract String getColumnText(Object element, XViewerColumn xCol, int columnIndex) throws Exception;

   /**
    * Returns the backing data object for operations like sorting
    */
   @Override
   public Object getBackingData(Object element, XViewerColumn xViewerColumn, int columnIndex) throws Exception {
      try {
         // If not shown, don't process any further
         if (!xViewerColumn.isShow()) {
            return "";
         }
         // First check value column's methods
         if (xViewerColumn instanceof IXViewerValueColumn) {
            Object obj = ((IXViewerValueColumn) xViewerColumn).getBackingData(element, xViewerColumn, columnIndex);
            if (obj != null) {
               return obj;
            }
         }
      } catch (Exception ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return null;
   }

   /**
    * Return value between 0..100 and cell will show bar graph shading that portion of the cell
    */
   @Override
   public int getColumnGradient(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      return 0;
   }

}
