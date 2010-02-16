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
package org.eclipse.nebula.widgets.xviewer;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * @author Donald G. Dunne
 */
public class XViewerColumnSorter extends ViewerSorter {

   public XViewerColumnSorter() {
   }

   @SuppressWarnings("unchecked")
   @Override
   public int compare(Viewer viewer, Object e1, Object e2) {
      if (e1 instanceof XViewerColumn) {
         return getComparator().compare(((XViewerColumn) e1).toString(), ((XViewerColumn) e2).toString());
      } else if ((e1 instanceof TreeColumn) && ((TreeColumn) e1).getData() instanceof XViewerColumn) {

         return getComparator().compare(((XViewerColumn) ((TreeColumn) e1).getData()).toString(),
               ((XViewerColumn) ((TreeColumn) e2).getData()).toString());
      } else
         return 0;
   }
}
