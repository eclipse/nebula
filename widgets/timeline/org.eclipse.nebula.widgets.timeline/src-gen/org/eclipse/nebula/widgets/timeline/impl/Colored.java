/**
 */
package org.eclipse.nebula.widgets.timeline.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.nebula.widgets.timeline.IColored;
import org.eclipse.nebula.widgets.timeline.ITimelinePackage;
import org.eclipse.swt.graphics.RGB;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Colored</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.impl.Colored#getColorCode <em>Color Code</em>}</li>
 *   <li>{@link org.eclipse.nebula.widgets.timeline.impl.Colored#getRgb <em>Rgb</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class Colored extends MinimalEObjectImpl.Container implements IColored {

	/**
	 * Long pattern matching HTML color codes.
	 *
	 * @generated NOT
	 */
	private static final Pattern LONG_HEX_PATTERN = Pattern.compile("#(\\p{XDigit}{2})(\\p{XDigit}{2})(\\p{XDigit}{2})");
	/**
	 * Short pattern matching HTML color codes.
	 *
	 * @generated NOT
	 */
	private static final Pattern SHORT_HEX_PATTERN = Pattern.compile("#(\\p{XDigit}{1})(\\p{XDigit}{1})(\\p{XDigit}{1})");

	/**
	 * The default value of the '{@link #getColorCode() <em>Color Code</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getColorCode()
	 * @generated
	 * @ordered
	 */
	protected static final String COLOR_CODE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getColorCode() <em>Color Code</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getColorCode()
	 * @generated
	 * @ordered
	 */
	protected String colorCode = COLOR_CODE_EDEFAULT;

	/**
	 * The default value of the '{@link #getRgb() <em>Rgb</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getRgb()
	 * @generated
	 * @ordered
	 */
	protected static final RGB RGB_EDEFAULT = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected Colored() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ITimelinePackage.Literals.COLORED;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getColorCode() {
		return colorCode;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setColorCode(String newColorCode) {
		String oldColorCode = colorCode;
		colorCode = newColorCode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ITimelinePackage.COLORED__COLOR_CODE, oldColorCode, colorCode));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated NOT
	 */
	@Override
	public RGB getRgb() {
		final String colorCode = getColorCode();
		if (colorCode != null) {
			Matcher matcher = LONG_HEX_PATTERN.matcher(colorCode);
			if (matcher.matches()) {
				final int red = Integer.parseInt(matcher.group(1), 16);
				final int green = Integer.parseInt(matcher.group(2), 16);
				final int blue = Integer.parseInt(matcher.group(3), 16);

				return new RGB(red, green, blue);

			} else {
				matcher = SHORT_HEX_PATTERN.matcher(colorCode);
				if (matcher.matches()) {
					final int red = Integer.parseInt(matcher.group(1) + matcher.group(1), 16);
					final int green = Integer.parseInt(matcher.group(2) + matcher.group(2), 16);
					final int blue = Integer.parseInt(matcher.group(3) + matcher.group(3), 16);

					return new RGB(red, green, blue);
				}
			}
		}

		return null;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ITimelinePackage.COLORED__COLOR_CODE:
				return getColorCode();
			case ITimelinePackage.COLORED__RGB:
				return getRgb();
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
			case ITimelinePackage.COLORED__COLOR_CODE:
				setColorCode((String)newValue);
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
			case ITimelinePackage.COLORED__COLOR_CODE:
				setColorCode(COLOR_CODE_EDEFAULT);
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
			case ITimelinePackage.COLORED__COLOR_CODE:
				return COLOR_CODE_EDEFAULT == null ? colorCode != null : !COLOR_CODE_EDEFAULT.equals(colorCode);
			case ITimelinePackage.COLORED__RGB:
				return RGB_EDEFAULT == null ? getRgb() != null : !RGB_EDEFAULT.equals(getRgb());
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (colorCode: ");
		result.append(colorCode);
		result.append(')');
		return result.toString();
	}

} // Colored
