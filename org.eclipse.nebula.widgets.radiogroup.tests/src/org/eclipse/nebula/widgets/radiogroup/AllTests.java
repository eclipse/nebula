package org.eclipse.nebula.widgets.radiogroup;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.eclipse.nebula.widgets.radiogroup.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(RadioGroupTests.class);
		
		//$JUnit-END$
		return suite;
	}

}
