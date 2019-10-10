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

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLib;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
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
   private Button regularExpression;

   public FilterDataUI(XViewer xViewer, boolean filterRealTime) {
      this.xViewer = xViewer;
      this.filterRealTime = filterRealTime;
   }

   public void createWidgets(Composite comp) {
      Label label = new Label(comp, SWT.NONE);
      label.setText(XViewerText.get("label.filter")); //$NON-NLS-1$
      label.setToolTipText(XViewerText.get("FilterDataUI.prompt.tooltip")); //$NON-NLS-1$
      GridData gd = new GridData(SWT.RIGHT, SWT.NONE, false, false);
      label.setLayoutData(gd);

      filterText = new Text(comp, SWT.SINGLE | SWT.BORDER);
      gd = new GridData(SWT.RIGHT, SWT.NONE, false, false);
      gd.widthHint = 100;
      filterText.setLayoutData(gd);

      filterText.addListener(SWT.KeyUp, e->  {
			if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR || filterRealTime) {
				// System.out.println(e.keyCode);
				String newText = filterText.getText();
				xViewer.getCustomizeMgr().setFilterText(newText, isRegularExpression());
			}
      });

      Label filterLabel = new Label(comp, SWT.NONE);
      filterLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false));
      filterLabel.setImage(XViewerLib.getImage("clear.gif")); //$NON-NLS-1$
      filterLabel.addListener(SWT.MouseUp, event -> {
            filterText.setText(""); //$NON-NLS-1$
            xViewer.getCustomizeMgr().setFilterText("", isRegularExpression()); //$NON-NLS-1$
      });

      regularExpression = new Button(comp, SWT.CHECK);
      regularExpression.setText(XViewerText.get("regex.prompt")); //$NON-NLS-1$
      regularExpression.setToolTipText(XViewerText.get("regex.prompt.tooltip")); //$NON-NLS-1$
      regularExpression.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false));
      regularExpression.addListener(SWT.Selection, e -> {
            xViewer.getCustomizeMgr().setFilterText(filterText.getText(), isRegularExpression());
      });

   }

   public boolean isRegularExpression() {
      if (regularExpression != null && !regularExpression.isDisposed()) {
         return regularExpression.getSelection();
      }
      return false;
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

   public void addFilterTextListener(KeyListener keyListener) {
      if (filterText != null && !filterText.isDisposed()) {
         filterText.addKeyListener(keyListener);
      }
   }

   public void clear() {
      filterText.setText(""); //$NON-NLS-1$
      xViewer.getCustomizeMgr().setFilterText("", isRegularExpression()); //$NON-NLS-1$
   }

   public void appendToStatusLabel(StringBuilder sb) {
      if (filterText != null && !filterText.getText().equals("")) { //$NON-NLS-1$
         sb.append(XViewerText.get("status.text_filter")); //$NON-NLS-1$
      }
   }

}
