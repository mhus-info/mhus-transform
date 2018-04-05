package de.mhus.osgi.transform.core;

import java.io.File;

import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.IReadProperties;
import de.mhus.lib.core.MProperties;
import de.mhus.osgi.transform.api.TransformContext;

public class ContextImpl implements TransformContext {

	private File root;
	private MProperties config;
	private MProperties param;

	public ContextImpl(File rootDir, MProperties config, MProperties param) {
		if (rootDir != null) this.root = rootDir;
		this.config = config;
		this.param = param;
	}

	@Override
	public IReadProperties getProcessorConfig() {
		return config;
	}

	@Override
	public File getTemplateRoot() {
		return root;
	}

	@Override
	public IProperties getParameters() {
		return param;
	}

}
