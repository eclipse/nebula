/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.xviewer.util.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class CustomizeDataImages {

   public static Image getImage(boolean isDefault, CustomizeData data) {
      Image image = XViewerLib.getImage("customize.gif"); //$NON-NLS-1$
      if (!data.isPersonal() && isDefault) {
         image = XViewerLib.getImage("customizeSharedDefault.gif"); //$NON-NLS-1$
      } else if (!data.isPersonal()) {
         image = XViewerLib.getImage("customizeShared.gif"); //$NON-NLS-1$
      } else if (isDefault) {
         image = XViewerLib.getImage("customizeDefault.gif"); //$NON-NLS-1$
      }
      return image;
   }

   public static ImageDescriptor getImageDescriptor(boolean isDefault, CustomizeData data) {
      ImageDescriptor image = XViewerLib.getImageDescriptor("customize.gif"); //$NON-NLS-1$
      if (!data.isPersonal() && isDefault) {
         image = XViewerLib.getImageDescriptor("customizeSharedDefault.gif"); //$NON-NLS-1$
      } else if (!data.isPersonal()) {
         image = XViewerLib.getImageDescriptor("customizeShared.gif"); //$NON-NLS-1$
      } else if (isDefault) {
         image = XViewerLib.getImageDescriptor("customizeDefault.gif"); //$NON-NLS-1$
      }
      return image;
   }

}
