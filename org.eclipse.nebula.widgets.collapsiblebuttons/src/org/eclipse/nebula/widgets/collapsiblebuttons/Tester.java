package org.eclipse.nebula.widgets.collapsiblebuttons;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Tester();
	}
	
	public Tester() {
		Display display = new Display();
		Shell shell = new Shell (display);
		shell.setText("Buttons Widget Tester");
		shell.setSize(400, 400);
		shell.setLayout(new FillLayout());

		Composite inner = new Composite(shell, SWT.NONE);
		GridLayout gl = new GridLayout(1, true);
		gl.marginBottom = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		inner.setLayout(gl);

		final CollapsibleButtons cp = new CollapsibleButtons(inner, SWT.NONE, IColorManager.SKIN_OFFICE_2007);
		cp.addButtonListener(new IButtonListener() {

			public void buttonClicked(CustomButton button, MouseEvent e) {
				button.dispose();
				
			}

			public void buttonEnter(CustomButton button, MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void buttonExit(CustomButton button, MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void buttonHover(CustomButton button, MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		cp.setLayoutData(new GridData(GridData.GRAB_VERTICAL | GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_END));
		cp.addButton("Synchronize", null, ImageCache.getImage("/selection_recycle_24.png"), ImageCache.getImage("/selection_recycle_16.gif"));

		cp.addMenuListener(new IMenuListener() {

			public void postMenuItemsCreated(Menu menu) {
				System.err.println("postCreate " + menu);
				
			}

			public void preMenuItemsCreated(Menu menu) {
				System.err.println("preCreate " + menu);
				
			}
			
		});
		
		shell.open();
		
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();

		
	}

}
