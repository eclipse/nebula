/**
 */
package org.eclipse.nebula.widgets.timeline.tests;

import junit.framework.TestCase;

import org.eclipse.nebula.widgets.timeline.IColored;
import org.eclipse.swt.graphics.RGB;

/**
 * <!-- begin-user-doc --> A test case for the model object
 * '<em><b>Colored</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are tested:
 * <ul>
 * <li>{@link org.eclipse.nebula.widgets.timeline.IColored#getRgb()
 * <em>Rgb</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public abstract class ColoredTest extends TestCase {

	/**
	 * The fixture for this Colored test case. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	protected IColored fixture = null;

	/**
	 * Constructs a new Colored test case with the given name. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ColoredTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Colored test case. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	protected void setFixture(IColored fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Colored test case. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	protected IColored getFixture() {
		return fixture;
	}

	/**
	 * Tests the '{@link org.eclipse.nebula.widgets.timeline.IColored#getRgb()
	 * <em>Rgb</em>}' feature getter. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.nebula.widgets.timeline.IColored#getRgb()
	 * @generated NOT
	 */
	public void testGetRgb() {
		getFixture().setColorCode("#334455");
		RGB rgb = getFixture().getRgb();

		assertEquals(0x33, rgb.red);
		assertEquals(0x44, rgb.green);
		assertEquals(0x55, rgb.blue);
	}

	/**
	 * Tests the '{@link org.eclipse.nebula.widgets.timeline.IColored#getRgb()
	 * <em>Rgb</em>}' feature getter. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.nebula.widgets.timeline.IColored#getRgb()
	 * @generated NOT
	 */
	public void testEmptyGetRgb() {
		RGB rgb = getFixture().getRgb();
		
		assertNull(rgb);
	}

} // ColoredTest
