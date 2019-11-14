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
package org.eclipse.nebula.widgets.TextFieldWithEvaluation;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * This widget creates a text field with a build in evaluation of the text.
 */
public class TextFieldWithEvaluation extends Composite {

	private Text input;
	private Label hint;
	private IEvaluator evaluator;

	/**
	 * Default constructor. Uses a {@link MaxLengthEvaluator}.
	 * 
	 * @param parent
	 *            The parent composite
	 * @param maxLength
	 *            The maximum allowed length of the text
	 */
	public TextFieldWithEvaluation(Composite parent, int maxLength) {
		this(parent, SWT.NONE, maxLength);
	}

	/**
	 * Constructor which uses a maximum length evaluator.
	 * 
	 * @param parent
	 *            The parent composite
	 * @param style
	 *            The style of widget to construct
	 * @param maxLength
	 *            The maximum allowed length of the text
	 */
	public TextFieldWithEvaluation(Composite parent, int style, int maxLength) {
		this(parent, style, new MaxLengthEvaluator(maxLength));
	}

	/**
	 * Constructor which can uses the given {@link IEvaluator} to evaluate the text.
	 * 
	 * @param parent
	 *            The parent composite
	 * @param evaluator
	 *            The {@link IEvaluator} object
	 */
	public TextFieldWithEvaluation(Composite parent, IEvaluator evaluator) {
		this(parent, SWT.NONE, evaluator);
	}

	/**
	 * Constructor which can uses the given {@link IEvaluator} to evaluate the text.
	 * 
	 * @param parent
	 *            The parent composite
	 * @param style
	 *            The style of widget to construct
	 * @param evaluator
	 *            The {@link IEvaluator} object
	 */
	public TextFieldWithEvaluation(Composite parent, int style, IEvaluator evaluator) {
		super(parent, style);

		this.evaluator = evaluator;

		create();
		updateLayout();
	}

	/**
	 * Returns the text of the input field.
	 * 
	 * @return The text
	 */
	public String getText() {
		return input.getText();
	}

	/**
	 * Sets the text of the input field. Afterwards the validation is triggered.
	 * 
	 * @param text
	 *            The text to set
	 */
	public void setText(String text) {
		input.setText(text);
		validate();
	}

	/**
	 * Adds a {@link ModifyListener} to the input field.
	 * 
	 * @param listener
	 *            The listener which should be notified
	 */
	public void addModifyListener(ModifyListener listener) {
		input.addModifyListener(listener);
	}

	/**
	 * Removes the given {@link ModifyListener} from the input field.
	 * 
	 * @param listener
	 *            The listener which should no longer be notified
	 */
	public void removeModifyListener(ModifyListener listener) {
		input.removeModifyListener(listener);
	}

	/**
	 * Returns the {@link IEvaluator} of this widget.
	 * 
	 * @return The {@link IEvaluator} of this widget
	 */
	public IEvaluator getEvaluator() {
		return evaluator;
	}

	/**
	 * Sets a new {@link IEvaluator} for this widget
	 * 
	 * @param evaluator
	 *            The {@link IEvaluator} to set
	 */
	public void setEvaluator(IEvaluator evaluator) {
		checkWidget();

		this.evaluator = evaluator;

		if (this.hint != null) {
			this.hint.dispose();
		}
		if (this.input != null) {
			this.input.dispose();
		}

		create();
		updateLayout();
	}

	/**
	 * Selects all text in the input field.
	 */
	public void selectAll() {
		this.input.selectAll();
	}

	private void create() {
		setLayout();

		createInputField();
		createHintLabel();

		input.addListener(SWT.MouseUp, e -> validate());
		input.addListener(SWT.KeyUp, e -> validate());
		input.addListener(SWT.FocusIn, e -> validate());
		input.addListener(SWT.FocusOut, e -> {
			if (evaluator.isValid(input.getText())) {
				hint.setText("");
				updateLayout();
			}
		});

		this.addPaintListener(this::paint);
	}

	private void createHintLabel() {
		hint = new Label(this, SWT.READ_ONLY);

		GridData gdHint = GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.FILL).create();
		hint.setLayoutData(gdHint);
	}

	private void createInputField() {
		input = new Text(this, SWT.NONE);

		GridData gd = GridDataFactory.fillDefaults().grab(true, false).create();
		input.setLayoutData(gd);
	}

	private void validate() {
		String inputText = input.getText();
		Evaluation evaluation = evaluator.evaluate(inputText);

		if (evaluation != null) {
			if (evaluation.color != null) {
				hint.setForeground(evaluation.color);
			}
			if (evaluation.text != null) {
				hint.setText(evaluation.text);
				hint.setToolTipText(evaluation.text);
			} else {
				hint.setText("");
				hint.setToolTipText(null);
			}
		}

		updateLayout();
	}

	private void updateLayout() {
		try {
			setRedraw(false);
			hint.setBackground(input.getBackground());
			setBackground(input.getBackground());
		} finally {
			setRedraw(true);
			layout();
			redraw();
		}
	}

	private void setLayout() {
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 3;

		setLayout(layout);
		setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
	}

	private void paint(PaintEvent e) {
		int fgColorId = input.isFocusControl() ? SWT.COLOR_LIST_SELECTION : SWT.COLOR_WIDGET_BORDER;
		Color fgColor = getDisplay().getSystemColor(fgColorId);
		e.gc.setForeground(fgColor);

		Point size = getSize();
		e.gc.drawRectangle(0, 0, size.x - 1, size.y - 1);
	}
}
