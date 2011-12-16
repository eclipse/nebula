package org.eclipse.nebula.examples.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

	private static Activator bundle;

	public Activator() {

	}

	public void start(BundleContext context) throws Exception {

		bundle = this;

		super.start(context);
	}

	public static Activator getBundleObject() {
		return bundle;
	}

}
