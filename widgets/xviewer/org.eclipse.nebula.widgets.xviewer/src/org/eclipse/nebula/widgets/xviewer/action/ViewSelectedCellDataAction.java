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
package org.eclipse.nebula.widgets.xviewer.action;

import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.xviewer.Activator;
import org.eclipse.nebula.widgets.xviewer.IXViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.IXViewerPreComputedColumn;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.nebula.widgets.xviewer.util.internal.HtmlUtil;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLog;
import org.eclipse.nebula.widgets.xviewer.util.internal.dialog.HtmlDialog;
import org.eclipse.nebula.widgets.xviewer.util.internal.images.XViewerImageCache;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class ViewSelectedCellDataAction extends Action {

   private final XViewer xViewer;
   private final Option option;
   private final Clipboard clipboard;
   public static enum Option {
      View,
      Copy
   }

   public ViewSelectedCellDataAction(XViewer xViewer, Clipboard clipboard, Option option) {
      super(option.equals(Option.View) ? XViewerText.get("action.selectedCellData.view") : XViewerText.get( //$NON-NLS-1$
         "action.selectedCellData.copy")); //$NON-NLS-1$
      this.xViewer = xViewer;
      this.clipboard = clipboard;
      this.option = option;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return XViewerImageCache.getImageDescriptor("report.gif"); //$NON-NLS-1$
   }

   @Override
   public void run() {
      try {
         TreeColumn treeCol = xViewer.getRightClickSelectedColumn();
         TreeItem treeItem = xViewer.getRightClickSelectedItem();
         run(treeCol, treeItem, xViewer.getRightClickSelectedColumnNum());
      } catch (Exception ex) {
         XViewerLog.logAndPopup(Activator.class, Level.SEVERE, ex);
      }
   }

   public void run(TreeColumn treeCol, TreeItem treeItem, int columnNum) throws XViewerException, Exception {
      if (treeCol != null) {
         XViewerColumn xCol = (XViewerColumn) treeCol.getData();
         String data = null;

         if (xCol instanceof IXViewerValueColumn) {
            data = ((IXViewerValueColumn) xCol).getColumnText(treeItem.getData(), xCol, columnNum);
         } else if (xCol instanceof IXViewerPreComputedColumn) {
            data = XViewerLabelProvider.getPrecomputedText(treeItem.getData(), xCol, (IXViewerPreComputedColumn) xCol,
               false);
         } else {
            data =
               ((IXViewerLabelProvider) xViewer.getLabelProvider()).getColumnText(treeItem.getData(), xCol, columnNum);
         }
         if (data != null && !data.equals("")) { //$NON-NLS-1$
            if (option == Option.View) {
               String html = HtmlUtil.simplePage(HtmlUtil.getPreData(data));
               new HtmlDialog(treeCol.getText() + " " + XViewerText.get("data"), //$NON-NLS-1$//$NON-NLS-2$
                  treeCol.getText() + " " + XViewerText.get("data"), html).open(); //$NON-NLS-1$ //$NON-NLS-2$
            } else {
               clipboard.setContents(new Object[] {data}, new Transfer[] {TextTransfer.getInstance()});
            }
         }
      }
   }

}
