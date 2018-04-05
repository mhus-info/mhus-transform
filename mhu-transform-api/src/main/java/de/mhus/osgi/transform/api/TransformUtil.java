package de.mhus.osgi.transform.api;

import java.io.File;

import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MProperties;

public class TransformUtil {

	public static void transform(File from, File to, MProperties param) throws Exception {
		transform(from, to, null, null, param, null);
	}
	
	public static void transform(File from, File to, File rootDir, MProperties config, MProperties param, String processorName) throws Exception {
		TransformApi api = MApi.lookup(TransformApi.class);

		TransformContext context = api.createContext(rootDir, config, param);
		ResourceProcessor processor = null;
		if (processorName != null)
			processor = api.findProcessor(processorName);
		else
			processor = api.findResourceProcessor(from.getName());
		api.doProcess(processor, from, to, context);

	}
	
}
