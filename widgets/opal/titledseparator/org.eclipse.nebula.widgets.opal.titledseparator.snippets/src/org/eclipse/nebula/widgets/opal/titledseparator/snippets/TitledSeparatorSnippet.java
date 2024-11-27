/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Laurent CARON (laurent.caron at gmail dot com) - initial API
 * and implementation
 *******************************************************************************/
package org.eclipse.nebula.widgets.opal.titledseparator.snippets;

import java.io.ByteArrayInputStream;
import java.util.Base64;

import org.eclipse.nebula.widgets.opal.commons.SWTGraphicUtil;
import org.eclipse.nebula.widgets.opal.titledseparator.TitledSeparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This snippet demonstrates the TitledSeparator widget
 *
 */
public class TitledSeparatorSnippet {

	private static final String IMAGE = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAFo9M"
		+ "/3AAAAB3RJTUUH2woHCQsOaNepFwAAAAlwSFlzAAAewgAAHsIBbtB1"
		+ "PgAAAARnQU1BAACxjwv8YQUAAAI+SURBVHjalVF7SNNRFP5+mzhkGbX"
		+ "IZSqyBg2igtQFTsyBKURJjB7EgkhSe+wP6QG9/7WCaaRgFEWjKKEsMh"
		+ "2Z6VZiVKQLVKYVuVyauWCQSkG636l7ZXev+qMP7j2H8/ju/c6RiAgMC"
		+ "nY1nygjSUS0pZ3U+nKIwCLnCxdSuUlLzOcll5xfqedTCLKvG6InDM42"
		+ "7aylPaWZ8xlWce/4Fnr9PjjPMdVmp4rZQzx51FUY4fC03yfXdRv3v/"
		+ "2QccEZkJifFCYf839G1dlGxIMz5FnraF91BbxQIjDB/4H8LwT9utHIE"
		+ "xcfDJLjrpf7qiVquA8UQL1mkZSgIx7iD/Ydy0i9OBlTw9+x8rADFotF"
		+ "EsIZDKadMOZsRjBVhVb7wUSGomIzt6dNJXje9SxWBRu7PnsFvPXbedD"
		+ "mqcZkx0b+hMRGODFF6JxTCYkZ8gy6m2+h984RSYGGEtxseiKSYbh3U+QJ"
		+ "hm3HmmjkFSBnKrGrWIeTlUYppiAa0XsJTf6EUpsCvfWykJ6wimiwea2dm"
		+ "Ub/glTRzCwbz5xSi9pHAzEkSfGMuuzlKDp1A/l+F/zjclTcjMeOyoTfKuI"
		+ "DPsMZtNWUcz8rSycOa2a5f0po6QvS/pohuBry4CMF2sd+iSI2YXP6LHI1I"
		+ "ZTZ+nD1XA625moiQ3xbZySDvgCNgQ14s3QT0tLlmLXI40Fuh/8otib7UZ1"
		+ "2G4NdPVhf3yvxqlV7r/HAaL8bHx56eDMjETozNPwEWgaQMvIU7z6+wOqqK3"
		+ "/fwv/iN7ZiFLq3HVKbAAAAAElFTkSuQmCC";

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, false));
		
		byte[] imageBytes = Base64.getDecoder().decode(IMAGE);
        final Image icon = new Image(display, new ByteArrayInputStream(imageBytes));
		final Font font = new Font(display, "Courier New", 18, SWT.BOLD | SWT.ITALIC);

		// Default separator (no image, title aligned on the left)
		final TitledSeparator sep1 = new TitledSeparator(shell, SWT.NONE);
		sep1.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		sep1.setText("Customer Info");

		// Separator with image
		final TitledSeparator sep2 = new TitledSeparator(shell, SWT.NONE);
		sep2.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		sep2.setText("Customer Info");
		sep2.setImage(icon);

		// Separator aligned on the right
		final TitledSeparator sep3 = new TitledSeparator(shell, SWT.NONE);
		sep3.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		sep3.setText("Customer Info");
		sep3.setAlignment(SWT.RIGHT);

		// Custom font & text color
		final TitledSeparator sep4 = new TitledSeparator(shell, SWT.NONE);
		sep4.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		sep4.setText("Customized Color and Font");
		sep4.setAlignment(SWT.CENTER);

		sep4.setForeground(display.getSystemColor(SWT.COLOR_DARK_RED));
		sep4.setFont(font);

		shell.setSize(640, 350);
		shell.pack();
		shell.open();
		SWTGraphicUtil.centerShell(shell);

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		icon.dispose();
		font.dispose();

		display.dispose();
	}

}
