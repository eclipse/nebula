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

package org.eclipse.nebula.widgets.xviewer.util.internal.dialog;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.Activator;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLib;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author Donald G. Dunne
 */
public class HtmlDialog extends MessageDialog {
   protected Browser b;
   private LocationListener listener;
   private final String html;

   public HtmlDialog(String title, String message, String html) {
      super(Display.getCurrent().getActiveShell(), title, null, message, SWT.NONE, new String[] {"OK", "Cancel"}, 0);
      this.html = html;
   }

   /**
    * Add listener to browser widget.
    * 
    * @param listener
    */
   public void addLocationListener(LocationListener listener) {
      this.listener = listener;
   }

   @Override
   protected boolean isResizable() {
      return true;
   }

   @Override
   protected Control createDialogArea(Composite parent) {
      Composite c = (Composite) super.createDialogArea(parent);
      b = new Browser(c, SWT.BORDER);
      GridData gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
      b.setLayoutData(gd);
      b.setText(html);
      b.setSize(500, 500);
      if (listener != null) b.addLocationListener(listener);
      b.setMenu(pageOverviewGetPopup());

      return c;
   }

   public Menu pageOverviewGetPopup() {
      Menu menu = new Menu(b.getShell());
      MenuItem item = new MenuItem(menu, SWT.NONE);
      item.setText("View Source");
      item.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            String file = System.getProperty("user.home") + File.separator + "out.html";
            try {
               XViewerLib.writeStringToFile(html, new File(file));
            } catch (IOException ex) {
               XViewerLog.logAndPopup(Activator.class, Level.SEVERE, ex);
            }
            Program.launch(file);
         }
      });
      return menu;
   }

}
