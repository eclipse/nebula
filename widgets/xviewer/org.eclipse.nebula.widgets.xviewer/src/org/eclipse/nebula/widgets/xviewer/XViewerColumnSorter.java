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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * @author Donald G. Dunne
 */
public class XViewerColumnSorter extends ViewerSorter {

   @Override
   public int compare(Viewer viewer, Object e1, Object e2) {
      if (e1 instanceof XViewerColumn) {
         return getComparator().compare(((XViewerColumn) e1).getName(), ((XViewerColumn) e2).getName());
      } else if ((e1 instanceof TreeColumn) && ((TreeColumn) e1).getData() instanceof XViewerColumn) {

         return getComparator().compare(((XViewerColumn) ((TreeColumn) e1).getData()).toString(),
            ((XViewerColumn) ((TreeColumn) e2).getData()).toString());
      } else {
         return 0;
      }
   }
}
