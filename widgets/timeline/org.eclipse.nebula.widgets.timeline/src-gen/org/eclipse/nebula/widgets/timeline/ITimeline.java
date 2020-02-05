/**
 */
package org.eclipse.nebula.widgets.timeline;

import java.util.concurrent.TimeUnit;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Timeline</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.ITimeline#getTracks <em>Tracks</em>}</li>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.ITimeline#getCursors <em>Cursors</em>}</li>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.ITimeline#getSelectedEvent <em>Selected Event</em>}</li>
 * </ul>
 *
 * @see org.eclipse.nebula.widgets.timeline.ITimelinePackage#getTimeline()
 * @model
 * @generated
 */
public interface ITimeline extends EObject {
	/**
	 * Returns the value of the '<em><b>Tracks</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.nebula.widgets.timeline.ITrack}.
	 * It is bidirectional and its opposite is '{@link org.eclipse.nebula.widgets.timeline.ITrack#getTimeline <em>Timeline</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Tracks</em>' containment reference list.
	 * @see org.eclipse.nebula.widgets.timeline.ITimelinePackage#getTimeline_Tracks()
	 * @see org.eclipse.nebula.widgets.timeline.ITrack#getTimeline
	 * @model opposite="timeline" containment="true"
	 * @generated
	 */
	EList<ITrack> getTracks();

	/**
	 * Returns the value of the '<em><b>Cursors</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.nebula.widgets.timeline.ICursor}.
	 * It is bidirectional and its opposite is '{@link org.eclipse.nebula.widgets.timeline.ICursor#getTimeline <em>Timeline</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Cursors</em>' containment reference list.
	 * @see org.eclipse.nebula.widgets.timeline.ITimelinePackage#getTimeline_Cursors()
	 * @see org.eclipse.nebula.widgets.timeline.ICursor#getTimeline
	 * @model opposite="timeline" containment="true"
	 * @generated
	 */
	EList<ICursor> getCursors();

	/**
	 * Returns the value of the '<em><b>Selected Event</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Selected Event</em>' reference.
	 * @see #setSelectedEvent(ITimelineEvent)
	 * @see org.eclipse.nebula.widgets.timeline.ITimelinePackage#getTimeline_SelectedEvent()
	 * @model
	 * @generated
	 */
	ITimelineEvent getSelectedEvent();

	/**
	 * Sets the value of the '{@link org.eclipse.nebula.widgets.timeline.ITimeline#getSelectedEvent <em>Selected Event</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Selected Event</em>' reference.
	 * @see #getSelectedEvent()
	 * @generated
	 */
	void setSelectedEvent(ITimelineEvent value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model required="true"
	 * @generated
	 */
	ITrack createTrack(String title);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model required="true" timeUnitDataType="org.eclipse.nebula.widgets.timeline.TimeUnit"
	 * @generated
	 */
	ICursor createCursor(long timestamp, TimeUnit timeUnit);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model required="true"
	 * @generated
	 */
	ICursor createCursor(long timestamp);

} // ITimeline
