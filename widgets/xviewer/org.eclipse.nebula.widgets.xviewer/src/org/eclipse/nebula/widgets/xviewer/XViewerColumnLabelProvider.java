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

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class XViewerColumnLabelProvider implements ILabelProvider {

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   @Override
   public void addListener(ILabelProviderListener listener) {
      // do nothing
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
      // do nothing
   }

   @Override
   public Image getImage(Object element) {
      return null;
   }

   @Override
   public String getText(Object element) {
      if (!(element instanceof XViewerColumn)) {
         return "";
      }
      XViewerColumn xCol = (XViewerColumn) element;
      return xCol.getDisplayName();
   }
}
