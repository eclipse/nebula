package org.eclipse.nebula.cwt.svg;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.nebula.cwt.v.VCanvas;
import org.eclipse.nebula.cwt.v.VGridLayout;
import org.eclipse.nebula.cwt.v.VLabel;
import org.eclipse.nebula.cwt.v.VPanel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class VControlTest {
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout(1, false));
		shell.setText("Test");
		
		SvgDocument SVG_BACK = null;
		try (final InputStream in = VControlTest.class.getResourceAsStream("caretLeft.svg")) {
		     SVG_BACK = SvgDocument.load(in);
		}
		catch (IOException e) {
		    e.printStackTrace();
		}
		if(SVG_BACK == null) {
		    return;
		}

		VCanvas vCanvas = new VCanvas(shell,SWT.NONE);
		        
		VPanel vPanel = vCanvas.getPanel();

		VGridLayout layout = new VGridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		vPanel.setLayout(layout);

		VLabel vLabel = new VLabel(vPanel, SWT.NONE);
		vLabel.setImage(SVG_BACK);

		Point size = vCanvas.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		System.out.println(size);
		
		// display the shell...
		shell.open();
		shell.pack();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}
