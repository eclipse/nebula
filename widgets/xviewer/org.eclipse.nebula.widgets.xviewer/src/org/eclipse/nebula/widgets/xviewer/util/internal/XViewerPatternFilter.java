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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;

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
