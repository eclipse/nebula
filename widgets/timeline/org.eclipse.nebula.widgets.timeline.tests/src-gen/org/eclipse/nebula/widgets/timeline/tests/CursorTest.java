/**
 */
package org.eclipse.nebula.widgets.timeline.tests;

import junit.framework.TestCase;

import junit.textui.TestRunner;

import java.util.concurrent.TimeUnit;

import org.eclipse.nebula.widgets.timeline.ICursor;
import org.eclipse.nebula.widgets.timeline.ITimelineFactory;
import org.eclipse.nebula.widgets.timeline.Timing;

/**
 * <!-- begin-user-doc --> A test case for the model object
 * '<em><b>Cursor</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following operations are tested:
 * <ul>
 * <li>{@link org.eclipse.nebula.widgets.timeline.ITimed#getTiming() <em>Get
 * Timing</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class CursorTest extends TestCase {

	/**
	 * The fixture for this Cursor test case. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	protected ICursor fixture = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(CursorTest.class);
	}

	/**
	 * Constructs a new Cursor test case with the given name. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public CursorTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Cursor test case. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	protected void setFixture(ICursor fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Cursor test case. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	protected ICursor getFixture() {
		return fixture;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see junit.framework.TestCase#setUp()
	 * @generated
	 */
	@Override
	protected void setUp() throws Exception {
		setFixture(ITimelineFactory.eINSTANCE.createCursor());
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 * @generated
	 */
	@Override
	protected void tearDown() throws Exception {
		setFixture(null);
	}

	/**
	 * Tests the '{@link org.eclipse.nebula.widgets.timeline.ITimed#getTiming()
	 * <em>Get Timing</em>}' operation. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see org.eclipse.nebula.widgets.timeline.ITimed#getTiming()
	 * @generated NOT
	 */
	public void testGetTiming() {
		getFixture().setTimestamp(100);

		Timing timing = getFixture().getTiming();
		assertEquals(getFixture().getTimestamp(), timing.getTimestamp(), 0.1);
		assertEquals(0.0, timing.getDuration());
	}

} // CursorTest
