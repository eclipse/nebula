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
package org.eclipse.nebula.widgets.xviewer;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;

/**
 * @author Andrew M. Finkbeiner
 */
public class XSubMenuManager extends MenuManager {

   private boolean visible = true;

   public XSubMenuManager(String string) {
      super(string);
   }

   @Override
   public boolean isEnabled() {
      return true;
   }

   @Override
   public boolean isVisible() {
      return visible;
   }

   @Override
   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   public void setEnabled(boolean enabled) {
      this.add(new Separator());
   }
}
