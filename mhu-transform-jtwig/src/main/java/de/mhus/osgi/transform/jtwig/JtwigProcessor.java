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
package de.mhus.osgi.transform.jtwig;

import java.io.File;
import java.io.FileOutputStream;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import aQute.bnd.annotation.component.Component;
import de.mhus.osgi.transform.api.ResourceProcessor;
import de.mhus.osgi.transform.api.TransformContext;

@Component(properties="extension=twig")
public class JtwigProcessor implements ResourceProcessor {

	@Override
	public void doProcess(File from, File to, TransformContext context) throws Exception {
		JtwigTemplate template = JtwigTemplate.fileTemplate(from);
        JtwigModel model = JtwigModel.newModel(context.getParameters());

        FileOutputStream os = new FileOutputStream(to);
        template.render(model, os);
        os.close();
        
	}

}
