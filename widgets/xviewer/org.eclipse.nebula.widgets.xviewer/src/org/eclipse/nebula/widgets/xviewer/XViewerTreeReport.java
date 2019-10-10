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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.nebula.widgets.xviewer.util.internal.HtmlUtil;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class XViewerTreeReport extends XViewerHtmlReport {

   protected final XViewer xViewer;
   protected final String title;

   public XViewerTreeReport(String title, XViewer treeViewer) {
      super(title);
      this.title = title;
      this.xViewer = treeViewer;
   }

   public XViewerTreeReport(XViewer xViewer) {
      this(XViewerText.get("XViewerTreeReport.title"), xViewer); //$NON-NLS-1$
   }

   @Override
   public void open(String defaultFilename) {
      super.open(defaultFilename);
   }

   @Override
   public String getHtml() throws XViewerException {
      return getHtml(xViewer.getTree().getItems());
   }

   private Map<XViewerColumn, Integer> xColToColumnIndex = null;

   public String getHtml(TreeItem items[]) throws XViewerException {
      StringBuilder sb = new StringBuilder("<html><body>"); //$NON-NLS-1$
      sb.append(HtmlUtil.beginMultiColumnTable(100, 1));
      List<XViewerColumn> columns = xViewer.getCustomizeMgr().getCurrentTableColumnsInOrder();
      List<String> headerStrs = new ArrayList<>(50);
      List<XViewerColumn> showCols = new ArrayList<>(50);
      xColToColumnIndex = xViewer.getCustomizeMgr().getCurrentTableColumnsIndex();
      for (XViewerColumn xCol : columns) {
         if (xCol.isShow()) {
            showCols.add(xCol);
            headerStrs.add(xCol.getName());
         }
      }
      sb.append(HtmlUtil.addHeaderRowMultiColumnTable(headerStrs.toArray(new String[headerStrs.size()])));
      // Get column widths and column name and setup the columns
      IXViewerLabelProvider labelProv = (IXViewerLabelProvider) xViewer.getLabelProvider();
      ArrayList<String[]> list = new ArrayList<>();
      for (TreeItem item : items) {
         addRow(item, list, labelProv, showCols, 1);
      }
      for (String[] strs : list) {
         sb.append(HtmlUtil.addRowMultiColumnTable(strs));
      }
      sb.append(HtmlUtil.endMultiColumnTable());
      sb.append("</body></html>"); //$NON-NLS-1$
      return sb.toString();
   }

   private void addRow(TreeItem item, List<String[]> rowData, IXViewerLabelProvider labelProv, List<XViewerColumn> showCols, int level) throws XViewerException {
      List<String> cellData = new ArrayList<>(showCols.size());
      boolean firstCell = true;
      for (XViewerColumn xCol : showCols) {
         StringBuilder str = new StringBuilder();
         if (firstCell) {
            for (int y = 1; y < level; y++) {
               str.append("__INSERT_TAB_HERE__"); //$NON-NLS-1$
            }
            firstCell = false;
         }
         str.append(labelProv.getColumnText(item.getData(), xColToColumnIndex.get(xCol)));
         String html = HtmlUtil.textToHtml(str.toString());
         html = html.replaceAll("__INSERT_TAB_HERE__", "&nbsp;&nbsp;&nbsp;&nbsp;"); //$NON-NLS-1$ //$NON-NLS-2$
         cellData.add(html);
      }
      rowData.add(cellData.toArray(new String[cellData.size()]));
      if (item.getExpanded()) {
         for (TreeItem i : item.getItems()) {
            addRow(i, rowData, labelProv, showCols, level + 1);
         }
      }

   }

}
