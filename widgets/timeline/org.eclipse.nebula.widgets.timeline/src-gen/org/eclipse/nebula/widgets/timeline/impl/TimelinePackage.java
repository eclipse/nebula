/**
 */
package org.eclipse.nebula.widgets.timeline.impl;

import java.util.concurrent.TimeUnit;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.nebula.widgets.timeline.IColored;
import org.eclipse.nebula.widgets.timeline.ICursor;
import org.eclipse.nebula.widgets.timeline.ILane;
import org.eclipse.nebula.widgets.timeline.ITimed;
import org.eclipse.nebula.widgets.timeline.ITimeline;
import org.eclipse.nebula.widgets.timeline.ITimelineEvent;
import org.eclipse.nebula.widgets.timeline.ITimelineFactory;
import org.eclipse.nebula.widgets.timeline.ITimelinePackage;
import org.eclipse.nebula.widgets.timeline.ITrack;
import org.eclipse.nebula.widgets.timeline.Timing;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class TimelinePackage extends EPackageImpl implements ITimelinePackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass timelineEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass trackEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass laneEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass timelineEventEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass cursorEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass coloredEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass timedEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType timeUnitEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType rgbEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType timingEDataType = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.nebula.widgets.timeline.ITimelinePackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private TimelinePackage() {
		super(eNS_URI, ITimelineFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link ITimelinePackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static ITimelinePackage init() {
		if (isInited) return (ITimelinePackage)EPackage.Registry.INSTANCE.getEPackage(ITimelinePackage.eNS_URI);

		// Obtain or create and register package
		Object registeredTimelinePackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		TimelinePackage theTimelinePackage = registeredTimelinePackage instanceof TimelinePackage ? (TimelinePackage)registeredTimelinePackage : new TimelinePackage();

		isInited = true;

		// Create package meta-data objects
		theTimelinePackage.createPackageContents();

		// Initialize created meta-data
		theTimelinePackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theTimelinePackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(ITimelinePackage.eNS_URI, theTimelinePackage);
		return theTimelinePackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTimeline() {
		return timelineEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getTimeline_Tracks() {
		return (EReference)timelineEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getTimeline_Cursors() {
		return (EReference)timelineEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getTimeline_SelectedEvent() {
		return (EReference)timelineEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getTimeline__CreateTrack__String() {
		return timelineEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getTimeline__CreateCursor__long_TimeUnit() {
		return timelineEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getTimeline__CreateCursor__long() {
		return timelineEClass.getEOperations().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTrack() {
		return trackEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getTrack_Timeline() {
		return (EReference)trackEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getTrack_Lanes() {
		return (EReference)trackEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTrack_Title() {
		return (EAttribute)trackEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getTrack__CreateLane() {
		return trackEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getLane() {
		return laneEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getLane_Track() {
		return (EReference)laneEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getLane_TimeEvents() {
		return (EReference)laneEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getLane__AddEvent__String_String_long_long_TimeUnit() {
		return laneEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getLane__AddEvent__String_String_long_long() {
		return laneEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTimelineEvent() {
		return timelineEventEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getTimelineEvent_Lane() {
		return (EReference)timelineEventEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTimelineEvent_StartTimestamp() {
		return (EAttribute)timelineEventEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTimelineEvent_EndTimestamp() {
		return (EAttribute)timelineEventEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTimelineEvent_Title() {
		return (EAttribute)timelineEventEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTimelineEvent_Message() {
		return (EAttribute)timelineEventEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getTimelineEvent__GetDuration() {
		return timelineEventEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getTimelineEvent__SetStartTimestamp__long_TimeUnit() {
		return timelineEventEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getTimelineEvent__SetEndTimestamp__long_TimeUnit() {
		return timelineEventEClass.getEOperations().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getTimelineEvent__SetDuration__long_TimeUnit() {
		return timelineEventEClass.getEOperations().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getTimelineEvent__SetDuration__long() {
		return timelineEventEClass.getEOperations().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCursor() {
		return cursorEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCursor_Timeline() {
		return (EReference)cursorEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCursor_Timestamp() {
		return (EAttribute)cursorEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getColored() {
		return coloredEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getColored_ColorCode() {
		return (EAttribute)coloredEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getColored_Rgb() {
		return (EAttribute)coloredEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTimed() {
		return timedEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getTimed__GetTiming() {
		return timedEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getTimeUnit() {
		return timeUnitEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getRGB() {
		return rgbEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getTiming() {
		return timingEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ITimelineFactory getTimelineFactory() {
		return (ITimelineFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		timelineEClass = createEClass(TIMELINE);
		createEReference(timelineEClass, TIMELINE__TRACKS);
		createEReference(timelineEClass, TIMELINE__CURSORS);
		createEReference(timelineEClass, TIMELINE__SELECTED_EVENT);
		createEOperation(timelineEClass, TIMELINE___CREATE_TRACK__STRING);
		createEOperation(timelineEClass, TIMELINE___CREATE_CURSOR__LONG_TIMEUNIT);
		createEOperation(timelineEClass, TIMELINE___CREATE_CURSOR__LONG);

		trackEClass = createEClass(TRACK);
		createEReference(trackEClass, TRACK__TIMELINE);
		createEReference(trackEClass, TRACK__LANES);
		createEAttribute(trackEClass, TRACK__TITLE);
		createEOperation(trackEClass, TRACK___CREATE_LANE);

		laneEClass = createEClass(LANE);
		createEReference(laneEClass, LANE__TRACK);
		createEReference(laneEClass, LANE__TIME_EVENTS);
		createEOperation(laneEClass, LANE___ADD_EVENT__STRING_STRING_LONG_LONG_TIMEUNIT);
		createEOperation(laneEClass, LANE___ADD_EVENT__STRING_STRING_LONG_LONG);

		timelineEventEClass = createEClass(TIMELINE_EVENT);
		createEReference(timelineEventEClass, TIMELINE_EVENT__LANE);
		createEAttribute(timelineEventEClass, TIMELINE_EVENT__START_TIMESTAMP);
		createEAttribute(timelineEventEClass, TIMELINE_EVENT__END_TIMESTAMP);
		createEAttribute(timelineEventEClass, TIMELINE_EVENT__TITLE);
		createEAttribute(timelineEventEClass, TIMELINE_EVENT__MESSAGE);
		createEOperation(timelineEventEClass, TIMELINE_EVENT___GET_DURATION);
		createEOperation(timelineEventEClass, TIMELINE_EVENT___SET_START_TIMESTAMP__LONG_TIMEUNIT);
		createEOperation(timelineEventEClass, TIMELINE_EVENT___SET_END_TIMESTAMP__LONG_TIMEUNIT);
		createEOperation(timelineEventEClass, TIMELINE_EVENT___SET_DURATION__LONG_TIMEUNIT);
		createEOperation(timelineEventEClass, TIMELINE_EVENT___SET_DURATION__LONG);

		cursorEClass = createEClass(CURSOR);
		createEReference(cursorEClass, CURSOR__TIMELINE);
		createEAttribute(cursorEClass, CURSOR__TIMESTAMP);

		coloredEClass = createEClass(COLORED);
		createEAttribute(coloredEClass, COLORED__COLOR_CODE);
		createEAttribute(coloredEClass, COLORED__RGB);

		timedEClass = createEClass(TIMED);
		createEOperation(timedEClass, TIMED___GET_TIMING);

		// Create data types
		timeUnitEDataType = createEDataType(TIME_UNIT);
		rgbEDataType = createEDataType(RGB);
		timingEDataType = createEDataType(TIMING);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		laneEClass.getESuperTypes().add(this.getColored());
		timelineEventEClass.getESuperTypes().add(this.getColored());
		timelineEventEClass.getESuperTypes().add(this.getTimed());
		cursorEClass.getESuperTypes().add(this.getTimed());

		// Initialize classes, features, and operations; add parameters
		initEClass(timelineEClass, ITimeline.class, "Timeline", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getTimeline_Tracks(), this.getTrack(), this.getTrack_Timeline(), "tracks", null, 0, -1, ITimeline.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getTimeline_Cursors(), this.getCursor(), this.getCursor_Timeline(), "cursors", null, 0, -1, ITimeline.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getTimeline_SelectedEvent(), this.getTimelineEvent(), null, "selectedEvent", null, 0, 1, ITimeline.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		EOperation op = initEOperation(getTimeline__CreateTrack__String(), this.getTrack(), "createTrack", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "title", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getTimeline__CreateCursor__long_TimeUnit(), this.getCursor(), "createCursor", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getELong(), "timestamp", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getTimeUnit(), "timeUnit", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getTimeline__CreateCursor__long(), this.getCursor(), "createCursor", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getELong(), "timestamp", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(trackEClass, ITrack.class, "Track", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getTrack_Timeline(), this.getTimeline(), this.getTimeline_Tracks(), "timeline", null, 1, 1, ITrack.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getTrack_Lanes(), this.getLane(), this.getLane_Track(), "lanes", null, 0, -1, ITrack.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTrack_Title(), ecorePackage.getEString(), "title", null, 0, 1, ITrack.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEOperation(getTrack__CreateLane(), this.getLane(), "createLane", 1, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(laneEClass, ILane.class, "Lane", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getLane_Track(), this.getTrack(), this.getTrack_Lanes(), "track", null, 1, 1, ILane.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLane_TimeEvents(), this.getTimelineEvent(), this.getTimelineEvent_Lane(), "timeEvents", null, 0, -1, ILane.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = initEOperation(getLane__AddEvent__String_String_long_long_TimeUnit(), this.getTimelineEvent(), "addEvent", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "title", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "message", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getELong(), "startTimestamp", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getELong(), "endTimestamp", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getTimeUnit(), "timeUnit", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getLane__AddEvent__String_String_long_long(), this.getTimelineEvent(), "addEvent", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "title", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "message", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getELong(), "startTimestamp", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getELong(), "endTimestamp", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(timelineEventEClass, ITimelineEvent.class, "TimelineEvent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getTimelineEvent_Lane(), this.getLane(), this.getLane_TimeEvents(), "lane", null, 1, 1, ITimelineEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTimelineEvent_StartTimestamp(), ecorePackage.getELong(), "startTimestamp", null, 0, 1, ITimelineEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTimelineEvent_EndTimestamp(), ecorePackage.getELong(), "endTimestamp", null, 0, 1, ITimelineEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTimelineEvent_Title(), ecorePackage.getEString(), "title", null, 0, 1, ITimelineEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTimelineEvent_Message(), ecorePackage.getEString(), "message", null, 0, 1, ITimelineEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEOperation(getTimelineEvent__GetDuration(), ecorePackage.getELong(), "getDuration", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getTimelineEvent__SetStartTimestamp__long_TimeUnit(), null, "setStartTimestamp", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getELong(), "value", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getTimeUnit(), "timeUnit", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getTimelineEvent__SetEndTimestamp__long_TimeUnit(), null, "setEndTimestamp", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getELong(), "value", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getTimeUnit(), "timeUnit", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getTimelineEvent__SetDuration__long_TimeUnit(), null, "setDuration", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getELong(), "value", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getTimeUnit(), "timeUnit", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getTimelineEvent__SetDuration__long(), null, "setDuration", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getELong(), "value", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(cursorEClass, ICursor.class, "Cursor", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getCursor_Timeline(), this.getTimeline(), this.getTimeline_Cursors(), "timeline", null, 1, 1, ICursor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getCursor_Timestamp(), ecorePackage.getELong(), "timestamp", null, 0, 1, ICursor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(coloredEClass, IColored.class, "Colored", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getColored_ColorCode(), ecorePackage.getEString(), "colorCode", null, 0, 1, IColored.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getColored_Rgb(), this.getRGB(), "rgb", null, 0, 1, IColored.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(timedEClass, ITimed.class, "Timed", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEOperation(getTimed__GetTiming(), this.getTiming(), "getTiming", 0, 1, IS_UNIQUE, IS_ORDERED);

		// Initialize data types
		initEDataType(timeUnitEDataType, TimeUnit.class, "TimeUnit", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(rgbEDataType, org.eclipse.swt.graphics.RGB.class, "RGB", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(timingEDataType, Timing.class, "Timing", !IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
	}

} //TimelinePackage
