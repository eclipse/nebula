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

import org.eclipse.nebula.widgets.xviewer.util.internal.XmlUtil;

/**
 * Provides storage of filter
 * 
 * @author Donald G. Dunne
 */
public class FilterData {

   private String filterText = "";
   private static final String FILTER_TAG = "xFilter";
   private static final String FILTER_REGEX_TAG = "xFilterRE";
   private boolean regularExpression;

   public String getFilterText() {
      return filterText;
   }

   public boolean isFiltering() {
      return filterText != null && !filterText.equals("");
   }

   public void setFilterText(String filterText, boolean regularExpression) {
      this.filterText = filterText;
      this.regularExpression = regularExpression;
   }

   public String getXml() {
      return XmlUtil.addTagData(FILTER_TAG, filterText) + XmlUtil.addTagDataBoolean(FILTER_REGEX_TAG, regularExpression);
   }

   public void setFromXml(String xml) {
      filterText = XmlUtil.getTagData(xml, FILTER_TAG);
      regularExpression = XmlUtil.getTagBooleanData(xml, FILTER_REGEX_TAG);
   }

   @Override
   public String toString() {
      return "filterData:[" + filterText + "]" + (regularExpression ? "-RE" : "");
   }

   public boolean isRegularExpression() {
      return regularExpression;
   }

   public void setRegularExpression(boolean regularExpression) {
      this.regularExpression = regularExpression;
   }

}
