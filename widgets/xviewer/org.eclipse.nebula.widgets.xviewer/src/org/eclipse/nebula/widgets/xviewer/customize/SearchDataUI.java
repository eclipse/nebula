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

package org.eclipse.nebula.widgets.xviewer.customize;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLib;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
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

      //	  ExpandBar bar = new ExpandBar(comp, SWT.V_SCROLL);

      Label label = new Label(bar, SWT.NONE);
      label.setText(XViewerText.get("SearchDataUI.prompt")); //$NON-NLS-1$
      label.setToolTipText(XViewerText.get("SearchDataUI.prompt.tooltip")); //$NON-NLS-1$
      GridData gd = new GridData(SWT.RIGHT, SWT.NONE, false, false);
      label.setLayoutData(gd);

      searchText = new Text(bar, SWT.SINGLE | SWT.BORDER);

      gd = new GridData(SWT.RIGHT, SWT.NONE, false, false);
      gd.widthHint = 100;
      searchText.setLayoutData(gd);

      searchText.addKeyListener(new KeyListener() {
         @Override
         public void keyPressed(KeyEvent e) {
            // do nothing
         }

         @Override
         public void keyReleased(KeyEvent e) {
            // System.out.println(e.keyCode);
            if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR || searchRealTime) {
               //               xViewer.getCustomizeMgr().setSearchText(searchText.getText());
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
         }
      });

      Label searchLabel = new Label(bar, SWT.NONE);
      searchLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false));
      searchLabel.setImage(XViewerLib.getImage("clear.gif")); //$NON-NLS-1$

      regularExpression = new Button(bar, SWT.CHECK);
      regularExpression.setText(XViewerText.get("regex.prompt")); //$NON-NLS-1$
      regularExpression.setToolTipText(XViewerText.get("regex.prompt.tooltip")); //$NON-NLS-1$
      regularExpression.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false));
      searchLabel.addListener(SWT.MouseUp, new Listener() {
         @Override
         public void handleEvent(Event event) {
            searchText.setText(""); //$NON-NLS-1$
            search = false;
            match = Pattern.compile(searchText.getText()).matcher(""); //$NON-NLS-1$
            xViewer.refresh();
         }
      });
   }

   public void clear() {
      searchText.setText(""); //$NON-NLS-1$
      xViewer.getCustomizeMgr().setFilterText("", false); //$NON-NLS-1$
   }

   public void appendToStatusLabel(StringBuffer sb) {
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
