/*******************************************************************************
 * Copyright (c) 2010 Boeing.
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
package org.eclipse.nebula.widgets.xviewer.util.internal;

import java.util.Date;

/**
 * @author Donald G. Dunne
 */
public class ElapsedTime {

   Date startDate;
   Date endDate;
   private String name;
   private final boolean logStart;

   public ElapsedTime(String name) {
      this(name, false);
   }

   public ElapsedTime(String name, boolean logStart) {
      this.logStart = logStart;
      start(name);
   }

   public void start(String name) {
      this.name = name;
      startDate = new Date();
      if (logStart) {
         System.err.println("\n" + name + " - start " + DateUtil.getTimeStamp());
      }
   }

   public void logPoint(String pointName) {
      System.err.println("\n" + name + " - [" + pointName + "] " + DateUtil.getTimeStamp());
   }

   public static enum Units {
      SEC,
      MSEC,
      MIN
   }

   public String end() {
      return end(Units.SEC);
   }

   public String end(Units units) {
      return end(units, true);
   }

   public String end(Units units, boolean printToSysErr) {
      endDate = new Date();
      long timeSpent = endDate.getTime() - startDate.getTime();
      long time = timeSpent; // milliseconds
      String milliseconds = "";
      if (units == Units.SEC) {
         time = time / 1000; // convert from milliseconds to seconds
         milliseconds = "";
      } else if (units == Units.MIN) {
         time = time / 60000; // convert from milliseconds to minutes
         milliseconds = " ( " + timeSpent + " ms ) ";
      }
      String str = String.format("%s- elapsed %d %s%s - start %s - end %s", name, time, units.name(), milliseconds,
         DateUtil.getDateStr(startDate, DateUtil.HHMMSSSS), DateUtil.getDateStr(endDate, DateUtil.HHMMSSSS));
      if (printToSysErr) {
         System.err.println(str + (logStart ? "" : "\n"));
      }
      return str;
   }

   /**
    * Milliseconds spent so far. Does not call end().
    */
   public Long getTimeSpent() {
      Date endDate = new Date();
      return endDate.getTime() - startDate.getTime();
   }
}
