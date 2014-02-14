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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.nebula.widgets.xviewer.util.internal.HtmlUtil;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLib;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class XViewerTreeReport {

   protected final XViewer xViewer;
   protected final String title;

   public XViewerTreeReport(String title, XViewer treeViewer) {
      this.title = title;
      this.xViewer = treeViewer;
   }

   public XViewerTreeReport(XViewer xViewer) {
      this(XViewerText.get("XViewerTreeReport.title"), xViewer); //$NON-NLS-1$
   }

   public void open() {
      open(xViewer.getTree().getItems(), null);
   }

   public void open(String defaultFilename) {
      open(xViewer.getTree().getItems(), defaultFilename);
   }

   public String getHtml() throws XViewerException {
      return getHtml(xViewer.getTree().getItems());
   }

   public void open(TreeItem items[], String defaultFilename) {
      try {
         String html = getHtml(items);
         final FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell().getShell(), SWT.SAVE);
         dialog.setFilterExtensions(new String[] {"*.html"}); //$NON-NLS-1$
         if (defaultFilename != null && !defaultFilename.equals("")) { //$NON-NLS-1$
            dialog.setFileName(defaultFilename);
         }
         String filename = dialog.open();
         if (filename == null || filename.equals("")) { //$NON-NLS-1$
            return;
         }
         try {
            XViewerLib.writeStringToFile(html, new File(filename));
         } catch (IOException ex) {
            XViewerLog.log(Activator.class, Level.SEVERE, ex);
            return;
         }
         Program.launch(filename);
      } catch (Exception ex) {
         XViewerLog.logAndPopup(Activator.class, Level.SEVERE, ex);
      }
   }
   private Map<XViewerColumn, Integer> xColToColumnIndex = null;

   public String getHtml(TreeItem items[]) throws XViewerException {
      StringBuffer sb = new StringBuffer("<html><body>"); //$NON-NLS-1$
      sb.append(HtmlUtil.beginMultiColumnTable(100, 1));
      List<XViewerColumn> columns = xViewer.getCustomizeMgr().getCurrentTableColumnsInOrder();
      List<String> headerStrs = new ArrayList<String>(50);
      List<XViewerColumn> showCols = new ArrayList<XViewerColumn>(50);
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
      ArrayList<String[]> list = new ArrayList<String[]>();
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
      List<String> cellData = new ArrayList<String>(showCols.size());
      boolean firstCell = true;
      for (XViewerColumn xCol : showCols) {
         StringBuffer str = new StringBuffer();
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
