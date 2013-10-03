/*******************************************************************************
 * Copyright (c) 2012 Remain Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Wim jongman <wim.jongman@remainsoftware.com> - creation
 *    Max Hohenegger <eclipse@hohenegger.eu> - adjusted from XViewer to Visualization
 *******************************************************************************/

package org.eclipse.nebula.visualization.example;

import org.eclipse.nebula.examples.AbstractExampleTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ExampleTab extends AbstractExampleTab {

   /**
    * @wbp.parser.entryPoint
    */
   @Override
   public Control createControl(Composite parent) {
      int style = SWT.None;
      Button button = new Button(parent, style);
      button.setText("Run Visualization Widgets Example");
      button.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
        	 try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.nebula.visualization.xygraph.exampleview.widgetsexampleview");
			} catch (PartInitException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
         }
      });
      
      Button button2 = new Button(parent, style);
      button2.setText("Run Visualization XY-Graph Example");
      button2.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
        	 try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.nebula.visualization.xygraph.exampleview.xyexampleview");
			} catch (PartInitException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
         }
      });

      return button;
   }

   @Override
   public String[] createLinks() {
      return new String[] {
         "<a href=\"http://eclipse.org/nebula/widgets/visualization/visualization.php\">Visualization Home Page</a>",
         "<a href=\"https://bugs.eclipse.org/bugs/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=&classification=Technology&product=Nebula&component=Visualization&long_desc_type=allwordssubstr&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&status_whiteboard_type=allwordssubstr&status_whiteboard=&keywords_type=allwords&keywords=&emailtype1=substring&email1=&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=\">Bugs</a>"};
   }

   @Override
   public void createParameters(Composite parent) {
      // do nothing
   }

}
