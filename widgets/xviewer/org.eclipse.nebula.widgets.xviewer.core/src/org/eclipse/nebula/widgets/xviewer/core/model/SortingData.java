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
package org.eclipse.nebula.widgets.xviewer.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.nebula.widgets.xviewer.core.util.XViewerUtil;
import org.eclipse.nebula.widgets.xviewer.core.util.XmlUtil;

/**
 * Provides object for storage of sorting data
 *
 * @author Donald G. Dunne
 */
public class SortingData {

   private final static String XTREESORTER_TAG = "xSorter"; //$NON-NLS-1$
   private final static String COL_NAME_TAG = "id"; //$NON-NLS-1$
   private final static String OLD_COL_NAME_TAG = "name"; //$NON-NLS-1$
   private final List<String> sortingIds = new ArrayList<>();
   private final CustomizeData custData;

   public SortingData() {
      this.custData = null;
   }

   public SortingData(CustomizeData custData) {
      this.custData = custData;
   }

   public SortingData(String xml) {
      this.custData = null;
      setFromXml(xml);
   }

   public void clearSorter() {
      sortingIds.clear();
   }

   public boolean isSorting() {
      return sortingIds.size() > 0;
   }

   public List<XViewerColumn> getSortXCols(Map<String, XViewerColumn> oldNameToColumnId) {
      List<XViewerColumn> cols = new ArrayList<>();
      for (String id : getSortingIds()) {
         XViewerColumn xCol = custData.getColumnData().getXColumn(id);
         // For backward compatibility, try to resolve column name
         if (xCol == null) {
            XViewerColumn resolvedCol = oldNameToColumnId.get(id);
            if (resolvedCol != null) {
               xCol = custData.getColumnData().getXColumn(resolvedCol.getId());
            }
         }
         if (xCol != null) {
            cols.add(xCol);
         }
      }
      return cols;
   }

   public void setSortXCols(List<XViewerColumn> sortXCols) {
      sortingIds.clear();
      for (XViewerColumn xCol : sortXCols) {
         sortingIds.add(XViewerUtil.intern(xCol.getId()));
      }
   }

   public String getXml() {
      StringBuilder sb = new StringBuilder("<" + XTREESORTER_TAG + ">"); //$NON-NLS-1$ //$NON-NLS-2$
      // NOTE: Sorting direction is stored as part of the column data
      for (String item : sortingIds) {
         sb.append(XmlUtil.addTagData(COL_NAME_TAG, item));
      }
      sb.append("</" + XTREESORTER_TAG + ">"); //$NON-NLS-1$ //$NON-NLS-2$
      return sb.toString();
   }

   private static Pattern pattern1 = Pattern.compile("<" + COL_NAME_TAG + ">(.*?)</" + COL_NAME_TAG + ">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
   private static Pattern pattern2 = Pattern.compile("<" + OLD_COL_NAME_TAG + ">(.*?)</" + OLD_COL_NAME_TAG + ">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

   public void setFromXml(String xml) {
      // NOTE: Sorting direction is stored as part of the column data
      sortingIds.clear();
      String xmlSortStr = XmlUtil.getTagData(xml, XTREESORTER_TAG);
      Matcher m = pattern1.matcher(xmlSortStr);
      while (m.find()) {
         sortingIds.add(m.group(1));
      }
      Matcher mOld = pattern2.matcher(xmlSortStr);
      while (mOld.find()) {
         sortingIds.add(mOld.group(1));
      }
   }

   public List<String> getSortingIds() {
      return sortingIds;
   }

   public void removeSortingName(String name) {
      this.sortingIds.remove(name);
   }

   public void addSortingName(String name) {
      if (!this.sortingIds.contains(name)) {
         this.sortingIds.add(XViewerUtil.intern(name));
      }
   }

   public void setSortingNames(String... xViewerColumnId) {
      this.sortingIds.clear();
      for (String id : xViewerColumnId) {
         this.sortingIds.add(XViewerUtil.intern(id));
      }
   }

   @Override
   public String toString() {
      return "SortingData [sortIds=" + sortingIds + "]";
   }

}
