/**
 */
package org.eclipse.nebula.widgets.timeline.tests;

import junit.framework.TestCase;

import junit.textui.TestRunner;

import java.util.concurrent.TimeUnit;

import org.eclipse.nebula.widgets.timeline.ICursor;
import org.eclipse.nebula.widgets.timeline.ITimeline;
import org.eclipse.nebula.widgets.timeline.ITimelineFactory;
import org.eclipse.nebula.widgets.timeline.ITrack;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Timeline</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following operations are tested:
 * <ul>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.ITimeline#createTrack(java.lang.String) <em>Create Track</em>}</li>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.ITimeline#createCursor(long, java.util.concurrent.TimeUnit) <em>Create Cursor</em>}</li>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.ITimeline#createCursor(long) <em>Create Cursor</em>}</li>
 * </ul>
 * </p>
 * @generated
 */
public class TimelineTest extends TestCase {

	/**
	 * The fixture for this Timeline test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ITimeline fixture = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(TimelineTest.class);
	}

	/**
	 * Constructs a new Timeline test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TimelineTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Timeline test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(ITimeline fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Timeline test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ITimeline getFixture() {
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
		setFixture(ITimelineFactory.eINSTANCE.createTimeline());
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
	 * Tests the '{@link org.eclipse.nebula.widgets.timeline.ITimeline#createTrack(java.lang.String) <em>Create Track</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.nebula.widgets.timeline.ITimeline#createTrack(java.lang.String)
	 * @generated NOT
	 */
	public void testCreateTrack__String() {
		ITrack track = getFixture().createTrack("Testing");
		
		assertNotNull(track);
		assertEquals("Testing", track.getTitle());
		assertTrue(getFixture().getTracks().contains(track));
	}

	/**
	 * Tests the '{@link org.eclipse.nebula.widgets.timeline.ITimeline#createCursor(long, java.util.concurrent.TimeUnit) <em>Create Cursor</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.nebula.widgets.timeline.ITimeline#createCursor(long, java.util.concurrent.TimeUnit)
	 * @generated NOT
	 */
	public void testCreateCursor__long_TimeUnit() {
		ICursor cursor = getFixture().createCursor(1234, TimeUnit.MICROSECONDS);
		
		assertNotNull(cursor);
		assertEquals(1234*1000, cursor.getTimestamp());
		
		assertTrue(getFixture().getCursors().contains(cursor));
	}

	/**
	 * Tests the '{@link org.eclipse.nebula.widgets.timeline.ITimeline#createCursor(long) <em>Create Cursor</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.nebula.widgets.timeline.ITimeline#createCursor(long)
	 * @generated NOT
	 */
	public void testCreateCursor__long() {
		ICursor cursor = getFixture().createCursor(1234);
		
		assertNotNull(cursor);
		assertEquals(1234, cursor.getTimestamp());
		
		assertTrue(getFixture().getCursors().contains(cursor));
	}

} //TimelineTest
