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
package org.eclipse.nebula.widgets.xviewer.example.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Example object for use of example XViewer implementation
 * 
 * @author Donald G. Dunne
 */
public class SomeTask implements ISomeTask {

   private final RunDb runDb;
   private TaskType taskType;
   private final String id;
   private final String startTime;
   private final String description;
   private final String category;
   private final String emailAddress;
   private Date lastRunDate;
   private int percentComplete;
   private final List<SomeTask> children = new ArrayList<SomeTask>();
   private final long longValue;

   public SomeTask(RunDb runDb, TaskType taskType, String id, String startTime, String description, String category, String emailAddress, int percentComplete) {
      this(runDb, taskType, new Date(), id, startTime, description, category, emailAddress, percentComplete, 50000);
   }

   public SomeTask(RunDb runDb, TaskType taskType, Date lastRunDate, String id, String startTime, String description, String category, String emailAddress, int percentComplete, long longValue) {
      this.runDb = runDb;
      this.taskType = taskType;
      this.lastRunDate = lastRunDate;
      this.id = id;
      this.startTime = startTime;
      this.description = description;
      this.category = category;
      this.emailAddress = emailAddress;
      this.percentComplete = percentComplete;
      this.longValue = longValue;
   }

   public void addChild(SomeTask someTask) {
      children.add(someTask);
   }

   public Collection<SomeTask> getChildren() {
      return children;

   }

   @Override
   public RunDb getRunDb() {
      return runDb;
   }

   @Override
   public TaskType getTaskType() {
      return taskType;
   }

   @Override
   public String getId() {
      return id;
   }

   @Override
   public String getStartTime() {
      return startTime;
   }

   @Override
   public String getDescription() {
      return description;
   }

   @Override
   public String getCategory() {
      return category;
   }

   @Override
   public String getEmailAddress() {
      return emailAddress;
   }

   @Override
   public Date getLastRunDate() {
      return lastRunDate;
   }

   @Override
   public String getLastRunDateStr() {
      Date date = getLastRunDate();
      if (date == null) {
         return "";
      }
      return new SimpleDateFormat("MM/dd/yyyy hh:mm a").format(date);
   }

   @Override
   public int getPercentComplete() {
      return percentComplete;
   }

   public void setPercentComplete(int percentComplete) {
      this.percentComplete = percentComplete;
   }

   public void setLastRunDate(Date lastRunDate) {
      this.lastRunDate = lastRunDate;
   }

   @Override
   public long getLongValue() {
      return longValue;
   }

   public void setTaskType(TaskType taskType) {
      this.taskType = taskType;
   }

}
