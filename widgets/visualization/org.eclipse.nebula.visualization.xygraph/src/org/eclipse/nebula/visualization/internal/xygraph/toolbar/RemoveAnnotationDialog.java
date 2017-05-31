/*******************************************************************************
 * Copyright (c) 2010, 2017 Oak Ridge National Laboratory and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.nebula.visualization.internal.xygraph.toolbar;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.nebula.visualization.xygraph.figures.Annotation;
import org.eclipse.nebula.visualization.xygraph.figures.IXYGraph;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * The dialog for removing annotation.
 * 
 * @author Xihui Chen
 * @author Kay Kasemir layout tweaks
 */
public class RemoveAnnotationDialog extends Dialog {
	private IXYGraph xyGraph;
	private Combo annotationsCombo;
	private Annotation removedAnnotation;

	/**
	 * Construct a remove annotation dialog to remove all or some of the
	 * annotation of a graph
	 *
	 * @param parentShell
	 *            shell of the parent
	 * @param xyGraph
	 *            the graph where the annotation is
	 */
	public RemoveAnnotationDialog(Shell parentShell, IXYGraph xyGraph) {
		super(parentShell);
		this.xyGraph = xyGraph;
		// Allow resize
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Remove Annotation");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite parent_composite = (Composite) super.createDialogArea(parent);
		final Composite composite = new Composite(parent_composite, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(1, false));
		final Label removeLabel = new Label(composite, SWT.None);
		removeLabel.setLayoutData(new GridData());
		if (xyGraph.getPlotArea().getAnnotationList().size() > 0) {
			removeLabel.setText("Select the annotation to be removed: ");
			annotationsCombo = new Combo(composite, SWT.DROP_DOWN);
			annotationsCombo.setLayoutData(new GridData(SWT.FILL, 0, true, false));
			for (Annotation annotation : xyGraph.getPlotArea().getAnnotationList())
				annotationsCombo.add(annotation.getName());
			annotationsCombo.select(0);
		} else {
			removeLabel.setText("There is no annotation on the graph.");
		}

		return parent_composite;
	}

	@Override
	protected void okPressed() {
		if (annotationsCombo != null)
			removedAnnotation = xyGraph.getPlotArea().getAnnotationList().get(annotationsCombo.getSelectionIndex());
		super.okPressed();
	}

	/**
	 * @return the annotation to be removed.
	 */
	public Annotation getAnnotation() {
		return removedAnnotation;
	}
}
