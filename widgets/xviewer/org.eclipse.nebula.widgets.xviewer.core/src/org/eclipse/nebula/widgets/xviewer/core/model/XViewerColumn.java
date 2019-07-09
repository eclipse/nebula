/*******************************************************************************
 * Copyright (c) 2016 Boeing.
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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.nebula.widgets.xviewer.core.util.CollectionsUtil;
import org.eclipse.nebula.widgets.xviewer.core.util.XViewerUtil;
import org.eclipse.nebula.widgets.xviewer.core.util.XmlUtil;

/**
 * @author Donald G. Dunne
 */
public class XViewerColumn {

   protected String id;
   protected String name = "";
   private String description;
   private boolean multiColumnEditable = false;
   private int width;
   private XViewerAlign align;
   private boolean sortForward = true; // if true, sort alphabetically; else reverse
   private boolean show = true;
   private SortDataType sortDataType = SortDataType.String;
   private String toolTip = "";
   protected Map<Long, String> preComputedValueMap = null;
   private Object xViewer;
   private Long elapsedTime = 0L;

   protected XViewerColumn() {
      super();
   }

   public XViewerColumn(String id, String name, int width, XViewerAlign align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super();
      setId(id);
      setName(name);
      this.width = width;
      this.align = align;
      this.show = show;
      this.sortDataType = sortDataType;
      this.multiColumnEditable = multiColumnEditable;
      setDescription(description);
      setToolTip(this.name);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerColumn need to extend this method to copy extra stored fields
    */
   public XViewerColumn copy() {
      XViewerColumn newXCol = new XViewerColumn();
      copy(this, newXCol);
      return newXCol;
   }

   /**
    * This method should be extended if new column needs to store (and copy) it's own data.
    */
   protected void copy(XViewerColumn fromXCol, XViewerColumn toXCol) {
      toXCol.setAlign(fromXCol.align);
      toXCol.setDescription(fromXCol.description);
      toXCol.setMultiColumnEditable(fromXCol.multiColumnEditable);
      toXCol.setName(fromXCol.name);
      toXCol.setSortDataType(fromXCol.sortDataType);
      toXCol.setSortForward(isSortForward());
      toXCol.setToolTip(fromXCol.toolTip);
      toXCol.setWidth(fromXCol.width);
      toXCol.setShow(fromXCol.show);
      toXCol.setId(fromXCol.id);
   }

   public XViewerColumn(Object xViewer, String xml) {
      setFromXml(xml);
      this.xViewer = xViewer;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof XViewerColumn) {
         return ((XViewerColumn) obj).getId().equals(id);
      } else if (obj instanceof String) {
         return id.equals(obj);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return getId().hashCode();
   }

   public final static String ID = "id";
   public final static String NAME = "name";
   public final static String WIDTH = "wdth";
   public final static String ALIGN = "algn";
   public final static String SORT_FORWARD = "srtFwd";
   public final static String SHOW = "show";
   public final static String XTREECOLUMN_TAG = "xCol";

   public String toXml() {
      StringBuilder sb = new StringBuilder("<" + XTREECOLUMN_TAG + ">");
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
      setId(XmlUtil.getTagData(xml, ID));
      setName(XmlUtil.getTagData(xml, NAME));
      width = XmlUtil.getTagIntData(xml, WIDTH);
      align = getAlignStoreValue(XmlUtil.getTagData(xml, ALIGN));
      sortForward = XmlUtil.getTagBooleanData(xml, SORT_FORWARD);
      show = XmlUtil.getTagBooleanData(xml, SHOW);
   }

   public static String getColumnId(String xml) {
      return XmlUtil.getTagData(xml, ID);
   }

   public String getAlignStoreName(XViewerAlign align) {
      if (align == XViewerAlign.Center) {
         return "center";
      } else if (align == XViewerAlign.Right) {
         return "right";
      } else {
         return "left";
      }
   }

   public XViewerAlign getAlignStoreValue(String str) {
      if (str.equals("center")) {
         return XViewerAlign.Center;
      } else if (str.equals("right")) {
         return XViewerAlign.Right;
      } else {
         return XViewerAlign.Left;
      }
   }

   public String getDisplayName() {
      if (id != null && !id.equals(name)) {
         return name + " - " + id + " - width:" + width + " - show:" + show;
      } else {
         return name + " - width:" + width + " - show:" + show;
      }
   }

   public XViewerAlign getAlign() {
      return align;
   }

   public void setAlign(XViewerAlign align) {
      this.align = align;
   }

   public String getId() {
      return id;
   }

   public int getWidth() {
      return width;
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

   public String getToolTip() {
      return toolTip;
   }

   public void setToolTip(String toolTip) {
      if (toolTip != null) {
         this.toolTip = XViewerUtil.intern(toolTip);
      }
   }

   public void setName(String name) {
      this.name = XViewerUtil.intern(name);
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public boolean isMultiColumnEditable() {
      return multiColumnEditable;
   }

   public void setMultiColumnEditable(boolean multiColumnEditable) {
      this.multiColumnEditable = multiColumnEditable;
   }

   public void setWidth(int newWidth) {
      this.width = newWidth;
   }

   public boolean isSummable() {
      if (sortDataType == SortDataType.Float || sortDataType == SortDataType.Integer || sortDataType == SortDataType.Long || sortDataType == SortDataType.Percent) {
         return true;
      }
      return false;
   }

   public String sumValues(Collection<String> values) {
      if (sortDataType == SortDataType.Float) {
         double sum = 0.0;
         Set<String> exceptions = new HashSet<>();
         sum = sumFloatValues(values, sum, exceptions);
         return "Sum: " + XViewerUtil.doubleToI18nString(
            sum) + "\n\nNum Items: " + values.size() + (exceptions.size() > 0 ? "\n\nErrors: " + CollectionsUtil.toString(
               ";", exceptions) : "");
      } else if (sortDataType == SortDataType.Integer || sortDataType == SortDataType.Percent) {
         int sum = 0;
         Set<String> exceptions = new HashSet<>();
         sum = sumIntegerValues(values, sum, exceptions);
         return "Sum: " + sum + "\n\nNum Items: " + values.size() + (exceptions.size() > 0 ? "\n\nErrors: " + CollectionsUtil.toString(
            ";", exceptions) : "");
      } else if (sortDataType == SortDataType.Long) {
         long sum = 0;
         Set<String> exceptions = new HashSet<>();
         sum = sumLongValues(values, sum, exceptions);
         return "Sum: " + sum + "\n\nNum Items: " + values.size() + (exceptions.size() > 0 ? "\n\nErrors: " + CollectionsUtil.toString(
            ";", exceptions) : "");
      }
      return "Unhandled column type";
   }

   private double sumFloatValues(Collection<String> values, double sum, Set<String> exceptions) {
      for (String value : values) {
         if (value == null || value.equals("")) {
            continue;
         }
         try {
            sum += new Double(value);
         } catch (Exception ex) {
            exceptions.add(ex.getLocalizedMessage());
         }
      }
      return sum;
   }

   public String averageValues(Collection<String> values) {
      if (sortDataType == SortDataType.Float) {
         double sum = 0.0;
         Set<String> exceptions = new HashSet<>();
         sum = sumFloatValues(values, sum, exceptions);
         Double average = sum == 0 || values.isEmpty() ? 0 : sum / values.size();
         return "Average: " + XViewerUtil.doubleToI18nString(
            average) + "\n\nNum Items: " + values.size() + (exceptions.size() > 0 ? "\n\nErrors: " + CollectionsUtil.toString(
               ";", exceptions) : "");
      } else if (sortDataType == SortDataType.Integer || sortDataType == SortDataType.Percent) {
         int sum = 0;
         Set<String> exceptions = new HashSet<>();
         sum = sumIntegerValues(values, sum, exceptions);
         Integer average = sum == 0 || values.isEmpty() ? 0 : sum / values.size();
         return "Average: " + average + "\n\nNum Items: " + values.size() + (exceptions.size() > 0 ? "\n\nErrors: " + CollectionsUtil.toString(
            ";", exceptions) : "");
      } else if (sortDataType == SortDataType.Long) {
         long sum = 0;
         Set<String> exceptions = new HashSet<>();
         sum = sumLongValues(values, sum, exceptions);
         Long average = sum == 0 || values.isEmpty() ? 0 : sum / Long.valueOf(values.size());
         return "Average: " + average + "\n\nNum Items: " + values.size() + (exceptions.size() > 0 ? "\n\nErrors: " + CollectionsUtil.toString(
            ";", exceptions) : "");
      }
      return "Unhandled column type";
   }

   private long sumLongValues(Collection<String> values, long sum, Set<String> exceptions) {
      for (String value : values) {
         if (value == null || value.equals("")) {
            continue;
         }
         try {
            sum += Long.valueOf(value);
         } catch (Exception ex) {
            exceptions.add(ex.getLocalizedMessage());
         }
      }
      return sum;
   }

   private int sumIntegerValues(Collection<String> values, int sum, Set<String> exceptions) {
      for (String value : values) {
         if (value == null || value.equals("")) {
            continue;
         }
         try {
            sum += Integer.valueOf(value);
         } catch (Exception ex) {
            exceptions.add(ex.getLocalizedMessage());
         }
      }
      return sum;
   }

   public void setId(String id) {
      this.id = XViewerUtil.intern(id);
   }

   public String getPreComputedValue(Long key) {
      String result = null;
      if (preComputedValueMap == null) {
         return result;
      }
      return preComputedValueMap.get(key);
   }

   public Object getXViewer() {
      return xViewer;
   }

   public void setXViewer(Object xViewer) {
      this.xViewer = xViewer;
   }

   public Map<Long, String> getPreComputedValueMap() {
      if (preComputedValueMap == null) {
         preComputedValueMap = new HashMap<>();
      }
      return preComputedValueMap;
   }

   public void setPreComputedValueMap(Map<Long, String> preComputedValueMap) {
      this.preComputedValueMap = preComputedValueMap;
   }

   @Override
   public String toString() {
      return "XViewerColumn [id=" + id + ", name=" + name + ", sortDataType=" + sortDataType + ", sortForward=" + isSortForward() + ", show=" + show + ", width=" + width + "]";
   }

   /**
    * @return time in milliseconds
    */
   public Long getElapsedTime() {
      return elapsedTime;
   }

   public void addElapsedTime(Long elapsedTimeMs) {
      this.elapsedTime += elapsedTimeMs;
   }

   public void setElapsedTime(Long elapsedTimeMs) {
      this.elapsedTime = elapsedTimeMs;
   }

   public void resetElapsedTime() {
      this.elapsedTime = 0L;
   }

}
