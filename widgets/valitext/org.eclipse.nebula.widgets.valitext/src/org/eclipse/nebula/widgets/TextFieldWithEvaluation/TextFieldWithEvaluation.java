package org.eclipse.nebula.widgets.TextFieldWithEvaluation;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class TextFieldWithEvaluation extends Composite {

	private Text input;
	private Label hint;
	private IEvaluator evaluator;

	public TextFieldWithEvaluation(Composite parent, int maxLength) {
		this(parent, SWT.NONE, maxLength);
	}

	public TextFieldWithEvaluation(Composite parent, int style, int maxLength) {
		this(parent, style, new MaxLengthEvaluator(maxLength));
	}

	public TextFieldWithEvaluation(Composite parent, IEvaluator evaluator) {
		this(parent, SWT.NONE, evaluator);
	}

	public TextFieldWithEvaluation(Composite parent, int style, IEvaluator evaluator) {
		super(parent, SWT.NONE);

		this.evaluator = evaluator;

		create();
		updateLayout();
	}

	public String getText() {
		return input.getText();
	}

	public void setText(String text) {
		input.setText(text);
		validate();
	}

	public void addModifyListener(ModifyListener listener) {
		input.addModifyListener(listener);
	}

	public void removeModifyListener(ModifyListener listener) {
		input.removeModifyListener(listener);
	}

	public IEvaluator getEvaluator() {
		return evaluator;
	}

	public void setEvaluator(IEvaluator evaluator) {
		this.evaluator = evaluator;
	}

	public void selectAll() {
		this.input.selectAll();
	}

	private void create() {
		setLayout();

		createInputField();
		createHintLabel();

		input.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				validate();
			}

			@Override
			public void mouseDown(MouseEvent e) {
				validate();
			}
		});

		input.addModifyListener(e -> validate());
		input.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				validate();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				validate();
			};
		});

		input.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				if (evaluator.isValid(input.getText())) {
					hint.setText("");
					updateLayout();
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
				validate();
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
		hint.pack();
		hint.setBackground(input.getBackground());
		setBackground(input.getBackground());
		layout();
		redraw();
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
