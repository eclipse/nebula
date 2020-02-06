/**
 */
package org.eclipse.nebula.widgets.timeline.tests;

import java.util.concurrent.TimeUnit;

import org.eclipse.nebula.widgets.timeline.ILane;
import org.eclipse.nebula.widgets.timeline.ITimelineEvent;
import org.eclipse.nebula.widgets.timeline.ITimelineFactory;

import junit.textui.TestRunner;

/**
 * <!-- begin-user-doc --> A test case for the model object '<em><b>Lane</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following operations are tested:
 * <ul>
 * <li>{@link org.eclipse.nebula.widgets.timeline.ILane#createEvent(java.lang.String, java.lang.String, long, long, java.util.concurrent.TimeUnit) <em>Create
 * Event</em>}</li>
 * <li>{@link org.eclipse.nebula.widgets.timeline.ILane#createEvent(java.lang.String, java.lang.String, long, long) <em>Create Event</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class LaneTest extends ColoredTest {

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(LaneTest.class);
	}

	/**
	 * Constructs a new Lane test case with the given name. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public LaneTest(String name) {
		super(name);
	}

	/**
	 * Returns the fixture for this Lane test case. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected ILane getFixture() {
		return (ILane) fixture;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see junit.framework.TestCase#setUp()
	 * @generated
	 */
	@Override
	protected void setUp() throws Exception {
		setFixture(ITimelineFactory.eINSTANCE.createLane());
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
	 * Tests the '{@link org.eclipse.nebula.widgets.timeline.ILane#createEvent(java.lang.String, java.lang.String, long, long, java.util.concurrent.TimeUnit)
	 * <em>Create Event</em>}' operation. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.nebula.widgets.timeline.ILane#createEvent(java.lang.String, java.lang.String, long, long, java.util.concurrent.TimeUnit)
	 * @generated NOT
	 */
	public void testCreateEvent__String_String_long_long_TimeUnit() {
		final ITimelineEvent event = getFixture().createEvent("title", "message", 100, 200, TimeUnit.MICROSECONDS);

		assertNotNull(event);
		assertEquals("title", event.getTitle());
		assertEquals("message", event.getMessage());
		assertEquals(100 * 1000, event.getStartTimestamp());
		assertEquals(200 * 1000, event.getDuration());

		assertTrue(getFixture().getTimeEvents().contains(event));
	}

	/**
	 * Tests the '{@link org.eclipse.nebula.widgets.timeline.ILane#createEvent(java.lang.String, java.lang.String, long, long) <em>Create Event</em>}'
	 * operation. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.nebula.widgets.timeline.ILane#createEvent(java.lang.String, java.lang.String, long, long)
	 * @generated NOT
	 */
	public void testCreateEvent__String_String_long_long() {
		final ITimelineEvent event = getFixture().createEvent("title", "message", 100, 200);

		assertNotNull(event);
		assertEquals("title", event.getTitle());
		assertEquals("message", event.getMessage());
		assertEquals(100, event.getStartTimestamp());
		assertEquals(200, event.getDuration());

		assertTrue(getFixture().getTimeEvents().contains(event));
	}
} // LaneTest
