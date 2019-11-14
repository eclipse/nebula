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
package org.eclipse.nebula.widgets.xviewer.util.internal;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;

public class XViewerPatternFilter extends org.eclipse.nebula.widgets.xviewer.util.internal.PatternFilter {

   private String text;

   public XViewerPatternFilter() {
      // do nothing
   }

   public void setFilterText(String text) {
      this.text = text;

   }

   @Override
   public void setPattern(String patternString) {
      super.setPattern(patternString);
      if (patternString == null || patternString.isEmpty()) {
         text = null;
      } else {
         text = patternString.toLowerCase();
      }
   }

   @Override
   public boolean isLeafMatch(Viewer viewer, Object element) {
      if (element == null) {
         return true;
      }
      if (text == null || text.isEmpty()) {
         return true;
      }
      try {
         String name = element.toString();
         if (element instanceof XViewerColumn) {
            XViewerColumn xCol = (XViewerColumn) element;
            name = xCol.getDisplayName();
         } else if (element instanceof CustomizeData) {
            CustomizeData custData = (CustomizeData) element;
            name = custData.getName();
         }
         return name.toLowerCase().contains(text);
      } catch (Exception ex) {
         System.err.println("Exception matching leaf " + ex.getLocalizedMessage());
      }
      return true;
   }

}
