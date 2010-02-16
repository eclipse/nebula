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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.nebula.widgets.xviewer.Activator;
import org.eclipse.nebula.widgets.xviewer.util.internal.OverlayUtil;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLib;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLog;
import org.eclipse.nebula.widgets.xviewer.util.internal.OverlayUtil.Location;
import org.eclipse.swt.graphics.Image;

/**
 * Single customization object storing name, id, sort, filter and column filter
 * 
 * @author Donald G. Dunne
 */
public class CustomizeData {

   private String guid = XViewerLib.generateGuidStr();
   private String name;
   private String nameSpace;
   private boolean personal = false;
   protected SortingData sortingData = new SortingData(this);
   protected FilterData filterData = new FilterData();
   protected ColumnFilterData columnFilterData = new ColumnFilterData();

   /**
    * @return the columnFilterData
    */
   public ColumnFilterData getColumnFilterData() {
      return columnFilterData;
   }

   protected ColumnData columnData = new ColumnData();
   private final Map<String, Image> imageMap = new HashMap<String, Image>();

   public CustomizeData() {
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
      if (name.equals(CustomizeManager.TABLE_DEFAULT_LABEL) || name.equals(CustomizeManager.CURRENT_LABEL)) {
         return XViewerLib.getImage("customize.gif");
      }
      String index = "" + personal + isDefault;
      if (imageMap.containsKey(index)) return imageMap.get(index);
      Image image = XViewerLib.getImage("customize.gif");
      if (!personal) image =
            new OverlayUtil(image, XViewerLib.getImageDescriptor("customizeG.gif"), Location.BOT_RIGHT).createImage();
      if (isDefault) image =
            new OverlayUtil(image, XViewerLib.getImageDescriptor("customizeD.gif"), Location.TOP_RIGHT).createImage();
      imageMap.put(index, image);
      return image;
   }

   public String getXml(boolean visibleColumnsOnly) {
      StringBuffer sb =
            new StringBuffer(
                  "<XTreeProperties name=\"" + name + "\" namespace=\"" + nameSpace + "\" guid=\"" + guid + "\">");
      sb.append(sortingData.getXml());
      sb.append(filterData.getXml());
      sb.append(columnFilterData.getXml());
      sb.append(columnData.getXml(visibleColumnsOnly));
      sb.append("</XTreeProperties>");
      return sb.toString();
   }

   private static Pattern pattern = Pattern.compile("name=\"(.*?)\".*?namespace=\"(.*?)\".*?guid=\"(.*?)\"");

   public void setFromXml(String xml) {
      Matcher m = pattern.matcher(xml);
      if (m.find()) {
         name = m.group(1);
         nameSpace = m.group(2).intern();
         guid = m.group(3);
      } else {
         name = "Invalid customize format for " + xml.substring(0, 50);
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
      return "[" + name + "][" + nameSpace + "][" + guid + "][" + columnData + "][" + filterData + "][" + columnFilterData + "][" + sortingData + "]";
   }

   public boolean isPersonal() {
      return personal;
   }

   /**
    * @param personal the personal to set
    */
   public void setPersonal(boolean personal) {
      this.personal = personal;
   }

   /**
    * @return the columnData
    */
   public ColumnData getColumnData() {
      return columnData;
   }

   /**
    * @return the filterData
    */
   public FilterData getFilterData() {
      return filterData;
   }

   /**
    * @return the sortingData
    */
   public SortingData getSortingData() {
      return sortingData;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * @return the nameSpace
    */
   public String getNameSpace() {
      return nameSpace;
   }

   /**
    * @param nameSpace the nameSpace to set
    */
   public void setNameSpace(String nameSpace) {
      this.nameSpace = nameSpace;
   }

   public String getGuid() {
      return guid;
   }

   /**
    * @param guid the guid to set
    */
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
}
