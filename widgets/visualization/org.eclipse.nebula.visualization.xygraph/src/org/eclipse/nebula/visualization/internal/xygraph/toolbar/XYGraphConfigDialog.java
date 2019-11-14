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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.nebula.visualization.internal.xygraph.undo.XYGraphConfigCommand;
import org.eclipse.nebula.visualization.xygraph.figures.Annotation;
import org.eclipse.nebula.visualization.xygraph.figures.Axis;
import org.eclipse.nebula.visualization.xygraph.figures.IXYGraph;
import org.eclipse.nebula.visualization.xygraph.figures.Trace;
import org.eclipse.nebula.visualization.xygraph.util.XYGraphMediaFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * The dialog for configuring XYGraph properties.
 * 
 * @author Xihui Chen
 * @author Baha El-Kassaby - Add ability to have custom config dialogs
 *
 */
public class XYGraphConfigDialog extends Dialog {

	/**
	 * Bug 514179: At the moment the implementation of the configuration means
	 * that each trace, axis or annotation requires its own config page
	 * instance, with its own set of controls. The result is if there are very
	 * many traces, axes or annotations, the UI crashes.
	 * 
	 * The result is we limit how many traces, axes or annotations can be edited
	 * manually in this UI and display a warning to the user referencing the
	 * bug.
	 */
	private static final int MAX_CONFIG_PAGE_COUNT = 50;

	private GraphConfigPage graphConfigPage;
	private List<AnnotationConfigPage> annotationConfigPageList;
	private List<AxisConfigPage> axisConfigPageList;
	private List<ITraceConfigPage> traceConfigPageList;
	private Combo traceCombo;
	private Combo axisCombo;
	private Combo annotationsCombo;
	private IXYGraph xyGraph;
	private XYGraphConfigCommand command;
	private boolean changed = false;

	/**
	 * Construct a XYGraph configuration dialog
	 *
	 * @param parentShell
	 *          shell of the parent
	 * @param xyGraph
	 *          the graph to be configured
	 */
	public XYGraphConfigDialog(Shell parentShell, IXYGraph xyGraph) {
		super(parentShell);
		this.xyGraph = xyGraph;
		graphConfigPage = new GraphConfigPage(this.xyGraph);
		annotationConfigPageList = new ArrayList<AnnotationConfigPage>();
		axisConfigPageList = new ArrayList<AxisConfigPage>();
		traceConfigPageList = new ArrayList<ITraceConfigPage>();
		command = new XYGraphConfigCommand(xyGraph);
		command.savePreviousStates();
		// Allow resize
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Configure Graph Settings");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		return createDialogArea(parent, true);
	}

	protected Control createDialogArea(Composite parent, boolean enableAxisRanges) {

		final Composite parent_composite = (Composite) super.createDialogArea(parent);
		parent_composite.setLayout(new FillLayout());
		final TabFolder tabFolder = new TabFolder(parent_composite, SWT.NONE);

		Composite graphTabComposite = new Composite(tabFolder, SWT.NONE);
		graphConfigPage.createPage(graphTabComposite);

		TabItem graphConfigTab = new TabItem(tabFolder, SWT.NONE);
		graphConfigTab.setText("Graph");
		graphConfigTab.setToolTipText("Configure General Graph Settings");
		graphConfigTab.setControl(graphTabComposite);

		// Axis Configure Page
		Composite axisTabComposite = new Composite(tabFolder, SWT.NONE);
		axisTabComposite.setLayout(new GridLayout(1, false));
		TabItem axisConfigTab = new TabItem(tabFolder, SWT.NONE);
		axisConfigTab.setText("Axes");
		axisConfigTab.setToolTipText("Configure Axes Settings");
		axisConfigTab.setControl(axisTabComposite);

		if (xyGraph.getAxisList().size() > MAX_CONFIG_PAGE_COUNT) {
			addMaxWarningMessage(axisTabComposite, "axes");
		}

		Group axisSelectGroup = new Group(axisTabComposite, SWT.NONE);
		axisSelectGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		axisSelectGroup.setText("Select Axis");
		axisSelectGroup.setLayout(new GridLayout(1, false));
		axisCombo = new Combo(axisSelectGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		axisCombo.setLayoutData(new GridData(SWT.FILL, 0, true, false));
		int count = 0;
		for (Axis axis : xyGraph.getAxisList()) {
			if (++count > MAX_CONFIG_PAGE_COUNT) {
				break;
			}
			axisCombo.add(axis.getTitle() + (axis.isHorizontal() ? "(X-Axis)" : "(Y-Axis)"));
		}
		axisCombo.select(0);

		final Composite axisConfigComposite = new Composite(axisTabComposite, SWT.NONE);
		axisConfigComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		final StackLayout axisStackLayout = new StackLayout();
		axisConfigComposite.setLayout(axisStackLayout);
		count = 0;
		for (Axis axis : xyGraph.getAxisList()) {
			if (++count > MAX_CONFIG_PAGE_COUNT) {
				break;
			}
			Group axisConfigGroup = new Group(axisConfigComposite, SWT.NONE);
			axisConfigGroup.setText("Change Settings");
			axisConfigGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			AxisConfigPage axisConfigPage = new AxisConfigPage(xyGraph, axis, enableAxisRanges);
			axisConfigPageList.add(axisConfigPage);
			axisConfigPage.createPage(axisConfigGroup);
		}
		axisStackLayout.topControl = axisConfigPageList.get(0).getComposite();
		axisCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				axisStackLayout.topControl = axisConfigPageList.get(axisCombo.getSelectionIndex()).getComposite();
				axisConfigComposite.layout(true, true);
			}
		});

		// Trace Configure Page
		if (xyGraph.getPlotArea().getTraceList().size() > 0) {
			Composite traceTabComposite = new Composite(tabFolder, SWT.NONE);
			traceTabComposite.setLayout(new GridLayout(1, false));
			TabItem traceConfigTab = new TabItem(tabFolder, SWT.NONE);
			traceConfigTab.setText("Traces");
			traceConfigTab.setToolTipText("Configure Traces Settings");
			traceConfigTab.setControl(traceTabComposite);

			if (xyGraph.getPlotArea().getTraceList().size() > MAX_CONFIG_PAGE_COUNT) {
				addMaxWarningMessage(traceTabComposite, "traces");
			}

			Group traceSelectGroup = new Group(traceTabComposite, SWT.NONE);
			traceSelectGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			traceSelectGroup.setText("Select Trace");
			traceSelectGroup.setLayout(new GridLayout(1, false));
			traceCombo = new Combo(traceSelectGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
			traceCombo.setLayoutData(new GridData(SWT.FILL, 0, true, false));
			count = 0;
			for (Trace trace : xyGraph.getPlotArea().getTraceList()) {
				if (++count > MAX_CONFIG_PAGE_COUNT) {
					break;
				}
				traceCombo.add(trace.getName());
			}
			traceCombo.select(0);

			final Composite traceConfigComposite = new Composite(traceTabComposite, SWT.NONE);
			traceConfigComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			final StackLayout traceStackLayout = new StackLayout();
			traceConfigComposite.setLayout(traceStackLayout);

			count = 0;
			for (Trace trace : xyGraph.getPlotArea().getTraceList()) {
				if (++count > MAX_CONFIG_PAGE_COUNT) {
					break;
				}
				Group traceConfigGroup = new Group(traceConfigComposite, SWT.NONE);
				traceConfigGroup.setText("Change Settings");
				traceConfigGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				ITraceConfigPage traceConfigPage = createTraceConfigPage(trace);
				traceConfigPageList.add(traceConfigPage);
				traceConfigPage.createPage(traceConfigGroup);
			}
			traceStackLayout.topControl = traceConfigPageList.get(0).getComposite();
			traceCombo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					traceStackLayout.topControl = traceConfigPageList.get(traceCombo.getSelectionIndex())
							.getComposite();
					traceConfigComposite.layout(true, true);
				}
			});
		}

		// Annotation Configure Page
		if (xyGraph.getPlotArea().getAnnotationList().size() > 0) {
			Composite annoTabComposite = new Composite(tabFolder, SWT.NONE);
			annoTabComposite.setLayout(new GridLayout(1, false));
			TabItem annoConfigTab = new TabItem(tabFolder, SWT.NONE);
			annoConfigTab.setText("Annotations");
			annoConfigTab.setToolTipText("Configure Annotation Settings");
			annoConfigTab.setControl(annoTabComposite);

			if (xyGraph.getPlotArea().getAnnotationList().size() > MAX_CONFIG_PAGE_COUNT) {
				addMaxWarningMessage(annoTabComposite, "annotations");
			}

			Group annoSelectGroup = new Group(annoTabComposite, SWT.NONE);
			annoSelectGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			annoSelectGroup.setText("Select Annotation");
			annoSelectGroup.setLayout(new GridLayout(1, false));
			annotationsCombo = new Combo(annoSelectGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
			annotationsCombo.setLayoutData(new GridData(SWT.FILL, 0, true, false));
			count = 0;
			for (Annotation annotation : xyGraph.getPlotArea().getAnnotationList()) {
				if (++count > MAX_CONFIG_PAGE_COUNT) {
					break;
				}
				annotationsCombo.add(annotation.getName());
			}
			annotationsCombo.select(0);

			final Composite annoConfigComposite = new Composite(annoTabComposite, SWT.NONE);
			annoConfigComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			final StackLayout stackLayout = new StackLayout();
			annoConfigComposite.setLayout(stackLayout);
			count = 0;
			for (Annotation annotation : xyGraph.getPlotArea().getAnnotationList()) {
				if (++count > MAX_CONFIG_PAGE_COUNT) {
					break;
				}
				Group annoConfigGroup = new Group(annoConfigComposite, SWT.NONE);
				annoConfigGroup.setText("Change Settings");
				annoConfigGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				AnnotationConfigPage annotationConfigPage = new AnnotationConfigPage(xyGraph, annotation);
				annotationConfigPageList.add(annotationConfigPage);
				annotationConfigPage.createPage(annoConfigGroup);
			}
			stackLayout.topControl = annotationConfigPageList.get(0).getComposite();
			annotationsCombo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					stackLayout.topControl = annotationConfigPageList.get(annotationsCombo.getSelectionIndex())
							.getComposite();
					annoConfigComposite.layout(true, true);
				}
			});
		}

		return parent_composite;
	}

	/**
	 * Override to create one own trace config page
	 * 
	 * @param trace
	 * @return traceConfigPage
	 */
	protected ITraceConfigPage createTraceConfigPage(Trace trace) {
		return new TraceConfigPage(xyGraph, trace);
	}

	private void addMaxWarningMessage(Composite composite, String type) {
		final CLabel warning = new CLabel(composite, SWT.NONE);
		warning.setText("There are too many " + type + " to edit");
		warning.setToolTipText("Currently only the first " + MAX_CONFIG_PAGE_COUNT + " " + type
				+ " can have their properties manually edited.\n"
				+ "This is due to a limitation with the current widget design on the configure form.\n"
				+ "Please see Bug 514179 for more details.");
		warning.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		warning.setImage(XYGraphMediaFactory.getInstance().getImage("images/warning.png"));
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		((GridLayout) parent.getLayout()).numColumns++;
		Button button = new Button(parent, SWT.PUSH);
		button.setText("Apply");
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		Point minSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		data.widthHint = Math.max(widthHint, minSize.x);
		button.setLayoutData(data);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				applyChanges();
			}
		});
		super.createButtonsForButtonBar(parent);
		Shell shell = parent.getShell();
		if (shell != null) {
			shell.setDefaultButton(button);
		}
	}

	@Override
	protected void okPressed() {
		applyChanges();
		command.saveAfterStates();
		xyGraph.getOperationsManager().addCommand(command);
		super.okPressed();
	}

	protected void applyChanges() {
		changed = true;
		graphConfigPage.applyChanges();
		for (AxisConfigPage axisConfigPage : axisConfigPageList)
			axisConfigPage.applyChanges();
		for (ITraceConfigPage traceConfigPage : traceConfigPageList)
			traceConfigPage.applyChanges();
		for (AnnotationConfigPage annotationConfigPage : annotationConfigPageList)
			annotationConfigPage.applyChanges();

		// Add L.PHILIPPE
		xyGraph.fireConfigChanged();
	}

	@Override
	protected void cancelPressed() {
		if (changed) {
			command.saveAfterStates();
			xyGraph.getOperationsManager().addCommand(command);
		}
		super.cancelPressed();
	}

	public Combo getTraceCombo() {
		return traceCombo;
	}

	public Combo getAxisCombo() {
		return axisCombo;
	}

	public Combo getAnnotationsCombo() {
		return annotationsCombo;
	}

	public List<AnnotationConfigPage> getAnnotationConfigPageList() {
		return annotationConfigPageList;
	}

	public List<AxisConfigPage> getAxisConfigPageList() {
		return axisConfigPageList;
	}

	public List<ITraceConfigPage> getTraceConfigPageList() {
		return traceConfigPageList;
	}

	/**
	 * @return a graph
	 */
	public IXYGraph getXYGraph() {
		return xyGraph;
	}

	public XYGraphConfigCommand getCommand() {
		return command;
	}

	public void setCommand(XYGraphConfigCommand command) {
		this.command = command;
	}

}
