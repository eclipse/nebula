/**
 */
package org.eclipse.nebula.widgets.timeline.tests;

import junit.textui.TestRunner;

import java.util.concurrent.TimeUnit;

import org.eclipse.nebula.widgets.timeline.ITimelineEvent;
import org.eclipse.nebula.widgets.timeline.ITimelineFactory;
import org.eclipse.nebula.widgets.timeline.Timing;

/**
 * <!-- begin-user-doc --> A test case for the model object
 * '<em><b>Event</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following operations are tested:
 * <ul>
 * <li>{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#getDuration()
 * <em>Get Duration</em>}</li>
 * <li>{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#setStartTimestamp(long, java.util.concurrent.TimeUnit)
 * <em>Set Start Timestamp</em>}</li>
 * <li>{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#setEndTimestamp(long, java.util.concurrent.TimeUnit)
 * <em>Set End Timestamp</em>}</li>
 * <li>{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#setDuration(long, java.util.concurrent.TimeUnit)
 * <em>Set Duration</em>}</li>
 * <li>{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#setDuration(long)
 * <em>Set Duration</em>}</li>
 * <li>{@link org.eclipse.nebula.widgets.timeline.ITimed#getTiming() <em>Get
 * Timing</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class TimelineEventTest extends ColoredTest {

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(TimelineEventTest.class);
	}

	/**
	 * Constructs a new Event test case with the given name. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public TimelineEventTest(String name) {
		super(name);
	}

	/**
	 * Returns the fixture for this Event test case. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected ITimelineEvent getFixture() {
		return (ITimelineEvent) fixture;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see junit.framework.TestCase#setUp()
	 * @generated
	 */
	@Override
	protected void setUp() throws Exception {
		setFixture(ITimelineFactory.eINSTANCE.createTimelineEvent());
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
	 * Tests the
	 * '{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#getDuration()
	 * <em>Get Duration</em>}' operation. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see org.eclipse.nebula.widgets.timeline.ITimelineEvent#getDuration()
	 * @generated NOT
	 */
	public void testGetDuration() {
		getFixture().setStartTimestamp(100, TimeUnit.NANOSECONDS);
		getFixture().setEndTimestamp(100, TimeUnit.MICROSECONDS);

		assertEquals(getFixture().getEndTimestamp() - getFixture().getStartTimestamp(), getFixture().getDuration());
	}

	/**
	 * Tests the
	 * '{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#setStartTimestamp(long, java.util.concurrent.TimeUnit)
	 * <em>Set Start Timestamp</em>}' operation. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see org.eclipse.nebula.widgets.timeline.ITimelineEvent#setStartTimestamp(long,
	 *      java.util.concurrent.TimeUnit)
	 * @generated NOT
	 */
	public void testSetStartTimestamp__long_TimeUnit() {
		getFixture().setStartTimestamp(100, TimeUnit.MICROSECONDS);

		assertEquals(100 * 1000, getFixture().getStartTimestamp());
	}

	/**
	 * Tests the
	 * '{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#setEndTimestamp(long, java.util.concurrent.TimeUnit)
	 * <em>Set End Timestamp</em>}' operation. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see org.eclipse.nebula.widgets.timeline.ITimelineEvent#setEndTimestamp(long,
	 *      java.util.concurrent.TimeUnit)
	 * @generated NOT
	 */
	public void testSetEndTimestamp__long_TimeUnit() {
		getFixture().setEndTimestamp(100, TimeUnit.MICROSECONDS);

		assertEquals(100 * 1000, getFixture().getEndTimestamp());
	}

	/**
	 * Tests the
	 * '{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#setDuration(long, java.util.concurrent.TimeUnit)
	 * <em>Set Duration</em>}' operation. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see org.eclipse.nebula.widgets.timeline.ITimelineEvent#setDuration(long,
	 *      java.util.concurrent.TimeUnit)
	 * @generated NOT
	 */
	public void testSetDuration__long_TimeUnit() {
		getFixture().setDuration(100, TimeUnit.MICROSECONDS);

		assertEquals(100 * 1000, getFixture().getDuration());
	}

	/**
	 * Tests the
	 * '{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#setDuration(long)
	 * <em>Set Duration</em>}' operation. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see org.eclipse.nebula.widgets.timeline.ITimelineEvent#setDuration(long)
	 * @generated NOT
	 */
	public void testSetDuration__long() {
		getFixture().setDuration(100);

		assertEquals(100, getFixture().getDuration());
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
		getFixture().setStartTimestamp(100, TimeUnit.SECONDS);
		getFixture().setDuration(500, TimeUnit.MILLISECONDS);

		Timing timing = getFixture().getTiming();
		assertEquals(getFixture().getStartTimestamp(), timing.getTimestamp(), 0.1);
		assertEquals( getFixture().getDuration() , timing.getDuration(), 0.1);
	}

} // TimelineEventTest
