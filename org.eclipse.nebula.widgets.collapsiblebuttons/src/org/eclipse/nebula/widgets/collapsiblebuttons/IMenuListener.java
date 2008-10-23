package org.eclipse.nebula.widgets.collapsiblebuttons;

import org.eclipse.swt.widgets.Menu;

public interface IMenuListener {

	public void preMenuItemsCreated(Menu menu);
	public void postMenuItemsCreated(Menu menu);
}
