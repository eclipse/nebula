/*******************************************************************************
 * Copyright (c) 2012 Remain Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Wim jongman <wim.jongman@remainsoftware.com> - creation
 *******************************************************************************/

package org.eclipse.nebula.widgets.xviewer.example;

import org.eclipse.nebula.examples.AbstractExampleTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class ExampleTab extends AbstractExampleTab {

   /**
    * @wbp.parser.entryPoint
    */
   @Override
   public Control createControl(Composite parent) {
      int style = SWT.None;
      Button button = new Button(parent, style);
      button.setText("Run XViewer Example");
      button.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            new MyXViewerTest();
            MyXViewerTest.main(new String[0]);
         }
      });

      return button;
   }

   @Override
   public String[] createLinks() {
      return null;
   }

   @Override
   public void createParameters(Composite parent) {
      // do nothing
   }

}
