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
import org.eclipse.jface.viewers.ViewerComparator;

/**
 * @author Donald G. Dunne
 */
public class StringNameComparator extends ViewerComparator {

   public StringNameComparator() {
      super();
   }

   @Override
   public int compare(Viewer viewer, Object o1, Object o2) {
      return o1.toString().compareTo(o2.toString());
   }

}