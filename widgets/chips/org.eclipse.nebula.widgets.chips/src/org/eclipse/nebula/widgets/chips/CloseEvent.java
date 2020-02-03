package org.eclipse.nebula.widgets.chips;

import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.Event;

/**
 * Instances of this class are sent as a result of
 * widgets being closed.
 * <p>
 * Note: The fields that are filled in depend on the widget.
 * </p>
 *
 * @see CloseListener
 */
public class CloseEvent extends TypedEvent {

	private static final long serialVersionUID = 5028668219476966408L;

	/**
	 * Constructs a new instance of this class based on the
	 * information in the given untyped event.
	 *
	 * @param e the untyped event containing the information
	 */
	public CloseEvent(final Event e) {
		super(e);
	}
}
