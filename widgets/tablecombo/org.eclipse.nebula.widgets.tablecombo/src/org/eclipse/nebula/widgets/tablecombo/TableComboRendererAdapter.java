/*******************************************************************************
 * Copyright (c) 2011-2021 Nebula Team.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.tablecombo;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * TableComboRenderer adapter
 */
public class TableComboRendererAdapter implements TableComboRenderer {

	/** 
	 * @see org.eclipse.nebula.widgets.tablecombo.TableComboRenderer#getLabel(int)
	 */
	@Override
	public String getLabel(int selectionIndex) {
		return null;
	}

	/** 
	 * @see org.eclipse.nebula.widgets.tablecombo.TableComboRenderer#getImage(int)
	 */
	@Override
	public Image getImage(int selectionIndex) {
		return null;
	}

	/** 
	 * @see org.eclipse.nebula.widgets.tablecombo.TableComboRenderer#getBackground(int)
	 */
	@Override
	public Color getBackground(int selectionIndex) {
		return null;
	}

	/** 
	 * @see org.eclipse.nebula.widgets.tablecombo.TableComboRenderer#getForeground(int)
	 */
	@Override
	public Color getForeground(int selectionIndex) {
		return null;
	}

	/** 
	 * @see org.eclipse.nebula.widgets.tablecombo.TableComboRenderer#getFont(int)
	 */
	@Override
	public Font getFont(int selectionIndex) {
		return null;
	}

}
