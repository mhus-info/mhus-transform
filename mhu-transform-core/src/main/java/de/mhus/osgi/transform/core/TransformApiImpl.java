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
	public TransformContext createContext(File projectRoot, File templateRoot, MProperties config, MProperties param) {
		return new ContextImpl(projectRoot, templateRoot, config, param);
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
