/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.compositetable.day;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @since 3.2
 *
 */
public class CalendarControlTestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.eclipse.nebula.widgets.compositetable.day");
		//$JUnit-BEGIN$
		suite.addTestSuite(DayModel_GetColumnsForEventsTest.class);
		suite.addTestSuite(CalendarableModel_InitTest.class);
		suite.addTestSuite(CalendarableModel_RefreshResultsTest.class);
		suite.addTestSuite(CalendarableModel_TimedFindMethodsTest.class);
		//$JUnit-END$
		return suite;
	}
	
}
