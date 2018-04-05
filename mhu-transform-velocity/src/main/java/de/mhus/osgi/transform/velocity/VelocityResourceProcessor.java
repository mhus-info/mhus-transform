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
package de.mhus.osgi.transform.velocity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.EscapeTool;

import aQute.bnd.annotation.component.Component;
import de.mhus.lib.core.IReadProperties;
import de.mhus.osgi.transform.api.ResourceProcessor;
import de.mhus.osgi.transform.api.TransformContext;

@Component(properties="extension=vm")
public class VelocityResourceProcessor implements ResourceProcessor {

	@Override
	public void doProcess(File from, File to, TransformContext context) throws Exception {
		
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty( VelocityEngine.RUNTIME_LOG, "mylog");
		IReadProperties config = context.getProcessorConfig();
		String velocityProperties = config.getString("velocity.properties","velocity.properties");
		File propFile = new File(context.getTemplateRoot(), velocityProperties );
		Properties props = new Properties();
		
		if (propFile.exists()) {
			FileInputStream is = new FileInputStream(propFile);
			props.load(is);
			is.close();
		}

		String path = from.getParentFile().getAbsolutePath();
		props.put(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, path  + "," + context.getTemplateRoot().getCanonicalPath());
		
		ve.init(props);
		Template t = ve.getTemplate(from.getName());
		
		VelocityContext vcontext = new VelocityContext();
		
		vcontext.put("vars", context.getParameters());
		vcontext.put("esc", new EscapeTool());
		vcontext.put("date", new DateTool());
		vcontext.put("path", path);
		vcontext.put("config", config);

			FileWriter writer = new FileWriter(to);
			t.merge(vcontext, writer);
			writer.close();
	}
	
}
