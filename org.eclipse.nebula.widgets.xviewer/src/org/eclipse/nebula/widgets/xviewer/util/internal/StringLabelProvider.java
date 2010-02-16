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

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

/**
 * Generic Artifact Label Provider showing Descriptive Name as text
 * 
 * @author Donald G. Dunne
 */
public class StringLabelProvider implements ILabelProvider {

   public Image getImage(Object arg0) {
      return null;
   }

   public String getText(Object arg0) {
      return ((String) arg0);
   }

   public void addListener(ILabelProviderListener arg0) {
   }

   public void dispose() {
   }

   public boolean isLabelProperty(Object arg0, String arg1) {
      return false;
   }

   public void removeListener(ILabelProviderListener arg0) {
   }

}
