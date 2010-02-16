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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.util.internal.CollectionsUtil;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLib;
import org.eclipse.nebula.widgets.xviewer.util.internal.XmlUtil;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class XViewerColumn {

   private XViewer xViewer;
   protected String id;
   protected String name = "";
   private String description;
   private boolean multiColumnEditable = false;
   private int width;
   private int align;
   private boolean sortForward = true; // if true, sort alphabetically; else reverse
   private boolean show = true;
   private SortDataType sortDataType = SortDataType.String;
   private String toolTip = "";
   public enum SortDataType {
      Date, Float, Percent, String, String_MultiLine, Boolean, Integer, Paragraph_Number, Check
   };

   public XViewerColumn(String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super();
      this.id = id;
      this.name = name;
      this.width = width;
      this.align = align;
      this.show = show;
      this.sortDataType = sortDataType;
      this.multiColumnEditable = multiColumnEditable;
      this.description = description;
      this.toolTip = this.name;
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerColumn need to extend this method to copy extra stored fields
    * 
    * @param col
    */
   public XViewerColumn copy() {
      return new XViewerColumn(id, name, width, align, show, sortDataType, multiColumnEditable, description);
   }

   public XViewerColumn(XViewer xViewer, String xml) {
      this.xViewer = xViewer;
      setFromXml(xml);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof XViewerColumn) {
         return ((XViewerColumn) obj).getId().equals(id);
      }
      return super.equals(obj);
   }

   @Override
   public int hashCode() {
      return getId().hashCode();
   }

   public static String ID = "id";
   public static String NAME = "name";
   public static String WIDTH = "wdth";
   public static String ALIGN = "algn";
   public static String SORT_FORWARD = "srtFwd";
   public static String SHOW = "show";
   public static String XTREECOLUMN_TAG = "xCol";

   public String toXml() {
      StringBuffer sb = new StringBuffer("<" + XTREECOLUMN_TAG + ">");
      sb.append(XmlUtil.addTagData(ID, id));
      sb.append(XmlUtil.addTagData(NAME, name));
      sb.append(XmlUtil.addTagData(WIDTH, width + ""));
      sb.append(XmlUtil.addTagData(ALIGN, getAlignStoreName(align)));
      sb.append(XmlUtil.addTagData(SORT_FORWARD, sortForward + ""));
      sb.append(XmlUtil.addTagData(SHOW, show + ""));
      sb.append("</" + XTREECOLUMN_TAG + ">");
      return sb.toString();
   }

   public void setFromXml(String xml) {
      id = XmlUtil.getTagData(xml, ID);
      name = XmlUtil.getTagData(xml, NAME);
      width = XmlUtil.getTagIntData(xml, WIDTH);
      align = getAlignStoreValue(XmlUtil.getTagData(xml, ALIGN));
      sortForward = XmlUtil.getTagBooleanData(xml, SORT_FORWARD);
      show = XmlUtil.getTagBooleanData(xml, SHOW);
   }

   public static String getColumnId(String xml) {
      return XmlUtil.getTagData(xml, ID);
   }

   public String getAlignStoreName(int align) {
      if (align == SWT.CENTER)
         return "center";
      else if (align == SWT.RIGHT)
         return "right";
      else
         return "left";
   }

   public int getAlignStoreValue(String str) {
      if (str.equals("center"))
         return SWT.CENTER;
      else if (str.equals("right"))
         return SWT.RIGHT;
      else
         return SWT.LEFT;
   }

   public String getDisplayName() {
      return name + " - " + id + " - width:" + width + " - show:" + show;
   }

   @Override
   public String toString() {
      return "column:[" + name + "][" + id + "][" + width + "][" + show + "][" + align + "]";
   }

   public int getAlign() {
      return align;
   }

   public void setAlign(int align) {
      this.align = align;
   }

   public String getId() {
      return id;
   }

   public int getWidth() {
      return width;
   }

   public XViewer getTreeViewer() {
      return xViewer;
   }

   public boolean isSortForward() {
      return sortForward;
   }

   public void setSortForward(boolean sortForward) {
      this.sortForward = sortForward;
   }

   public void reverseSort() {
      setSortForward(!sortForward);
   }

   public boolean isShow() {
      return show;
   }

   public void setShow(boolean show) {
      this.show = show;
   }

   public String getName() {
      return name;
   }

   public SortDataType getSortDataType() {
      return sortDataType;
   }

   public void setSortDataType(SortDataType sortDataType) {
      this.sortDataType = sortDataType;
   }

   public void setXViewer(XViewer treeViewer) {
      this.xViewer = treeViewer;
   }

   public String getToolTip() {
      return toolTip;
   }

   public void setToolTip(String toolTip) {
      if (toolTip != null) this.toolTip = toolTip;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
      this.toolTip = getName() + ":\n" + getDescription();
   }

   public boolean isMultiColumnEditable() {
      return multiColumnEditable;
   }

   public void setMultiColumnEditable(boolean multiColumnEditable) {
      this.multiColumnEditable = multiColumnEditable;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public boolean isSummable() {
      if (sortDataType == SortDataType.Float || sortDataType == SortDataType.Integer) {
         return true;
      }
      return false;
   }

   public String sumValues(Collection<String> values) {
      if (sortDataType == SortDataType.Float) {
         double sum = 0.0;
         Set<String> exceptions = new HashSet<String>();
         for (String value : values) {
            if (value == null || value.equals("")) continue;
            try {
               sum += new Double(value);
            } catch (Exception ex) {
               exceptions.add(ex.getLocalizedMessage());
            }
         }
         return "Sum: " + XViewerLib.doubleToI18nString(sum) + "\n\nNum Items: " + values.size() + (exceptions.size() > 0 ? "\n\nErrors: " + CollectionsUtil.toString(
               ";", exceptions) : "");
      } else if (sortDataType == SortDataType.Integer) {
         int sum = 0;
         Set<String> exceptions = new HashSet<String>();
         for (String value : values) {
            if (value == null || value.equals("")) continue;
            try {
               sum += new Integer(value);
            } catch (Exception ex) {
               exceptions.add(ex.getLocalizedMessage());
            }
         }
         return "Sum: " + sum + "\n\nNum Items: " + values.size() + (exceptions.size() > 0 ? "\n\nErrors: " + CollectionsUtil.toString(
               ";", exceptions) : "");
      }
      return "Unhandled column type";
   }
}
