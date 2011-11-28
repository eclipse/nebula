package org.eclipse.nebula.widgets.treemapper.examples;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;


public class TestHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		return new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				new TestWizard()).open();
	}

}
