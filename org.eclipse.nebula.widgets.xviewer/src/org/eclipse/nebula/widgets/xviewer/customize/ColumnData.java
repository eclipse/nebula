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
package org.eclipse.nebula.widgets.xviewer.customize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;

/**
 * Provides for xml packing/unpacking of columns
 * 
 * @author Donald G. Dunne
 */
public class ColumnData {

   List<XViewerColumn> columns = new ArrayList<XViewerColumn>();
   Map<String, XViewerColumn> idToColumn = new HashMap<String, XViewerColumn>();

   private static Pattern pattern =
         Pattern.compile("<" + XViewerColumn.XTREECOLUMN_TAG + ">(.*?)</" + XViewerColumn.XTREECOLUMN_TAG + ">");

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

   @Override
   public String toString() {
      StringBuffer sb = new StringBuffer("columnData:[");
      for (XViewerColumn xCol : columns) {
         sb.append(xCol);
      }
      sb.append("]");
      return sb.toString();
   }

   public XViewerColumn getXColumn(String id) {
      return idToColumn.get(id);
   }

   public String getXml(boolean visibleColumnsOnly) {
      StringBuffer sb = new StringBuffer();
      for (XViewerColumn xCol : columns) {
         if (!visibleColumnsOnly || (visibleColumnsOnly && xCol.isShow())) {
            sb.append(xCol.toXml());
         }
      }
      return sb.toString();
   }

   /**
    * @return the columns
    */
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

}
