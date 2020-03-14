/**
 */
package org.eclipse.nebula.widgets.timeline;

import java.util.concurrent.TimeUnit;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Event</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#getLane <em>Lane</em>}</li>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#getStartTimestamp <em>Start Timestamp</em>}</li>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#getEndTimestamp <em>End Timestamp</em>}</li>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#getTitle <em>Title</em>}</li>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#getMessage <em>Message</em>}</li>
 * </ul>
 *
 * @see org.eclipse.nebula.widgets.timeline.ITimelinePackage#getTimelineEvent()
 * @model
 * @generated
 */
public interface ITimelineEvent extends IColored, ITimed {
	/**
	 * Returns the value of the '<em><b>Lane</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.eclipse.nebula.widgets.timeline.ILane#getTimeEvents <em>Time Events</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Lane</em>' container reference.
	 * @see #setLane(ILane)
	 * @see org.eclipse.nebula.widgets.timeline.ITimelinePackage#getTimelineEvent_Lane()
	 * @see org.eclipse.nebula.widgets.timeline.ILane#getTimeEvents
	 * @model opposite="timeEvents" required="true" transient="false"
	 * @generated
	 */
	ILane getLane();

	/**
	 * Sets the value of the '{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#getLane <em>Lane</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Lane</em>' container reference.
	 * @see #getLane()
	 * @generated
	 */
	void setLane(ILane value);

	/**
	 * Returns the value of the '<em><b>Start Timestamp</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Start Timestamp</em>' attribute.
	 * @see #setStartTimestamp(long)
	 * @see org.eclipse.nebula.widgets.timeline.ITimelinePackage#getTimelineEvent_StartTimestamp()
	 * @model
	 * @generated
	 */
	long getStartTimestamp();

	/**
	 * Sets the value of the '{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#getStartTimestamp <em>Start Timestamp</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Start Timestamp</em>' attribute.
	 * @see #getStartTimestamp()
	 * @generated
	 */
	void setStartTimestamp(long value);

	/**
	 * Returns the value of the '<em><b>End Timestamp</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>End Timestamp</em>' attribute.
	 * @see #setEndTimestamp(long)
	 * @see org.eclipse.nebula.widgets.timeline.ITimelinePackage#getTimelineEvent_EndTimestamp()
	 * @model
	 * @generated
	 */
	long getEndTimestamp();

	/**
	 * Sets the value of the '{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#getEndTimestamp <em>End Timestamp</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>End Timestamp</em>' attribute.
	 * @see #getEndTimestamp()
	 * @generated
	 */
	void setEndTimestamp(long value);

	/**
	 * Returns the value of the '<em><b>Title</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Title</em>' attribute.
	 * @see #setTitle(String)
	 * @see org.eclipse.nebula.widgets.timeline.ITimelinePackage#getTimelineEvent_Title()
	 * @model
	 * @generated
	 */
	String getTitle();

	/**
	 * Sets the value of the '{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#getTitle <em>Title</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Title</em>' attribute.
	 * @see #getTitle()
	 * @generated
	 */
	void setTitle(String value);

	/**
	 * Returns the value of the '<em><b>Message</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Message</em>' attribute.
	 * @see #setMessage(String)
	 * @see org.eclipse.nebula.widgets.timeline.ITimelinePackage#getTimelineEvent_Message()
	 * @model
	 * @generated
	 */
	String getMessage();

	/**
	 * Sets the value of the '{@link org.eclipse.nebula.widgets.timeline.ITimelineEvent#getMessage <em>Message</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Message</em>' attribute.
	 * @see #getMessage()
	 * @generated
	 */
	void setMessage(String value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	long getDuration();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model timeUnitDataType="org.eclipse.nebula.widgets.timeline.TimeUnit"
	 * @generated
	 */
	void setStartTimestamp(long value, TimeUnit timeUnit);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model timeUnitDataType="org.eclipse.nebula.widgets.timeline.TimeUnit"
	 * @generated
	 */
	void setEndTimestamp(long value, TimeUnit timeUnit);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model timeUnitDataType="org.eclipse.nebula.widgets.timeline.TimeUnit"
	 * @generated
	 */
	void setDuration(long value, TimeUnit timeUnit);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	void setDuration(long value);

} // ITimelineEvent
