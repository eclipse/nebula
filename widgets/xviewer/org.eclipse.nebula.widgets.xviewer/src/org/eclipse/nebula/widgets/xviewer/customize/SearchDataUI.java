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

package org.eclipse.nebula.widgets.xviewer.customize;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLib;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Provides UI for displaying/entering search string
 *
 * @author Andrew M. Finkbeiner
 */
public class SearchDataUI {

   private Text searchText;
   private final XViewer xViewer;
   private Matcher match;
   private boolean search = false;
   private Button regularExpression;
   private boolean regex;
   private final boolean searchRealTime;

   public SearchDataUI(XViewer xViewer, boolean searchRealTime) {
      this.xViewer = xViewer;
      this.searchRealTime = searchRealTime;
   }

   public void createWidgets(Composite bar) {

      Label label = new Label(bar, SWT.NONE);
      label.setText(XViewerText.get("SearchDataUI.prompt")); //$NON-NLS-1$
      label.setToolTipText(XViewerText.get("SearchDataUI.prompt.tooltip")); //$NON-NLS-1$
      GridData gd = new GridData(SWT.RIGHT, SWT.NONE, false, false);
      label.setLayoutData(gd);

      searchText = new Text(bar, SWT.SINGLE | SWT.BORDER);

      gd = new GridData(SWT.RIGHT, SWT.NONE, false, false);
      gd.widthHint = 100;
      searchText.setLayoutData(gd);

      searchText.addListener(SWT.KeyUp, e->  {
            if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR || searchRealTime) {
               String newText = searchText.getText();
               if (newText.trim().length() == 0) {
                  search = false;
                  match = Pattern.compile(searchText.getText()).matcher(""); //$NON-NLS-1$
               } else {
                  regex = true;
                  if (!regularExpression.getSelection()) {
                     regex = false;
                     newText = newText.replace("*", ".*"); //$NON-NLS-1$ //$NON-NLS-2$
                     newText = ".*" + newText + ".*"; //$NON-NLS-1$ //$NON-NLS-2$
                  }
                  match =
                     Pattern.compile(newText, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE).matcher(""); //$NON-NLS-1$
                  search = true;
               }
               xViewer.refresh();
            }
      });

      Label searchLabel = new Label(bar, SWT.NONE);
      searchLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false));
      searchLabel.setImage(XViewerLib.getImage("clear.gif")); //$NON-NLS-1$

      regularExpression = new Button(bar, SWT.CHECK);
      regularExpression.setText(XViewerText.get("regex.prompt")); //$NON-NLS-1$
      regularExpression.setToolTipText(XViewerText.get("regex.prompt.tooltip")); //$NON-NLS-1$
      regularExpression.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false));
      searchLabel.addListener(SWT.MouseUp, event -> {
            searchText.setText(""); //$NON-NLS-1$
            search = false;
            match = Pattern.compile(searchText.getText()).matcher(""); //$NON-NLS-1$
            xViewer.refresh();
      });
   }

   public void clear() {
      searchText.setText(""); //$NON-NLS-1$
      xViewer.getCustomizeMgr().setFilterText("", false); //$NON-NLS-1$
   }

   public void appendToStatusLabel(StringBuilder sb) {
      if (searchText != null && !searchText.getText().equals("")) { //$NON-NLS-1$
         sb.append(XViewerText.get("SearchDataUI.status")); //$NON-NLS-1$
      }
   }

   public boolean match(String textString) {
      if (search) {
         if (regex) {
            match.reset(textString);
            return match.matches();
         } else {
            match.reset(textString);
            return match.matches();
         }
      } else {
         return false;
      }
   }

   public boolean isSearch() {
      return search;
   }

}
