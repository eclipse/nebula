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

package org.eclipse.nebula.widgets.xviewer.util.internal.dialog;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.Activator;
import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLib;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationListener;
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
      super(Display.getCurrent().getActiveShell(), title, null, message, SWT.NONE, new String[] {XViewerText.get("button.ok"), XViewerText.get("button.cancel")}, 0); //$NON-NLS-1$ //$NON-NLS-2$
      this.html = html;
   }

   /**
    * Add listener to browser widget.
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
      if (listener != null) {
         b.addLocationListener(listener);
      }
      b.setMenu(pageOverviewGetPopup());

      return c;
   }

   public Menu pageOverviewGetPopup() {
      Menu menu = new Menu(b.getShell());
      MenuItem item = new MenuItem(menu, SWT.NONE);
      item.setText(XViewerText.get("HtmlDialog.menu.view_source")); //$NON-NLS-1$
      item.addListener(SWT.Selection, e->  {
            String file = System.getProperty("user.home") + File.separator + "out.html"; //$NON-NLS-1$ //$NON-NLS-2$
            try {
               XViewerLib.writeStringToFile(html, new File(file));
            } catch (IOException ex) {
               XViewerLog.logAndPopup(Activator.class, Level.SEVERE, ex);
            }
            Program.launch(file);
      });
      return menu;
   }

}
