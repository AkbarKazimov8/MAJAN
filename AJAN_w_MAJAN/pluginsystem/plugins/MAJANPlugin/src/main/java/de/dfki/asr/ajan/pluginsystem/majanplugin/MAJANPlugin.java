/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dfki.asr.ajan.pluginsystem.majanplugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.fortsoft.pf4j.Plugin;
import ro.fortsoft.pf4j.PluginWrapper;
import ro.fortsoft.pf4j.RuntimeMode;

/**
 *
 * @author Akbar
 */
public class MAJANPlugin extends Plugin{
    	
	private static final Logger LOG = LoggerFactory.getLogger(MAJANPlugin.class);

	public MAJANPlugin(PluginWrapper wrapper) {
		super(wrapper);
	}

	@Override
    public void start() {
        System.out.println("MappingPlugin.start()");
        if (RuntimeMode.DEVELOPMENT.equals(wrapper.getRuntimeMode())) {
			LOG.debug("MappingPlugin");
        }
    }

    @Override
    public void stop() {
        System.out.println("MappingPlugin.stop()");
    }
}
