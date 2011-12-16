/*******************************************************************************
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    mhall - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.radiogroup.example;

import org.eclipse.nebula.widgets.radiogroup.RadioGroup;
import org.eclipse.nebula.widgets.radiogroup.RadioItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.nebula.examples.AbstractExampleTab;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;

public class RadioGroupExampleTab extends AbstractExampleTab {

	private RadioGroup radioGroup;
	private int style = SWT.BORDER;
	private RadioGroup orientation;
	private RadioGroup alignment;
	private RadioGroup bidi;

	public RadioGroupExampleTab() {
		// TODO Auto-generated constructor stub
	}

	public Control createControl(Composite parent) {
		radioGroup = new RadioGroup(parent, style);
		new RadioItem(radioGroup, SWT.NONE).setText("Red");
		new RadioItem(radioGroup, SWT.NONE).setText("Orange");
		new RadioItem(radioGroup, SWT.NONE).setText("Yellow");
		new RadioItem(radioGroup, SWT.NONE).setText("Green");
		new RadioItem(radioGroup, SWT.NONE).setText("Blue");
		new RadioItem(radioGroup, SWT.NONE).setText("Indigo");
		new RadioItem(radioGroup, SWT.NONE).setText("Violet");
		return radioGroup;
	}

	public String[] createLinks() {
		return new String[] {
				"<a href=\"http://www.eclipse.org/nebula/widgets/radiogroup/radiogroup.php\">RadioGroup Home Page</a>",
				"<a href=\"http://www.eclipse.org/nebula/snippets.php#RadioGroup\">Snippets</a>",
				"<a href=\"https://bugs.eclipse.org/bugs/buglist.cgi?classification=Technology&product=Nebula&component=RadioGroup&bug_status=UNCONFIRMED&bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED\">Bugs</a>" };
	}

	public void createParameters(Composite parent) {
		parent.setLayout(new GridLayout());

		Group g = new Group(parent, SWT.BORDER);
		g.setText("Styles:");
		g.setLayout(new GridLayout());
		g.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		orientation = new RadioGroup(g, SWT.HORIZONTAL);
		new RadioItem(orientation, SWT.NONE).setText("HORIZONTAL");
		new RadioItem(orientation, SWT.NONE).setText("VERTICAL");
		orientation.select(0);
		orientation.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				style &= ~(SWT.VERTICAL | SWT.HORIZONTAL);
				switch (orientation.getSelectionIndex()) {
				case 0:
					style |= SWT.HORIZONTAL;
					break;
				case 1:
					style |= SWT.VERTICAL;
					break;
				}
				recreateAndLayout();
			}
		});

		alignment = new RadioGroup(g, SWT.HORIZONTAL);
		new RadioItem(alignment, SWT.NONE).setText("LEFT");
		new RadioItem(alignment, SWT.NONE).setText("CENTER");
		new RadioItem(alignment, SWT.NONE).setText("RIGHT");
		alignment.select(0);
		alignment.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				style &= ~(SWT.LEFT | SWT.CENTER | SWT.RIGHT);
				switch (alignment.getSelectionIndex()) {
				case 0:
					style |= SWT.LEFT;
					break;
				case 1:
					style |= SWT.CENTER;
					break;
				case 2:
					style |= SWT.RIGHT;
					break;
				}
				recreateAndLayout();
			}
		});

		bidi = new RadioGroup(g, SWT.HORIZONTAL);
		new RadioItem(bidi, SWT.NONE).setText("LEFT_TO_RIGHT");
		new RadioItem(bidi, SWT.NONE).setText("RIGHT_TO_LEFT");
		bidi.select(0);
		bidi.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				style &= ~(SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT);
				switch (bidi.getSelectionIndex()) {
				case 0:
					style |= SWT.LEFT_TO_RIGHT;
					break;
				case 1:
					style |= SWT.RIGHT_TO_LEFT;
					break;
				}
				recreateAndLayout();
			}
		});

		Button b = new Button(g, SWT.CHECK);
		b.setText("Border");
		b.setSelection(true);
		b.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (((Button) e.widget).getSelection()) {
					style |= SWT.BORDER;
				} else {
					style &= ~SWT.BORDER;
				}
				recreateAndLayout();
			}
		});

		Button clear = new Button(g, SWT.PUSH);
		clear.setText("Selection to Null");
		clear.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				radioGroup.deselectAll();
				relayoutExample();
			}
		});
	}

	private void recreateAndLayout() {
		recreateExample();
		relayoutExample();
	}
}
