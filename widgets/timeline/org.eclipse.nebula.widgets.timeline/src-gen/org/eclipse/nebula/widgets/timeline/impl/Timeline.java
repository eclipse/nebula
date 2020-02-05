/**
 */
package org.eclipse.nebula.widgets.timeline.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.nebula.widgets.timeline.ICursor;
import org.eclipse.nebula.widgets.timeline.ITimeline;
import org.eclipse.nebula.widgets.timeline.ITimelineEvent;
import org.eclipse.nebula.widgets.timeline.ITimelineFactory;
import org.eclipse.nebula.widgets.timeline.ITimelinePackage;
import org.eclipse.nebula.widgets.timeline.ITrack;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Timeline</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.impl.Timeline#getTracks <em>Tracks</em>}</li>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.impl.Timeline#getCursors <em>Cursors</em>}</li>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.impl.Timeline#getSelectedEvent <em>Selected Event</em>}</li>
 * </ul>
 *
 * @generated
 */
public class Timeline extends MinimalEObjectImpl.Container implements ITimeline {
	/**
	 * The cached value of the '{@link #getTracks() <em>Tracks</em>}' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getTracks()
	 * @generated
	 * @ordered
	 */
	protected EList<ITrack> tracks;

	/**
	 * The cached value of the '{@link #getCursors() <em>Cursors</em>}' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getCursors()
	 * @generated
	 * @ordered
	 */
	protected EList<ICursor> cursors;

	/**
	 * The cached value of the '{@link #getSelectedEvent() <em>Selected Event</em>}' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getSelectedEvent()
	 * @generated
	 * @ordered
	 */
	protected ITimelineEvent selectedEvent;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected Timeline() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ITimelinePackage.Literals.TIMELINE;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ITrack> getTracks() {
		if (tracks == null) {
			tracks = new EObjectContainmentWithInverseEList<ITrack>(ITrack.class, this, ITimelinePackage.TIMELINE__TRACKS, ITimelinePackage.TRACK__TIMELINE);
		}
		return tracks;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ICursor> getCursors() {
		if (cursors == null) {
			cursors = new EObjectContainmentWithInverseEList<ICursor>(ICursor.class, this, ITimelinePackage.TIMELINE__CURSORS, ITimelinePackage.CURSOR__TIMELINE);
		}
		return cursors;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ITimelineEvent getSelectedEvent() {
		if (selectedEvent != null && selectedEvent.eIsProxy()) {
			InternalEObject oldSelectedEvent = (InternalEObject)selectedEvent;
			selectedEvent = (ITimelineEvent)eResolveProxy(oldSelectedEvent);
			if (selectedEvent != oldSelectedEvent) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ITimelinePackage.TIMELINE__SELECTED_EVENT, oldSelectedEvent, selectedEvent));
			}
		}
		return selectedEvent;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public ITimelineEvent basicGetSelectedEvent() {
		return selectedEvent;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSelectedEvent(ITimelineEvent newSelectedEvent) {
		ITimelineEvent oldSelectedEvent = selectedEvent;
		selectedEvent = newSelectedEvent;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ITimelinePackage.TIMELINE__SELECTED_EVENT, oldSelectedEvent, selectedEvent));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	@Override
	public ITrack createTrack(String title) {
		final ITrack track = ITimelineFactory.eINSTANCE.createTrack();
		track.setTitle(title);

		getTracks().add(track);

		return track;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	@Override
	public ICursor createCursor(long timestamp, TimeUnit timeUnit) {
		final ICursor cursor = ITimelineFactory.eINSTANCE.createCursor();
		cursor.setTimestamp(timeUnit.toNanos(timestamp));

		getCursors().add(cursor);

		return cursor;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	@Override
	public ICursor createCursor(long timestamp) {
		return createCursor(timestamp, TimeUnit.NANOSECONDS);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ITimelinePackage.TIMELINE__TRACKS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getTracks()).basicAdd(otherEnd, msgs);
			case ITimelinePackage.TIMELINE__CURSORS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getCursors()).basicAdd(otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ITimelinePackage.TIMELINE__TRACKS:
				return ((InternalEList<?>)getTracks()).basicRemove(otherEnd, msgs);
			case ITimelinePackage.TIMELINE__CURSORS:
				return ((InternalEList<?>)getCursors()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ITimelinePackage.TIMELINE__TRACKS:
				return getTracks();
			case ITimelinePackage.TIMELINE__CURSORS:
				return getCursors();
			case ITimelinePackage.TIMELINE__SELECTED_EVENT:
				if (resolve) return getSelectedEvent();
				return basicGetSelectedEvent();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ITimelinePackage.TIMELINE__TRACKS:
				getTracks().clear();
				getTracks().addAll((Collection<? extends ITrack>)newValue);
				return;
			case ITimelinePackage.TIMELINE__CURSORS:
				getCursors().clear();
				getCursors().addAll((Collection<? extends ICursor>)newValue);
				return;
			case ITimelinePackage.TIMELINE__SELECTED_EVENT:
				setSelectedEvent((ITimelineEvent)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case ITimelinePackage.TIMELINE__TRACKS:
				getTracks().clear();
				return;
			case ITimelinePackage.TIMELINE__CURSORS:
				getCursors().clear();
				return;
			case ITimelinePackage.TIMELINE__SELECTED_EVENT:
				setSelectedEvent((ITimelineEvent)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case ITimelinePackage.TIMELINE__TRACKS:
				return tracks != null && !tracks.isEmpty();
			case ITimelinePackage.TIMELINE__CURSORS:
				return cursors != null && !cursors.isEmpty();
			case ITimelinePackage.TIMELINE__SELECTED_EVENT:
				return selectedEvent != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		switch (operationID) {
			case ITimelinePackage.TIMELINE___CREATE_TRACK__STRING:
				return createTrack((String)arguments.get(0));
			case ITimelinePackage.TIMELINE___CREATE_CURSOR__LONG_TIMEUNIT:
				return createCursor((Long)arguments.get(0), (TimeUnit)arguments.get(1));
			case ITimelinePackage.TIMELINE___CREATE_CURSOR__LONG:
				return createCursor((Long)arguments.get(0));
		}
		return super.eInvoke(operationID, arguments);
	}

} // Timeline
