/*******************************************************************************
 * Copyright (c) 2015 Boeing.
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

import java.util.Date;

/**
 * @author Donald G. Dunne
 */
public class ColumnDateFilter {
   private DateRangeType type;
   private Date date1;
   private Date date2;

   public ColumnDateFilter(DateRangeType type, Date date1, Date date2) {
      this.type = type;
      this.date1 = date1;
      this.date2 = date2;
   }

   public DateRangeType getType() {
      return type;
   }

   public void setType(DateRangeType type) {
      this.type = type;
   }

   public Date getDate1() {
      return date1;
   }

   public void setDate1(Date date1) {
      this.date1 = date1;
   }

   public Date getDate2() {
      return date2;
   }

   public void setDate2(Date date2) {
      this.date2 = date2;
   }

   @Override
   public String toString() {
      return "colDateFilter [type=" + type + ", date1=" + date1 + ", date2=" + date2 + "]";
   }

}
