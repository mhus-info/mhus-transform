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
package de.mhus.osgi.transform.soffice;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.osgi.service.component.annotations.Component;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.io.SOfficeConnector;
import de.mhus.lib.core.io.StreamRewriter;
import de.mhus.lib.core.io.UserFieldValuesRewriter;
import de.mhus.osgi.transform.api.ProcessorContext;
import de.mhus.osgi.transform.api.ResourceProcessor;
import de.mhus.osgi.transform.api.TransformConfig;

@Component(property = {"processor=pdfsoffice", "extension=odt"})
public class SOfficeProcessor extends MLog implements ResourceProcessor {

    private SOfficeConnector connector = new SOfficeConnector();

    @Override
    public ProcessorContext createContext(TransformConfig config) throws Exception {
        return new Context(config);
    }

    private class Context implements ProcessorContext {

        private TransformConfig context;

        public Context(TransformConfig context) {
            this.context = context;
        }

        @Override
        public void doProcess(File from, File to) throws Exception {
            //			IReadProperties config = context.getProcessorConfig();
            log().d("process", from, to, context.getParameters());

            File tmp = new File(context.getProjectRoot(), "prepared_" + UUID.randomUUID() + ".odt");
            StreamRewriter replacer = new UserFieldValuesRewriter(context.getParameters());
            // replace user fields
            SOfficeConnector.replace(from, tmp, replacer);
            // transfer to pdf
            String pdfFile =
                    connector.convertToPdf(
                            tmp.getAbsolutePath(), context.getProjectRoot().getAbsolutePath());
            // move
            new File(pdfFile).renameTo(to);
            // cleanup
            tmp.delete();
        }

        @Override
        public void close() {}

        @Override
        public void doProcess(File from, OutputStream out) throws Exception {
            File to = new File(context.getProjectRoot(), "output_" + UUID.randomUUID() + ".pdf");
            doProcess(from, to);
            try(FileInputStream is = new FileInputStream(to)){            	
            	MFile.copyFile(is, out);
            }
            to.delete();
        }
    }
}
