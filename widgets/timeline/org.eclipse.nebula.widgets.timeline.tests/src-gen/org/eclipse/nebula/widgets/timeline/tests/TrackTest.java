/**
 */
package org.eclipse.nebula.widgets.timeline.tests;

import junit.framework.TestCase;

import junit.textui.TestRunner;

import org.eclipse.nebula.widgets.timeline.ICursor;
import org.eclipse.nebula.widgets.timeline.ILane;
import org.eclipse.nebula.widgets.timeline.ITimelineFactory;
import org.eclipse.nebula.widgets.timeline.ITrack;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Track</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following operations are tested:
 * <ul>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.ITrack#createLane() <em>Create Lane</em>}</li>
 * </ul>
 * </p>
 * @generated
 */
public class TrackTest extends TestCase {

	/**
	 * The fixture for this Track test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ITrack fixture = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(TrackTest.class);
	}

	/**
	 * Constructs a new Track test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TrackTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Track test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(ITrack fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Track test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ITrack getFixture() {
		return fixture;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated
	 */
	@Override
	protected void setUp() throws Exception {
		setFixture(ITimelineFactory.eINSTANCE.createTrack());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#tearDown()
	 * @generated
	 */
	@Override
	protected void tearDown() throws Exception {
		setFixture(null);
	}

	/**
	 * Tests the '{@link org.eclipse.nebula.widgets.timeline.ITrack#createLane() <em>Create Lane</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.nebula.widgets.timeline.ITrack#createLane()
	 * @generated NOT
	 */
	public void testCreateLane() {
		ILane lane = getFixture().createLane();
		
		assertNotNull(lane);
		
		assertTrue(getFixture().getLanes().contains(lane));
	}

} //TrackTest
