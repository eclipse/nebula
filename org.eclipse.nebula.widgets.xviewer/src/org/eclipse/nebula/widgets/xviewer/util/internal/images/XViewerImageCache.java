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
package org.eclipse.nebula.widgets.xviewer.util.internal.images;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class XViewerImageCache {

   static Map<String, Image> imageCache = new HashMap<String, Image>();
   static Map<String, ImageDescriptor> imageDescriptorCache = new HashMap<String, ImageDescriptor>();

   /**
    * Return image
    * 
    * @param example clear.gif
    * @return the clearImage
    */
   public static Image getImage(String imageName) {
      if (!imageCache.containsKey(imageName)) {
         imageCache.put(imageName, getImageDescriptor(imageName).createImage());
      }
      return imageCache.get(imageName);
   }

   public static ImageDescriptor getImageDescriptor(String imageName) {
      if (!imageDescriptorCache.containsKey(imageName)) {
         URL url = XViewerImageCache.class.getResource(imageName);
         imageDescriptorCache.put(imageName, ImageDescriptor.createFromURL(url));
      }
      return imageDescriptorCache.get(imageName);
   }

}
