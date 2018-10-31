/******************************************************************************
 * Copyright (c) 2018 Remain BV (Remain Software)
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: 
 *  Wim Jongman (wim.jongman@remainsoftware.com) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.nebula.widgets.floatingtext;

import org.eclipse.nebula.widgets.opal.promptsupport.PromptSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

/**
 * Instances of this class are selectable user interface objects that allow the
 * user to enter and modify text with a label floating above the input area.
 * FloatinText controls can be either single or multi-line. In contrast to a
 * Text widget, when a floating text control is created with a border, a custom
 * drawn inset will be painted INSTEAD OF the platform specific inset.
 * 
 * <p>
 * Style SEPARATOR leaves a gap between the label and the text of one pixel in
 * the color set by {@link #setBackground(Color)}. To add more space use the
 * {@link #setSeparatorSpace(int)} method.
 * 
 * <dl>
 * <dt><b>Styles are inherited from Text and subclasses:</b></dt>
 * <dd>CENTER, ICON_CANCEL, ICON_SEARCH, LEFT, MULTI, PASSWORD, SEARCH, SINGLE,
 * RIGHT, READ_ONLY, WRAP, BORDER, H_SCROLL, V_SCROLL, LEFT_TO_RIGT,
 * RIGHT_TO_LEFT, FLIP_TEXT_DIRECTION, SEPARATOR</dd>
 * <dt><b>Events:</b></dt>
 * <dd>DefaultSelection, Modify, Verify, OrientationChange</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles MULTI and SINGLE may be specified, and only one
 * of the styles LEFT, CENTER, and RIGHT may be specified.
 * </p>
 * <p>
 * Note: The styles ICON_CANCEL and ICON_SEARCH are hints used in combination
 * with SEARCH. When the platform supports the hint, the text control shows
 * these icons. When an icon is selected, a default selection event is sent with
 * the detail field set to one of ICON_CANCEL or ICON_SEARCH. Normally,
 * application code does not need to check the detail. In the case of
 * ICON_CANCEL, the text is cleared before the default selection event is sent
 * causing the application to search for an empty string.
 * </p>
 */
public class FloatingText extends Composite implements FocusListener {
	private Text fText;
	private Label fLabel;
	private int fStyle;
	private int fRatio = 80;

	/**
	 * Constructs a new instance of this class given its parent and a style value
	 * describing its behavior and appearance.
	 * <p>
	 * The style value is either one of the style constants defined in class
	 * <code>SWT</code> which is applicable to instances of this class, or must be
	 * built by <em>bitwise OR</em>'ing together (that is, using the
	 * <code>int</code> "|" operator) two or more of those <code>SWT</code> style
	 * constants. The class description lists the style constants that are
	 * applicable to the class. Style bits are NOT inherited from superclasses.
	 * </p>
	 *
	 * @param parent a composite control which will be the parent of the new
	 *               instance (cannot be null)
	 * @param style  the style of control to construct
	 *
	 * @exception IllegalArgumentException
	 *                                     <ul>
	 *                                     <li>ERROR_NULL_ARGUMENT - if the parent
	 *                                     is null</li>
	 *                                     </ul>
	 * @exception SWTException
	 *                                     <ul>
	 *                                     <li>ERROR_THREAD_INVALID_ACCESS - if not
	 *                                     called from the thread that created the
	 *                                     parent</li>
	 *                                     <li>ERROR_INVALID_SUBCLASS - if this
	 *                                     class is not an allowed subclass</li>
	 *                                     </ul>
	 *
	 * @see SWT#SEPARATOR
	 * @see SWT#SINGLE
	 * @see SWT#MULTI
	 * @see SWT#READ_ONLY
	 * @see SWT#WRAP
	 * @see SWT#LEFT
	 * @see SWT#RIGHT
	 * @see SWT#CENTER
	 * @see SWT#PASSWORD
	 * @see SWT#SEARCH
	 * @see SWT#BORDER
	 * @see SWT#H_SCROLL
	 * @see SWT#V_SCROLL
	 * @see SWT#LEFT_TO_RIGHT
	 * @see SWT#RIGHT_TO_LEFT
	 * @see SWT#FLIP_TEXT_DIRECTION
	 * @see Widget#checkSubclass
	 * @see Widget#getStyle
	 */
	public FloatingText(final Composite pParent, final int pStyle) {

		super(pParent, SWT.DOUBLE_BUFFERED | (pStyle & SWT.BORDER));
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		setLayout(gridLayout);

		if ((pStyle & SWT.SEPARATOR) == SWT.SEPARATOR) {
			gridLayout.verticalSpacing = 1;
		}

		fLabel = new Label(this, SWT.NONE);

		fStyle = ((pStyle & SWT.SINGLE) | (pStyle & SWT.MULTI) | (pStyle & SWT.READ_ONLY) | (pStyle & SWT.WRAP)
				| (pStyle & SWT.LEFT) | (pStyle & SWT.RIGHT) | (pStyle & SWT.CENTER) | (pStyle & SWT.PASSWORD)
				| (pStyle & SWT.SEARCH) | (pStyle & SWT.ICON_SEARCH) | (pStyle & SWT.ICON_CANCEL)
				| (pStyle & SWT.BORDER) | (pStyle & SWT.LEFT_TO_RIGHT) | (pStyle & SWT.RIGHT_TO_LEFT)
				| (pStyle & SWT.FLIP_TEXT_DIRECTION) | (pStyle & SWT.H_SCROLL) | (pStyle & SWT.V_SCROLL));

		fText = new Text(this, removeStyles(pStyle, SWT.BORDER, SWT.SEPARATOR));
		fText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		fLabel.setBackground(fText.getBackground());
		fLabel.setLayoutData(getLabelLayoutData());

		fText.addFocusListener(this);
	}

	private int removeStyles(int pStyle, int... styles) {
		int result = pStyle;
		for (int i : styles) {
			if ((result & i) == i) {
				result = result ^ i;
			}
		}
		return result;
	}

	private GridData getLabelLayoutData() {
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.heightHint = (fText.computeSize(-1, -1).y * fRatio) / 100;
		return gridData;
	}

	private void setLabelText(boolean pFocus) {
		fLabel.setText("");
		String prompt = PromptSupport.getPrompt(fText);
		if (pFocus) {
			doSetLabelText();
		} else {
			if (!fText.getText().isEmpty() && !fText.getText().equals(prompt)) {
				doSetLabelText();
			}
		}
	}

	public FloatingText setSeparatorSpace(final int space) {
		getLayout().verticalSpacing = space;
		requestLayout();
		return this;
	}

	private void doSetLabelText() {
		String message = getMessage();
		if (!message.isEmpty()) {
			FontData[] fontData = fText.getFont().getFontData();
			int height = (fLabel.getSize().y * fRatio) /130;
			if(height == 0) {
				return;
			}
			System.out.println(height);
			fontData[0].setHeight(height);
			Font font = new Font(getDisplay(), fontData[0]);
			fLabel.setFont(font);
			fLabel.setText(message);
			font.dispose();
		}
	}

	private String getMessage() {
		String message = fText.getMessage();
		if (message == null || message.trim().isEmpty()) {
			message = PromptSupport.getPrompt(fText);
		}
		return message == null ? "" : message.trim();
	}

	/**
	 * @return the underlying text widget.
	 */
	public Text getText() {
		return fText;
	}

	@Override
	public Point computeSize(int pWidthHint, int pHeightHint, boolean pChanged) {
		Point textSize = fText.computeSize(pWidthHint, pHeightHint, pChanged);
		Point labelSize = fLabel.computeSize(pWidthHint, pHeightHint, pChanged);
		Point result = new Point(pWidthHint, pHeightHint);
		result.x = Math.max(textSize.x, labelSize.x);
		if (pWidthHint > 0) {
			result.x = pWidthHint;
		}
		result.y = textSize.y + labelSize.y;
		if (pHeightHint > result.y) {
			result.y = pHeightHint;
		}
		if ((fStyle & SWT.BORDER) == SWT.BORDER) {
			result.x += 2;
			result.y += 2;
		}
		result.y += getLayout().verticalSpacing;
		return result;
	}

	@Override
	public void focusGained(FocusEvent pEvent) {
		setLabelText(true);
	}

	@Override
	public void focusLost(FocusEvent pEvent) {
		setLabelText(false);
	}

	/**
	 * Sets the backgrounds of the label and the text to the provided color.
	 * 
	 * @param color the color.
	 */
	public void setBackgroundColors(Color pColor) {
		fText.setBackground(pColor);
		fLabel.setBackground(pColor);
	}

	/**
	 * Sets the foregrounds of the label and the text to the provided color.
	 * 
	 * @param color the color.
	 */
	public void setForegroundColors(Color pColor) {
		fText.setForeground(pColor);
		fLabel.setForeground(pColor);
	}

	/**
	 * @return the label that floats above the text
	 */
	public Label getLabel() {
		return fLabel;
	}

	@Override
	public GridLayout getLayout() {
		return (GridLayout) super.getLayout();
	}

	/**
	 * Sets the height of the label as ratio of the text height.
	 * 
	 * @param pRatio the ratio of the label versus the text height
	 */
	public void setRatio(int pRatio) {
		fRatio = pRatio;
		requestLayout();
	}
}
