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
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.nebula.widgets.timeline.ILane;
import org.eclipse.nebula.widgets.timeline.ITimelineEvent;
import org.eclipse.nebula.widgets.timeline.ITimelineFactory;
import org.eclipse.nebula.widgets.timeline.ITimelinePackage;
import org.eclipse.nebula.widgets.timeline.ITrack;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Lane</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.impl.Lane#getTrack <em>Track</em>}</li>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.impl.Lane#getTimeEvents <em>Time Events</em>}</li>
 * </ul>
 *
 * @generated
 */
public class Lane extends Colored implements ILane {
	/**
	 * The cached value of the '{@link #getTimeEvents() <em>Time Events</em>}' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getTimeEvents()
	 * @generated
	 * @ordered
	 */
	protected EList<ITimelineEvent> timeEvents;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected Lane() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ITimelinePackage.Literals.LANE;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ITrack getTrack() {
		if (eContainerFeatureID() != ITimelinePackage.LANE__TRACK) return null;
		return (ITrack)eInternalContainer();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetTrack(ITrack newTrack, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newTrack, ITimelinePackage.LANE__TRACK, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTrack(ITrack newTrack) {
		if (newTrack != eInternalContainer() || (eContainerFeatureID() != ITimelinePackage.LANE__TRACK && newTrack != null)) {
			if (EcoreUtil.isAncestor(this, newTrack))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newTrack != null)
				msgs = ((InternalEObject)newTrack).eInverseAdd(this, ITimelinePackage.TRACK__LANES, ITrack.class, msgs);
			msgs = basicSetTrack(newTrack, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ITimelinePackage.LANE__TRACK, newTrack, newTrack));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ITimelineEvent> getTimeEvents() {
		if (timeEvents == null) {
			timeEvents = new EObjectContainmentWithInverseEList<ITimelineEvent>(ITimelineEvent.class, this, ITimelinePackage.LANE__TIME_EVENTS, ITimelinePackage.TIMELINE_EVENT__LANE);
		}
		return timeEvents;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	@Override
	public ITimelineEvent addEvent(String title, String message, long startTimestamp, long endTimestamp, TimeUnit timeUnit) {
		final ITimelineEvent event = ITimelineFactory.eINSTANCE.createTimelineEvent();

		event.setTitle(title);
		event.setMessage(message);
		event.setStartTimestamp(timeUnit.toNanos(startTimestamp));
		event.setEndTimestamp(timeUnit.toNanos(endTimestamp));

		getTimeEvents().add(event);

		return event;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	@Override
	public ITimelineEvent addEvent(String title, String message, long startTimestamp, long endTimestamp) {
		return addEvent(title, message, startTimestamp, endTimestamp, TimeUnit.NANOSECONDS);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ITimelinePackage.LANE__TRACK:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetTrack((ITrack)otherEnd, msgs);
			case ITimelinePackage.LANE__TIME_EVENTS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getTimeEvents()).basicAdd(otherEnd, msgs);
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
			case ITimelinePackage.LANE__TRACK:
				return basicSetTrack(null, msgs);
			case ITimelinePackage.LANE__TIME_EVENTS:
				return ((InternalEList<?>)getTimeEvents()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID()) {
			case ITimelinePackage.LANE__TRACK:
				return eInternalContainer().eInverseRemove(this, ITimelinePackage.TRACK__LANES, ITrack.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ITimelinePackage.LANE__TRACK:
				return getTrack();
			case ITimelinePackage.LANE__TIME_EVENTS:
				return getTimeEvents();
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
			case ITimelinePackage.LANE__TRACK:
				setTrack((ITrack)newValue);
				return;
			case ITimelinePackage.LANE__TIME_EVENTS:
				getTimeEvents().clear();
				getTimeEvents().addAll((Collection<? extends ITimelineEvent>)newValue);
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
			case ITimelinePackage.LANE__TRACK:
				setTrack((ITrack)null);
				return;
			case ITimelinePackage.LANE__TIME_EVENTS:
				getTimeEvents().clear();
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
			case ITimelinePackage.LANE__TRACK:
				return getTrack() != null;
			case ITimelinePackage.LANE__TIME_EVENTS:
				return timeEvents != null && !timeEvents.isEmpty();
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
			case ITimelinePackage.LANE___ADD_EVENT__STRING_STRING_LONG_LONG_TIMEUNIT:
				return addEvent((String)arguments.get(0), (String)arguments.get(1), (Long)arguments.get(2), (Long)arguments.get(3), (TimeUnit)arguments.get(4));
			case ITimelinePackage.LANE___ADD_EVENT__STRING_STRING_LONG_LONG:
				return addEvent((String)arguments.get(0), (String)arguments.get(1), (Long)arguments.get(2), (Long)arguments.get(3));
		}
		return super.eInvoke(operationID, arguments);
	}

} // Lane
