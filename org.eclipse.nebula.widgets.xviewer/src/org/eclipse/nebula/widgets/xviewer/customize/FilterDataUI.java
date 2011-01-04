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

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.nebula.widgets.xviewer.util.XViewerLib;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * Provides UI for display of current filter
 * 
 * @author Donald G. Dunne
 */
public class FilterDataUI {

   private Text filterText;
   private final XViewer xViewer;
   private final boolean filterRealTime;

   public FilterDataUI(XViewer xViewer, boolean filterRealTime) {
      this.xViewer = xViewer;
      this.filterRealTime = filterRealTime;
   }

   public void createWidgets(Composite comp) {
      Label label = new Label(comp, SWT.NONE);
      label.setText(XViewerText.get("FilterDataUI.prompt"));
      label.setToolTipText(XViewerText.get("FilterDataUI.prompt.tooltip"));
      GridData gd = new GridData(SWT.RIGHT, SWT.NONE, false, false);
      label.setLayoutData(gd);

      filterText = new Text(comp, SWT.SINGLE | SWT.BORDER);
      gd = new GridData(SWT.RIGHT, SWT.NONE, false, false);
      gd.widthHint = 100;
      filterText.setLayoutData(gd);

      filterText.addKeyListener(new KeyListener() {
         @Override
         public void keyPressed(KeyEvent e) {
            // do nothing
         }

         @Override
         public void keyReleased(KeyEvent e) {
            // System.out.println(e.keyCode);
            if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR || filterRealTime) {
               xViewer.getCustomizeMgr().setFilterText(filterText.getText());
            }
         }
      });

      Label filterLabel = new Label(comp, SWT.NONE);
      filterLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false));
      filterLabel.setImage(XViewerLib.getImage("clear.gif"));
      filterLabel.addListener(SWT.MouseUp, new Listener() {
         @Override
         public void handleEvent(Event event) {
            filterText.setText("");
            xViewer.getCustomizeMgr().setFilterText("");
         }
      });
   }

   public void update() {
      if (!xViewer.getCustomizeMgr().getFilterText().equals(filterText.getText())) {
         filterText.setText(xViewer.getCustomizeMgr().getFilterText());
      }
   }

   public void setFocus() {
      if (filterText != null && !filterText.isDisposed()) {
         filterText.setFocus();
      }
   }

   public void dispose() {
      // provided for subclass implementation
   }

   public void addFilterTextListener(KeyListener keyListener) {
      if (filterText != null && !filterText.isDisposed()) {
         filterText.addKeyListener(keyListener);
      }
   }

   public void clear() {
      filterText.setText("");
      xViewer.getCustomizeMgr().setFilterText("");
   }

   public void appendToStatusLabel(StringBuffer sb) {
      if (filterText != null && !filterText.getText().equals("")) {
         sb.append(XViewerText.get("status.text_filter"));
      }
   }

}
