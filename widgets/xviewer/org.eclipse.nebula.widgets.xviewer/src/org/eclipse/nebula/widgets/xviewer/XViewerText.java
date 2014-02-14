package org.eclipse.nebula.widgets.xviewer;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLog;

/**
 * @author Web Performance Inc, Copyright 2010
 */
public class XViewerText {
   private static ResourceBundle TEXT = ResourceBundle.getBundle(XViewerText.class.getName());

   public static String get(String key) {
      try {
         return TEXT.getString(key);
      }
      catch (MissingResourceException e) {
         // This error isn't necessarily severe, but using SEVERE puts a stack trace in the log - making it much
         // easier to find where the missing resource was used.
         // It would likely be much more severe if we let this exception propogate up.
         XViewerLog.log(XViewerText.class, Level.SEVERE, "Text not found for key: " + key);   //$NON-NLS-1$
         return "!!" + key + "!!"; //$NON-NLS-1$ //$NON-NLS-2$
      }
   }

   public static String get(String key, String... params) {
      String string;
      try {
         string = TEXT.getString(key);
      }
      catch (MissingResourceException e) {
         // This error isn't necessarily severe, but using SEVERE puts a stack trace in the log - making it much
         // easier to find where the missing resource was used.
         // It would likely be much more severe if we let this exception propogate up.
         XViewerLog.log(XViewerText.class, Level.SEVERE, "Text not found for key: " + key); //$NON-NLS-1$
         return "!!" + key + "!!"; //$NON-NLS-1$ //$NON-NLS-2$
      }
      return MessageFormat.format(string, (Object[]) params);
   }
}


