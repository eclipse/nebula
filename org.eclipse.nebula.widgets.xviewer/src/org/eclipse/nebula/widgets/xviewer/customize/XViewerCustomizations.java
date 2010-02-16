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

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of IViewerCustomizations interface
 * 
 * @author Donald G. Dunne
 */
public class XViewerCustomizations implements IXViewerCustomizations {

   public void deleteCustomization(CustomizeData custData) throws Exception {
   }

   public List<CustomizeData> getSavedCustDatas() {
      return new ArrayList<CustomizeData>();
   }

   public CustomizeData getUserDefaultCustData() {
      return null;
   }

   public boolean isCustomizationUserDefault(CustomizeData custData) {
      return false;
   }

   public void saveCustomization(CustomizeData custData) throws Exception {
   }

   public void setUserDefaultCustData(CustomizeData newCustData, boolean set) {
   }

   public boolean isCustomizationPersistAvailable() {
      return false;
   }

}
