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
package org.eclipse.nebula.widgets.xviewer.util.internal;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

/**
 * @author Donald G. Dunne
 */
public class OverlayUtil extends CompositeImageDescriptor {

   private final Image baseImage;
   private ImageDescriptor overlayImageDescriptor;
   private int xValue = 0;
   private int yValue = 0;
   private Collection<ImageInfo> imageInfo;

   public static enum Location {
      TOP_LEFT, TOP_RIGHT, BOT_LEFT, BOT_RIGHT
   };

   public static class ImageInfo {
      public ImageDescriptor descriptor;
      public Location location;

      public ImageInfo(ImageDescriptor descriptor, Location location) {
         this.descriptor = descriptor;
         this.location = location;
      }
   }

   public OverlayUtil(Image baseImage, Collection<ImageInfo> imageInfo) {
      this.baseImage = baseImage;
      this.imageInfo = imageInfo;
   }

   public OverlayUtil(Image baseImage, ImageDescriptor overlayImageDescriptor, Location location) {
      this.baseImage = baseImage;
      this.overlayImageDescriptor = overlayImageDescriptor;
      this.imageInfo = new ArrayList<ImageInfo>(2);
      imageInfo.add(new ImageInfo(overlayImageDescriptor, location));
   }

   public OverlayUtil(Image baseImage, ImageDescriptor overlayImageDescriptor) {
      this(baseImage, overlayImageDescriptor, 0, 0);
   }

   public OverlayUtil(Image baseImage, ImageDescriptor overlayImageDescriptor, int xValue, int yValue) {
      if (baseImage == null) throw new IllegalArgumentException("baseImage can not be null");
      if (overlayImageDescriptor == null) throw new IllegalArgumentException("overlayImageDescriptor can not be null");

      this.baseImage = baseImage;
      this.overlayImageDescriptor = overlayImageDescriptor;
      this.xValue = xValue;
      this.yValue = yValue;
   }

   /**
    * Set x,y pixel to draw the overlay image eg: 8,8 for bottom right of a 16x16 image 0,0 for top left
    * 
    * @param xValue
    * @param yValue
    */
   public void setXY(int xValue, int yValue) {
      this.xValue = xValue;
      this.yValue = yValue;
   }

   @Override
   protected void drawCompositeImage(int width, int height) {
      // To draw a composite image, the base image should be
      // drawn first (first layer) and then the overlay image
      // (second layer)
      // Draw the base image using the base image's image data
      drawImage(baseImage.getImageData(), 0, 0);

      if (imageInfo == null) {
         // Overlaying the icon in the top left corner i.e. x and y
         // coordinates are both zero
         drawImage(overlayImageDescriptor.getImageData(), xValue, yValue);
      } else {
         for (ImageInfo info : imageInfo) {
            if (info.location == Location.TOP_LEFT)
               drawImage(info.descriptor.getImageData(), 0, 0);
            else if (info.location == Location.BOT_LEFT)
               drawImage(info.descriptor.getImageData(), 0, 8);
            else if (info.location == Location.TOP_RIGHT)
               drawImage(info.descriptor.getImageData(), 8, 0);
            else if (info.location == Location.BOT_RIGHT) drawImage(info.descriptor.getImageData(), 8, 8);
         }
      }
   }

   @Override
   protected Point getSize() {
      // System.err.println("Width = " + baseImage.getBounds().width);
      // System.err.println("Height = " + baseImage.getBounds().height);
      int baseWidth = baseImage.getBounds().width;
      int baseHeight = baseImage.getBounds().height;

      Image overImg = overlayImageDescriptor.createImage();
      int overWidth = overImg.getBounds().width;
      int overHeight = overImg.getBounds().height;
      overlayImageDescriptor.destroyResource(overImg);

      return new Point(baseWidth > overWidth ? baseWidth : overWidth, baseHeight > overHeight ? baseHeight : overHeight);
   }

}