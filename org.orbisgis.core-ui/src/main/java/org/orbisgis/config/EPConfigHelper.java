package org.orbisgis.config;

import java.util.ArrayList;

import org.orbisgis.pluginManager.ExtensionPointManager;
import org.orbisgis.pluginManager.ItemAttributes;

public class EPConfigHelper {

	/**
	 * Gets the installed configurations in the plugin.xml file
	 * 
	 * @return the installed configurations
	 */
	static ArrayList<ConfigDecorator> getConfigurations() {
		ExtensionPointManager<IConfiguration> epm = new ExtensionPointManager<IConfiguration>(
				"org.orbisgis.Configuration");
		ArrayList<ItemAttributes<IConfiguration>> configs;
		configs = epm.getItemAttributes("/extension/configuration");
		ArrayList<ConfigDecorator> ret = new ArrayList<ConfigDecorator>();
		for (ItemAttributes<IConfiguration> itemAttributes : configs) {
			String className = itemAttributes.getAttribute("class");
			String id = itemAttributes.getAttribute("id");
			String text = itemAttributes.getAttribute("text");
			ret.add(new ConfigDecorator(className, id, text));
		}

		return ret;
	}
}
