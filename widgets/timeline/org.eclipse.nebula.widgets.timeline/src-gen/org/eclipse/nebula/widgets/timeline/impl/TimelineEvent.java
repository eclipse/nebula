/**
 */
package org.eclipse.nebula.widgets.timeline.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.nebula.widgets.timeline.ILane;
import org.eclipse.nebula.widgets.timeline.ITimed;
import org.eclipse.nebula.widgets.timeline.ITimelineEvent;
import org.eclipse.nebula.widgets.timeline.ITimelinePackage;
import org.eclipse.nebula.widgets.timeline.Timing;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Event</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.impl.TimelineEvent#getLane <em>Lane</em>}</li>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.impl.TimelineEvent#getStartTimestamp <em>Start Timestamp</em>}</li>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.impl.TimelineEvent#getEndTimestamp <em>End Timestamp</em>}</li>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.impl.TimelineEvent#getTitle <em>Title</em>}</li>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.impl.TimelineEvent#getMessage <em>Message</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TimelineEvent extends Colored implements ITimelineEvent {
	/**
	 * The default value of the '{@link #getStartTimestamp() <em>Start Timestamp</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getStartTimestamp()
	 * @generated
	 * @ordered
	 */
	protected static final long START_TIMESTAMP_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getStartTimestamp() <em>Start Timestamp</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getStartTimestamp()
	 * @generated
	 * @ordered
	 */
	protected long startTimestamp = START_TIMESTAMP_EDEFAULT;

	/**
	 * The default value of the '{@link #getEndTimestamp() <em>End Timestamp</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getEndTimestamp()
	 * @generated
	 * @ordered
	 */
	protected static final long END_TIMESTAMP_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getEndTimestamp() <em>End Timestamp</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getEndTimestamp()
	 * @generated
	 * @ordered
	 */
	protected long endTimestamp = END_TIMESTAMP_EDEFAULT;

	/**
	 * The default value of the '{@link #getTitle() <em>Title</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getTitle()
	 * @generated
	 * @ordered
	 */
	protected static final String TITLE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTitle() <em>Title</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getTitle()
	 * @generated
	 * @ordered
	 */
	protected String title = TITLE_EDEFAULT;

	/**
	 * The default value of the '{@link #getMessage() <em>Message</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getMessage()
	 * @generated
	 * @ordered
	 */
	protected static final String MESSAGE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMessage() <em>Message</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getMessage()
	 * @generated
	 * @ordered
	 */
	protected String message = MESSAGE_EDEFAULT;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected TimelineEvent() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ITimelinePackage.Literals.TIMELINE_EVENT;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ILane getLane() {
		if (eContainerFeatureID() != ITimelinePackage.TIMELINE_EVENT__LANE) return null;
		return (ILane)eInternalContainer();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetLane(ILane newLane, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newLane, ITimelinePackage.TIMELINE_EVENT__LANE, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setLane(ILane newLane) {
		if (newLane != eInternalContainer() || (eContainerFeatureID() != ITimelinePackage.TIMELINE_EVENT__LANE && newLane != null)) {
			if (EcoreUtil.isAncestor(this, newLane))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newLane != null)
				msgs = ((InternalEObject)newLane).eInverseAdd(this, ITimelinePackage.LANE__TIME_EVENTS, ILane.class, msgs);
			msgs = basicSetLane(newLane, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ITimelinePackage.TIMELINE_EVENT__LANE, newLane, newLane));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public long getStartTimestamp() {
		return startTimestamp;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setStartTimestamp(long newStartTimestamp) {
		long oldStartTimestamp = startTimestamp;
		startTimestamp = newStartTimestamp;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ITimelinePackage.TIMELINE_EVENT__START_TIMESTAMP, oldStartTimestamp, startTimestamp));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public long getEndTimestamp() {
		return endTimestamp;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setEndTimestamp(long newEndTimestamp) {
		long oldEndTimestamp = endTimestamp;
		endTimestamp = newEndTimestamp;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ITimelinePackage.TIMELINE_EVENT__END_TIMESTAMP, oldEndTimestamp, endTimestamp));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getTitle() {
		return title;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTitle(String newTitle) {
		String oldTitle = title;
		title = newTitle;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ITimelinePackage.TIMELINE_EVENT__TITLE, oldTitle, title));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getMessage() {
		return message;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMessage(String newMessage) {
		String oldMessage = message;
		message = newMessage;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ITimelinePackage.TIMELINE_EVENT__MESSAGE, oldMessage, message));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	@Override
	public long getDuration() {
		return getEndTimestamp() - getStartTimestamp();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	@Override
	public void setStartTimestamp(long value, TimeUnit timeUnit) {
		setStartTimestamp(timeUnit.toNanos(value));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	@Override
	public void setEndTimestamp(long value, TimeUnit timeUnit) {
		setEndTimestamp(timeUnit.toNanos(value));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	@Override
	public void setDuration(long value, TimeUnit timeUnit) {
		setEndTimestamp(getStartTimestamp() + timeUnit.toNanos(value));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	@Override
	public void setDuration(long value) {
		setDuration(value, TimeUnit.NANOSECONDS);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	@Override
	public Timing getTiming() {
		return new Timing(getStartTimestamp(), getDuration());
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ITimelinePackage.TIMELINE_EVENT__LANE:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetLane((ILane)otherEnd, msgs);
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
			case ITimelinePackage.TIMELINE_EVENT__LANE:
				return basicSetLane(null, msgs);
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
			case ITimelinePackage.TIMELINE_EVENT__LANE:
				return eInternalContainer().eInverseRemove(this, ITimelinePackage.LANE__TIME_EVENTS, ILane.class, msgs);
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
			case ITimelinePackage.TIMELINE_EVENT__LANE:
				return getLane();
			case ITimelinePackage.TIMELINE_EVENT__START_TIMESTAMP:
				return getStartTimestamp();
			case ITimelinePackage.TIMELINE_EVENT__END_TIMESTAMP:
				return getEndTimestamp();
			case ITimelinePackage.TIMELINE_EVENT__TITLE:
				return getTitle();
			case ITimelinePackage.TIMELINE_EVENT__MESSAGE:
				return getMessage();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ITimelinePackage.TIMELINE_EVENT__LANE:
				setLane((ILane)newValue);
				return;
			case ITimelinePackage.TIMELINE_EVENT__START_TIMESTAMP:
				setStartTimestamp((Long)newValue);
				return;
			case ITimelinePackage.TIMELINE_EVENT__END_TIMESTAMP:
				setEndTimestamp((Long)newValue);
				return;
			case ITimelinePackage.TIMELINE_EVENT__TITLE:
				setTitle((String)newValue);
				return;
			case ITimelinePackage.TIMELINE_EVENT__MESSAGE:
				setMessage((String)newValue);
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
			case ITimelinePackage.TIMELINE_EVENT__LANE:
				setLane((ILane)null);
				return;
			case ITimelinePackage.TIMELINE_EVENT__START_TIMESTAMP:
				setStartTimestamp(START_TIMESTAMP_EDEFAULT);
				return;
			case ITimelinePackage.TIMELINE_EVENT__END_TIMESTAMP:
				setEndTimestamp(END_TIMESTAMP_EDEFAULT);
				return;
			case ITimelinePackage.TIMELINE_EVENT__TITLE:
				setTitle(TITLE_EDEFAULT);
				return;
			case ITimelinePackage.TIMELINE_EVENT__MESSAGE:
				setMessage(MESSAGE_EDEFAULT);
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
			case ITimelinePackage.TIMELINE_EVENT__LANE:
				return getLane() != null;
			case ITimelinePackage.TIMELINE_EVENT__START_TIMESTAMP:
				return startTimestamp != START_TIMESTAMP_EDEFAULT;
			case ITimelinePackage.TIMELINE_EVENT__END_TIMESTAMP:
				return endTimestamp != END_TIMESTAMP_EDEFAULT;
			case ITimelinePackage.TIMELINE_EVENT__TITLE:
				return TITLE_EDEFAULT == null ? title != null : !TITLE_EDEFAULT.equals(title);
			case ITimelinePackage.TIMELINE_EVENT__MESSAGE:
				return MESSAGE_EDEFAULT == null ? message != null : !MESSAGE_EDEFAULT.equals(message);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eDerivedOperationID(int baseOperationID, Class<?> baseClass) {
		if (baseClass == ITimed.class) {
			switch (baseOperationID) {
				case ITimelinePackage.TIMED___GET_TIMING: return ITimelinePackage.TIMELINE_EVENT___GET_TIMING;
				default: return -1;
			}
		}
		return super.eDerivedOperationID(baseOperationID, baseClass);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		switch (operationID) {
			case ITimelinePackage.TIMELINE_EVENT___GET_DURATION:
				return getDuration();
			case ITimelinePackage.TIMELINE_EVENT___SET_START_TIMESTAMP__LONG_TIMEUNIT:
				setStartTimestamp((Long)arguments.get(0), (TimeUnit)arguments.get(1));
				return null;
			case ITimelinePackage.TIMELINE_EVENT___SET_END_TIMESTAMP__LONG_TIMEUNIT:
				setEndTimestamp((Long)arguments.get(0), (TimeUnit)arguments.get(1));
				return null;
			case ITimelinePackage.TIMELINE_EVENT___SET_DURATION__LONG_TIMEUNIT:
				setDuration((Long)arguments.get(0), (TimeUnit)arguments.get(1));
				return null;
			case ITimelinePackage.TIMELINE_EVENT___SET_DURATION__LONG:
				setDuration((Long)arguments.get(0));
				return null;
			case ITimelinePackage.TIMELINE_EVENT___GET_TIMING:
				return getTiming();
		}
		return super.eInvoke(operationID, arguments);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	@Override
	public String toString() {
		return getTitle();
	}

} // TimelineEvent
