package de.mhus.osgi.transform.core;

import java.io.File;

import aQute.bnd.annotation.component.Component;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.errors.NotFoundException;
import de.mhus.lib.karaf.MOsgi;
import de.mhus.osgi.transform.api.ResourceProcessor;
import de.mhus.osgi.transform.api.TransformApi;
import de.mhus.osgi.transform.api.TransformContext;

@Component
public class TransformApiImpl extends MLog implements TransformApi {

	@Override
	public ResourceProcessor findResourceProcessor(String fileName) throws NotFoundException {
		
		String ext = MFile.getFileSuffix(fileName);
		ResourceProcessor processor = MOsgi.getService(ResourceProcessor.class, "(extension="+ext+")");
		
		return processor;
	}

	@Override
	public TransformContext createContext(File rootDir, MProperties config, MProperties param) {
		return new ContextImpl(rootDir, config, param);
	}

	@Override
	public void doProcess(ResourceProcessor processor, File from, File to, TransformContext context) throws Exception {
		
		processor.doProcess(from, to, context);
	}

	@Override
	public ResourceProcessor findProcessor(String processorName) throws NotFoundException {
		ResourceProcessor processor = MOsgi.getService(ResourceProcessor.class, "(processor="+processorName+")");
		
		return processor;
	}
	

}
