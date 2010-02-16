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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.test.IXViewerTestTask.RunDb;
import org.eclipse.nebula.widgets.xviewer.test.IXViewerTestTask.TaskType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Example implementation of XViewer that shows usage of styled string label provider
 * 
 * @author Andrew M. Finkbeiner
 */
public class XViewerStyledStringLableProviderTest extends XViewer {
   private final Set<IXViewerTestTask> runList = new HashSet<IXViewerTestTask>();

   /**
    * @param parent
    * @param style
    * @param namespace
    * @param viewerFactory
    */
   public XViewerStyledStringLableProviderTest(Composite parent, int style) {
      super(parent, style, new XViewerTestFactory());
   }

   public boolean isScheduled(IXViewerTestTask autoRunTask) {
      return true;
   }

   public boolean isRun(IXViewerTestTask autoRunTask) {
      return runList.contains(autoRunTask);
   }

   public void setRun(IXViewerTestTask autoRunTask, boolean run) {
      if (run)
         runList.add(autoRunTask);
      else
         runList.remove(autoRunTask);
   }

   /**
    * @param args
    */
   public static void main(String[] args) {
      Display Display_1 = Display.getDefault();
      Shell Shell_1 = new Shell(Display_1, SWT.SHELL_TRIM);
      Shell_1.setText("XViewer Styled Text Test");
      Shell_1.setBounds(0, 0, 1000, 500);
      Shell_1.setLayout(new GridLayout());
      Shell_1.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.HORIZONTAL_ALIGN_BEGINNING));

      XViewerStyledStringLableProviderTest xViewerTest =
            new XViewerStyledStringLableProviderTest(Shell_1, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
      xViewerTest.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
      xViewerTest.setContentProvider(new XViewerTestContentProvider());
      xViewerTest.setLabelProvider(new XViewerTestStyledStringLabelProvider(xViewerTest));

      List<Object> tasks = new ArrayList<Object>();
      for (int x = 0; x < 1; x++) {
         tasks.addAll(getTestTasks());
      }
      System.err.println("Setting Input...");
      xViewerTest.setInput(tasks);
      Shell_1.open();
      while (!Shell_1.isDisposed()) {
         if (!Display_1.readAndDispatch()) {
            Display_1.sleep();
         }
      }

      Display_1.dispose();
   }

   private static List<IXViewerTestTask> getTestTasks() {
      List<IXViewerTestTask> tasks = new ArrayList<IXViewerTestTask>();
      tasks.add(new XViewerTestTask(RunDb.Test_Db, TaskType.Backup, "org.eclipse.osee.test1", "10:03",
            "run to test this", "Suite A", "mark", 99));
      tasks.add(new XViewerTestTask(RunDb.Production_Db, TaskType.Data_Exchange, "org.eclipse.osee.test2", "9:22",
            "run to test that", "Suite B", "john", 50));
      tasks.add(new XViewerTestTask(RunDb.Production_Db, TaskType.Backup, "org.eclipse.osee.test4", "8:23",
            "in this world", "Suite A", "john", 50));
      tasks.add(new XViewerTestTask(RunDb.Test_Db, TaskType.Backup, "org.eclipse.osee.test3", "23:01",
            "now is the time", "Suite B", "mike", 50));
      tasks.add(new XViewerTestTask(RunDb.Production_Db, TaskType.Db_Health, "org.eclipse.osee.test5", "7:32",
            "may be never", "Suite A", "steve", 100));
      tasks.add(new XViewerTestTask(RunDb.Test_Db, TaskType.Data_Exchange, "org.eclipse.osee.test14", "6:11",
            "how can this solve the problem", "Suite A", "steve", 50));
      tasks.add(new XViewerTestTask(RunDb.Production_Db, TaskType.Backup, "org.eclipse.osee.test6", "5:13",
            "run to test this", "Suite B", "john", 50));
      tasks.add(new XViewerTestTask(RunDb.Test_Db, TaskType.Db_Health, "org.eclipse.osee.test12", "23:15",
            "run to test this", "Suite A", "mike", 50));
      tasks.add(new XViewerTestTask(RunDb.Production_Db, TaskType.Backup, "org.eclipse.osee.test13", "4:01",
            "run to test this", "Suite B", "steve", 50));
      tasks.add(new XViewerTestTask(RunDb.Production_Db, TaskType.Data_Exchange, "org.eclipse.osee.test11", "3:16",
            "run to test this", "Suite A", "steve", 70));
      tasks.add(new XViewerTestTask(RunDb.Test_Db, TaskType.Backup, "org.eclipse.osee.test10", "5:01",
            "run to test this", "Suite C", "mike", 50));
      tasks.add(new XViewerTestTask(RunDb.Production_Db, TaskType.Data_Exchange, "org.eclipse.osee.test9", "4:27",
            "run to test this", "Suite C", "steve", 50));
      tasks.add(new XViewerTestTask(RunDb.Production_Db, TaskType.Regression, "org.eclipse.osee.test7", "2:37",
            "run to test this", "Suite C", "john", 50));
      tasks.add(new XViewerTestTask(RunDb.Test_Db, TaskType.Db_Health, "org.eclipse.osee.test8", "24:00",
            "run to test this", "Suite C", "mike", 20));
      return tasks;
   }
}
