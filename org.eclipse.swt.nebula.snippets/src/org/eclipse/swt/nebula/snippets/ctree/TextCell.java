package org.eclipse.swt.nebula.snippets.ctree;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.nebula.widgets.ctree.CTreeCell;
import org.eclipse.swt.nebula.widgets.ctree.CTreeItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


public class TextCell extends CTreeCell {
	private Label mText;

	public TextCell(CTreeItem item, int style) {
		super(item, style| SWT.TITLE);
	}
	
	protected void createControl(final Composite contents, int style) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns=1;
		gridLayout.marginHeight=0;
		contents.setLayout(gridLayout);
		mText = new Label(contents, SWT.BORDER);
		mText.setText("abc"); //<-- display data instead of abc
		mText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	}
}
