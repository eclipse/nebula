/**
 */
package org.eclipse.nebula.widgets.timeline;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.RGB;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Colored</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.IColored#getColorCode <em>Color Code</em>}</li>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.IColored#getRgb <em>Rgb</em>}</li>
 * </ul>
 *
 * @see org.eclipse.nebula.widgets.timeline.ITimelinePackage#getColored()
 * @model abstract="true"
 * @generated
 */
public interface IColored extends EObject {
	/**
	 * Returns the value of the '<em><b>Color Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * HTML color code, eg "#23885F"
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Color Code</em>' attribute.
	 * @see #setColorCode(String)
	 * @see org.eclipse.nebula.widgets.timeline.ITimelinePackage#getColored_ColorCode()
	 * @model
	 * @generated
	 */
	String getColorCode();

	/**
	 * Sets the value of the '{@link org.eclipse.nebula.widgets.timeline.IColored#getColorCode <em>Color Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Color Code</em>' attribute.
	 * @see #getColorCode()
	 * @generated
	 */
	void setColorCode(String value);

	/**
	 * Returns the value of the '<em><b>Rgb</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rgb</em>' attribute.
	 * @see org.eclipse.nebula.widgets.timeline.ITimelinePackage#getColored_Rgb()
	 * @model dataType="org.eclipse.nebula.widgets.timeline.RGB" transient="true" changeable="false" volatile="true" derived="true"
	 * @generated
	 */
	RGB getRgb();

} // IColored
