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

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.xviewer.Activator;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLib;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLog;
import org.eclipse.swt.graphics.Image;

/**
 * Single customization object storing name, id, sort, filter and column filter
 * 
 * @author Donald G. Dunne
 */
public class CustomizeData implements Comparable<CustomizeData> {

   private String guid = XViewerLib.generateGuidStr();
   private String name;
   private String nameSpace;
   private boolean personal = false;
   protected SortingData sortingData = new SortingData(this);
   protected FilterData filterData = new FilterData();
   protected ColumnFilterData columnFilterData = new ColumnFilterData();

   public ColumnFilterData getColumnFilterData() {
      return columnFilterData;
   }

   protected ColumnData columnData = new ColumnData();

   public CustomizeData() {
      // do nothing
   }

   public boolean isTableDefaultCustData() {
      return name.equals(CustomizeManager.TABLE_DEFAULT_LABEL);
   }

   public boolean isCurrentTableCustData() {
      return name.equals(CustomizeManager.CURRENT_LABEL);
   }

   public CustomizeData(String xml) {
      this();
      setFromXml(xml);
   }

   public void resetGuid() {
      guid = XViewerLib.generateGuidStr();
   }

   public Image getImage(boolean isDefault) {
      Image image = XViewerLib.getImage("customize.gif"); //$NON-NLS-1$
      if (!personal && isDefault) {
         image = XViewerLib.getImage("customizeSharedDefault.gif"); //$NON-NLS-1$
      } else if (!personal) {
         image = XViewerLib.getImage("customizeShared.gif"); //$NON-NLS-1$
      } else if (isDefault) {
         image = XViewerLib.getImage("customizeDefault.gif"); //$NON-NLS-1$
      }
      return image;
   }

   public ImageDescriptor getImageDescriptor(boolean isDefault) {
      ImageDescriptor image = XViewerLib.getImageDescriptor("customize.gif"); //$NON-NLS-1$
      if (!personal && isDefault) {
         image = XViewerLib.getImageDescriptor("customizeSharedDefault.gif"); //$NON-NLS-1$
      } else if (!personal) {
         image = XViewerLib.getImageDescriptor("customizeShared.gif"); //$NON-NLS-1$
      } else if (isDefault) {
         image = XViewerLib.getImageDescriptor("customizeDefault.gif"); //$NON-NLS-1$
      }
      return image;
   }

   public String getXml(boolean visibleColumnsOnly) {
      StringBuffer sb =
         new StringBuffer(
            "<XTreeProperties name=\"" + name + "\" namespace=\"" + nameSpace + "\" guid=\"" + guid + "\">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      sb.append(sortingData.getXml());
      sb.append(filterData.getXml());
      sb.append(columnFilterData.getXml());
      sb.append(columnData.getXml(visibleColumnsOnly));
      sb.append("</XTreeProperties>"); //$NON-NLS-1$
      return sb.toString();
   }

   private static Pattern pattern = Pattern.compile("name=\"(.*?)\".*?namespace=\"(.*?)\".*?guid=\"(.*?)\""); //$NON-NLS-1$

   public void setFromXml(String xml) {
      Matcher m = pattern.matcher(xml);
      if (m.find()) {
         setName(m.group(1));
         setNameSpace(m.group(2));
         guid = m.group(3);
      } else {
         setName("Invalid customize format for " + xml.substring(0, 50)); //$NON-NLS-1$
         XViewerLog.log(Activator.class, Level.SEVERE, new IllegalStateException(name));
         return;
      }
      sortingData.setFromXml(xml);
      filterData.setFromXml(xml);
      columnData.setFromXml(xml);
      columnFilterData.setFromXml(xml);
   }

   @Override
   public String toString() {
      return "[" + name + "][" + nameSpace + "][" + guid + "][" + columnData + "][" + filterData + "][" + columnFilterData + "][" + sortingData + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
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
      this.name = XViewerLib.intern(name);
   }

   public String getNameSpace() {
      return nameSpace;
   }

   public void setNameSpace(String nameSpace) {
      this.nameSpace = XViewerLib.intern(nameSpace);
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

}
