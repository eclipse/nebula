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
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;

/**
 * Default implementation of IViewerCustomizations interface
 * 
 * @author Donald G. Dunne
 */
public class XViewerCustomizations implements IXViewerCustomizations {

   @Override
   public void deleteCustomization(CustomizeData custData) throws Exception {
      // provided for subclass implementation
   }

   @Override
   public List<CustomizeData> getSavedCustDatas() throws Exception {
      return new ArrayList<CustomizeData>();
   }

   @SuppressWarnings("unused")
   @Override
   public CustomizeData getUserDefaultCustData() throws XViewerException {
      return null;
   }

   @SuppressWarnings("unused")
   @Override
   public boolean isCustomizationUserDefault(CustomizeData custData) throws XViewerException {
      return false;
   }

   @Override
   public void saveCustomization(CustomizeData custData) throws Exception {
      // provided for subclass implementation
   }

   @SuppressWarnings("unused")
   @Override
   public void setUserDefaultCustData(CustomizeData newCustData, boolean set) throws XViewerException {
      // provided for subclass implementation
   }

   @Override
   public boolean isCustomizationPersistAvailable() {
      return false;
   }

}
