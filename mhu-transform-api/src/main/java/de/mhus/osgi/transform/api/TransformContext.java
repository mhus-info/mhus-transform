package de.mhus.osgi.transform.api;

import java.io.File;

import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.IReadProperties;

public interface TransformContext {

	IReadProperties getProcessorConfig();

	File getTemplateRoot();

	IProperties getParameters();

}
