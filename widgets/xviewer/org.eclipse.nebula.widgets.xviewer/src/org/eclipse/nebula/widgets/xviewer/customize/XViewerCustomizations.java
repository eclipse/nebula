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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
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
      return new ArrayList<>();
   }

   @Override
   public CustomizeData getUserDefaultCustData() throws XViewerException {
      return null;
   }

   @Override
   public boolean isCustomizationUserDefault(CustomizeData custData) throws XViewerException {
      return false;
   }

   @Override
   public void saveCustomization(CustomizeData custData) throws Exception {
      // provided for subclass implementation
   }

   @Override
   public void setUserDefaultCustData(CustomizeData newCustData, boolean set) throws XViewerException {
      // provided for subclass implementation
   }

   @Override
   public boolean isCustomizationPersistAvailable() {
      return false;
   }

}
