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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.nebula.widgets.xviewer.core.util.Strings;
import org.eclipse.nebula.widgets.xviewer.core.util.XmlUtil;

/**
 * Storage for column filters
 *
 * @author Donald G. Dunne
 */
public class ColumnFilterData {

   private static final String COLUMN_FILTER_TAG = "xColFilter"; //$NON-NLS-1$
   private static final String COLUMN_ID_TAG = "id"; //$NON-NLS-1$
   private static final String FILTER_TEXT_TAG = "str"; //$NON-NLS-1$
   private static final String DATE_FILTER_TYPE_TAG = "dateType"; //$NON-NLS-1$
   private static final String DATE1_FILTER_TAG = "date1"; //$NON-NLS-1$
   private static final String DATE2_FILTER_TAG = "date2"; //$NON-NLS-1$
   private final Map<String, String> colIdToFilterText = new HashMap<String, String>();
   private final Map<String, ColumnDateFilter> colIdToDateFilter = new HashMap<String, ColumnDateFilter>();
   private static Pattern p = Pattern.compile("<" + COLUMN_FILTER_TAG + ">(.*?)</" + COLUMN_FILTER_TAG + ">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

   public String getFilterText(String colId) {
      return colIdToFilterText.get(colId);
   }

   public Set<String> getColIds() {
      Set<String> colIds = new HashSet<String>(colIdToFilterText.keySet());
      colIds.addAll(colIdToDateFilter.keySet());
      return colIds;
   }

   public void setFilterText(String colId, String filterText) {
      colIdToFilterText.put(colId, filterText);
   }

   public void clear() {
      colIdToFilterText.clear();
      colIdToDateFilter.clear();
   }

   public void removeFilterText(String colId) {
      colIdToFilterText.remove(colId);
   }

   public String getXml() {
      StringBuilder sb = new StringBuilder();
      for (String colId : getColIds()) {
         StringBuilder innerSb = new StringBuilder();
         innerSb.append(XmlUtil.addTagData(COLUMN_ID_TAG, colId));
         String filterText = colIdToFilterText.get(colId);
         if (Strings.isValid(filterText)) {
            innerSb.append(XmlUtil.addTagData(FILTER_TEXT_TAG, filterText));
         }
         ColumnDateFilter dateFilter = colIdToDateFilter.get(colId);
         if (dateFilter != null) {
            innerSb.append(XmlUtil.addTagData(DATE_FILTER_TYPE_TAG, dateFilter.getType().name()));
            Date date1 = dateFilter.getDate1();
            if (date1 != null) {
               innerSb.append(XmlUtil.addTagData(DATE1_FILTER_TAG, String.valueOf(date1.getTime())));
            }
            Date date2 = dateFilter.getDate2();
            if (date2 != null) {
               innerSb.append(XmlUtil.addTagData(DATE2_FILTER_TAG, String.valueOf(date2.getTime())));
            }
         }
         sb.append(XmlUtil.addTagData(COLUMN_FILTER_TAG, innerSb.toString()));
      }
      return sb.toString();
   }

   public void setFromXml(String xml) {
      colIdToFilterText.clear();
      Matcher columnMatch = p.matcher(xml);
      while (columnMatch.find()) {
         String columnXml = columnMatch.group(1);
         String columnId = XmlUtil.getTagData(columnXml, COLUMN_ID_TAG);
         String filterText = XmlUtil.getTagData(columnXml, FILTER_TEXT_TAG);
         if (Strings.isValid(filterText)) {
            colIdToFilterText.put(columnId, filterText);
         }
         String dateFilterTypeStr = XmlUtil.getTagData(columnXml, DATE_FILTER_TYPE_TAG);
         if (Strings.isValid(dateFilterTypeStr)) {
            DateRangeType dateRangeType = DateRangeType.get(dateFilterTypeStr);
            Date date1 = null, date2 = null;
            if (dateRangeType != DateRangeType.None) {
               String date1Str = XmlUtil.getTagData(columnXml, DATE1_FILTER_TAG);
               if (Strings.isValid(date1Str)) {
                  date1 = new Date(Long.valueOf(date1Str));
               }
               String date2Str = XmlUtil.getTagData(columnXml, DATE2_FILTER_TAG);
               if (Strings.isValid(date2Str)) {
                  date2 = new Date(Long.valueOf(date2Str));
               }
            }
            if (date1 != null || date2 != null) {
               colIdToDateFilter.put(columnId, new ColumnDateFilter(dateRangeType, date1, date2));
            }
         }
      }
   }

   public boolean isFiltered() {
      return colIdToFilterText.size() > 0;
   }

   public void setDateFilter(String columnId, DateRangeType dateRangeType, Date date1, Date date2) {
      colIdToDateFilter.put(columnId, new ColumnDateFilter(dateRangeType, date1, date2));
   }

   public ColumnDateFilter getDateFilter(String columnId) {
      return colIdToDateFilter.get(columnId);
   }

   public void removeDateFilter(String columnId) {
      colIdToDateFilter.remove(columnId);
   }

   @Override
   public String toString() {
      return "ColumnFilterData [colIdToFilterText=" + colIdToFilterText + ", colIdToDateFilter=" + colIdToDateFilter + "]";
   }
}
