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
package de.mhus.osgi.transform.pdf;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import de.mhus.lib.core.MCast;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.MXml;
import de.mhus.osgi.transform.api.TransformConfig;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class FillModel extends MLog {

	private HashMap<String,Element> fields = new HashMap<>();
	private TransformConfig context;
	private GroovyShell shell;
	private String scriptTemplate;

	public FillModel(File file, TransformConfig context) throws ParserConfigurationException, SAXException, IOException {
		Element root = MXml.loadXml(file).getDocumentElement();
		for (Element fieldE : MXml.getLocalElementIterator(root, "field")) {
			fields.put(fieldE.getAttribute("name"), fieldE);
		}
		this.context = context;
		
		for (Element scriptE : MXml.getLocalElementIterator(root, "script")) {
			String script = MXml.getValue(scriptE, true);
			try {
				getGroovyShell();
				shell.evaluate(script);
			} catch (Throwable t) {
				log().e(script, t);
			}
		}
		scriptTemplate = "";
		for (Element scriptE : MXml.getLocalElementIterator(root, "scripttemplate")) {
			String script = MXml.getValue(scriptE, true);
			scriptTemplate = scriptTemplate + script + "\n";
		}
		
		log().t("scriptTemplate",scriptTemplate);
		if (shell != null)
			log().d("variable", shell.getContext().getProperty("vars"));
		else
			log().d("variable", context.getParameters() );
			
	}

	public String getValue(String name, PDField field) throws Exception {
		Element def = fields.get(name);
		if (def == null) {
			log().d("field not found",name);
			return null;
		}
		
		String value = def.getAttribute("default");
		
		String script = MXml.getValue(def, "value", "");
		if (MString.isSetTrim(script)) {
			getGroovyShell();
			script = scriptTemplate + script;
			log().t("script",name,script);
			value = MCast.toString( shell.evaluate(script) );
		} else
		if (def.hasAttribute("parameter")) {
			String pName = def.getAttribute("parameter");
			log().t("parameter",name,pName);
			value = context.getParameters().getString(pName,"");
		} else
		if (def.hasAttribute("value")) {
			value = def.getAttribute("value");
		}
		log().t("result",name,value);
		
		boolean one = false;
		for (Element mapE : MXml.getLocalElementIterator(def, "map")) {
			one = true;
			if (value.matches(mapE.getAttribute("match"))) {
				String v = mapE.getAttribute("value");
				log().t("map",name,mapE.getAttribute("match"),v);
				return v;
			}
		}
		if (!one && !MString.isEmpty(value))
			return value;
				
		if (MString.isSet(value)) return value;
		
		return null;
	}

	public synchronized GroovyShell getGroovyShell() throws Exception {
		if (shell == null) {
			Binding binding = new Binding();
			binding.setVariable("vars", context.getParameters());
			binding.setVariable("config", context.getProcessorConfig());
			shell = new GroovyShell(binding);
		}
		return shell;
	}

	public boolean toReadOnly(String name, PDField field) {
		Element def = fields.get(name);
		if (def == null) {
			return false;
		}
		return MCast.toboolean( def.getAttribute("readOnly"), false );
	}

	public String getDefaultValue(String name, PDField field) {
		Element def = fields.get(name);
		if (def == null) {
			log().t("field not found",name);
			return null;
		}
		
		String value = def.getAttribute("default");
		return value;
	}

}
