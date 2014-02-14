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

import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class XViewerFilteredTree extends FilteredTreeComposite {

   public XViewerFilteredTree(Composite parent) {
      this(parent, SWT.BORDER | SWT.MULTI);
   }

   public XViewerFilteredTree(Composite parent, int treeStyle) {
      this(parent, treeStyle, new XViewerPatternFilter());
   }

   public XViewerFilteredTree(Composite parent, int treeStyle, PatternFilter filter) {
      super(parent, treeStyle, filter);
      setInitialText(""); //$NON-NLS-1$
   }

   @Override
   protected Composite createFilterControls(Composite parent) {
      Composite comp = new Composite(parent, SWT.NONE);
      comp.setLayout(XViewerLib.getZeroMarginLayout(3, false));
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      (new Label(comp, SWT.NONE)).setText(XViewerText.get("label.filter") + " ");  //$NON-NLS-1$//$NON-NLS-2$
      super.createFilterControls(comp);

      return comp;
   }

   @Override
   protected void createFilterText(Composite parent) {
      super.createFilterText(parent);
      filterText.addKeyListener(new KeyListener() {
         @Override
         public void keyPressed(KeyEvent e) {
            filterText.setFocus();
         }

         @Override
         public void keyReleased(KeyEvent e) {
            filterText.setFocus();
         }
      });
   }

}
