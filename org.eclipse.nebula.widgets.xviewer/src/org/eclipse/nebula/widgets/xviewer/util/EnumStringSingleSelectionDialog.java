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
package org.eclipse.nebula.widgets.xviewer.util;

import java.util.Collection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.nebula.widgets.xviewer.util.internal.StringLabelProvider;
import org.eclipse.nebula.widgets.xviewer.util.internal.StringViewerSorter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class EnumStringSingleSelectionDialog extends org.eclipse.ui.dialogs.ListDialog {

   public EnumStringSingleSelectionDialog(String title, String message, Collection<String> options, String currSelected) {
      super(Display.getCurrent().getActiveShell());
      this.setTitle(title);
      this.setMessage(message);
      this.setContentProvider(new ArrayContentProvider());
      setLabelProvider(new StringLabelProvider());
      setInput(options);
      if (currSelected != null) setInitialSelections(new Object[] {currSelected});
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control c = super.createDialogArea(container);
      getTableViewer().setSorter(new StringViewerSorter());
      return c;
   }

}
