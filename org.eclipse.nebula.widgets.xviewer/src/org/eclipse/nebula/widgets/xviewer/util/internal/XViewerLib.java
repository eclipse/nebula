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
package org.eclipse.nebula.widgets.xviewer.util.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.xviewer.Activator;
import org.eclipse.nebula.widgets.xviewer.util.internal.images.XViewerImageCache;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class XViewerLib {
   static Random random = new Random();
   private static final Date today = new Date();
   public static int MILLISECS_PER_DAY = (1000 * 60 * 60 * 24);

   public static long daysTillToday(Date date) {
      return (date.getTime() - today.getTime()) / MILLISECS_PER_DAY;
   }

   public static String generateGuidStr() {
      long rand = (random.nextLong() & 0x7FFFFFFFFFFFFFFFL) | 0x4000000000000000L;
      return Long.toString(rand, 32) + Long.toString(System.currentTimeMillis() & 0xFFFFFFFFFFFFFL, 32);
   }

   public static void writeStringToFile(String str, File outFile) throws IOException {
      OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8");
      char[] chars = str.toCharArray();
      out.write(chars, 0, chars.length);
      out.close();
   }

   public static void popup(final String title, final String message) {
      if (!PlatformUI.isWorkbenchRunning()) {
         XViewerLog.log(Activator.class, Level.SEVERE, message);
      } else {
         ensureInDisplayThread(new Runnable() {
            public void run() {
               MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title,
                     message);
            }
         });
      }
   }

   public static GridLayout getZeroMarginLayout(int numColumns, boolean equalColumnWidth) {
      GridLayout layout = new GridLayout();
      layout.numColumns = numColumns;
      layout.makeColumnsEqualWidth = equalColumnWidth;
      layout.verticalSpacing = 0;
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      return layout;
   }

   public static GridLayout getZeroMarginLayout() {
      return getZeroMarginLayout(1, false);
   }

   public static ArrayList<String> readListFromDir(File directory, FilenameFilter filter, boolean keepExtension) {
      ArrayList<String> list = new ArrayList<String>(400);

      if (directory == null) {
         System.out.println("Invalid path: " + directory);
         return list;
      }

      File[] files = directory.listFiles(filter);
      if (files == null) {
         System.out.println("Invalid path: " + directory);
         return list;
      }
      if (files.length > 0) {
         Arrays.sort(files);
      }

      if (keepExtension) {
         for (int i = 0; i < files.length; i++) {
            list.add(files[i].getName());
         }
      } else {
         for (int i = 0; i < files.length; i++) {
            list.add(removeExtension(files[i].getName()));
         }
      }

      return list;
   }

   public static ArrayList<String> readListFromDir(String directory, FilenameFilter filter) {
      return readListFromDir(new File(directory), filter, false);
   }

   /**
    * Remove the file extension from the file path
    * 
    * @param filepath
    * @return modified file path
    */
   public static String removeExtension(String filepath) {
      String ext = getExtension(filepath);
      if (ext != null && ext.length() > 0) {
         filepath = filepath.substring(0, filepath.length() - (ext.length() + 1));
      }
      return filepath;
   }

   /**
    * Determine is OS is windows
    * 
    * @return <b>true</b> if OS is windows
    */
   public static boolean isWindows() {
      return System.getProperty("os.name").indexOf("indows") != -1;
   }

   /**
    * Get file extension from the file path
    * 
    * @param filepath
    * @return file extension
    */
   public static String getExtension(String filepath) {
      filepath = filepath.trim();
      String separatorRegEx = File.separator;
      if (isWindows()) {
         separatorRegEx = "\\\\";
      }
      String[] pathsArray = filepath.split(separatorRegEx);

      String fileName = pathsArray[0];
      if (pathsArray.length > 0) {
         fileName = pathsArray[pathsArray.length - 1];
      }

      int index = fileName.lastIndexOf('.');
      if (index >= 0 && index + 1 < fileName.length()) {
         return fileName.substring(index + 1);
      } else {
         return "";
      }
   }

   public static void ensureInDisplayThread(Runnable runnable) {
      ensureInDisplayThread(runnable, false);
   }

   public static void ensureInDisplayThread(Runnable runnable, boolean forcePend) {
      if (isDisplayThread()) {
         // No need to check for force since this will always pend
         runnable.run();
      } else {
         if (forcePend) {
            Display.getDefault().syncExec(runnable);
         } else {
            Display.getDefault().asyncExec(runnable);
         }
      }
   }

   public static boolean isDisplayThread() {
      if (Display.getCurrent() == null) return false;

      return Display.getCurrent().getThread() == Thread.currentThread();
   }

   public static Image getImage(String imageName) {
      return XViewerImageCache.getImage(imageName);
   }

   public static ImageDescriptor getImageDescriptor(String imageName) {
      return XViewerImageCache.getImageDescriptor(imageName);
   }

   public static String doubleToI18nString(double d) {
      return doubleToI18nString(d, false);
   }

   public static String doubleToI18nString(double d, boolean blankIfZero) {
      if (blankIfZero && d == 0) {
         return "";
      } else {
         return String.format("%4.2f", d);
      }
   }
}
