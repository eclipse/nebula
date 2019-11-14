/*******************************************************************************
 * Copyright (c) 2019 Boeing.
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

package org.eclipse.nebula.widgets.xviewer;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLib;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

/**
 * Either call with html and open() or extend to provide getHtml().
 * 
 * @author Donald G. Dunne
 */
public class XViewerHtmlReport {

   protected final String title;
   protected final String html;

   public XViewerHtmlReport(String title) {
      this(title, null);
   }

   public XViewerHtmlReport(String title, String html) {
      this.title = title;
      this.html = html;
   }

   /**
    * Override to provide html
    */
   @SuppressWarnings("unused")
   protected String getHtml() throws XViewerException {
      return this.html;
   }

   public void open() {
      this.open(null);
   }

   public void open(String defaultFilename) {
      try {
         String html = getHtml();
         final FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell().getShell(), SWT.SAVE);
         dialog.setFilterExtensions(new String[] {"*.html"}); //$NON-NLS-1$
         if (defaultFilename != null && !defaultFilename.equals("")) { //$NON-NLS-1$
            dialog.setFileName(defaultFilename);
         }
         String filename = dialog.open();
         if (filename == null || filename.equals("")) { //$NON-NLS-1$
            return;
         }
         try {
            XViewerLib.writeStringToFile(html, new File(filename));
         } catch (IOException ex) {
            XViewerLog.log(Activator.class, Level.SEVERE, ex);
            return;
         }
         Program.launch(filename);
      } catch (Exception ex) {
         XViewerLog.logAndPopup(Activator.class, Level.SEVERE, ex);
      }
   }

}
