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
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.util.internal.images.XViewerImageCache;

/**
 * Action to load a specified customize data object
 * 
 * @author Donald G. Dunne
 */
public class TableCustomizationCustomizeDataAction extends Action {

   private final XViewer xViewer;
   private final CustomizeData custData;

   public TableCustomizationCustomizeDataAction(XViewer xViewer, CustomizeData custData) {
      super(custData.getName());
      this.xViewer = xViewer;
      this.custData = custData;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      try {
         return custData.getImageDescriptor(xViewer.getCustomizeMgr().isCustomizationUserDefault(custData));
      } catch (Exception ex) {
         // do nothing
      }
      return XViewerImageCache.getImageDescriptor("customize.gif"); //$NON-NLS-1$
   }

   @Override
   public void run() {
      xViewer.getCustomizeMgr().loadCustomization(custData);
      xViewer.refresh();
   }

   @Override
   public String getToolTipText() {
      return XViewerText.get("toolTip.customizeTable"); //$NON-NLS-1$
   }

}
