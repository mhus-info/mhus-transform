/**
 * Copyright (C) 2019 Mike Hummel (mh@mhus.de)
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
package de.mhus.osgi.transform.core;

import java.io.File;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MString;
import de.mhus.osgi.api.MOsgi;
import de.mhus.osgi.api.karaf.AbstractCmd;
import de.mhus.osgi.transform.api.ResourceProcessor;
import de.mhus.osgi.transform.api.TransformUtil;

@Command(scope = "transform", name = "transform", description = "Transform file")
@Service
public class TransformCmd extends AbstractCmd {

    @Argument(
            index = 0,
            name = "from",
            required = true,
            description = "From or command (if to is not set or empty)")
    String from;

    @Argument(index = 1, name = "to", required = false, description = "")
    String to;

    @Argument(
            index = 2,
            name = "parameters",
            required = false,
            description = "Parameters",
            multiValued = true)
    String[] parameters;

    @Option(name = "-c", aliases = "--config", description = "config file", required = false)
    String configFile = null;

    @Option(
            name = "-t",
            aliases = "--templates",
            description = "template root directory",
            required = false)
    String dir = null;

    @Option(
            name = "-r",
            aliases = "--root",
            description = "project root directory",
            required = false)
    String root = null;

    @Option(
            name = "-p",
            aliases = "--print",
            description = "print after creation",
            required = false)
    boolean print = false;

    @Option(
            name = "-x",
            aliases = "--processor",
            description = "executing processor name",
            required = false)
    String processorName = null;

    @Override
    public Object execute2() throws Exception {

        if (MString.isEmpty(to)) {
            if (from.equals("list")) {
                for (MOsgi.Service<ResourceProcessor> ref :
                        MOsgi.getServiceRefs(ResourceProcessor.class, null)) {
                    System.out.println(">>> Processor");
                    System.out.println("  Name: " + ref.getName());
                    for (String key : ref.getReference().getPropertyKeys())
                        System.out.println(" " + key + "=" + ref.getReference().getProperty(key));
                }
            }
            return null;
        }

        MProperties param = IProperties.explodeToMProperties(parameters);
        MProperties config = null;
        if (configFile != null) config = MProperties.load(configFile);

        TransformUtil.transform(
                new File(from),
                new File(to),
                root == null ? null : new File(root),
                dir == null ? null : new File(dir),
                config,
                param,
                processorName);

        if (print) {
            System.out.println(MFile.readFile(new File(to)));
        } else {
            System.out.println("OK");
        }
        return null;
    }
}
