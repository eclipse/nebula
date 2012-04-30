/*******************************************************************************
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    jdowdall - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.calendarcombo.example;

import org.eclipse.nebula.examples.AbstractExampleTab;
import org.eclipse.nebula.widgets.calendarcombo.CalendarComboTester;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class CalendarComboExampleTab extends AbstractExampleTab {

   public CalendarComboExampleTab() {
      // TODO Auto-generated constructor stub
   }

   @Override
   public Control createControl(Composite parent) {
      CalendarComboTester.createExample(parent);
      return parent;
   }

   @Override
   public String[] createLinks() {
      return new String[] {
         "<a href=\"http://eclipse.org/nebula/widgets/calendarcombo/calendarcombo.php\">CalendarCombo Home Page</a>",
         "<a href=\"https://bugs.eclipse.org/bugs/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=&classification=Technology&product=Nebula&component=CalendarCombo&long_desc_type=allwordssubstr&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&status_whiteboard_type=allwordssubstr&status_whiteboard=&keywords_type=allwords&keywords=&emailtype1=substring&email1=&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=\">Bugs</a>"};
   }

   @Override
   public void createParameters(Composite parent) {
      // do nothing
   }
}
