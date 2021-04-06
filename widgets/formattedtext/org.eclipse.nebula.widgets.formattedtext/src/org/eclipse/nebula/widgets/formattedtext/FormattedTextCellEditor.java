package org.eclipse.nebula.widgets.formattedtext;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * A CellEditor based on a FormattedText. It extends the TextCellEditor,
 * adding formatting capabilities based on an IFormatter.
 * A formatter can be associated with the editor with the setFormatter method.
 * If not, a formatter is automatically created at the first call of the
 * setValue method, based on the type of the value.
 */
public class FormattedTextCellEditor extends TextCellEditor {
	protected FormattedText formattedText;

  /**
   * Creates a new formatted text cell editor parented under the given control.
   *
   * @param parent the parent control
   */
	public FormattedTextCellEditor(Composite parent) {
		super(parent);
	}

  /**
   * Creates a new formatted text cell editor parented under the given control.
   *
   * @param parent the parent control
   * @param style the style bits
   */
	public FormattedTextCellEditor(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @see org.eclipse.jface.viewers.TextCellEditor#createControl(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createControl(Composite parent) {
		Text text = (Text) super.createControl(parent);
		formattedText = new FormattedText(text);
		return text;
	}

	/**
	 * @see org.eclipse.jface.viewers.TextCellEditor#doGetValue()
	 */
	protected Object doGetValue() {
		return formattedText.getValue();
	}

	/**
	 * @see org.eclipse.jface.viewers.TextCellEditor#doSetValue(java.lang.Object)
	 */
	protected void doSetValue(Object value) {
		formattedText.setValue(value);
	}

	/**
	 * Returns the FormattedText object used by this cell editor.
	 *
	 * @return FormattedText object
	 */
	public FormattedText getFormattedText() {
		return formattedText;
	}

	/**
	 * Sets the formatter that this cell editor must use to edit the value.
	 *
	 * @param formatter the formatter
	 */
	public void setFormatter(ITextFormatter formatter) {
		formattedText.setFormatter(formatter);
	}
}
