/*******************************************************************************
* Copyright (c) 2011 PetalsLink
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Mickael Istria, PetalsLink - initial API and implementation
*******************************************************************************/
package org.eclipse.nebula.widgets.treemapper.tests;

import java.util.Arrays;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.nebula.widgets.treemapper.TreeMapper;
import org.eclipse.nebula.widgets.treemapper.TreeMapperUIConfigProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.junit.Test;

/**
 * @author Mickael Istria (PetalsLink)
 *
 */
public class ProgrammaticTest {

	/**
	 * @param treeContent
	 * @param mappings
	 * @return
	 */
	private Dialog openMapperDialog(final String[] treeContent,
			final String[] mappings) {
		Dialog dialog = new Dialog(Display.getDefault().getActiveShell()) {
			@Override
			public Composite createDialogArea(Composite parent) {
				Composite res = (Composite)super.createDialogArea(parent);
				TreeMapper<String, String, String> mapper = new TreeMapper<String, String, String>(
						parent,
						new ObjectSemanticSupport(),
						new TreeMapperUIConfigProvider(ColorConstants.blue, 2, ColorConstants.darkBlue, 4));
				mapper.setContentProviders(new ArrayTreeContentProvider(), new ArrayTreeContentProvider());
				mapper.setInput(treeContent, treeContent, Arrays.asList(mappings));
				return res;
			}
		};
		dialog.setBlockOnOpen(false);
		dialog.open();
		return dialog;
	}
	
	@Test
	public void testBug365445_ok() {
		final String a = "a", b = "b", c = "c";
		final String[] treeContent = new String[] { a, b, c };
		final String[] mappings = new String[] { a, b, c };
		Dialog dialog = openMapperDialog(treeContent, mappings);
		dialog.close();
	}

	@Test
	public void testBug365445_ko() {
		final String a = "a", b = "b", c = "c";
		final String[] treeContent = new String[] { a, b, c };
		final String[] mappings = new String[] { a, b, c, "d" };
		Dialog dialog = openMapperDialog(treeContent, mappings);
		dialog.close();
	}

}
