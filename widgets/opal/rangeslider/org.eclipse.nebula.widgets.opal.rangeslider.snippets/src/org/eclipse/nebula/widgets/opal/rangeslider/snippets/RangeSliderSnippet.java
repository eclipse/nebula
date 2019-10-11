/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron@gmail.com) - initial API and
 * implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.rangeslider.snippets;

import org.eclipse.nebula.widgets.opal.rangeslider.RangeSlider;
import org.eclipse.nebula.widgets.opal.titledseparator.TitledSeparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A simple snipper for the RangleSlider widget
 *
 */
public class RangeSliderSnippet {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		createNormalSliders(new Group(shell, SWT.NONE));
		createDisabledSliders(new Group(shell, SWT.NONE));
		createDifferentSliders(new Group(shell, SWT.NONE));
		createSlidersCanceledSelectionListener(new Group(shell, SWT.NONE));

		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();

	}

	private static void createNormalSliders(final Group group) {
		group.setLayout(new GridLayout(3, false));

		final TitledSeparator tsh = new TitledSeparator(group, SWT.NONE);
		tsh.setText("Horizontal Range Slider");
		tsh.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));

		final RangeSlider hRangeSlider = new RangeSlider(group, SWT.HORIZONTAL);
		final GridData gd = new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 2);
		gd.widthHint = 250;
		hRangeSlider.setLayoutData(gd);
		hRangeSlider.setMinimum(0);
		hRangeSlider.setMaximum(100);
		hRangeSlider.setLowerValue(0);
		hRangeSlider.setUpperValue(60);

		final Label hLabelLower = new Label(group, SWT.NONE);
		hLabelLower.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));
		hLabelLower.setText("Lower Value:");

		final Text hTextLower = new Text(group, SWT.BORDER);
		hTextLower.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1));
		hTextLower.setText(hRangeSlider.getLowerValue() + "   ");
		hTextLower.setEnabled(false);

		final Label hLabelUpper = new Label(group, SWT.NONE);
		hLabelUpper.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));
		hLabelUpper.setText("Upper Value:");

		final Text hTextUpper = new Text(group, SWT.BORDER);
		hTextUpper.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1));
		hTextUpper.setText(hRangeSlider.getUpperValue() + "   ");
		hTextUpper.setEnabled(false);

		hRangeSlider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				hTextLower.setText(hRangeSlider.getLowerValue() + "   ");
				hTextUpper.setText(hRangeSlider.getUpperValue() + "   ");
			}
		});

		final TitledSeparator tsv = new TitledSeparator(group, SWT.NONE);
		tsv.setText("Vertical Range Slider");
		tsv.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));

		final RangeSlider vRangeSlider = new RangeSlider(group, SWT.VERTICAL);
		final GridData gd2 = new GridData(GridData.CENTER, GridData.FILL, false, false, 1, 2);
		gd2.heightHint = 300;
		vRangeSlider.setLayoutData(gd2);

		final Label vLabelLower = new Label(group, SWT.NONE);
		vLabelLower.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));
		vLabelLower.setText("Lower Value:");

		final Text vTextLower = new Text(group, SWT.BORDER);
		vTextLower.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1));
		vTextLower.setText(vRangeSlider.getLowerValue() + "   ");
		vTextLower.setEnabled(false);

		final Label vLabelUpper = new Label(group, SWT.NONE);
		vLabelUpper.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));
		vLabelUpper.setText("Upper Value:");

		final Text vTextUpper = new Text(group, SWT.BORDER);
		vTextUpper.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1));
		vTextUpper.setText(vRangeSlider.getUpperValue() + "   ");
		vTextUpper.setEnabled(false);

		vRangeSlider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				vTextLower.setText(hRangeSlider.getLowerValue() + "   ");
				vTextUpper.setText(hRangeSlider.getUpperValue() + "   ");
			}
		});

	}

	private static void createDisabledSliders(final Group group) {
		group.setLayout(new GridLayout(3, false));

		final TitledSeparator tsh = new TitledSeparator(group, SWT.NONE);
		tsh.setText("Horizontal Range Slider, disabled");
		tsh.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));

		final RangeSlider hRangeSlider = new RangeSlider(group, SWT.HORIZONTAL);
		final GridData gd = new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 2);
		gd.widthHint = 250;
		hRangeSlider.setLayoutData(gd);
		hRangeSlider.setMinimum(0);
		hRangeSlider.setMaximum(100);
		hRangeSlider.setLowerValue(0);
		hRangeSlider.setUpperValue(60);
		hRangeSlider.setEnabled(false);

		final Label hLabelLower = new Label(group, SWT.NONE);
		hLabelLower.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));
		hLabelLower.setText("Lower Value:");

		final Text hTextLower = new Text(group, SWT.BORDER);
		hTextLower.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1));
		hTextLower.setText(hRangeSlider.getLowerValue() + "   ");
		hTextLower.setEnabled(false);

		final Label hLabelUpper = new Label(group, SWT.NONE);
		hLabelUpper.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));
		hLabelUpper.setText("Upper Value:");

		final Text hTextUpper = new Text(group, SWT.BORDER);
		hTextUpper.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1));
		hTextUpper.setText(hRangeSlider.getUpperValue() + "   ");
		hTextUpper.setEnabled(false);

		final TitledSeparator tsv = new TitledSeparator(group, SWT.NONE);
		tsv.setText("Vertical Range Slider, disabled");
		tsv.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));

		final RangeSlider vRangeSlider = new RangeSlider(group, SWT.VERTICAL);
		final GridData gd2 = new GridData(GridData.CENTER, GridData.FILL, false, false, 1, 2);
		gd2.heightHint = 300;
		vRangeSlider.setLayoutData(gd2);
		vRangeSlider.setEnabled(false);

		final Label vLabelLower = new Label(group, SWT.NONE);
		vLabelLower.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));
		vLabelLower.setText("Lower Value:");

		final Text vTextLower = new Text(group, SWT.BORDER);
		vTextLower.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1));
		vTextLower.setText(vRangeSlider.getLowerValue() + "   ");
		vTextLower.setEnabled(false);

		final Label vLabelUpper = new Label(group, SWT.NONE);
		vLabelUpper.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));
		vLabelUpper.setText("Upper Value:");

		final Text vTextUpper = new Text(group, SWT.BORDER);
		vTextUpper.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1));
		vTextUpper.setText(vRangeSlider.getUpperValue() + "   ");
		vTextUpper.setEnabled(false);

	}

	private static void createDifferentSliders(final Group group) {
		group.setLayout(new GridLayout(3, false));

		final TitledSeparator tsh = new TitledSeparator(group, SWT.NONE);
		tsh.setText("Horizontal Range Slider, between 100 and 1000, increment by 100");
		tsh.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));

		final RangeSlider hRangeSlider = new RangeSlider(group, SWT.HORIZONTAL);
		final GridData gd = new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 2);
		gd.widthHint = 250;
		hRangeSlider.setLayoutData(gd);
		hRangeSlider.setMinimum(100);
		hRangeSlider.setMaximum(1000);
		hRangeSlider.setLowerValue(200);
		hRangeSlider.setUpperValue(800);
		hRangeSlider.setIncrement(100);
		hRangeSlider.setPageIncrement(200);

		final Label hLabelLower = new Label(group, SWT.NONE);
		hLabelLower.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));
		hLabelLower.setText("Lower Value:");

		final Text hTextLower = new Text(group, SWT.BORDER);
		hTextLower.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1));
		hTextLower.setText(hRangeSlider.getLowerValue() + "   ");
		hTextLower.setEnabled(false);

		final Label hLabelUpper = new Label(group, SWT.NONE);
		hLabelUpper.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));
		hLabelUpper.setText("Upper Value:");

		final Text hTextUpper = new Text(group, SWT.BORDER);
		hTextUpper.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1));
		hTextUpper.setText(hRangeSlider.getUpperValue() + "   ");
		hTextUpper.setEnabled(false);

		final TitledSeparator tsv = new TitledSeparator(group, SWT.NONE);
		tsv.setText("Vertical Range Slider, between 100 and 1000, increment by 100");
		tsv.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));

		final RangeSlider vRangeSlider = new RangeSlider(group, SWT.VERTICAL);
		final GridData gd2 = new GridData(GridData.CENTER, GridData.FILL, false, false, 1, 2);
		gd2.heightHint = 300;
		vRangeSlider.setLayoutData(gd2);
		vRangeSlider.setMinimum(100);
		vRangeSlider.setMaximum(1000);
		vRangeSlider.setLowerValue(200);
		vRangeSlider.setUpperValue(800);
		vRangeSlider.setIncrement(100);
		vRangeSlider.setPageIncrement(200);

		final Label vLabelLower = new Label(group, SWT.NONE);
		vLabelLower.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));
		vLabelLower.setText("Lower Value:");

		final Text vTextLower = new Text(group, SWT.BORDER);
		vTextLower.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1));
		vTextLower.setText(vRangeSlider.getLowerValue() + "   ");
		vTextLower.setEnabled(false);

		final Label vLabelUpper = new Label(group, SWT.NONE);
		vLabelUpper.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));
		vLabelUpper.setText("Upper Value:");

		final Text vTextUpper = new Text(group, SWT.BORDER);
		vTextUpper.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1));
		vTextUpper.setText(vRangeSlider.getUpperValue() + "   ");
		vTextUpper.setEnabled(false);

	}

	private static void createSlidersCanceledSelectionListener(final Group group) {
		group.setLayout(new GridLayout(3, false));

		final TitledSeparator tsh = new TitledSeparator(group, SWT.NONE);
		tsh.setText("Horizontal Range Slider, cancel selection");
		tsh.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));

		final RangeSlider hRangeSlider = new RangeSlider(group, SWT.HORIZONTAL);
		final GridData gd = new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 2);
		gd.widthHint = 250;
		hRangeSlider.setLayoutData(gd);
		hRangeSlider.setMinimum(0);
		hRangeSlider.setMaximum(100);
		hRangeSlider.setLowerValue(0);
		hRangeSlider.setUpperValue(60);

		hRangeSlider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				e.doit = false;
			}
		});

		final Label hLabelLower = new Label(group, SWT.NONE);
		hLabelLower.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));
		hLabelLower.setText("Lower Value:");

		final Text hTextLower = new Text(group, SWT.BORDER);
		hTextLower.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1));
		hTextLower.setText(hRangeSlider.getLowerValue() + "   ");
		hTextLower.setEnabled(false);

		final Label hLabelUpper = new Label(group, SWT.NONE);
		hLabelUpper.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));
		hLabelUpper.setText("Upper Value:");

		final Text hTextUpper = new Text(group, SWT.BORDER);
		hTextUpper.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1));
		hTextUpper.setText(hRangeSlider.getUpperValue() + "   ");
		hTextUpper.setEnabled(false);

		final TitledSeparator tsv = new TitledSeparator(group, SWT.NONE);
		tsv.setText("Vertical Range Slider, cancel selection");
		tsv.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));

		final RangeSlider vRangeSlider = new RangeSlider(group, SWT.VERTICAL);
		final GridData gd2 = new GridData(GridData.CENTER, GridData.FILL, false, false, 1, 2);
		gd2.heightHint = 300;
		vRangeSlider.setLayoutData(gd2);

		final Label vLabelLower = new Label(group, SWT.NONE);
		vLabelLower.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));
		vLabelLower.setText("Lower Value:");

		final Text vTextLower = new Text(group, SWT.BORDER);
		vTextLower.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1));
		vTextLower.setText(vRangeSlider.getLowerValue() + "   ");
		vTextLower.setEnabled(false);

		final Label vLabelUpper = new Label(group, SWT.NONE);
		vLabelUpper.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1));
		vLabelUpper.setText("Upper Value:");

		final Text vTextUpper = new Text(group, SWT.BORDER);
		vTextUpper.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false, 1, 1));
		vTextUpper.setText(vRangeSlider.getUpperValue() + "   ");
		vTextUpper.setEnabled(false);

		vRangeSlider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				e.doit = false;
			}
		});

	}
}
