/**
 */
package org.eclipse.nebula.widgets.timeline;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.nebula.widgets.timeline.ITimelineFactory
 * @model kind="package"
 * @generated
 */
public interface ITimelinePackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "timeline";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://eclipse.org/nebula/timeline/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "timeline";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ITimelinePackage eINSTANCE = org.eclipse.nebula.widgets.timeline.impl.TimelinePackage.init();

	/**
	 * The meta object id for the '{@link org.eclipse.nebula.widgets.timeline.ITimed <em>Timed</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.nebula.widgets.timeline.ITimed
	 * @see org.eclipse.nebula.widgets.timeline.impl.TimelinePackage#getTimed()
	 * @generated
	 */
	int TIMED = 6;

	/**
	 * The meta object id for the '{@link org.eclipse.nebula.widgets.timeline.impl.Timeline <em>Timeline</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.nebula.widgets.timeline.impl.Timeline
	 * @see org.eclipse.nebula.widgets.timeline.impl.TimelinePackage#getTimeline()
	 * @generated
	 */
	int TIMELINE = 0;

	/**
	 * The feature id for the '<em><b>Tracks</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMELINE__TRACKS = 0;

	/**
	 * The feature id for the '<em><b>Cursors</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMELINE__CURSORS = 1;

	/**
	 * The feature id for the '<em><b>Selected Event</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMELINE__SELECTED_EVENT = 2;

	/**
	 * The number of structural features of the '<em>Timeline</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMELINE_FEATURE_COUNT = 3;

	/**
	 * The operation id for the '<em>Create Track</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMELINE___CREATE_TRACK__STRING = 0;

	/**
	 * The operation id for the '<em>Create Cursor</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMELINE___CREATE_CURSOR__LONG_TIMEUNIT = 1;

	/**
	 * The operation id for the '<em>Create Cursor</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMELINE___CREATE_CURSOR__LONG = 2;

	/**
	 * The number of operations of the '<em>Timeline</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMELINE_OPERATION_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.nebula.widgets.timeline.impl.Track <em>Track</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.nebula.widgets.timeline.impl.Track
	 * @see org.eclipse.nebula.widgets.timeline.impl.TimelinePackage#getTrack()
	 * @generated
	 */
	int TRACK = 1;

	/**
	 * The feature id for the '<em><b>Timeline</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRACK__TIMELINE = 0;

	/**
	 * The feature id for the '<em><b>Lanes</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRACK__LANES = 1;

	/**
	 * The feature id for the '<em><b>Title</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRACK__TITLE = 2;

	/**
	 * The number of structural features of the '<em>Track</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRACK_FEATURE_COUNT = 3;

	/**
	 * The operation id for the '<em>Create Lane</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRACK___CREATE_LANE = 0;

	/**
	 * The number of operations of the '<em>Track</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRACK_OPERATION_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.nebula.widgets.timeline.impl.Colored <em>Colored</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.nebula.widgets.timeline.impl.Colored
	 * @see org.eclipse.nebula.widgets.timeline.impl.TimelinePackage#getColored()
	 * @generated
	 */
	int COLORED = 5;

	/**
	 * The feature id for the '<em><b>Color Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLORED__COLOR_CODE = 0;

	/**
	 * The feature id for the '<em><b>Rgb</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLORED__RGB = 1;

	/**
	 * The number of structural features of the '<em>Colored</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLORED_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Colored</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COLORED_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.nebula.widgets.timeline.impl.Lane <em>Lane</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.nebula.widgets.timeline.impl.Lane
	 * @see org.eclipse.nebula.widgets.timeline.impl.TimelinePackage#getLane()
	 * @generated
	 */
	int LANE = 2;

	/**
	 * The feature id for the '<em><b>Color Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LANE__COLOR_CODE = COLORED__COLOR_CODE;

	/**
	 * The feature id for the '<em><b>Rgb</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LANE__RGB = COLORED__RGB;

	/**
	 * The feature id for the '<em><b>Track</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LANE__TRACK = COLORED_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Time Events</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LANE__TIME_EVENTS = COLORED_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Lane</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LANE_FEATURE_COUNT = COLORED_FEATURE_COUNT + 2;

	/**
	 * The operation id for the '<em>Add Event</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LANE___ADD_EVENT__STRING_STRING_LONG_LONG_TIMEUNIT = COLORED_OPERATION_COUNT + 0;

	/**
	 * The operation id for the '<em>Add Event</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LANE___ADD_EVENT__STRING_STRING_LONG_LONG = COLORED_OPERATION_COUNT + 1;

	/**
	 * The number of operations of the '<em>Lane</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LANE_OPERATION_COUNT = COLORED_OPERATION_COUNT + 2;

	/**
	 * The meta object id for the '{@link org.eclipse.nebula.widgets.timeline.impl.TimelineEvent <em>Event</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.nebula.widgets.timeline.impl.TimelineEvent
	 * @see org.eclipse.nebula.widgets.timeline.impl.TimelinePackage#getTimelineEvent()
	 * @generated
	 */
	int TIMELINE_EVENT = 3;

	/**
	 * The feature id for the '<em><b>Color Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMELINE_EVENT__COLOR_CODE = COLORED__COLOR_CODE;

	/**
	 * The feature id for the '<em><b>Rgb</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMELINE_EVENT__RGB = COLORED__RGB;

	/**
	 * The feature id for the '<em><b>Lane</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMELINE_EVENT__LANE = COLORED_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Start Timestamp</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMELINE_EVENT__START_TIMESTAMP = COLORED_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>End Timestamp</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMELINE_EVENT__END_TIMESTAMP = COLORED_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Title</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMELINE_EVENT__TITLE = COLORED_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Message</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMELINE_EVENT__MESSAGE = COLORED_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMELINE_EVENT_FEATURE_COUNT = COLORED_FEATURE_COUNT + 5;

	/**
	 * The operation id for the '<em>Get Timing</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMELINE_EVENT___GET_TIMING = COLORED_OPERATION_COUNT + 0;

	/**
	 * The operation id for the '<em>Get Duration</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMELINE_EVENT___GET_DURATION = COLORED_OPERATION_COUNT + 1;

	/**
	 * The operation id for the '<em>Set Start Timestamp</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMELINE_EVENT___SET_START_TIMESTAMP__LONG_TIMEUNIT = COLORED_OPERATION_COUNT + 2;

	/**
	 * The operation id for the '<em>Set End Timestamp</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMELINE_EVENT___SET_END_TIMESTAMP__LONG_TIMEUNIT = COLORED_OPERATION_COUNT + 3;

	/**
	 * The operation id for the '<em>Set Duration</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMELINE_EVENT___SET_DURATION__LONG_TIMEUNIT = COLORED_OPERATION_COUNT + 4;

	/**
	 * The operation id for the '<em>Set Duration</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMELINE_EVENT___SET_DURATION__LONG = COLORED_OPERATION_COUNT + 5;

	/**
	 * The number of operations of the '<em>Event</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMELINE_EVENT_OPERATION_COUNT = COLORED_OPERATION_COUNT + 6;

	/**
	 * The number of structural features of the '<em>Timed</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMED_FEATURE_COUNT = 0;

	/**
	 * The operation id for the '<em>Get Timing</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMED___GET_TIMING = 0;

	/**
	 * The number of operations of the '<em>Timed</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMED_OPERATION_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.nebula.widgets.timeline.impl.Cursor <em>Cursor</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.nebula.widgets.timeline.impl.Cursor
	 * @see org.eclipse.nebula.widgets.timeline.impl.TimelinePackage#getCursor()
	 * @generated
	 */
	int CURSOR = 4;

	/**
	 * The feature id for the '<em><b>Timeline</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CURSOR__TIMELINE = TIMED_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Timestamp</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CURSOR__TIMESTAMP = TIMED_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Cursor</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CURSOR_FEATURE_COUNT = TIMED_FEATURE_COUNT + 2;

	/**
	 * The operation id for the '<em>Get Timing</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CURSOR___GET_TIMING = TIMED___GET_TIMING;

	/**
	 * The number of operations of the '<em>Cursor</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CURSOR_OPERATION_COUNT = TIMED_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '<em>Time Unit</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.concurrent.TimeUnit
	 * @see org.eclipse.nebula.widgets.timeline.impl.TimelinePackage#getTimeUnit()
	 * @generated
	 */
	int TIME_UNIT = 7;


	/**
	 * The meta object id for the '<em>RGB</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.swt.graphics.RGB
	 * @see org.eclipse.nebula.widgets.timeline.impl.TimelinePackage#getRGB()
	 * @generated
	 */
	int RGB = 8;

	/**
	 * The meta object id for the '<em>Timing</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.nebula.widgets.timeline.Timing
	 * @see org.eclipse.nebula.widgets.timeline.impl.TimelinePackage#getTiming()
	 * @generated
	 */
	int TIMING = 9;

	/**
	 * Returns the meta object for class '{@link org.eclipse.nebula.widgets.timeline.ITimeline <em>Timeline</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Timeline</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.ITimeline
	 * @generated
	 */
	EClass getTimeline();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.nebula.widgets.timeline.ITimeline#getTracks <em>Tracks</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Tracks</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.ITimeline#getTracks()
	 * @see #getTimeline()
	 * @generated
	 */
	EReference getTimeline_Tracks();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.nebula.widgets.timeline.ITimeline#getCursors <em>Cursors</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Cursors</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.ITimeline#getCursors()
	 * @see #getTimeline()
	 * @generated
	 */
	EReference getTimeline_Cursors();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.nebula.widgets.timeline.ITimeline#getSelectedEvent <em>Selected Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Selected Event</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.ITimeline#getSelectedEvent()
	 * @see #getTimeline()
	 * @generated
	 */
	EReference getTimeline_SelectedEvent();

	/**
	 * Returns the meta object for the '{@link org.eclipse.nebula.widgets.timeline.ITimeline#createTrack(java.lang.String) <em>Create Track</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Create Track</em>' operation.
	 * @see org.eclipse.nebula.widgets.timeline.ITimeline#createTrack(java.lang.String)
	 * @generated
	 */
	EOperation getTimeline__CreateTrack__String();

	/**
	 * Returns the meta object for the '{@link org.eclipse.nebula.widgets.timeline.ITimeline#createCursor(long, java.util.concurrent.TimeUnit) <em>Create Cursor</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Create Cursor</em>' operation.
	 * @see org.eclipse.nebula.widgets.timeline.ITimeline#createCursor(long, java.util.concurrent.TimeUnit)
	 * @generated
	 */
	EOperation getTimeline__CreateCursor__long_TimeUnit();

	/**
	 * Returns the meta object for the '{@link org.eclipse.nebula.widgets.timeline.ITimeline#createCursor(long) <em>Create Cursor</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Create Cursor</em>' operation.
	 * @see org.eclipse.nebula.widgets.timeline.ITimeline#createCursor(long)
	 * @generated
	 */
	EOperation getTimeline__CreateCursor__long();

	/**
	 * Returns the meta object for class '{@link org.eclipse.nebula.widgets.timeline.ITrack <em>Track</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Track</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.ITrack
	 * @generated
	 */
	EClass getTrack();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.nebula.widgets.timeline.ITrack#getTimeline <em>Timeline</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Timeline</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.ITrack#getTimeline()
	 * @see #getTrack()
	 * @generated
	 */
	EReference getTrack_Timeline();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.nebula.widgets.timeline.ITrack#getLanes <em>Lanes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Lanes</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.ITrack#getLanes()
	 * @see #getTrack()
	 * @generated
	 */
	EReference getTrack_Lanes();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.nebula.widgets.timeline.ITrack#getTitle <em>Title</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Title</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.ITrack#getTitle()
	 * @see #getTrack()
	 * @generated
	 */
	EAttribute getTrack_Title();

	/**
	 * Returns the meta object for the '{@link org.eclipse.nebula.widgets.timeline.ITrack#createLane() <em>Create Lane</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Create Lane</em>' operation.
	 * @see org.eclipse.nebula.widgets.timeline.ITrack#createLane()
	 * @generated
	 */
	EOperation getTrack__CreateLane();

	/**
	 * Returns the meta object for class '{@link org.eclipse.nebula.widgets.timeline.ILane <em>Lane</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Lane</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.ILane
	 * @generated
	 */
	EClass getLane();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.nebula.widgets.timeline.ILane#getTrack <em>Track</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Track</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.ILane#getTrack()
	 * @see #getLane()
	 * @generated
	 */
	EReference getLane_Track();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.nebula.widgets.timeline.ILane#getTimeEvents <em>Time Events</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Time Events</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.ILane#getTimeEvents()
	 * @see #getLane()
	 * @generated
	 */
	EReference getLane_TimeEvents();

	/**
	 * Returns the meta object for the '{@link org.eclipse.nebula.widgets.timeline.ILane#addEvent(java.lang.String, java.lang.String, long, long, java.util.concurrent.TimeUnit) <em>Add Event</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Add Event</em>' operation.
	 * @see org.eclipse.nebula.widgets.timeline.ILane#addEvent(java.lang.String, java.lang.String, long, long, java.util.concurrent.TimeUnit)
	 * @generated
	 */
	EOperation getLane__AddEvent__String_String_long_long_TimeUnit();

	/**
	 * Returns the meta object for the '{@link org.eclipse.nebula.widgets.timeline.ILane#addEvent(java.lang.String, java.lang.String, long, long) <em>Add Event</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Add Event</em>' operation.
	 * @see org.eclipse.nebula.widgets.timeline.ILane#addEvent(java.lang.String, java.lang.String, long, long)
	 * @generated
	 */
	EOperation getLane__AddEvent__String_String_long_long();

	/**
	 * Returns the meta object for class '{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent <em>Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Event</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.ITimelineEvent
	 * @generated
	 */
	EClass getTimelineEvent();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#getLane <em>Lane</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Lane</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.ITimelineEvent#getLane()
	 * @see #getTimelineEvent()
	 * @generated
	 */
	EReference getTimelineEvent_Lane();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#getStartTimestamp <em>Start Timestamp</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Start Timestamp</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.ITimelineEvent#getStartTimestamp()
	 * @see #getTimelineEvent()
	 * @generated
	 */
	EAttribute getTimelineEvent_StartTimestamp();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#getEndTimestamp <em>End Timestamp</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>End Timestamp</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.ITimelineEvent#getEndTimestamp()
	 * @see #getTimelineEvent()
	 * @generated
	 */
	EAttribute getTimelineEvent_EndTimestamp();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#getTitle <em>Title</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Title</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.ITimelineEvent#getTitle()
	 * @see #getTimelineEvent()
	 * @generated
	 */
	EAttribute getTimelineEvent_Title();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#getMessage <em>Message</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Message</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.ITimelineEvent#getMessage()
	 * @see #getTimelineEvent()
	 * @generated
	 */
	EAttribute getTimelineEvent_Message();

	/**
	 * Returns the meta object for the '{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#getDuration() <em>Get Duration</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Duration</em>' operation.
	 * @see org.eclipse.nebula.widgets.timeline.ITimelineEvent#getDuration()
	 * @generated
	 */
	EOperation getTimelineEvent__GetDuration();

	/**
	 * Returns the meta object for the '{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#setStartTimestamp(long, java.util.concurrent.TimeUnit) <em>Set Start Timestamp</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Set Start Timestamp</em>' operation.
	 * @see org.eclipse.nebula.widgets.timeline.ITimelineEvent#setStartTimestamp(long, java.util.concurrent.TimeUnit)
	 * @generated
	 */
	EOperation getTimelineEvent__SetStartTimestamp__long_TimeUnit();

	/**
	 * Returns the meta object for the '{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#setEndTimestamp(long, java.util.concurrent.TimeUnit) <em>Set End Timestamp</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Set End Timestamp</em>' operation.
	 * @see org.eclipse.nebula.widgets.timeline.ITimelineEvent#setEndTimestamp(long, java.util.concurrent.TimeUnit)
	 * @generated
	 */
	EOperation getTimelineEvent__SetEndTimestamp__long_TimeUnit();

	/**
	 * Returns the meta object for the '{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#setDuration(long, java.util.concurrent.TimeUnit) <em>Set Duration</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Set Duration</em>' operation.
	 * @see org.eclipse.nebula.widgets.timeline.ITimelineEvent#setDuration(long, java.util.concurrent.TimeUnit)
	 * @generated
	 */
	EOperation getTimelineEvent__SetDuration__long_TimeUnit();

	/**
	 * Returns the meta object for the '{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#setDuration(long) <em>Set Duration</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Set Duration</em>' operation.
	 * @see org.eclipse.nebula.widgets.timeline.ITimelineEvent#setDuration(long)
	 * @generated
	 */
	EOperation getTimelineEvent__SetDuration__long();

	/**
	 * Returns the meta object for class '{@link org.eclipse.nebula.widgets.timeline.ICursor <em>Cursor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Cursor</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.ICursor
	 * @generated
	 */
	EClass getCursor();

	/**
	 * Returns the meta object for the container reference '{@link org.eclipse.nebula.widgets.timeline.ICursor#getTimeline <em>Timeline</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Timeline</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.ICursor#getTimeline()
	 * @see #getCursor()
	 * @generated
	 */
	EReference getCursor_Timeline();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.nebula.widgets.timeline.ICursor#getTimestamp <em>Timestamp</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Timestamp</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.ICursor#getTimestamp()
	 * @see #getCursor()
	 * @generated
	 */
	EAttribute getCursor_Timestamp();

	/**
	 * Returns the meta object for class '{@link org.eclipse.nebula.widgets.timeline.IColored <em>Colored</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Colored</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.IColored
	 * @generated
	 */
	EClass getColored();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.nebula.widgets.timeline.IColored#getColorCode <em>Color Code</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Color Code</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.IColored#getColorCode()
	 * @see #getColored()
	 * @generated
	 */
	EAttribute getColored_ColorCode();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.nebula.widgets.timeline.IColored#getRgb <em>Rgb</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Rgb</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.IColored#getRgb()
	 * @see #getColored()
	 * @generated
	 */
	EAttribute getColored_Rgb();

	/**
	 * Returns the meta object for class '{@link org.eclipse.nebula.widgets.timeline.ITimed <em>Timed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Timed</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.ITimed
	 * @generated
	 */
	EClass getTimed();

	/**
	 * Returns the meta object for the '{@link org.eclipse.nebula.widgets.timeline.ITimed#getTiming() <em>Get Timing</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Timing</em>' operation.
	 * @see org.eclipse.nebula.widgets.timeline.ITimed#getTiming()
	 * @generated
	 */
	EOperation getTimed__GetTiming();

	/**
	 * Returns the meta object for data type '{@link java.util.concurrent.TimeUnit <em>Time Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Time Unit</em>'.
	 * @see java.util.concurrent.TimeUnit
	 * @model instanceClass="java.util.concurrent.TimeUnit"
	 * @generated
	 */
	EDataType getTimeUnit();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.swt.graphics.RGB <em>RGB</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>RGB</em>'.
	 * @see org.eclipse.swt.graphics.RGB
	 * @model instanceClass="org.eclipse.swt.graphics.RGB"
	 * @generated
	 */
	EDataType getRGB();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.nebula.widgets.timeline.Timing <em>Timing</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Timing</em>'.
	 * @see org.eclipse.nebula.widgets.timeline.Timing
	 * @model instanceClass="org.eclipse.nebula.widgets.timeline.Timing" serializeable="false"
	 * @generated
	 */
	EDataType getTiming();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ITimelineFactory getTimelineFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.nebula.widgets.timeline.impl.Timeline <em>Timeline</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.nebula.widgets.timeline.impl.Timeline
		 * @see org.eclipse.nebula.widgets.timeline.impl.TimelinePackage#getTimeline()
		 * @generated
		 */
		EClass TIMELINE = eINSTANCE.getTimeline();

		/**
		 * The meta object literal for the '<em><b>Tracks</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TIMELINE__TRACKS = eINSTANCE.getTimeline_Tracks();

		/**
		 * The meta object literal for the '<em><b>Cursors</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TIMELINE__CURSORS = eINSTANCE.getTimeline_Cursors();

		/**
		 * The meta object literal for the '<em><b>Selected Event</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TIMELINE__SELECTED_EVENT = eINSTANCE.getTimeline_SelectedEvent();

		/**
		 * The meta object literal for the '<em><b>Create Track</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation TIMELINE___CREATE_TRACK__STRING = eINSTANCE.getTimeline__CreateTrack__String();

		/**
		 * The meta object literal for the '<em><b>Create Cursor</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation TIMELINE___CREATE_CURSOR__LONG_TIMEUNIT = eINSTANCE.getTimeline__CreateCursor__long_TimeUnit();

		/**
		 * The meta object literal for the '<em><b>Create Cursor</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation TIMELINE___CREATE_CURSOR__LONG = eINSTANCE.getTimeline__CreateCursor__long();

		/**
		 * The meta object literal for the '{@link org.eclipse.nebula.widgets.timeline.impl.Track <em>Track</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.nebula.widgets.timeline.impl.Track
		 * @see org.eclipse.nebula.widgets.timeline.impl.TimelinePackage#getTrack()
		 * @generated
		 */
		EClass TRACK = eINSTANCE.getTrack();

		/**
		 * The meta object literal for the '<em><b>Timeline</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TRACK__TIMELINE = eINSTANCE.getTrack_Timeline();

		/**
		 * The meta object literal for the '<em><b>Lanes</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TRACK__LANES = eINSTANCE.getTrack_Lanes();

		/**
		 * The meta object literal for the '<em><b>Title</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRACK__TITLE = eINSTANCE.getTrack_Title();

		/**
		 * The meta object literal for the '<em><b>Create Lane</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation TRACK___CREATE_LANE = eINSTANCE.getTrack__CreateLane();

		/**
		 * The meta object literal for the '{@link org.eclipse.nebula.widgets.timeline.impl.Lane <em>Lane</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.nebula.widgets.timeline.impl.Lane
		 * @see org.eclipse.nebula.widgets.timeline.impl.TimelinePackage#getLane()
		 * @generated
		 */
		EClass LANE = eINSTANCE.getLane();

		/**
		 * The meta object literal for the '<em><b>Track</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LANE__TRACK = eINSTANCE.getLane_Track();

		/**
		 * The meta object literal for the '<em><b>Time Events</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LANE__TIME_EVENTS = eINSTANCE.getLane_TimeEvents();

		/**
		 * The meta object literal for the '<em><b>Add Event</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation LANE___ADD_EVENT__STRING_STRING_LONG_LONG_TIMEUNIT = eINSTANCE.getLane__AddEvent__String_String_long_long_TimeUnit();

		/**
		 * The meta object literal for the '<em><b>Add Event</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation LANE___ADD_EVENT__STRING_STRING_LONG_LONG = eINSTANCE.getLane__AddEvent__String_String_long_long();

		/**
		 * The meta object literal for the '{@link org.eclipse.nebula.widgets.timeline.impl.TimelineEvent <em>Event</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.nebula.widgets.timeline.impl.TimelineEvent
		 * @see org.eclipse.nebula.widgets.timeline.impl.TimelinePackage#getTimelineEvent()
		 * @generated
		 */
		EClass TIMELINE_EVENT = eINSTANCE.getTimelineEvent();

		/**
		 * The meta object literal for the '<em><b>Lane</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TIMELINE_EVENT__LANE = eINSTANCE.getTimelineEvent_Lane();

		/**
		 * The meta object literal for the '<em><b>Start Timestamp</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TIMELINE_EVENT__START_TIMESTAMP = eINSTANCE.getTimelineEvent_StartTimestamp();

		/**
		 * The meta object literal for the '<em><b>End Timestamp</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TIMELINE_EVENT__END_TIMESTAMP = eINSTANCE.getTimelineEvent_EndTimestamp();

		/**
		 * The meta object literal for the '<em><b>Title</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TIMELINE_EVENT__TITLE = eINSTANCE.getTimelineEvent_Title();

		/**
		 * The meta object literal for the '<em><b>Message</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TIMELINE_EVENT__MESSAGE = eINSTANCE.getTimelineEvent_Message();

		/**
		 * The meta object literal for the '<em><b>Get Duration</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation TIMELINE_EVENT___GET_DURATION = eINSTANCE.getTimelineEvent__GetDuration();

		/**
		 * The meta object literal for the '<em><b>Set Start Timestamp</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation TIMELINE_EVENT___SET_START_TIMESTAMP__LONG_TIMEUNIT = eINSTANCE.getTimelineEvent__SetStartTimestamp__long_TimeUnit();

		/**
		 * The meta object literal for the '<em><b>Set End Timestamp</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation TIMELINE_EVENT___SET_END_TIMESTAMP__LONG_TIMEUNIT = eINSTANCE.getTimelineEvent__SetEndTimestamp__long_TimeUnit();

		/**
		 * The meta object literal for the '<em><b>Set Duration</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation TIMELINE_EVENT___SET_DURATION__LONG_TIMEUNIT = eINSTANCE.getTimelineEvent__SetDuration__long_TimeUnit();

		/**
		 * The meta object literal for the '<em><b>Set Duration</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation TIMELINE_EVENT___SET_DURATION__LONG = eINSTANCE.getTimelineEvent__SetDuration__long();

		/**
		 * The meta object literal for the '{@link org.eclipse.nebula.widgets.timeline.impl.Cursor <em>Cursor</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.nebula.widgets.timeline.impl.Cursor
		 * @see org.eclipse.nebula.widgets.timeline.impl.TimelinePackage#getCursor()
		 * @generated
		 */
		EClass CURSOR = eINSTANCE.getCursor();

		/**
		 * The meta object literal for the '<em><b>Timeline</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CURSOR__TIMELINE = eINSTANCE.getCursor_Timeline();

		/**
		 * The meta object literal for the '<em><b>Timestamp</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CURSOR__TIMESTAMP = eINSTANCE.getCursor_Timestamp();

		/**
		 * The meta object literal for the '{@link org.eclipse.nebula.widgets.timeline.impl.Colored <em>Colored</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.nebula.widgets.timeline.impl.Colored
		 * @see org.eclipse.nebula.widgets.timeline.impl.TimelinePackage#getColored()
		 * @generated
		 */
		EClass COLORED = eINSTANCE.getColored();

		/**
		 * The meta object literal for the '<em><b>Color Code</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COLORED__COLOR_CODE = eINSTANCE.getColored_ColorCode();

		/**
		 * The meta object literal for the '<em><b>Rgb</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COLORED__RGB = eINSTANCE.getColored_Rgb();

		/**
		 * The meta object literal for the '{@link org.eclipse.nebula.widgets.timeline.ITimed <em>Timed</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.nebula.widgets.timeline.ITimed
		 * @see org.eclipse.nebula.widgets.timeline.impl.TimelinePackage#getTimed()
		 * @generated
		 */
		EClass TIMED = eINSTANCE.getTimed();

		/**
		 * The meta object literal for the '<em><b>Get Timing</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation TIMED___GET_TIMING = eINSTANCE.getTimed__GetTiming();

		/**
		 * The meta object literal for the '<em>Time Unit</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.util.concurrent.TimeUnit
		 * @see org.eclipse.nebula.widgets.timeline.impl.TimelinePackage#getTimeUnit()
		 * @generated
		 */
		EDataType TIME_UNIT = eINSTANCE.getTimeUnit();

		/**
		 * The meta object literal for the '<em>RGB</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.swt.graphics.RGB
		 * @see org.eclipse.nebula.widgets.timeline.impl.TimelinePackage#getRGB()
		 * @generated
		 */
		EDataType RGB = eINSTANCE.getRGB();

		/**
		 * The meta object literal for the '<em>Timing</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.nebula.widgets.timeline.Timing
		 * @see org.eclipse.nebula.widgets.timeline.impl.TimelinePackage#getTiming()
		 * @generated
		 */
		EDataType TIMING = eINSTANCE.getTiming();

	}

} //ITimelinePackage
