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
package de.mhus.osgi.transform.api;

import java.io.File;

import de.mhus.lib.core.MProperties;
import de.mhus.lib.errors.NotFoundException;

public interface TransformApi {

    ResourceProcessor findResourceProcessor(String fileName) throws NotFoundException;

    /**
     * Create the context object before processing a template. The context can be used multiple
     * times.
     *
     * @param projectRoot Root of multiple template configurations (not mandatory for all template
     *     engines) set to null if not needed
     * @param templateRoot Root of the specified template configuration (not mandatory for all
     *     template engines) set to null if not needed
     * @param config Parameters to configure the template engine
     * @param param Parameters for the template
     * @return The context to process templates
     */
    MutableTransformConfig createConfig(
            File projectRoot, File templateRoot, MProperties config, MProperties param);

    /**
     * Search the processor by the processor name
     *
     * @param processorName
     * @return The processor
     * @throws NotFoundException
     */
    ResourceProcessor findProcessor(String processorName) throws NotFoundException;
}
