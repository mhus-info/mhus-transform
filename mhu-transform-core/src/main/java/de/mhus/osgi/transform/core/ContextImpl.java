/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
