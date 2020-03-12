/**
 * Copyright 2018 Mike Hummel
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.osgi.transform.velocity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.EscapeTool;

import org.osgi.service.component.annotations.Component;
import de.mhus.lib.core.IReadProperties;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.errors.MException;
import de.mhus.osgi.transform.api.ProcessorContext;
import de.mhus.osgi.transform.api.ResourceProcessor;
import de.mhus.osgi.transform.api.TransformConfig;

@Component(property = {"extension=vm", "processor=velocity"})
public class VelocityResourceProcessor extends MLog implements ResourceProcessor {

    @Override
    public ProcessorContext createContext(TransformConfig config) throws Exception {
        return new Context(config);
    }

    private class Context implements ProcessorContext {

        private Properties props;
        private VelocityContext vcontext;
        private File templateRoot;
        private String projectPath;
        private VelocityEngine ve;
        private TransformConfig context;

        public Context(TransformConfig context) throws MException, IOException {
            this.context = context;
            ve = new VelocityEngine();
            ve.setProperty(VelocityEngine.RUNTIME_LOG, "mylog");

            IReadProperties config = context.getProcessorConfig();
            if (config == null) config = new MProperties();

            String velocityProperties =
                    config.getString("velocity.properties", "velocity.properties");

            templateRoot = context.getTemplateRoot();
            if (templateRoot == null) throw new MException("template root not set");
            File propFile = new File(templateRoot, velocityProperties);
            props = new Properties();

            if (propFile.exists()) {
                FileInputStream is = new FileInputStream(propFile);
                props.load(is);
                is.close();
            }

            projectPath =
                    context.getProjectRoot() != null
                            ? context.getProjectRoot().getAbsolutePath()
                            : null;
            if (projectPath != null)
                props.setProperty(
                        RuntimeConstants.EVENTHANDLER_INCLUDE, IncludeFullPath.class.getName());

            ve.init(props);

            vcontext = new VelocityContext();

            for (Entry<String, Object> entry : context.getParameters().entrySet())
                vcontext.put(entry.getKey(), entry.getValue());

            // overwrite additional tooling
            vcontext.put("__esc", new EscapeTool());
            vcontext.put("__date", new DateTool());
            vcontext.put("__config", config);
        }

        @Override
        public void doProcess(File from, File to) throws Exception {
            synchronized (this) {
                String path = from.getParentFile().getAbsolutePath();
                props.put(
                        RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
                        path + "," + templateRoot.getCanonicalPath());

                Template t = ve.getTemplate(from.getName());
                vcontext.put("__path", path);

                if (projectPath != null) {
                    IncludeFullPath.setContext(vcontext);
                    IncludeFullPath.setProjectPath(projectPath);
                }
                FileWriter writer = new FileWriter(to);
                try {
                    t.merge(vcontext, writer);
                } catch (Throwable th) {
                    log().e(from, th);
                    throw th;
                } finally {
                    if (projectPath != null) {
                        IncludeFullPath.setContext(null);
                        IncludeFullPath.setProjectPath(null);
                    }
                    writer.close();
                }
            }
        }

        @Override
        public void close() {
            ve = null;
            vcontext = null;
        }

        @Override
        public void doProcess(File from, OutputStream out) throws Exception {
            synchronized (this) {
                String path = from.getParentFile().getAbsolutePath();
                props.put(
                        RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
                        path + "," + templateRoot.getCanonicalPath());

                Template t = ve.getTemplate(from.getName());
                vcontext.put("__path", path);

                if (projectPath != null) {
                    IncludeFullPath.setContext(vcontext);
                    IncludeFullPath.setProjectPath(projectPath);
                }

                OutputStreamWriter writer = new OutputStreamWriter(out, context.getCharset());
                try {
                    t.merge(vcontext, writer);
                } catch (Throwable th) {
                    log().e(from, th);
                    throw th;
                } finally {
                    if (projectPath != null) {
                        IncludeFullPath.setContext(null);
                        IncludeFullPath.setProjectPath(null);
                    }
                    writer.flush();
                }
            }
        }
    }
}
