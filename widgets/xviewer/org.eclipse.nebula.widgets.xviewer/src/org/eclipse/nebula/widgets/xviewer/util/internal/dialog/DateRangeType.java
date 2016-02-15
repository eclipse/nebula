/*
 * Created on Feb 15, 2016
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.nebula.widgets.xviewer.util.internal.dialog;

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
