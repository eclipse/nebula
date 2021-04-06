/**
 */
package org.eclipse.nebula.widgets.timeline;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Track</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.ITrack#getTimeline <em>Timeline</em>}</li>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.ITrack#getLanes <em>Lanes</em>}</li>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.ITrack#getTitle <em>Title</em>}</li>
 * </ul>
 *
 * @see org.eclipse.nebula.widgets.timeline.ITimelinePackage#getTrack()
 * @model
 * @generated
 */
public interface ITrack extends EObject {
	/**
	 * Returns the value of the '<em><b>Timeline</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.eclipse.nebula.widgets.timeline.ITimeline#getTracks <em>Tracks</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Timeline</em>' container reference.
	 * @see #setTimeline(ITimeline)
	 * @see org.eclipse.nebula.widgets.timeline.ITimelinePackage#getTrack_Timeline()
	 * @see org.eclipse.nebula.widgets.timeline.ITimeline#getTracks
	 * @model opposite="tracks" required="true" transient="false"
	 * @generated
	 */
	ITimeline getTimeline();

	/**
	 * Sets the value of the '{@link org.eclipse.nebula.widgets.timeline.ITrack#getTimeline <em>Timeline</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Timeline</em>' container reference.
	 * @see #getTimeline()
	 * @generated
	 */
	void setTimeline(ITimeline value);

	/**
	 * Returns the value of the '<em><b>Lanes</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.nebula.widgets.timeline.ILane}.
	 * It is bidirectional and its opposite is '{@link org.eclipse.nebula.widgets.timeline.ILane#getTrack <em>Track</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Lanes</em>' containment reference list.
	 * @see org.eclipse.nebula.widgets.timeline.ITimelinePackage#getTrack_Lanes()
	 * @see org.eclipse.nebula.widgets.timeline.ILane#getTrack
	 * @model opposite="track" containment="true"
	 * @generated
	 */
	EList<ILane> getLanes();

	/**
	 * Returns the value of the '<em><b>Title</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Title</em>' attribute.
	 * @see #setTitle(String)
	 * @see org.eclipse.nebula.widgets.timeline.ITimelinePackage#getTrack_Title()
	 * @model
	 * @generated
	 */
	String getTitle();

	/**
	 * Sets the value of the '{@link org.eclipse.nebula.widgets.timeline.ITrack#getTitle <em>Title</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Title</em>' attribute.
	 * @see #getTitle()
	 * @generated
	 */
	void setTitle(String value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model required="true"
	 * @generated
	 */
	ILane createLane();

} // ITrack
