/*******************************************************************************
 * Copyright (c) 2015 Peter Schulz <eclipse-ps@kurzepost.de>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Peter Schulz - initial implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.formattedtext;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.junit.Test;


/**
 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=459484
 */
public class Bug459484Test {

    /**
     * Checks what happens if 3/29/2015 is attempted to be changed
     * into 2/29/2015 which is not a valid date. The {@link Calendar} instance
     * used internally makes this into 3/1/2015 which should be reflected
     * in the {@link Text} widget.
     */
    @Test
    public void dateCorrectionAfterFocusLost() {
        Shell shell = new Shell();

        Locale locale = Locale.US;
        DateTimeFormatter formatter = new DateFormatter(locale);
        FormattedText formattedText = new FormattedText(shell);
        formattedText.setFormatter(formatter);

        Calendar calendar = Calendar.getInstance(locale);
        calendar.clear();
        calendar.set(2015, Calendar.MARCH, 29, 0, 0, 0);

        formattedText.setValue(calendar.getTime());
        formattedText.getControl().setFocus();
        formattedText.getControl().setText("2/29/2015");

        for (Listener listener : formattedText.getControl().getListeners(SWT.FocusOut)) {
        	listener.handleEvent(createEvent(SWT.FocusOut, formattedText.getControl()));
        }

        calendar.clear();
        calendar.set(2015, Calendar.MARCH, 1, 0, 0, 0);

        assertEquals("Expected corrected date value", calendar.getTime(), formattedText.getValue());
        assertEquals("Expected corrected date string", "3/1/2015", formattedText.getControl().getText());
    }

	private Event createEvent(int type, Widget wiget) {
		Event event = new Event();
		event.widget = wiget;
		event.type = type;
		return event;
	}

}
