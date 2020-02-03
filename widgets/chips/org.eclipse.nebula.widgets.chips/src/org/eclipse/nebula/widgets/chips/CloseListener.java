package org.eclipse.nebula.widgets.chips;

import org.eclipse.swt.internal.SWTEventListener;

/**
 * Classes which implement this interface provide methods
 * that deal with the events that are generated when a Chips widget is closed.
 * <p>
 * After creating an instance of a class that implements
 * this interface it can be added to a control using the
 * <code>addCloseListener</code> method and removed using
 * the <code>removeCloseListener</code> method. When
 * selection occurs in a control the appropriate method
 * will be invoked.
 * </p>
 *
 * @see CloseEvent
 */
@SuppressWarnings("restriction")
@FunctionalInterface
public interface CloseListener extends SWTEventListener {
	/**
	 * Sent when a Chips widget is closed.
	 *
	 * @param e an event containing information about the chips that is closed
	 */
	void onClose(CloseEvent event);
}
