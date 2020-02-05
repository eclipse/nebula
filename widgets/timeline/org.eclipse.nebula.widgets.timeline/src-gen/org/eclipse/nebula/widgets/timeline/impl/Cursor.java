/**
 */
package org.eclipse.nebula.widgets.timeline.impl;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.nebula.widgets.timeline.ICursor;
import org.eclipse.nebula.widgets.timeline.ITimeline;
import org.eclipse.nebula.widgets.timeline.ITimelinePackage;
import org.eclipse.nebula.widgets.timeline.Timing;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Cursor</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link org.eclipse.nebula.widgets.timeline.impl.Cursor#getTimeline <em>Timeline</em>}</li>
 * <li>{@link org.eclipse.nebula.widgets.timeline.impl.Cursor#getTimestamp <em>Timestamp</em>}</li>
 * </ul>
 *
 * @generated
 */
public class Cursor extends MinimalEObjectImpl.Container implements ICursor {
	/**
	 * The default value of the '{@link #getTimestamp() <em>Timestamp</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getTimestamp()
	 * @generated
	 * @ordered
	 */
	protected static final long TIMESTAMP_EDEFAULT = 0L;
	/**
	 * The cached value of the '{@link #getTimestamp() <em>Timestamp</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getTimestamp()
	 * @generated
	 * @ordered
	 */
	protected long timestamp = TIMESTAMP_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected Cursor() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ITimelinePackage.Literals.CURSOR;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public ITimeline getTimeline() {
		if (eContainerFeatureID() != ITimelinePackage.CURSOR__TIMELINE)
			return null;
		return (ITimeline) eInternalContainer();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetTimeline(ITimeline newTimeline, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject) newTimeline, ITimelinePackage.CURSOR__TIMELINE, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setTimeline(ITimeline newTimeline) {
		if ((newTimeline != eInternalContainer()) || ((eContainerFeatureID() != ITimelinePackage.CURSOR__TIMELINE) && (newTimeline != null))) {
			if (EcoreUtil.isAncestor(this, newTimeline))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newTimeline != null)
				msgs = ((InternalEObject) newTimeline).eInverseAdd(this, ITimelinePackage.TIMELINE__CURSORS, ITimeline.class, msgs);
			msgs = basicSetTimeline(newTimeline, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ITimelinePackage.CURSOR__TIMELINE, newTimeline, newTimeline));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setTimestamp(long newTimestamp) {
		final long oldTimestamp = timestamp;
		timestamp = newTimestamp;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ITimelinePackage.CURSOR__TIMESTAMP, oldTimestamp, timestamp));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	@Override
	public Timing getTiming() {
		return new Timing(getTimestamp(), 0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ITimelinePackage.CURSOR__TIMELINE:
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			return basicSetTimeline((ITimeline) otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case ITimelinePackage.CURSOR__TIMELINE:
			return basicSetTimeline(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID()) {
		case ITimelinePackage.CURSOR__TIMELINE:
			return eInternalContainer().eInverseRemove(this, ITimelinePackage.TIMELINE__CURSORS, ITimeline.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case ITimelinePackage.CURSOR__TIMELINE:
			return getTimeline();
		case ITimelinePackage.CURSOR__TIMESTAMP:
			return getTimestamp();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case ITimelinePackage.CURSOR__TIMELINE:
			setTimeline((ITimeline) newValue);
			return;
		case ITimelinePackage.CURSOR__TIMESTAMP:
			setTimestamp((Long) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case ITimelinePackage.CURSOR__TIMELINE:
			setTimeline((ITimeline) null);
			return;
		case ITimelinePackage.CURSOR__TIMESTAMP:
			setTimestamp(TIMESTAMP_EDEFAULT);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case ITimelinePackage.CURSOR__TIMELINE:
			return getTimeline() != null;
		case ITimelinePackage.CURSOR__TIMESTAMP:
			return timestamp != TIMESTAMP_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		switch (operationID) {
		case ITimelinePackage.CURSOR___GET_TIMING:
			return getTiming();
		}
		return super.eInvoke(operationID, arguments);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		final StringBuilder result = new StringBuilder(super.toString());
		result.append(" (timestamp: ");
		result.append(timestamp);
		result.append(')');
		return result.toString();
	}

} // Cursor
