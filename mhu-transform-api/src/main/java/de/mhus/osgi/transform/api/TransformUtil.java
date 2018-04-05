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
