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

import java.util.List;
import java.util.Map.Entry;

import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;

/**
 * @author Donald G. Dunne
 */
public class XViewerLoadingReport extends XViewerHtmlReport {

   protected final XViewer xViewer;
   protected final String title;

   public XViewerLoadingReport(String title, XViewer treeViewer) {
      super(title);
      this.title = title;
      this.xViewer = treeViewer;
   }

   public XViewerLoadingReport(XViewer xViewer) {
      this(XViewerText.get("XViewerLoadingReport.title"), xViewer); //$NON-NLS-1$
   }

   @Override
   public void open(String defaultFilename) {
      super.open(defaultFilename);
   }

   @SuppressWarnings("unused")
   @Override
   public String getHtml() throws XViewerException {
      StringBuilder sb = new StringBuilder("<html><body>"); //$NON-NLS-1$
      List<XViewerColumn> columns = xViewer.getCustomizeMgr().getCurrentTableColumnsInOrder();
      sb.append("</br>PRE-COMPUTED</br>");
      for (Entry<String, Long> nameToTime : xViewer.getPreComputeElapsedTime().entrySet()) {
         Long ms = nameToTime.getValue();
         Long sec = ms > 0 ? ms / 1000 : 0;
         Long min = sec > 0 ? sec / 60 : 0;
         sb.append(String.format("%s min or %s sec or %s ms - <b>%s</b></br>", min, sec, ms, nameToTime.getKey()));
      }
      sb.append("</br>OTHER</br>");
      for (XViewerColumn xCol : columns) {
         if (xCol.isShow()) {
            Long ms = xCol.getElapsedTime();
            Long sec = ms > 0 ? ms / 1000 : 0;
            Long min = sec > 0 ? sec / 60 : 0;
            sb.append(String.format("%s min or %s sec or %s ms - <b>%s</b></br>", min, sec, ms, xCol.getName()));
         }
      }
      sb.append("</body></html>"); //$NON-NLS-1$
      return sb.toString();
   }

}
