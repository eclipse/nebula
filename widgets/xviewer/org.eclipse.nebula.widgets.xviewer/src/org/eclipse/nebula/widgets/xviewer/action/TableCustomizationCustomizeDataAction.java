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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.util.internal.CustomizeDataImages;
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
         return CustomizeDataImages.getImageDescriptor(xViewer.getCustomizeMgr().isCustomizationUserDefault(custData),
            custData);
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
