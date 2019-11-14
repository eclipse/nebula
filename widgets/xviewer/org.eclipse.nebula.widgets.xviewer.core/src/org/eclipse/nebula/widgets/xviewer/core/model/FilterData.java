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
package org.eclipse.nebula.widgets.xviewer.core.model;

import org.eclipse.nebula.widgets.xviewer.core.util.XmlUtil;

/**
 * Provides storage of filter
 *
 * @author Donald G. Dunne
 */
public class FilterData {

   private String filterText = ""; //$NON-NLS-1$
   private static final String FILTER_TAG = "xFilter"; //$NON-NLS-1$
   private static final String FILTER_REGEX_TAG = "xFilterRE"; //$NON-NLS-1$
   private boolean regularExpression;

   public String getFilterText() {
      return filterText;
   }

   public boolean isFiltering() {
      return filterText != null && !filterText.equals(""); //$NON-NLS-1$
   }

   public void setFilterText(String filterText, boolean regularExpression) {
      this.filterText = filterText;
      this.regularExpression = regularExpression;
   }

   public String getXml() {
      return XmlUtil.addTagData(FILTER_TAG, filterText) + XmlUtil.addTagDataBoolean(FILTER_REGEX_TAG,
         regularExpression);
   }

   public void setFromXml(String xml) {
      filterText = XmlUtil.getTagData(xml, FILTER_TAG);
      regularExpression = XmlUtil.getTagBooleanData(xml, FILTER_REGEX_TAG);
   }

   public boolean isRegularExpression() {
      return regularExpression;
   }

   public void setRegularExpression(boolean regularExpression) {
      this.regularExpression = regularExpression;
   }

   @Override
   public String toString() {
      return "FilterData [filterText=" + filterText + ", regEx=" + regularExpression + "]";
   }

}
