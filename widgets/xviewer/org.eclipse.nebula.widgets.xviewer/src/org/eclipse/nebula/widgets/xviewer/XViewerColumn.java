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
import java.util.Map;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.util.internal.CollectionsUtil;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLib;
import org.eclipse.nebula.widgets.xviewer.util.internal.XmlUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;

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
   protected Map<Long, String> preComputedValueMap = null;
   public enum SortDataType {
      Date,
      Float,
      Percent,
      String,
      String_MultiLine,
      Boolean,
      Integer,
      Long,
      Paragraph_Number,
      Check
   };

   protected XViewerColumn() {
      super();
   }

   public XViewerColumn(String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
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
      toXCol.setToolTip(fromXCol.toolTip);
      toXCol.setWidth(fromXCol.width);
      toXCol.setShow(fromXCol.show);
      toXCol.setId(fromXCol.id);
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

   public final static String ID = "id";
   public final static String NAME = "name";
   public final static String WIDTH = "wdth";
   public final static String ALIGN = "algn";
   public final static String SORT_FORWARD = "srtFwd";
   public final static String SHOW = "show";
   public final static String XTREECOLUMN_TAG = "xCol";

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

   public String getAlignStoreName(int align) {
      if (align == SWT.CENTER) {
         return "center";
      } else if (align == SWT.RIGHT) {
         return "right";
      } else {
         return "left";
      }
   }

   public int getAlignStoreValue(String str) {
      if (str.equals("center")) {
         return SWT.CENTER;
      } else if (str.equals("right")) {
         return SWT.RIGHT;
      } else {
         return SWT.LEFT;
      }
   }

   public String getDisplayName() {
      if (id != null && !id.equals(name)) {
         return name + " - " + id + " - width:" + width + " - show:" + show;
      } else {
         return name + " - width:" + width + " - show:" + show;
      }
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

   public XViewer getXViewer() {
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
      if (toolTip != null) {
         this.toolTip = XViewerLib.intern(toolTip);
      }
   }

   public void setName(String name) {
      this.name = XViewerLib.intern(name);
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = XViewerLib.intern(description);
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
      if (sortDataType == SortDataType.Float || sortDataType == SortDataType.Integer || sortDataType == SortDataType.Long) {
         return true;
      }
      return false;
   }

   public String sumValues(Collection<String> values) {
      if (sortDataType == SortDataType.Float) {
         double sum = 0.0;
         Set<String> exceptions = new HashSet<String>();
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
         return "Sum: " + XViewerLib.doubleToI18nString(sum) + "\n\nNum Items: " + values.size() + (exceptions.size() > 0 ? "\n\nErrors: " + CollectionsUtil.toString(
            ";", exceptions) : "");
      } else if (sortDataType == SortDataType.Integer) {
         int sum = 0;
         Set<String> exceptions = new HashSet<String>();
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
         return "Sum: " + sum + "\n\nNum Items: " + values.size() + (exceptions.size() > 0 ? "\n\nErrors: " + CollectionsUtil.toString(
            ";", exceptions) : "");
      } else if (sortDataType == SortDataType.Long) {
         int sum = 0;
         Set<String> exceptions = new HashSet<String>();
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
         return "Sum: " + sum + "\n\nNum Items: " + values.size() + (exceptions.size() > 0 ? "\n\nErrors: " + CollectionsUtil.toString(
            ";", exceptions) : "");
      }
      return "Unhandled column type";
   }

   public boolean is(TreeColumn treeColumn) {
      return treeColumn.getText().equals(getName());
   }

   public void setId(String id) {
      this.id = XViewerLib.intern(id);
   }

   public String getPreComputedValue(Long key) {
      String result = null;
      if (preComputedValueMap == null) {
         return result;
      }
      return preComputedValueMap.get(key);
   }

}
