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
package org.eclipse.nebula.widgets.xviewer.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.nebula.widgets.xviewer.util.internal.images.XViewerImageCache;

/**
 * @author Donald G. Dunne
 */
public class TableCustomizationAction extends Action {

   private final XViewer xViewer;

   public TableCustomizationAction(XViewer xViewer) {
      super(XViewerText.get("action.tableCustomization")); //$NON-NLS-1$
      this.xViewer = xViewer;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return XViewerImageCache.getImageDescriptor("customize.gif"); //$NON-NLS-1$
   }

   @Override
   public void run() {
      xViewer.getCustomizeMgr().handleTableCustomization();
   }

   @Override
   public String getToolTipText() {
      return XViewerText.get("toolTip.customizeTable"); //$NON-NLS-1$
   }

}
