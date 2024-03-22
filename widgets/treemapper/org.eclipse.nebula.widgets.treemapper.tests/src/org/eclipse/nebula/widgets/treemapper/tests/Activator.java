package org.eclipse.nebula.widgets.treemapper.tests;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.nebula.widgets.treemapper.tests"; //$NON-NLS-1$

	// The shared instance
	private static BundleContext  plugin;

	public static LogService logger;

	/**
	 * The constructor
	 */
	public Activator() {
	}


	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
	       ServiceReference<?> logService = bundleContext.getServiceReference(LogService.class);
	        logger = (LogService)bundleContext.getService(logService);
	        logger.log(LogService.LOG_ERROR, "The bundle is starting...");
	        Activator.plugin = bundleContext;
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static BundleContext getDefault() {
		return plugin;
	}
}
