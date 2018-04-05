package de.mhus.osgi.transform.api;

import java.io.File;

public interface ResourceProcessor {
	
	void doProcess(File from, File to, TransformContext context) throws Exception;
	
}
