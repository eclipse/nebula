/**
 */
package org.eclipse.nebula.widgets.timeline.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.nebula.widgets.timeline.ILane;
import org.eclipse.nebula.widgets.timeline.ITimeline;
import org.eclipse.nebula.widgets.timeline.ITimelineFactory;
import org.eclipse.nebula.widgets.timeline.ITimelinePackage;
import org.eclipse.nebula.widgets.timeline.ITrack;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Track</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.impl.Track#getTimeline <em>Timeline</em>}</li>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.impl.Track#getLanes <em>Lanes</em>}</li>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.impl.Track#getTitle <em>Title</em>}</li>
 * </ul>
 *
 * @generated
 */
public class Track extends MinimalEObjectImpl.Container implements ITrack {
	/**
	 * The cached value of the '{@link #getLanes() <em>Lanes</em>}' containment reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getLanes()
	 * @generated
	 * @ordered
	 */
	protected EList<ILane> lanes;

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
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected Track() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ITimelinePackage.Literals.TRACK;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ITimeline getTimeline() {
		if (eContainerFeatureID() != ITimelinePackage.TRACK__TIMELINE) return null;
		return (ITimeline)eInternalContainer();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetTimeline(ITimeline newTimeline, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newTimeline, ITimelinePackage.TRACK__TIMELINE, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTimeline(ITimeline newTimeline) {
		if (newTimeline != eInternalContainer() || (eContainerFeatureID() != ITimelinePackage.TRACK__TIMELINE && newTimeline != null)) {
			if (EcoreUtil.isAncestor(this, newTimeline))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newTimeline != null)
				msgs = ((InternalEObject)newTimeline).eInverseAdd(this, ITimelinePackage.TIMELINE__TRACKS, ITimeline.class, msgs);
			msgs = basicSetTimeline(newTimeline, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ITimelinePackage.TRACK__TIMELINE, newTimeline, newTimeline));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<ILane> getLanes() {
		if (lanes == null) {
			lanes = new EObjectContainmentWithInverseEList<ILane>(ILane.class, this, ITimelinePackage.TRACK__LANES, ITimelinePackage.LANE__TRACK);
		}
		return lanes;
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
			eNotify(new ENotificationImpl(this, Notification.SET, ITimelinePackage.TRACK__TITLE, oldTitle, title));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	@Override
	public ILane createLane() {
		final ILane lane = ITimelineFactory.eINSTANCE.createLane();

		getLanes().add(lane);

		return lane;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ITimelinePackage.TRACK__TIMELINE:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetTimeline((ITimeline)otherEnd, msgs);
			case ITimelinePackage.TRACK__LANES:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getLanes()).basicAdd(otherEnd, msgs);
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
			case ITimelinePackage.TRACK__TIMELINE:
				return basicSetTimeline(null, msgs);
			case ITimelinePackage.TRACK__LANES:
				return ((InternalEList<?>)getLanes()).basicRemove(otherEnd, msgs);
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
			case ITimelinePackage.TRACK__TIMELINE:
				return eInternalContainer().eInverseRemove(this, ITimelinePackage.TIMELINE__TRACKS, ITimeline.class, msgs);
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
			case ITimelinePackage.TRACK__TIMELINE:
				return getTimeline();
			case ITimelinePackage.TRACK__LANES:
				return getLanes();
			case ITimelinePackage.TRACK__TITLE:
				return getTitle();
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
			case ITimelinePackage.TRACK__TIMELINE:
				setTimeline((ITimeline)newValue);
				return;
			case ITimelinePackage.TRACK__LANES:
				getLanes().clear();
				getLanes().addAll((Collection<? extends ILane>)newValue);
				return;
			case ITimelinePackage.TRACK__TITLE:
				setTitle((String)newValue);
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
			case ITimelinePackage.TRACK__TIMELINE:
				setTimeline((ITimeline)null);
				return;
			case ITimelinePackage.TRACK__LANES:
				getLanes().clear();
				return;
			case ITimelinePackage.TRACK__TITLE:
				setTitle(TITLE_EDEFAULT);
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
			case ITimelinePackage.TRACK__TIMELINE:
				return getTimeline() != null;
			case ITimelinePackage.TRACK__LANES:
				return lanes != null && !lanes.isEmpty();
			case ITimelinePackage.TRACK__TITLE:
				return TITLE_EDEFAULT == null ? title != null : !TITLE_EDEFAULT.equals(title);
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
			case ITimelinePackage.TRACK___CREATE_LANE:
				return createLane();
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

} // Track
