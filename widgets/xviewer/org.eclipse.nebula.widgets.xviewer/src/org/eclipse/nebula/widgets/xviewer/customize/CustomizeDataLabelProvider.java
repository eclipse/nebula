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
package org.eclipse.nebula.widgets.xviewer.customize;

import java.util.logging.Level;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.nebula.widgets.xviewer.util.internal.CustomizeDataImages;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLog;
import org.eclipse.swt.graphics.Image;

/**
 * Generic Label Provider showing Descriptive Name as text
 *
 * @author Donald G. Dunne
 */
public class CustomizeDataLabelProvider implements ILabelProvider {

   private final XViewer xViewer;

   public CustomizeDataLabelProvider(XViewer xViewer) {
      this.xViewer = xViewer;
   }

   @Override
   public Image getImage(Object arg0) {
      try {
         return CustomizeDataImages.getImage(xViewer.getCustomizeMgr().isCustomizationUserDefault((CustomizeData) arg0),
            ((CustomizeData) arg0));
      } catch (XViewerException ex) {
         return null;
      }
   }

   @Override
   public String getText(Object arg0) {
      try {
         CustomizeData custom = (CustomizeData) arg0;
         boolean custDefault = xViewer.getCustomizeMgr().isCustomizationUserDefault(custom);
         String text = (custDefault ? "(" + XViewerText.get("default") + ") " : "") + custom.getName(); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
         text = text + (custom.isPersonal() ? "" : " (" + XViewerText.get("shared") + ")"); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$

         return text;
      } catch (XViewerException ex) {
         XViewerLog.log(CustomizeDataLabelProvider.class, Level.SEVERE, ex.toString(), ex);
         return "Exception: " + ex.getLocalizedMessage(); //$NON-NLS-1$
      }
   }

   @Override
   public void addListener(ILabelProviderListener arg0) {
      // do nothing
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public boolean isLabelProperty(Object arg0, String arg1) {
      return false;
   }

   @Override
   public void removeListener(ILabelProviderListener arg0) {
      // do nothing
   }

}
