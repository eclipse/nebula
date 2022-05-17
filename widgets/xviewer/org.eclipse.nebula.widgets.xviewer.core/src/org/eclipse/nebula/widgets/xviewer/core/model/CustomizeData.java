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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.nebula.widgets.xviewer.core.util.XViewerUtil;

/**
 * Single customization object storing name, id, sort, filter and column filter
 *
 * @author Donald G. Dunne
 */
public class CustomizeData implements Comparable<CustomizeData> {

   private String guid = XViewerUtil.generateGuidStr();
   private String name;
   private String nameSpace;
   private boolean personal = false;
   protected SortingData sortingData = new SortingData(this);
   protected FilterData filterData = new FilterData();
   protected ColumnFilterData columnFilterData = new ColumnFilterData();
   public static String TABLE_DEFAULT_LABEL = "-- Current Table View --";
   public static String CURRENT_LABEL = "-- Table Default --";

   public ColumnFilterData getColumnFilterData() {
      return columnFilterData;
   }

   protected ColumnData columnData = new ColumnData();

   public CustomizeData() {
      // do nothing
   }

   public boolean isTableDefaultCustData() {
      return name.equals(TABLE_DEFAULT_LABEL);

   }

   public boolean isCurrentTableCustData() {
      return name.equals(CURRENT_LABEL);
   }

   public CustomizeData(String xml) {
      this();
      setFromXml(xml);
   }

   public void resetGuid() {
      guid = XViewerUtil.generateGuidStr();
   }

   public String getXml(boolean visibleColumnsOnly) {
      StringBuilder sb = new StringBuilder(
         "<XTreeProperties name=\"" + name + "\" namespace=\"" + nameSpace + "\" guid=\"" + guid + "\">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      sb.append("<personal>");
      sb.append(personal);
      sb.append("</personal>");
      sb.append(sortingData.getXml());
      sb.append(filterData.getXml());
      sb.append(columnFilterData.getXml());
      sb.append(columnData.getXml(visibleColumnsOnly));
      sb.append("</XTreeProperties>"); //$NON-NLS-1$
      return sb.toString();
   }

   private static Pattern pattern = Pattern.compile("name=\"(.*?)\".*?namespace=\"(.*?)\".*?guid=\"(.*?)\""); //$NON-NLS-1$
   private static Pattern personalPattern = Pattern.compile("<personal>(.*?)</personal>"); //$NON-NLS-1$

   public void setFromXml(String xml) {
      Matcher m = pattern.matcher(xml);
      if (m.find()) {
         setName(m.group(1));
         setNameSpace(m.group(2));
         guid = m.group(3);
      } else {
         String name2 = "Invalid customize format for " + xml.substring(0, 50);
         setName(name2); //$NON-NLS-1$
         return;
      }
      m = personalPattern.matcher(xml);
      if (m.find()) {
         personal = m.group(1).equals("true");
      }
      sortingData.setFromXml(xml);
      filterData.setFromXml(xml);
      columnData.setFromXml(xml);
      columnFilterData.setFromXml(xml);
   }

   public boolean isPersonal() {
      return personal;
   }

   public void setPersonal(boolean personal) {
      this.personal = personal;
   }

   public ColumnData getColumnData() {
      return columnData;
   }

   public FilterData getFilterData() {
      return filterData;
   }

   public SortingData getSortingData() {
      return sortingData;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = XViewerUtil.intern(name);
   }

   public String getNameSpace() {
      return nameSpace;
   }

   public void setNameSpace(String nameSpace) {
      this.nameSpace = XViewerUtil.intern(nameSpace);
   }

   public String getGuid() {
      return guid;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   @Override
   public boolean equals(Object obj) {
      return (obj instanceof CustomizeData) && ((CustomizeData) obj).getGuid().equals(guid);
   }

   @Override
   public int hashCode() {
      return guid.hashCode();
   }

   @Override
   public int compareTo(CustomizeData custData) {
      return getName().compareToIgnoreCase(custData.getName());
   }

   @Override
   public String toString() {
      return "CustomizeData [guid=" + guid + ", name=" + name + ", nameSpace=" + nameSpace + ", personal=" + personal + ", \n\n" + sortingData + ", \n\n" + filterData + ", \n\n" + columnFilterData + ", \n\n" + columnData + "]";
   }

}
