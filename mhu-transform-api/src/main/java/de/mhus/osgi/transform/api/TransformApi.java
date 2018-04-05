package de.mhus.osgi.transform.api;

import java.io.File;

import de.mhus.lib.core.MProperties;
import de.mhus.lib.errors.NotFoundException;

public interface TransformApi {

	ResourceProcessor findResourceProcessor(String fileName) throws NotFoundException;

	TransformContext createContext(File rootDir, MProperties config, MProperties param);

	void doProcess(ResourceProcessor processor, File from, File to, TransformContext context) throws Exception;

	/**
	 * Search the processor by the processor name
	 * 
	 * @param processorName
	 * @return The processor
	 * @throws NotFoundException 
	 */
	ResourceProcessor findProcessor(String processorName) throws NotFoundException;
	
}
