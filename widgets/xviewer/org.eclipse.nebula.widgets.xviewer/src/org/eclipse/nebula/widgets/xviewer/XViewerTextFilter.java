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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.nebula.widgets.xviewer.core.model.ColumnDateFilter;
import org.eclipse.nebula.widgets.xviewer.core.model.DateRangeType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class XViewerTextFilter extends ViewerFilter {

   protected final XViewer xViewer;
   protected Pattern textPattern;
   protected Matcher matcher;
   protected final Map<String, Pattern> colIdToPattern = new HashMap<String, Pattern>();
   protected final Map<String, ColumnDateFilter> colIdToDateFilter = new HashMap<String, ColumnDateFilter>();
   protected static final Pattern EMPTY_STR_PATTERN = Pattern.compile("");
   protected static final Pattern NOT_EMPTY_STR_PATTERN = Pattern.compile("^.+$");
   private final Set<Object> parentMatches = new HashSet<Object>();

   public XViewerTextFilter(XViewer xViewer) {
      this.xViewer = xViewer;
   }

   /**
    * Setup all patterns for text and column text filters
    */
   public void update() {
      parentMatches.clear();
      // Update text filter pattern
      if (!Strings.isValid(xViewer.getCustomizeMgr().getFilterText())) {
         textPattern = null;
      } else {
         int flags = Pattern.CASE_INSENSITIVE;
         if (!xViewer.getCustomizeMgr().isFilterTextRegularExpression()) {
            flags = Pattern.LITERAL | flags;
         }
         textPattern = Pattern.compile(xViewer.getCustomizeMgr().getFilterText(), flags);
      }
      // Update column filter patterns
      colIdToPattern.clear();
      colIdToDateFilter.clear();
      for (String colId : xViewer.getCustomizeMgr().getColumnFilterData().getColIds()) {
         String colFilterText = xViewer.getCustomizeMgr().getColumnFilterText(colId);
         if (colFilterText != null) {
            boolean isWrapped = (colFilterText.matches("^\\(.*\\)$"));
            boolean isNot;
            if (isWrapped) {
               colFilterText = colFilterText.substring(1, colFilterText.length() - 1);
            }
            isNot = colFilterText.startsWith("!");
            if (isNot) {
               colFilterText = colFilterText.replaceFirst("^!", "");
            }
            colFilterText = Pattern.quote(colFilterText);
            // Handle != case  ^(.(?<!big))*$
            if (isNot) {
               if (colFilterText.equals("")) {
                  colIdToPattern.put(colId, NOT_EMPTY_STR_PATTERN);
               } else {
                  colIdToPattern.put(colId,
                     Pattern.compile("^(.(?<!" + colFilterText + "))*$", Pattern.CASE_INSENSITIVE));
               }
            }
            // Handle normal case
            else {
               if (colFilterText.equals("")) {
                  colIdToPattern.put(colId, EMPTY_STR_PATTERN);
               } else {
                  colIdToPattern.put(colId, Pattern.compile(colFilterText, Pattern.CASE_INSENSITIVE));
               }
            }
         }
         ColumnDateFilter dateFilter = xViewer.getCustomizeMgr().getColumnDateFilter(colId);
         if (dateFilter != null) {
            colIdToDateFilter.put(colId, dateFilter);
         }
      }
   }

   @Override
   public boolean select(Viewer viewer, Object parentElement, Object element) {
      if (textPattern == null && colIdToPattern.isEmpty() && colIdToDateFilter.isEmpty()) {
         return true;
      }
      // If element matches, it's parent is added to this collection; it should always match so get full path shown
      if (parentMatches.contains(element)) {
         if (parentElement != null) {
            parentMatches.add(parentElement);
         }
         return true;
      }
      boolean match = true;
      // Must match all column filters or don't show
      Set<String> colIds = xViewer.getCustomizeMgr().getColumnFilterData().getColIds();
      for (String filteredColId : colIds) {
         XViewerColumn xCol = xViewer.getCustomizeMgr().getCurrentTableColumn(filteredColId);
         if (xCol.isShow()) {
            if (colIdToPattern.keySet().contains(xCol.getId())) {
               String cellStr =
                  xViewer.getColumnText(element, xViewer.getCustomizeMgr().getColumnNumFromXViewerColumn(xCol));
               if (cellStr != null) {
                  matcher = colIdToPattern.get(xCol.getId()).matcher(cellStr);
                  if (!matcher.find()) {
                     return false;
                  }
               }
            }
            if (colIdToDateFilter.containsKey(xCol.getId())) {
               String cellStr =
                  xViewer.getColumnText(element, xViewer.getCustomizeMgr().getColumnNumFromXViewerColumn(xCol));
               if (Strings.isValid(cellStr)) {
                  Date cellDate = XViewerSorter.parseDatePair(cellStr, "").getFirst();
                  if (cellDate != null) {
                     ColumnDateFilter columnDateFilter = colIdToDateFilter.get(xCol.getId());
                     Calendar cellCal = Calendar.getInstance();
                     cellCal.setTime(cellDate);
                     Calendar filterCal = Calendar.getInstance();
                     Date filterDate1 = columnDateFilter.getDate1();
                     filterCal.setTime(filterDate1);
                     DateRangeType rangeType = columnDateFilter.getType();
                     if (rangeType == DateRangeType.Equals_Date) {
                        if (cellCal.get(Calendar.YEAR) != filterCal.get(Calendar.YEAR) || cellCal.get(
                           Calendar.MONTH) != filterCal.get(Calendar.MONTH) || cellCal.get(
                              Calendar.DAY_OF_MONTH) != filterCal.get(Calendar.DAY_OF_MONTH)) {
                           return false;
                        }
                     } else if (rangeType == DateRangeType.After_Date && cellDate.before(filterDate1)) {
                        return false;
                     } else if (rangeType == DateRangeType.Before_Date && cellDate.after(filterDate1)) {
                        return false;
                     } else if (rangeType == DateRangeType.Between_Dates) {
                        if (cellDate.before(filterDate1)) {
                           return false;
                        }
                        Date filterDate2 = columnDateFilter.getDate2();
                        if (cellDate.after(filterDate2)) {
                           return false;
                        }
                     }
                  }
               } else {
                  // Do not show this row if date filter selected and no date is shown
                  return false;
               }
            }
         }
      }
      if (!match) {
         return false;
      }

      // Must match at least one column for filter text
      if (textPattern == null) {
         if (match && parentElement != null) {
            parentMatches.add(parentElement);
         }
         return match;
      }
      if (textPattern != null) {
         for (XViewerColumn xCol : xViewer.getCustomizeMgr().getCurrentTableColumns()) {
            if (xCol.isShow()) {
               // Check text filter
               String cellStr =
                  xViewer.getColumnText(element, xViewer.getCustomizeMgr().getColumnNumFromXViewerColumn(xCol));
               if (cellStr != null) {
                  matcher = textPattern.matcher(cellStr);
                  if (matcher.find()) {
                     if (parentElement != null) {
                        parentMatches.add(parentElement);
                     }
                     return true;
                  }
               }
            }
         }
      }
      return false;
   }

}
