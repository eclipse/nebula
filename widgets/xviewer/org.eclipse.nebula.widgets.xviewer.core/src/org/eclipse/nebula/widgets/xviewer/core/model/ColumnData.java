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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides for xml packing/unpacking of columns
 *
 * @author Donald G. Dunne
 */
public class ColumnData {

   List<XViewerColumn> columns = new ArrayList<>();
   Map<String, XViewerColumn> idToColumn = new HashMap<>();

   private static Pattern pattern =
      Pattern.compile("<" + XViewerColumn.XTREECOLUMN_TAG + ">(.*?)</" + XViewerColumn.XTREECOLUMN_TAG + ">"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

   public List<XViewerColumn> setFromXml(String xml) {
      columns.clear();
      Matcher columnMatch = pattern.matcher(xml);
      while (columnMatch.find()) {
         String colXml = columnMatch.group(1);
         XViewerColumn xCol = new XViewerColumn(null, colXml);
         columns.add(xCol);
         idToColumn.put(xCol.getId(), xCol);
      }
      return columns;
   }

   public XViewerColumn getXColumn(String id) {
      return idToColumn.get(id);
   }

   public String getXml(boolean visibleColumnsOnly) {
      StringBuilder sb = new StringBuilder();
      for (XViewerColumn xCol : columns) {
         if (!visibleColumnsOnly || (visibleColumnsOnly && xCol.isShow())) {
            sb.append(xCol.toXml());
         }
      }
      return sb.toString();
   }

   public List<XViewerColumn> getColumns() {
      return columns;
   }

   /**
    * @param columns the columns to set
    */
   public void setColumns(List<XViewerColumn> columns) {
      this.columns = columns;
      idToColumn.clear();
      for (XViewerColumn xCol : columns) {
         idToColumn.put(xCol.getId(), xCol);
      }
   }

   @Override
   public String toString() {
      return "ColumnData [columns=" + columns + "]";
   }

}
