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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * @author Donald G. Dunne
 */
public class XViewerTextFilter extends ViewerFilter {

   private final XViewer xViewer;
   //   private ITableLabelProvider labelProv;
   private Pattern textPattern;
   private Matcher matcher;
   private final Map<String, Pattern> colIdToPattern = new HashMap<String, Pattern>();

   public XViewerTextFilter(XViewer xViewer) {
      this.xViewer = xViewer;
   }

   /**
    * Setup all patterns for text and column text filters
    */
   public void update() {
      // Update text filter pattern
      if (xViewer.getCustomizeMgr().getFilterText() == null || xViewer.getCustomizeMgr().getFilterText().equals("")) {
         textPattern = null;
      } else {
         textPattern =
               Pattern.compile(xViewer.getCustomizeMgr().getFilterText(), Pattern.LITERAL | Pattern.CASE_INSENSITIVE);
      }
      // Update column filter patterns
      colIdToPattern.clear();
      for (String colId : xViewer.getCustomizeMgr().getColumnFilterData().getColIds()) {
         String colFilterText = xViewer.getCustomizeMgr().getColumnFilterText(colId);
         if (colFilterText != null) {
            boolean isNot = colFilterText.startsWith("!");
            if (isNot) {
               colFilterText = colFilterText.replaceFirst("^!", "");
            }
            // Handle != case  ^(.(?<!big))*$
            if (isNot) {
               if (colFilterText.equals("")) {
                  colIdToPattern.put(colId, NOT_EMPTY_STR_PATTERN);
               } else {
                  colIdToPattern.put(colId, Pattern.compile("^(.(?<!" + colFilterText + "))*$",
                        Pattern.CASE_INSENSITIVE));
               }
            }
            // Handle normal case
            else {
               if (colFilterText.equals("")) {
                  colIdToPattern.put(colId, EMPTY_STR_PATTERN);
               } else {
                  colIdToPattern.put(colId, Pattern.compile(
                        xViewer.getCustomizeMgr().getColumnFilterData().getFilterText(colId), Pattern.CASE_INSENSITIVE));
               }
            }
         }
      }
   }
   private static final Pattern EMPTY_STR_PATTERN = Pattern.compile("");
   private static final Pattern NOT_EMPTY_STR_PATTERN = Pattern.compile("^.+$");

   @Override
   public boolean select(Viewer viewer, Object parentElement, Object element) {
      if (textPattern == null && colIdToPattern.size() == 0) return true;
      //      if (labelProv == null) labelProv = (ITableLabelProvider) xViewer.getLabelProvider();
      boolean match = true;
      // Must match all column filters or don't show
      for (String filteredColId : xViewer.getCustomizeMgr().getColumnFilterData().getColIds()) {
         XViewerColumn xCol = xViewer.getCustomizeMgr().getCurrentTableColumn(filteredColId);
         if (xCol.isShow()) {
            // Check column filters
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
         }
      }
      if (!match) return false;

      // Must match at least one column for filter text
      if (textPattern == null) {
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
                  if (matcher.find()) return true;
               }
            }
         }
      }

      return false;
   }

}
