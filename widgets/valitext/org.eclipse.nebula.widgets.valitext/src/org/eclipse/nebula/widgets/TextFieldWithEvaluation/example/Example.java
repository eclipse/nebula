/*******************************************************************************
* Copyright (C) 2019 Uenal Akkaya <uenal.akkaya@sap.com>, Michael Gutfleisch <michael.gutfleisch@sap.com>
*
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     Uenal Akkaya
*     Michael Gutfleisch
 ******************************************************************************/
package org.eclipse.nebula.widgets.TextFieldWithEvaluation.example;

import org.eclipse.nebula.widgets.TextFieldWithEvaluation.Evaluation;
import org.eclipse.nebula.widgets.TextFieldWithEvaluation.IEvaluator;
import org.eclipse.nebula.widgets.TextFieldWithEvaluation.MaxLengthEvaluator;
import org.eclipse.nebula.widgets.TextFieldWithEvaluation.TextFieldWithEvaluation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Example {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout());
		shell.setSize(300, 600);

		Text text = new Text(shell, SWT.BORDER);
		text.setText("Normal Text object");

		new TextFieldWithEvaluation(shell, 5);
		new TextFieldWithEvaluation(shell, new MaxLengthEvaluator(5));
		
		new TextFieldWithEvaluation(shell, new IEvaluator() {
			private final int maxLength = 5;

			private final Color green = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
			private final Color red = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED);

			@Override
			public Evaluation evaluate(String text) {
				int remaining = this.maxLength - text.length();
				String text2 = (remaining < 0 ? -1 * remaining + " too long" : remaining + " remaining");

				return new Evaluation(text2, remaining < 0 ? red : green);
			}

			@Override
			public boolean isValid(String text) {
				return text.length() <= maxLength;
			}
		});
		
		new TextFieldWithEvaluation(shell, new IEvaluator() {
			private final Color cool = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN);
			private final Color lame = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_MAGENTA);

			@Override
			public Evaluation evaluate(String text) {
				String textLower = text.toLowerCase();
				if (textLower.contains("cool")) {
					return new Evaluation("contains cool", cool);
				} else {
					return new Evaluation("doesn't contain cool", lame);
				}
			}

			@Override
			public boolean isValid(String text) {
				return false;
			}
		});
		
		new TextFieldWithEvaluation(shell, new IEvaluator() {
			private static final int minLength = 5;
			private static final int maxLength = 8;
			private final Color red = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED);
			private final Color yellow = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW);
			private final Color green = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);

			@Override
			public Evaluation evaluate(String text) {
				int len = text.length();
				if (len < minLength) {
					return new Evaluation("too short", red);
				} else if (len <= maxLength) {
					return new Evaluation("ok", yellow);
				} else {
					return new Evaluation("too long", green);
				}

			}

			@Override
			public boolean isValid(String text) {
				int len = text.length();
				return len >= minLength && len <= maxLength;
			}
		});
		
		new TextFieldWithEvaluation(shell, new IEvaluator() {

			@Override
			public Evaluation evaluate(String text) {
				return new Evaluation("very long hint without color that doesn't disappear!!!!!!", null);
			}

			@Override
			public boolean isValid(String text) {
				return false;
			}
		});

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

}
