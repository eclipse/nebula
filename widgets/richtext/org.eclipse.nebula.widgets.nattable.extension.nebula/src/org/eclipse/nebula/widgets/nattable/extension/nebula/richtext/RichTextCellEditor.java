/*****************************************************************************
 * Copyright (c) 2015 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *		Dirk Fauth <dirk.fauth@googlemail.com> - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.nebula.widgets.nattable.extension.nebula.richtext;

import org.eclipse.nebula.widgets.nattable.edit.editor.AbstractCellEditor;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;
import org.eclipse.nebula.widgets.richtext.RichTextEditor;
import org.eclipse.nebula.widgets.richtext.toolbar.ToolbarConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class RichTextCellEditor extends AbstractCellEditor {

	/**
	 * The rich text editor control, initially <code>null</code>.
	 */
	protected RichTextEditor editor = null;

	/**
	 * The {@link ToolbarConfiguration} that should be used for creating the inline rich text editor
	 * control. If <code>null</code> the default {@link ToolbarConfiguration} will be used.
	 */
	protected ToolbarConfiguration toolbarConfiguration;

	/**
	 * The style bits that are used to create the rich text editor control.
	 */
	protected int style;

	public RichTextCellEditor() {
		this(null, SWT.RESIZE);
	}

	public RichTextCellEditor(ToolbarConfiguration toolbarConfiguration) {
		this(toolbarConfiguration, SWT.RESIZE);
	}

	public RichTextCellEditor(int style) {
		this(null, style);
	}

	public RichTextCellEditor(ToolbarConfiguration toolbarConfiguration, int style) {
		if (toolbarConfiguration == null) {
			toolbarConfiguration = new ToolbarConfiguration();
			toolbarConfiguration.toolbarCollapsible = true;
			toolbarConfiguration.toolbarInitialExpanded = true;
		}
		this.toolbarConfiguration = toolbarConfiguration;
		this.style = style | SWT.EMBEDDED;
	}

	@Override
	public Object getEditorValue() {
		return this.editor.getText();
	}

	@Override
	public void setEditorValue(Object value) {
		this.editor.setText(value != null ? (String) value : "");
	}

	@Override
	public Control getEditorControl() {
		return this.editor;
	}

	@Override
	protected Control activateCell(Composite parent, Object originalCanonicalValue) {
		this.editor = createEditorControl(parent);

		setCanonicalValue(originalCanonicalValue);

		this.editor.forceFocus();

		return this.editor;
	}

	@Override
	public RichTextEditor createEditorControl(Composite parent) {
		this.editor = new RichTextEditor(parent, this.toolbarConfiguration, this.style) {
			@Override
			protected int getMinimumHeight() {
				return getMinimumDimension().y;
			}

			@Override
			protected int getMinimumWidth() {
				return getMinimumDimension().x;
			}
		};

		this.editor.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ESC) {
					close();
				}

				// apply the value on key combination CTRL + RETURN
				// because RETURN will add a new line to the editor
				if (e.keyCode == SWT.CR && e.stateMask == SWT.MOD1) {
					commit(MoveDirectionEnum.NONE);
				}
			}
		});

		return editor;
	}

	/**
	 * @return The minimum dimension used for the rich text editor control.
	 */
	protected Point getMinimumDimension() {
		return new Point(370, 200);
	}
}
