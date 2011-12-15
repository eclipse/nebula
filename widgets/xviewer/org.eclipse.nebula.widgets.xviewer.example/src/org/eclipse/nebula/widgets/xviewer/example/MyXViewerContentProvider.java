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
package org.eclipse.nebula.widgets.xviewer.example;

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.xviewer.example.model.ISomeTask;
import org.eclipse.nebula.widgets.xviewer.example.model.SomeTask;

/**
 * Content provider for example XViewer implementation
 * 
 * @author Donald G. Dunne
 */
public class MyXViewerContentProvider implements ITreeContentProvider {

   protected Collection<ISomeTask> rootSet = new HashSet<ISomeTask>();
   private final static Object[] EMPTY_ARRAY = new Object[0];

   public MyXViewerContentProvider() {
      super();
   }

   @Override
   @SuppressWarnings("rawtypes")
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Object[]) {
         return (Object[]) parentElement;
      }
      if (parentElement instanceof Collection) {
         return ((Collection) parentElement).toArray();
      }
      if (parentElement instanceof SomeTask) {
         return ((SomeTask) parentElement).getChildren().toArray();
      }
      return EMPTY_ARRAY;
   }

   @Override
   public Object getParent(Object element) {
      return null;
   }

   @Override
   public boolean hasChildren(Object element) {
      if (element instanceof SomeTask) {
         return ((SomeTask) element).getChildren().size() > 0;
      }
      return false;
   }

   @Override
   public Object[] getElements(Object inputElement) {
      if (inputElement instanceof String) {
         return new Object[] {inputElement};
      }
      return getChildren(inputElement);
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      // do nothing
   }

   public Collection<ISomeTask> getRootSet() {
      return rootSet;
   }

}
