package org.jxse.test;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import Examples.A_JXTA_Connection_And_Local_Configuration._100_Starting_And_Stopping_JXTA_Example;

public class Activator implements BundleActivator {

	private static BundleContext context;
	
	private _100_Starting_And_Stopping_JXTA_Example example;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		example = new _100_Starting_And_Stopping_JXTA_Example();
		try{
			example.main(null);
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}