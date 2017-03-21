/*******************************************************************************
 * Copyright (c) 2010, 2017 Oak Ridge National Laboratory and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.nebula.visualization.widgets.examples;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.nebula.visualization.widgets.datadefinition.ColorMap;
import org.eclipse.nebula.visualization.widgets.datadefinition.ColorMap.PredefinedColorMap;
import org.eclipse.nebula.visualization.widgets.figureparts.ColorMapRamp;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A live updated Intensity Graph example
 * 
 * @author Jonah Graham
 *
 */
public class ColorMapRampExample {

	private static final String INFINITY_TOOLTIP = "Infinity/-Infinity/NaN can be set and will be passed to ColorMapRamp, but the figure will ignore it.";
	private ColorMapRamp colorMapRamp;
	private Combo colorMapCombo;
	private Button customImageDataCheck;
	private Text minText;
	private Text maxText;

	public static void main(String[] args) {
		new ColorMapRampExample().run();
	}

	private void run() {
		final Shell shell = new Shell();
		shell.setSize(500, 500);
		shell.setLayout(new GridLayout());

		Canvas colorMapRampCanvas = new Canvas(shell, SWT.NONE);
		GridData gd = new GridData(SWT.CENTER, SWT.FILL, false, true);
		gd.widthHint = 100;
		colorMapRampCanvas.setLayoutData(gd);
		final LightweightSystem lws = new LightweightSystem(colorMapRampCanvas);
		colorMapRamp = new ColorMapRamp();
		lws.setContents(colorMapRamp);

		colorMapCombo = new Combo(shell, SWT.READ_ONLY);
		colorMapCombo.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		String[] names = PredefinedColorMap.getStringValues();
		names[0] += " (Provides no color map, will NullPointerException on its own)";
		colorMapCombo.setItems(names);
		colorMapCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateRamp();
			}
		});

		customImageDataCheck = new Button(shell, SWT.CHECK);
		customImageDataCheck.setText("Custom Image Data");
		customImageDataCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				updateRamp();
			};
		});

		Composite minMaxComposite = new Composite(shell, SWT.NONE);
		minMaxComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		minMaxComposite.setLayout(new GridLayout(2, false));

		Label minLabel = new Label(minMaxComposite, SWT.NONE);
		minLabel.setText("Min");
		minLabel.setToolTipText(INFINITY_TOOLTIP);
		minText = new Text(minMaxComposite, SWT.SINGLE);
		minText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		minText.setText("0");
		minText.setToolTipText(INFINITY_TOOLTIP);
		minText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateRamp();
			}
		});

		Label maxLabel = new Label(minMaxComposite, SWT.NONE);
		maxLabel.setText("Max");
		maxLabel.setToolTipText(INFINITY_TOOLTIP);
		maxText = new Text(minMaxComposite, SWT.SINGLE);
		maxText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
		maxText.setText("1");
		maxText.setToolTipText(INFINITY_TOOLTIP);
		maxText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateRamp();
			}
		});

		shell.open();
		Display display = Display.getDefault();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private void updateRamp() {

		int selectionIndex = colorMapCombo.getSelectionIndex();
		if (selectionIndex < 0 || selectionIndex >= PredefinedColorMap.values().length) {
			// default to something other than None
			selectionIndex = 1;
		}
		PredefinedColorMap map = PredefinedColorMap.fromIndex(selectionIndex);
		ColorMap colorMap = new ColorMap(map, true, true);
		colorMapRamp.setColorMap(colorMap);

		if (customImageDataCheck.getSelection()) {
			ImageData imageData = new ImageData(1, 256, 24, new PaletteData(0xff, 0xff00, 0xff0000));
			for (int i = 0; i < 128; i++) {
				imageData.setPixel(0, i, i * 2);
				imageData.setPixel(0, 256 - i - 1, i * 2);
			}
			colorMapRamp.setImageData(imageData);
		} else {
			colorMapRamp.setImageData(null);
		}

		try {
			double min = Double.parseDouble(minText.getText());
			colorMapRamp.setMin(min);
		} catch (NumberFormatException nfe) {
			System.out.println("NumberFormatException: Ignoring Min setting: " + minText.getText());
		}

		try {
			double max = Double.parseDouble(maxText.getText());
			colorMapRamp.setMax(max);
		} catch (NumberFormatException nfe) {
			System.out.println("NumberFormatException: Ignoring Max setting: " + maxText.getText());
		}

		colorMapRamp.repaint();

	}

}
