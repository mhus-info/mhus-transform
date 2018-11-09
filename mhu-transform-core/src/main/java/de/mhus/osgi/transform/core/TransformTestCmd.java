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

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.osgi.framework.FrameworkUtil;

import de.mhus.lib.core.MApi;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MProperties;
import de.mhus.osgi.services.deploy.BundleDeployer;
import de.mhus.osgi.services.deploy.BundleDeployer.SENSIVITY;
import de.mhus.osgi.transform.api.ProcessorContext;
import de.mhus.osgi.transform.api.ResourceProcessor;
import de.mhus.osgi.transform.api.TransformApi;
import de.mhus.osgi.transform.api.TransformConfig;
import de.mhus.osgi.transform.api.TransformUtil;

@Command(scope = "transform", name = "test", description = "Transform test")
@Service
public class TransformTestCmd implements Action {

	@Argument(index=0, name="processor", required=false, description="processor or birt")
	String processor;
	private File target;

	@Override
	public Object execute() throws Exception {
		// Init
		target = BundleDeployer.deploy(FrameworkUtil.getBundle(TransformTestCmd.class), "/test", SENSIVITY.UPDATE);
		System.out.println("--- Directory: " + target);
		MProperties param = new MProperties();
		param.setString("text", "World");
		
		if (processor != null) {
			if (processor.equals("birt"))
				testBirt();
			else
				test(target,param,processor);
		} else {
			// Test twig
			test(target,param, "twig");
			// Test velocity
			test(target,param, "vm");
			// Test freemarker
			test(target,param, "ftl");
			// Test birt
			testBirt();
		}

		return null;
	}

	private void testBirt() throws Exception {
		System.out.println("======================");
		System.out.println(" Birt");
		System.out.println("======================");
		TransformApi api = MApi.lookup(TransformApi.class);
		ResourceProcessor birtProcessor = api.findProcessor("pdfreport");
		
		File projectRoot = target;
		File templateRoot = target;
		MProperties param = new MProperties();
		param.setString("text", "World");
		MProperties c = new MProperties();
		TransformConfig config = api.createConfig(projectRoot, templateRoot, c, param);
		ProcessorContext context = birtProcessor.createContext(config);
		
		File from = new File(target,"hello_world.rptdesign");
		File to = new File(target, "birt-to.pdf");
		if (to.exists()) to.delete();
		
		context.doProcess(from, to);
		
		if (to.exists()) {
			System.out.println(">>> Transform successful");
			System.out.println(to.getAbsolutePath());
		} else {
			System.out.println(">>> Transform failed !!!");
		}
		
	}

	private void test(File target, MProperties param, String name) {
		name = name.toLowerCase();
		try {
			System.out.println("======================");
			System.out.println(" " + name.toUpperCase());
			System.out.println("======================");
			File from = new File(target,name + "-in." + name);
			File to = new File(target, name + "-to.txt");
			File out = new File(target,name + "-out.txt");
			if (to.exists()) to.delete();
			TransformUtil.transform(from, to, param);
			
			String toContent = MFile.readFile(to).trim();
			String outContent = MFile.readFile(out).trim();
			
			if (toContent.equals(outContent)) 
				System.out.println(">>> Transform successful");
			else {
				System.out.println(">>> Transform failed !!!");
				System.out.println("Expected: " + outContent);
				System.out.println("Get     : " + toContent);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}		
	}

}
