/**
 * Copyright (C) 2020 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.osgi.transform.api;

import java.io.File;
import java.io.OutputStream;

import de.mhus.lib.core.M;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.logging.ITracer;
import io.opentracing.Scope;

public class TransformUtil {

    public static void transform(File from, File to, MProperties param) throws Exception {
        transform(from, to, null, null, null, param, null);
    }

    public static void transform(
            File from,
            File to,
            File projectRoot,
            File templateRoot,
            MProperties properties,
            MProperties param,
            String processorName)
            throws Exception {

        try (Scope scope =
                ITracer.get()
                        .enter(
                                "transform:" + processorName,
                                "from",
                                from,
                                "to",
                                to,
                                "template",
                                templateRoot,
                                "properties",
                                properties,
                                "param",
                                param)) {
            TransformApi api = M.l(TransformApi.class);

            if (templateRoot == null) templateRoot = from.getParentFile();
            TransformConfig config = api.createConfig(projectRoot, templateRoot, properties, param);
            ResourceProcessor processor = null;
            if (processorName != null) processor = api.findProcessor(processorName);
            else processor = api.findResourceProcessor(from.getName());

            ProcessorContext c = processor.createContext(config);
            c.doProcess(from, to);
            c.close();
        }
    }

    public static void transform(
            File from,
            OutputStream to,
            File projectRoot,
            File templateRoot,
            MProperties properties,
            MProperties param,
            String processorName)
            throws Exception {

        try (Scope scope =
                ITracer.get()
                        .enter(
                                "transform:" + processorName,
                                "from",
                                from,
                                "template",
                                templateRoot,
                                "properties",
                                properties,
                                "param",
                                param)) {
            TransformApi api = M.l(TransformApi.class);

            if (templateRoot == null) templateRoot = from.getParentFile();
            TransformConfig config = api.createConfig(projectRoot, templateRoot, properties, param);
            ResourceProcessor processor = null;
            if (processorName != null) processor = api.findProcessor(processorName);
            else processor = api.findResourceProcessor(from.getName());

            ProcessorContext c = processor.createContext(config);
            c.doProcess(from, to);
            c.close();
        }
    }
}
