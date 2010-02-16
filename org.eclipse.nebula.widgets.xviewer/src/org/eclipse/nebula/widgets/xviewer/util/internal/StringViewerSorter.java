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

import java.text.Collator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * @author Donald G. Dunne
 */
public class StringViewerSorter extends ViewerSorter {

   /**
    * 
    */
   public StringViewerSorter() {
   }

   /**
    * @param collator
    */
   public StringViewerSorter(Collator collator) {
      super(collator);
   }

   @SuppressWarnings("unchecked")
   @Override
   public int compare(Viewer viewer, Object e1, Object e2) {
      return getComparator().compare((String) e1, (String) e2);
   }
}
