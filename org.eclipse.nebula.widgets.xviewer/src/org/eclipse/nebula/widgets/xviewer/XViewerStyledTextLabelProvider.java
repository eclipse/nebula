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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLog;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public abstract class XViewerStyledTextLabelProvider extends StyledCellLabelProvider {

   private final XViewer viewer;

   // Store index of columnIndex to XViewerColumns to speed up label providing
   private final Map<Integer, XViewerColumn> indexToXViewerColumnMap = new HashMap<Integer, XViewerColumn>();

   private XViewerColumn getTreeColumnOffIndex(int columnIndex) {
      if (!indexToXViewerColumnMap.containsKey(columnIndex)) {
         XViewerColumn xViewerColumn = viewer.getXTreeColumn(columnIndex);
         if (xViewerColumn != null) {
            indexToXViewerColumnMap.put(columnIndex, xViewerColumn);
         }
      }
      return indexToXViewerColumnMap.get(columnIndex);
   }

   // When columns get re-ordered, need to clear out this cache so indexing can
   // be re-computed
   public void clearXViewerColumnIndexCache() {
      indexToXViewerColumnMap.clear();
   }

   /**
    * Creates a {@link XViewerStyledTextLabelProvider} that delegates the requests for the styled labels and the images
    * to a {@link IStyledLabelProvider}.
    * 
    * @param labelProvider the label provider that provides the styled labels and the images
    */
   public XViewerStyledTextLabelProvider(XViewer viewer) {
      this.viewer = viewer;
   }

   @Override
   public void update(ViewerCell cell) {
      Object element = cell.getElement();

      StyledString styledString = getStyledText(element, cell.getColumnIndex());
      String newText = styledString.toString();

      StyleRange[] oldStyleRanges = cell.getStyleRanges();
      StyleRange[] newStyleRanges = isOwnerDrawEnabled() ? styledString.getStyleRanges() : null;

      if (!Arrays.equals(oldStyleRanges, newStyleRanges)) {
         cell.setStyleRanges(newStyleRanges);
         //         if (cell.getText().equals(newText)) {
         //            // make sure there will be a refresh from a change
         //            cell.setText(""); //$NON-NLS-1$
         //         }
      }

      cell.setText(newText);
      cell.setImage(getColumnImage(element, cell.getColumnIndex()));
      cell.setFont(getFont(element, cell.getColumnIndex()));
      cell.setForeground(getForeground(element, cell.getColumnIndex()));
      cell.setBackground(getBackground(element, cell.getColumnIndex()));

      // no super call required. changes on item will trigger the refresh.
   }

   /**
    * @param element
    * @param columnIndex
    * @return
    */
   private Font getFont(Object element, int columnIndex) {
      try {
         XViewerColumn xViewerColumn = getTreeColumnOffIndex(columnIndex);
         // If not shown, don't process any further
         if (!xViewerColumn.isShow()) return null;
         if (xViewerColumn instanceof XViewerValueColumn) {
            return ((XViewerValueColumn) xViewerColumn).getFont(element, xViewerColumn, columnIndex);
         } else {
            return getFont(element, xViewerColumn, columnIndex);
         }
      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }

   public Image getColumnImage(Object element, int columnIndex) {
      try {
         XViewerColumn xViewerColumn = getTreeColumnOffIndex(columnIndex);
         // If not shown, don't process any further
         if (!xViewerColumn.isShow()) return null;
         if (xViewerColumn != null) {
            if (xViewerColumn instanceof XViewerValueColumn) {
               Image image = ((XViewerValueColumn) xViewerColumn).getColumnImage(element, xViewerColumn, columnIndex);
               if (image != null) return image;
            }
            return getColumnImage(element, xViewerColumn, columnIndex);
         }
      } catch (Exception ex) {
         XViewerLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   StyledString getStyledText(Object element, int columnIndex) {
      try {
         XViewerColumn xViewerColumn = getTreeColumnOffIndex(columnIndex);
         // If not shown, don't process any further
         if (!xViewerColumn.isShow()) return new StyledString("");
         if (xViewerColumn instanceof XViewerValueColumn) {
            return ((XViewerValueColumn) xViewerColumn).getStyledText(element, xViewerColumn, columnIndex);
         }
         return getStyledText(element, xViewerColumn, columnIndex);
      } catch (Exception ex) {
         return new StyledString(XViewerCells.getCellExceptionString(ex));
      }
   }

   private Color getBackground(Object element, int columnIndex) {
      try {

         XViewerColumn xViewerColumn = getTreeColumnOffIndex(columnIndex);

         if (viewer.isSearch()) {
            StyledString text = getStyledText(element, columnIndex);
            if (viewer.searchMatch(text.getString())) {
               return viewer.getSearchMatchColor();
            }
         }
         // If not shown, don't process any further
         if (!xViewerColumn.isShow()) return null;
         if (xViewerColumn instanceof XViewerValueColumn) {
            return ((XViewerValueColumn) xViewerColumn).getBackground(element, xViewerColumn, columnIndex);
         } else {
            return getBackground(element, xViewerColumn, columnIndex);
         }
      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }

   /**
    * @param element
    * @param viewerColumn
    * @param columnIndex
    * @return
    */

   private Color getForeground(Object element, int columnIndex) {
      try {
         XViewerColumn xViewerColumn = getTreeColumnOffIndex(columnIndex);
         // If not shown, don't process any further
         if (!xViewerColumn.isShow()) return null;
         if (xViewerColumn instanceof XViewerValueColumn) {
            return ((XViewerValueColumn) xViewerColumn).getForeground(element, xViewerColumn, columnIndex);
         } else {
            return getForeground(element, xViewerColumn, columnIndex);
         }
      } catch (Exception ex) {
         // do nothing
      }
      return null;
   }

   public abstract Image getColumnImage(Object element, XViewerColumn xCol, int column) throws Exception;

   public abstract StyledString getStyledText(Object element, XViewerColumn xCol, int column) throws Exception;

   public abstract Color getBackground(Object element, XViewerColumn viewerColumn, int columnIndex) throws Exception;

   public abstract Color getForeground(Object element, XViewerColumn viewerColumn, int columnIndex) throws Exception;

   public abstract Font getFont(Object element, XViewerColumn viewerColumn, int columnIndex) throws Exception;
}
