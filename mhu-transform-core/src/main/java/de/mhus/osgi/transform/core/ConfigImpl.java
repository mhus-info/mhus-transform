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
package de.mhus.osgi.transform.core;

import java.io.File;

import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.IReadProperties;
import de.mhus.lib.core.MProperties;
import de.mhus.osgi.transform.api.MutableTransformConfig;

public class ConfigImpl implements MutableTransformConfig {

    private File templateRoot; // root of the current template
    private File projectRoot; // base folder of all templates
    private MProperties config;
    private MProperties param;
    private String charset = "utf-8";

    public ConfigImpl(File projectRoot, File templateRoot, MProperties config, MProperties param) {
        this.templateRoot = templateRoot;
        this.projectRoot = projectRoot;
        this.config = config;
        this.param = param;
    }

    @Override
    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Override
    public IReadProperties getProcessorConfig() {
        return config;
    }

    @Override
    public File getTemplateRoot() {
        return templateRoot;
    }

    @Override
    public IProperties getParameters() {
        return param;
    }

    @Override
    public File getProjectRoot() {
        return projectRoot;
    }

    @Override
    public String getCharset() {
        return charset;
    }
}
