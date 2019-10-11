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

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Donald G. Dunne
 */
public class Activator extends AbstractUIPlugin {

   // The plug-in ID
   public static final String PLUGIN_ID = "org.eclipse.nebula.widgets.xviewer";

   // The shared instance
   private static Activator plugin;
   protected static final String imagePath = "images/";
   private ImageRegistry imageRegistry;

   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);
      plugin = this;
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      plugin = null;
      if (imageRegistry != null) {
         imageRegistry.dispose();
      }
      super.stop(context);
   }

   public static Activator getDefault() {
      return plugin;
   }

   public static Activator getInstance() {
      return plugin;
   }

}
