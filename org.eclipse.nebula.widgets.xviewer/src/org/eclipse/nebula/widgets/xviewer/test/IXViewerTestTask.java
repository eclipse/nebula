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
package org.eclipse.nebula.widgets.xviewer.test;

import java.util.Date;

/**
 * Interface for example objects used for example implementation of XViewer
 * 
 * @author Donald G. Dunne
 */
public interface IXViewerTestTask {
   public enum RunDb {
      Production_Db, Test_Db
   };

   public enum TaskType {
      Regression, Db_Health, Data_Exchange, Backup
   }

   public String getStartTime();

   public String getEmailAddress();

   public int getPercentComplete();

   public String getId();

   public TaskType getTaskType();

   public String getDescription();

   public RunDb getRunDb();

   public String getCategory();

   public Date getLastRunDate();

   public String getLastRunDateStr();
}
