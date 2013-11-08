package org.eclipse.nebula.cwt.v;

import org.eclipse.nebula.cwt.test.AbstractVTestCase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

public class PaintTest01 extends AbstractVTestCase {

	private VPanel panel;
	private VButton[] buttons;
	
	public void test1() {
		syncExec(new Runnable() {
			public void run() {
				Shell shell = getShell();
				shell.setLayout(new FillLayout());

				VCanvas comp = new VCanvas(shell, SWT.NONE);
				panel = comp.getPanel();
				panel.setLayout(new VGridLayout(2, true));
				
				VButtonPainter painter = new VButtonPainter() {
					@Override
					public void paintContent(VControl control, Event e) {
						VButton b = (VButton) control;
						b.setData("paintCount", (b.getData("paintCount", Integer.class)+1));
						System.out.println(b.getText() + "'s paint count: " + b.getData("paintCount"));
						super.paintContent(control, e);
					}
				};
				
				buttons = new VButton[4];
				for(int i = 0; i < buttons.length; i++) {
					buttons[i] = new VButton(panel, SWT.PUSH);
					buttons[i].setPainter(painter);
					buttons[i].setText("b"+i);
					buttons[i].setData("paintCount", 0);
				}
			}
		});
		layoutShell();

	}

}
