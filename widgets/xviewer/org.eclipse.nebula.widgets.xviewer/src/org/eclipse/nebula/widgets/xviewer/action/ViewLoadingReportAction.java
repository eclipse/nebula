/*******************************************************************************
 * Copyright (c) 2019 Boeing.
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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerLoadingReport;
import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.nebula.widgets.xviewer.util.internal.images.XViewerImageCache;

/**
 * @author Donald G. Dunne
 */
public class ViewLoadingReportAction extends Action {

   private final XViewer xViewer;

   public ViewLoadingReportAction(XViewer xViewer) {
      super(XViewerText.get("action.viewLoadingReport")); //$NON-NLS-1$
      this.xViewer = xViewer;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return XViewerImageCache.getImageDescriptor("report.gif"); //$NON-NLS-1$
   }

   @Override
   public void run() {
      if (xViewer.getXViewerFactory().getXViewerLoadingReport(xViewer) != null) {
         xViewer.getXViewerFactory().getXViewerLoadingReport(xViewer).open();
      } else {
         new XViewerLoadingReport(xViewer).open("XViewerLoadingReport");
      }
   }

}
