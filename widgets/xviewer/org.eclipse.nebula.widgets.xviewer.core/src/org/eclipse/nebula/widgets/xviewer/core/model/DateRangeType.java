/*******************************************************************************
 * Copyright (c) 2016 Boeing.
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

public enum DateRangeType {

   None("None"),
   Equals_Date("Equals Date"),
   Before_Date("Before Date"),
   After_Date("After Date"),
   Between_Dates("Between Dates");

   private final String displayName;

   private DateRangeType(String displayName) {
      this.displayName = displayName;
   }

   public String getDisplayName() {
      return displayName;
   }

   public static DateRangeType get(String value) {
      DateRangeType result = DateRangeType.None;
      for (DateRangeType type : values()) {
         if (type.name().equals(value) || type.getDisplayName().equals(value)) {
            result = type;
            break;
         }
      }
      return result;
   }
}
