/*
 * Created on Dec 20, 2013
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.nebula.widgets.xviewer.util;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class XViewerDisplay {

   private static Boolean standalone = false;
   private static Display display = null;

   public static Display getDisplay() {
      Display result = display;
      if (result == null && !isStandaloneXViewer()) {
         result = PlatformUI.getWorkbench().getDisplay();
      }
      return result;
   }

   public static Boolean isStandaloneXViewer() {
      return standalone;
   }

   /**
    * @return true if this xviewer is embedded in Eclipse workbench and it's running
    */
   public static boolean isWorkbenchRunning() {
      return PlatformUI.isWorkbenchRunning();
   }

   public static void setStandaloneXViewer(boolean workbenchRunningOverride, Display display) {
      XViewerDisplay.standalone = workbenchRunningOverride;
      XViewerDisplay.display = display;
   }

}
