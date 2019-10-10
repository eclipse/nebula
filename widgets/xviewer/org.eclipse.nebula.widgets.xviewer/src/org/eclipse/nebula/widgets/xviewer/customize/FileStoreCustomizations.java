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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerText;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.nebula.widgets.xviewer.util.internal.FileUtil;
import org.eclipse.nebula.widgets.xviewer.util.internal.XViewerLog;

/**
 * @author Andrew M. Finkbeiner
 */
public class FileStoreCustomizations implements IXViewerCustomizations {

   private final File baseStorage;
   private final String prefix;
   private final String postfix;
   private final String defaultCustomizationFileName;
   private final CustomizeData defaultCustomData;

   public FileStoreCustomizations(File pathToCustomizations, String fileNamePrefix, String fileNamePostfix, String defaultCustomizationFileName, String defaultCustomDataXml) {
      this.baseStorage = pathToCustomizations;
      this.prefix = fileNamePrefix;
      this.postfix = fileNamePostfix;
      this.defaultCustomizationFileName = defaultCustomizationFileName;
      this.defaultCustomData = new CustomizeData(defaultCustomDataXml);
   }

   @Override
   public void deleteCustomization(CustomizeData custData) throws Exception {
      File file = new File(getFilename(custData));
      if (file.exists()) {
         boolean success = file.delete();
         if (!success) {
            throw new XViewerException(XViewerText.get("error.delete_customization")); //$NON-NLS-1$
         }
      }
   }

   @Override
   public List<CustomizeData> getSavedCustDatas() throws XViewerException {
      List<CustomizeData> custDatas = new ArrayList<CustomizeData>();
      for (String filename : FileUtil.readListFromDir(getCustomDataDir(), new FilenameFilter() {
         @Override
         public boolean accept(File dir, String name) {
            if (name.endsWith(postfix) && name.startsWith(prefix)) {
               return true;
            }
            return false;
         }
      })) {
         custDatas.add(new CustomizeData(FileUtil.fileToString(new File(getCustomDataDir(), filename))));
      }
      return custDatas;
   }

   @Override
   public CustomizeData getUserDefaultCustData() throws XViewerException {
      File file = new File(getDefaultFilename());
      if (!file.exists()) {
         return defaultCustomData;
      } else {
         String defaultGuid;
         defaultGuid = FileUtil.fileToString(file).replaceAll("\\s", ""); //$NON-NLS-1$ //$NON-NLS-2$
         if (defaultGuid != null) {
            for (CustomizeData custData : getSavedCustDatas()) {
               if (custData.getGuid().equals(defaultGuid)) {
                  return custData;
               }
            }
         }
      }
      return null;
   }

   @Override
   public boolean isCustomizationPersistAvailable() {
      return true;
   }

   @Override
   public boolean isCustomizationUserDefault(CustomizeData custData) throws XViewerException {
      File file = new File(getDefaultFilename());
      if (!file.exists()) {
         return false;
      }
      String defaultGuid;
      defaultGuid = FileUtil.fileToString(new File(getDefaultFilename())).replaceAll("\\s", ""); //$NON-NLS-1$ //$NON-NLS-2$
      return custData.getGuid().equals(defaultGuid);
   }

   @Override
   public void saveCustomization(CustomizeData custData) throws Exception {
      FileUtil.writeStringToFile(custData.getXml(true), new File(getFilename(custData)));
   }

   private String getFilename(CustomizeData custData) {
      return new File(getCustomDataDir(), prefix + custData.getGuid() + postfix).getAbsolutePath();
   }

   private String getDefaultFilename() {
      return new File(getCustomDataDir(), defaultCustomizationFileName).getAbsolutePath();
   }

   private File getCustomDataDir() {
      return baseStorage;
   }

   @Override
   public void setUserDefaultCustData(CustomizeData newCustData, boolean set) throws XViewerException {
      if (set) {
         try {
            FileUtil.writeStringToFile(newCustData.getGuid(), new File(getDefaultFilename()));
         } catch (IOException ex) {
            XViewerLog.log(FileStoreCustomizations.class, Level.SEVERE, ex);
         }
      } else {
         File file = new File(getDefaultFilename());
         if (file.exists()) {
            boolean success = file.delete();
            if (!success) {
               throw new XViewerException(XViewerText.get("error.delete_customization")); //$NON-NLS-1$
            }
         }
      }
   }
}
