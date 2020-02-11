/**
 */
package org.eclipse.nebula.widgets.timeline;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Cursor</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.ICursor#getTimeline <em>Timeline</em>}</li>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.ICursor#getTimestamp <em>Timestamp</em>}</li>
 * </ul>
 *
 * @see org.eclipse.nebula.widgets.timeline.ITimelinePackage#getCursor()
 * @model
 * @generated
 */
public interface ICursor extends ITimed {
	/**
	 * Returns the value of the '<em><b>Timeline</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.eclipse.nebula.widgets.timeline.ITimeline#getCursors <em>Cursors</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Timeline</em>' container reference.
	 * @see #setTimeline(ITimeline)
	 * @see org.eclipse.nebula.widgets.timeline.ITimelinePackage#getCursor_Timeline()
	 * @see org.eclipse.nebula.widgets.timeline.ITimeline#getCursors
	 * @model opposite="cursors" required="true" transient="false"
	 * @generated
	 */
	ITimeline getTimeline();

	/**
	 * Sets the value of the '{@link org.eclipse.nebula.widgets.timeline.ICursor#getTimeline <em>Timeline</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Timeline</em>' container reference.
	 * @see #getTimeline()
	 * @generated
	 */
	void setTimeline(ITimeline value);

	/**
	 * Returns the value of the '<em><b>Timestamp</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Timestamp</em>' attribute.
	 * @see #setTimestamp(long)
	 * @see org.eclipse.nebula.widgets.timeline.ITimelinePackage#getCursor_Timestamp()
	 * @model
	 * @generated
	 */
	long getTimestamp();

	/**
	 * Sets the value of the '{@link org.eclipse.nebula.widgets.timeline.ICursor#getTimestamp <em>Timestamp</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Timestamp</em>' attribute.
	 * @see #getTimestamp()
	 * @generated
	 */
	void setTimestamp(long value);

} // ICursor
