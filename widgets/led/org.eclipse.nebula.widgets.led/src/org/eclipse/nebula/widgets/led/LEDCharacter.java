/*******************************************************************************
 * Copyright (c) 2019 Akuiteo (http://www.akuiteo.com).
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation
 * Frank DELPORTE - inspiration for his LED Number Display JavaFX widget
 * (https://webtechie.be/2019/10/02/led-number-display-javafx-library-published-to-maven)
 *******************************************************************************/
package org.eclipse.nebula.widgets.led;

/**
 * Enum with definition of the segments to be highlighted.
 *
 * Placing of the LEDs: 0000 5 1 6666 4 2 3333
 */
public enum LEDCharacter {
	CLEAR(false, false, false, false, false, false, false), //
	MINUS(false, false, false, false, false, false, true),
	//
	ZERO(true, true, true, true, true, true, false), //
	ONE(false, true, true, false, false, false, false), //
	TWO(true, true, false, true, true, false, true), //
	THREE(true, true, true, true, false, false, true), //
	FOUR(false, true, true, false, false, true, true), //
	FIVE(true, false, true, true, false, true, true), //
	SIX(true, false, true, true, true, true, true), //
	SEVEN(true, true, true, false, false, false, false), //
	EIGHT(true, true, true, true, true, true, true), //
	NINE(true, true, true, true, false, true, true), //
	//
	A(true, true, true, false, true, true, true), //
	B(true, true, true, true, true, true, true), //
	C(true, false, false, true, true, true, false), //
	D(true, true, true, true, true, true, false), //
	E(true, false, false, true, true, true, true), //
	F(true, false, false, false, true, true, true), //
	G(true, true, false, true, true, true, true), //
	H(false, true, true, false, true, true, true), //
	I(false, true, true, false, false, false, false), //
	J(false, true, true, true, false, false, false), //
	K(false, true, true, false, true, true, true), //
	L(false, false, false, true, true, true, false), //
	M(false, true, true, false, true, true, true), //
	N(false, true, true, false, true, true, true), //
	O(true, true, true, true, true, true, false), //
	P(true, true, false, false, true, true, true), //
	Q(true, true, true, true, true, true, false), //
	R(true, true, true, false, true, true, true), //
	S(true, false, true, true, false, true, true), //
	T(true, false, false, false, true, true, false), //
	U(false, true, true, true, true, true, false), //
	V(false, true, true, true, true, true, false), //
	W(false, true, true, true, true, true, false), //
	X(false, true, true, false, true, true, true), //
	Y(false, true, true, false, false, true, true), //
	Z(true, true, false, true, true, false, true);//

	private final boolean one;
	private final boolean two;
	private final boolean three;
	private final boolean four;
	private final boolean five;
	private final boolean six;
	private final boolean seven;

	LEDCharacter(boolean one, boolean two, boolean three, boolean four, boolean five, boolean six, boolean seven) {
		this.one = one;
		this.two = two;
		this.three = three;
		this.four = four;
		this.five = five;
		this.six = six;
		this.seven = seven;
	}

	public static LEDCharacter getByNumber(int number) {
		switch (number) {
		case 0:
			return LEDCharacter.ZERO;
		case 1:
			return LEDCharacter.ONE;
		case 2:
			return LEDCharacter.TWO;
		case 3:
			return LEDCharacter.THREE;
		case 4:
			return LEDCharacter.FOUR;
		case 5:
			return LEDCharacter.FIVE;
		case 6:
			return LEDCharacter.SIX;
		case 7:
			return LEDCharacter.SEVEN;
		case 8:
			return LEDCharacter.EIGHT;
		case 9:
			return LEDCharacter.NINE;
		default:
			return LEDCharacter.CLEAR;
		}
	}

	public boolean isSwitchedOn(int index) {
		switch (index) {
		case 0:
			return one;
		case 1:
			return two;
		case 2:
			return three;
		case 3:
			return four;
		case 4:
			return five;
		case 5:
			return six;
		default:
			return seven;
		}
	}

}
