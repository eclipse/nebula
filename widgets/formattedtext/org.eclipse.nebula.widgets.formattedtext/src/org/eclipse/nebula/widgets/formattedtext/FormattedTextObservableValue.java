package org.eclipse.nebula.widgets.formattedtext;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.jface.databinding.swt.ISWTObservable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

public class FormattedTextObservableValue extends AbstractObservableValue implements ISWTObservable{
	/**
	 * {@link FormattedText} FormattedText that this is being observed.
	 */
	protected final FormattedText formattedText;

	/**
	 * SWT event that on firing this observable will fire change events to its
	 * listeners.
	 */
	private final int updateEventType;

	/**
	 * Flag to track when the model is updating the widget. When
	 * <code>true</code> the handlers for the SWT events should not process
	 * the event as this would cause an infinite loop.
	 */
	private boolean updating = false;

	/**
	 * Previous value.
	 */
	private Object oldValue;

	private Listener updateListener = new Listener() {
		public void handleEvent(Event event) {
			if ( ! updating ) {
				Object newValue = formattedText.getValue();

				if ( (newValue == null && oldValue != null)
						 || (newValue != null && ! newValue.equals(oldValue)) ) {
					fireValueChange(Diffs.createValueDiff(oldValue, newValue));					
					oldValue = newValue;
				}
			}
		}
	};

	/**
	 * Valid types for the {@link #updateEventType}.
	 */
	private static final int[] validUpdateEventTypes = new int[] { SWT.Modify,
			SWT.FocusOut, SWT.None };

	private DisposeListener disposeListener = new DisposeListener() {
		public void widgetDisposed(DisposeEvent e) {
			FormattedTextObservableValue.this.dispose();
		}
	};

	public FormattedTextObservableValue(final FormattedText formattedText) {
		this(Realm.getDefault(), formattedText, SWT.Modify);
	}

	public FormattedTextObservableValue(final FormattedText formattedText, int updateEventType) {
		this(Realm.getDefault(), formattedText, updateEventType);
	}

	public FormattedTextObservableValue(final Realm realm, final FormattedText formattedText, int updateEventType) {
		super(realm);

		boolean eventValid = false;
		for (int i = 0; !eventValid && i < validUpdateEventTypes.length; i++) {
			eventValid = (updateEventType == validUpdateEventTypes[i]);
		}
		if ( ! eventValid ) {
			throw new IllegalArgumentException(
					"UpdateEventType [" + updateEventType + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
		}

		this.formattedText = formattedText;
		this.updateEventType = updateEventType;
		if ( updateEventType != SWT.None ) {
			formattedText.getControl().addListener(updateEventType, updateListener);
		}

		formattedText.getControl().addDisposeListener(disposeListener);
	}

	public void dispose() {
		if ( formattedText.getControl() != null && ! formattedText.getControl().isDisposed() ) {
			if ( updateEventType != SWT.None ) {
				formattedText.getControl().removeListener(updateEventType, updateListener);
			}
		}
		super.dispose();
	}

	protected Object doGetValue() {
		return oldValue = formattedText.getValue();
	}

	/**
	 * Sets the bound {@link DateChooser} date chooser to the passed <code>value</code>.
	 * 
	 * @param value new value, Date expected
	 * @see org.eclipse.core.databinding.observable.value.AbstractObservableValue#doSetValue(java.lang.Object)
	 * @throws ClassCastException if the value is anything other than a Date
	 */
	protected void doSetValue(final Object value) {
		try {
			updating = true;
			formattedText.setValue(value);
			oldValue = formattedText.getValue();
		} finally {
			updating = false;
		}
	}

	public Object getValueType() {
		return formattedText.getValueType();
	}

	public Widget getWidget() {
		return formattedText.getControl();
	}
}
