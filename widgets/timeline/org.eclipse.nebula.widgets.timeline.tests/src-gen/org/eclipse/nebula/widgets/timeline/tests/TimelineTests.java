/**
 */
package org.eclipse.nebula.widgets.timeline.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

/**
 * <!-- begin-user-doc -->
 * A test suite for the '<em><b>timeline</b></em>' package.
 * <!-- end-user-doc -->
 * @generated
 */
public class TimelineTests extends TestSuite {

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static Test suite() {
		TestSuite suite = new TimelineTests("timeline Tests");
		suite.addTestSuite(TimelineTest.class);
		suite.addTestSuite(TrackTest.class);
		suite.addTestSuite(LaneTest.class);
		suite.addTestSuite(TimelineEventTest.class);
		suite.addTestSuite(CursorTest.class);
		return suite;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TimelineTests(String name) {
		super(name);
	}

} //TimelineTests
