/*******************************************************************************
 * Copyright (c) 2010, 2017 Oak Ridge National Laboratory and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.nebula.visualization.internal.xygraph.toolbar;

import java.util.Arrays;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.nebula.visualization.xygraph.figures.Axis;
import org.eclipse.nebula.visualization.xygraph.figures.IXYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.BaseLine;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.ErrorBarType;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.PointStyle;
import org.eclipse.nebula.visualization.xygraph.figures.Trace.TraceType;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

/**
 * This will help to create the necessary widgets to configure an axis's
 * properties.
 * 
 * @author Xihui Chen
 * @author Baha El-Kassaby - make TraceConfigPage implement ITraceConfigPage
 *
 */
public class TraceConfigPage implements ITraceConfigPage {
	private IXYGraph xyGraph;
	private Trace trace;
	private Text nameText;
	private Combo xAxisCombo;
	private Combo yAxisCombo;
	private ColorSelector traceColorSelector;
	private Combo traceTypeCombo;
	private Spinner lineWidthSpinner;
	private Combo pointStyleCombo;
	private Spinner pointSizeSpinner;
	private Combo baseLineCombo;
	private Spinner areaAlphaSpinner;
	private Button antiAliasing;

	private Button errorBarEnabledButton;
	private Combo xErrorBarTypeCombo;
	private Combo yErrorBarTypeCombo;
	private ColorSelector errorBarColorSelector;
	private Spinner errorBarCapWidthSpinner;
	private Button drawYErrorInAreaButton;

	private Button visible;

	private Composite composite;

	/**
	 * Construct a trace configuration page
	 *
	 * @param xyGraph
	 *         the graph where the trace is
	 * @param trace
	 *         trace to be configured
	 */
	public TraceConfigPage(IXYGraph xyGraph, Trace trace) {
		this.xyGraph = xyGraph;
		this.trace = trace;
	}

	@Override
	public void createPage(final Composite composite) {
		this.composite = composite;
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(2, false));

		Composite traceCompo = new Composite(composite, SWT.NONE);
		traceCompo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		traceCompo.setLayout(new GridLayout(3, false));

		final Group errorBarGroup = new Group(composite, SWT.NONE);
		errorBarGroup.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
		errorBarGroup.setLayout(new GridLayout(2, false));
		errorBarGroup.setText("Error Bar");

		GridData gd;
		GridData labelGd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);

		visible = new Button(traceCompo, SWT.CHECK);
		visible.setText("Visible");
		visible.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));

		final Label nameLabel = new Label(traceCompo, 0);
		nameLabel.setText("Name: ");
		nameLabel.setLayoutData(labelGd);

		nameText = new Text(traceCompo, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		nameText.setLayoutData(gd);

		final Label xAxisLabel = new Label(traceCompo, 0);
		xAxisLabel.setText("X-Axis: ");
		labelGd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		xAxisLabel.setLayoutData(labelGd);

		xAxisCombo = new Combo(traceCompo, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (Axis axis : xyGraph.getXAxisList())
			xAxisCombo.add(axis.getTitle());
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		xAxisCombo.setLayoutData(gd);

		final Label yAxisLabel = new Label(traceCompo, 0);
		yAxisLabel.setText("Y-Axis: ");
		labelGd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		yAxisLabel.setLayoutData(labelGd);

		yAxisCombo = new Combo(traceCompo, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (Axis axis : xyGraph.getYAxisList())
			yAxisCombo.add(axis.getTitle());
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		yAxisCombo.setLayoutData(gd);

		final Label traceColorLabel = new Label(traceCompo, 0);
		traceColorLabel.setText("Trace Color: ");
		labelGd = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		traceColorLabel.setLayoutData(labelGd);

		traceColorSelector = new ColorSelector(traceCompo);
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1);
		traceColorSelector.getButton().setLayoutData(gd);

		final Label traceTypeLabel = new Label(traceCompo, 0);
		traceTypeLabel.setText("Trace Type: ");
		labelGd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		traceTypeLabel.setLayoutData(labelGd);

		traceTypeCombo = new Combo(traceCompo, SWT.DROP_DOWN | SWT.READ_ONLY);
		traceTypeCombo.setItems(TraceType.stringValues());
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		traceTypeCombo.setLayoutData(gd);

		traceTypeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateBaseLineComboEnable();
			}
		});

		final Label lineWidthLabel = new Label(traceCompo, 0);
		lineWidthLabel.setText("Line Width (pixels): ");
		labelGd = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		lineWidthLabel.setLayoutData(labelGd);

		lineWidthSpinner = new Spinner(traceCompo, SWT.BORDER);
		lineWidthSpinner.setMaximum(100);
		lineWidthSpinner.setMinimum(0);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		lineWidthSpinner.setLayoutData(gd);

		final Label pointStyleLabel = new Label(traceCompo, 0);
		pointStyleLabel.setText("Point Style: ");
		labelGd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		pointStyleLabel.setLayoutData(labelGd);

		pointStyleCombo = new Combo(traceCompo, SWT.DROP_DOWN | SWT.READ_ONLY);
		pointStyleCombo.setItems(PointStyle.stringValues());
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		pointStyleCombo.setLayoutData(gd);

		final Label pointSizeLabel = new Label(traceCompo, 0);
		pointSizeLabel.setText("Point Size (pixels): ");
		labelGd = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		pointSizeLabel.setLayoutData(labelGd);

		pointSizeSpinner = new Spinner(traceCompo, SWT.BORDER);
		pointSizeSpinner.setMaximum(100);
		pointSizeSpinner.setMinimum(0);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		pointSizeSpinner.setLayoutData(gd);

		final Label baseLineLabel = new Label(traceCompo, 0);
		baseLineLabel.setText("Base Line: ");
		labelGd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		baseLineLabel.setLayoutData(labelGd);
		baseLineLabel.setToolTipText("The baseline for BAR or AREA trace type");

		baseLineCombo = new Combo(traceCompo, SWT.DROP_DOWN |SWT.READ_ONLY);
		baseLineCombo.setItems(BaseLine.stringValues());
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		baseLineCombo.setLayoutData(gd);

		final Label alphaLabel = new Label(traceCompo, 0);
		alphaLabel.setText("Area Alpha: ");
		labelGd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		alphaLabel.setLayoutData(labelGd);

		areaAlphaSpinner = new Spinner(traceCompo, SWT.BORDER);
		areaAlphaSpinner.setMaximum(255);
		areaAlphaSpinner.setMinimum(0);
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 2, 1);
		areaAlphaSpinner.setLayoutData(gd);
		areaAlphaSpinner.setToolTipText("0 for transparent, 255 for opaque");

		antiAliasing = new Button(traceCompo, SWT.CHECK);
		antiAliasing.setText("Anti Aliasing Enabled");
		antiAliasing.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 3, 1));

		// error bar settings...
		errorBarEnabledButton = new Button(errorBarGroup, SWT.CHECK);
		errorBarEnabledButton.setText("Error Bar Enabled");
		errorBarEnabledButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 2, 1));
		errorBarEnabledButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean enabled = errorBarEnabledButton.getSelection();
				xErrorBarTypeCombo.setEnabled(enabled);
				yErrorBarTypeCombo.setEnabled(enabled);
				errorBarColorSelector.setEnabled(enabled);
				errorBarCapWidthSpinner.setEnabled(enabled);
				drawYErrorInAreaButton.setEnabled(enabled);
			}
		});

		final Label xErrorBarTypeLabel = new Label(errorBarGroup, 0);
		xErrorBarTypeLabel.setText("X Error Bar Type: ");
		labelGd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		xErrorBarTypeLabel.setLayoutData(labelGd);

		xErrorBarTypeCombo = new Combo(errorBarGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		xErrorBarTypeCombo.setItems(ErrorBarType.stringValues());
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		xErrorBarTypeCombo.setLayoutData(gd);

		final Label yErrorBarTypeLabel = new Label(errorBarGroup, 0);
		yErrorBarTypeLabel.setText("Y Error Bar Type: ");
		labelGd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		yErrorBarTypeLabel.setLayoutData(labelGd);

		yErrorBarTypeCombo = new Combo(errorBarGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		yErrorBarTypeCombo.setItems(ErrorBarType.stringValues());
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		yErrorBarTypeCombo.setLayoutData(gd);

		final Label errorBarColorLabel = new Label(errorBarGroup, 0);
		errorBarColorLabel.setText("Error Bar Color: ");
		labelGd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		errorBarColorLabel.setLayoutData(labelGd);

		errorBarColorSelector = new ColorSelector(errorBarGroup);
		gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1);
		errorBarColorSelector.getButton().setLayoutData(gd);

		final Label errorBarCapWidthLabel = new Label(errorBarGroup, 0);
		errorBarCapWidthLabel.setText("Error Bar Cap Width \n(pixels): ");
		labelGd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		errorBarCapWidthLabel.setLayoutData(labelGd);

		errorBarCapWidthSpinner = new Spinner(errorBarGroup, SWT.BORDER);
		errorBarCapWidthSpinner.setMaximum(100);
		errorBarCapWidthSpinner.setMinimum(0);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		errorBarCapWidthSpinner.setLayoutData(gd);

		drawYErrorInAreaButton = new Button(errorBarGroup, SWT.CHECK);
		drawYErrorInAreaButton.setText("Draw Y Error In Area");
		drawYErrorInAreaButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 2, 1));

		// add potential custom button if necessary by overriding this method to
		// the left composite
		addCustomButton(traceCompo);

		initialize();
	}

	/**
	 * Override this method if a custom set of buttons need to be added to the
	 * trace page.
	 *
	 * @param composite
	 */
	public void addCustomButton(Composite composite) {
		// do nothing
	}

	@Override
	public Composite getComposite() {
		return composite;
	}

	@Override
	public void applyChanges() {
		trace.setName(nameText.getText());
		if (xAxisCombo.getSelectionIndex() >= 0) {
			trace.setXAxis(xyGraph.getXAxisList().get(xAxisCombo.getSelectionIndex()));
		}
		if (yAxisCombo.getSelectionIndex() >= 0) {
			trace.setYAxis(xyGraph.getYAxisList().get(yAxisCombo.getSelectionIndex()));
		}
		trace.setTraceColor(XYGraphMediaFactory.getInstance().getColor(traceColorSelector.getColorValue()));
		trace.setTraceType(TraceType.values()[traceTypeCombo.getSelectionIndex()]);
		trace.setLineWidth(lineWidthSpinner.getSelection());
		trace.setPointStyle(PointStyle.values()[pointStyleCombo.getSelectionIndex()]);
		trace.setPointSize(pointSizeSpinner.getSelection());
		trace.setBaseLine(BaseLine.values()[baseLineCombo.getSelectionIndex()]);
		trace.setAreaAlpha(areaAlphaSpinner.getSelection());
		trace.setAntiAliasing(antiAliasing.getSelection());
		trace.setErrorBarEnabled(errorBarEnabledButton.getSelection());
		trace.setXErrorBarType(ErrorBarType.values()[xErrorBarTypeCombo.getSelectionIndex()]);
		trace.setYErrorBarType(ErrorBarType.values()[yErrorBarTypeCombo.getSelectionIndex()]);
		trace.setErrorBarColor(XYGraphMediaFactory.getInstance().getColor(errorBarColorSelector.getColorValue()));
		trace.setErrorBarCapWidth(errorBarCapWidthSpinner.getSelection());
		trace.setDrawYErrorInArea(drawYErrorInAreaButton.getSelection());

		boolean vis = visible.getSelection();
		if (vis != trace.isVisible())
			trace.setVisible(vis);

	}

	private void initialize() {
		nameText.setText(trace.getName());
		xAxisCombo.select(xyGraph.getXAxisList().indexOf(trace.getXAxis()));
		yAxisCombo.select(xyGraph.getYAxisList().indexOf(trace.getYAxis()));
		traceColorSelector.setColorValue(trace.getTraceColor().getRGB());
		traceTypeCombo.select(Arrays.asList(TraceType.values()).indexOf(trace.getTraceType()));
		lineWidthSpinner.setSelection(trace.getLineWidth());
		pointStyleCombo.select(Arrays.asList(PointStyle.values()).indexOf(trace.getPointStyle()));
		pointSizeSpinner.setSelection(trace.getPointSize());
		baseLineCombo.select(Arrays.asList(BaseLine.values()).indexOf(trace.getBaseLine()));
		areaAlphaSpinner.setSelection(trace.getAreaAlpha());
		antiAliasing.setSelection(trace.isAntiAliasing());
		errorBarEnabledButton.setSelection(trace.isErrorBarEnabled());
		xErrorBarTypeCombo.select(Arrays.asList(ErrorBarType.values()).indexOf(trace.getXErrorBarType()));
		yErrorBarTypeCombo.select(Arrays.asList(ErrorBarType.values()).indexOf(trace.getYErrorBarType()));
		errorBarColorSelector.setColorValue(trace.getErrorBarColor().getRGB());
		errorBarCapWidthSpinner.setSelection(trace.getErrorBarCapWidth());
		drawYErrorInAreaButton.setSelection(trace.isDrawYErrorInArea());

		boolean enabled = errorBarEnabledButton.getSelection();
		xErrorBarTypeCombo.setEnabled(enabled);
		yErrorBarTypeCombo.setEnabled(enabled);
		errorBarColorSelector.setEnabled(enabled);
		errorBarCapWidthSpinner.setEnabled(enabled);
		drawYErrorInAreaButton.setEnabled(enabled);

		visible.setSelection(trace.isVisible());

		updateBaseLineComboEnable();
	}

	private void updateBaseLineComboEnable() {
		baseLineCombo.setEnabled(traceTypeCombo.getSelectionIndex() == TraceType.BAR.ordinal()
				|| traceTypeCombo.getSelectionIndex() == TraceType.AREA.ordinal()
				|| traceTypeCombo.getSelectionIndex() == TraceType.LINE_AREA.ordinal());
	}

	/**
	 * @return the trace
	 */
	public Trace getTrace() {
		return trace;
	}

}
