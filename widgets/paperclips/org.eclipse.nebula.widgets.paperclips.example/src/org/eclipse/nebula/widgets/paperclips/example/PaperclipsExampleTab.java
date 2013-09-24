/*
 * Copyright (c) 2006 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthew Hall - initial API and implementation
 *     Cedric Brun - adapt to embed in the ExampleTab
 */
package org.eclipse.nebula.widgets.paperclips.example;

import org.eclipse.nebula.examples.AbstractExampleTab;
import org.eclipse.nebula.paperclips.core.ImagePrint;
import org.eclipse.nebula.paperclips.core.LinePrint;
import org.eclipse.nebula.paperclips.core.PaperClips;
import org.eclipse.nebula.paperclips.core.Print;
import org.eclipse.nebula.paperclips.core.PrintJob;
import org.eclipse.nebula.paperclips.core.grid.DefaultGridLook;
import org.eclipse.nebula.paperclips.core.grid.GridPrint;
import org.eclipse.nebula.paperclips.core.text.TextPrint;
import org.eclipse.nebula.paperclips.widgets.PrintPreview;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class PaperclipsExampleTab extends AbstractExampleTab {

	private static final String BUNDLE = "org.eclipse.nebula.widgets.paperclips.example";


	public Control createControl(Composite parent) {
		Composite shell = new Composite(parent, SWT.None);
		
		final PrintJob job = new PrintJob(
				"GridPrintVerticalAlignmentExample.java", createPrint());
		shell.setLayout(new org.eclipse.swt.layout.GridLayout());

		Composite buttonPanel = new Composite(shell, SWT.NONE);
		buttonPanel
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		buttonPanel.setLayout(new RowLayout(SWT.HORIZONTAL));

		final PrintPreview preview = new PrintPreview(shell, SWT.BORDER);

		Button prev = new Button(buttonPanel, SWT.PUSH);
		prev.setText("<< Prev");
		prev.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				preview.setPageIndex(Math.max(preview.getPageIndex() - 1, 0));
			}
		});

		Button next = new Button(buttonPanel, SWT.PUSH);
		next.setText("Next >>");
		next.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				preview.setPageIndex(Math.min(preview.getPageIndex() + 1,
						preview.getPageCount() - 1));
			}
		});

		Button print = new Button(buttonPanel, SWT.PUSH);
		print.setText("Print");
		print.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				PaperClips.print(job, new PrinterData());
			}
		});

		preview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		preview.setFitHorizontal(true);
		preview.setFitVertical(true);
		preview.setPrintJob(job);
		return shell;
	}
	
	public static Print createPrint() {
		DefaultGridLook look = new DefaultGridLook(5, 5);
		look.setHeaderGap(5);
		GridPrint grid = new GridPrint("d:g, d, d:g, d, d:g, d, d:g", look);

		ImageData imageData = new ImageData(
				PaperclipsExampleTab.class
						.getResourceAsStream("logo.png"));
		ImagePrint image = new ImagePrint(imageData);
		image.setDPI(300, 300);

		Print verticalRule = new LinePrint(SWT.VERTICAL);

		grid.addHeader(SWT.CENTER, SWT.DEFAULT, new TextPrint("Column 1"));
		grid.addHeader(SWT.DEFAULT, SWT.FILL, verticalRule);
		grid.addHeader(SWT.CENTER, SWT.DEFAULT, new TextPrint("Column 2"));
		grid.addHeader(SWT.DEFAULT, SWT.FILL, verticalRule);
		grid.addHeader(SWT.CENTER, SWT.DEFAULT, new TextPrint("Column 3"));
		grid.addHeader(SWT.DEFAULT, SWT.FILL, verticalRule);
		grid.addHeader(SWT.CENTER, SWT.DEFAULT, new TextPrint("Column 4"));

		grid.addHeader(new LinePrint(SWT.HORIZONTAL), GridPrint.REMAINDER);

		grid.add(SWT.LEFT, SWT.CENTER, image);
		grid.add(SWT.DEFAULT, SWT.FILL, verticalRule);
		grid.add(SWT.DEFAULT, SWT.DEFAULT,
				new TextPrint("triple\nline\nleft\n"));
		grid.add(SWT.DEFAULT, SWT.FILL, verticalRule);
		grid.add(SWT.CENTER, SWT.CENTER, new TextPrint("double line\ncenter",
				SWT.CENTER));
		grid.add(SWT.DEFAULT, SWT.FILL, verticalRule);
		grid.add(SWT.RIGHT, SWT.BOTTOM, new TextPrint("single line right"));

		grid.add(new LinePrint(SWT.HORIZONTAL), GridPrint.REMAINDER);

		grid.add(SWT.CENTER, SWT.CENTER, new TextPrint(
				"several\nlines\nof\ntext\nhere", SWT.CENTER));
		grid.add(SWT.DEFAULT, SWT.FILL, verticalRule);
		grid.add(SWT.LEFT, SWT.FILL, verticalRule);
		grid.add(SWT.DEFAULT, SWT.FILL, verticalRule);
		grid.add(SWT.CENTER, SWT.FILL, verticalRule);
		grid.add(SWT.DEFAULT, SWT.FILL, verticalRule);
		grid.add(SWT.RIGHT, SWT.FILL, verticalRule);

		return grid;
	}


	public void createParameters(Composite parent) {
		// do nothing
		
	}


	public String[] createLinks() {
		String[] links = { "<a href=\"https://github.com/eclipse/nebula/tree/master/widgets/paperclips/org.eclipse.nebula.paperclips.snippets/src/org/eclipse/nebula/paperclips/snippets\" >Paperclips Snippets</a>" };
		return links;
	}
}