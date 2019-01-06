package de.mhus.osgi.transform.freemarker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map.Entry;

import org.osgi.service.component.annotations.Component;
import de.mhus.lib.core.IReadProperties;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.errors.MException;
import de.mhus.osgi.transform.api.ProcessorContext;
import de.mhus.osgi.transform.api.ResourceProcessor;
import de.mhus.osgi.transform.api.TransformConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

@Component(properties="extension=ftl")
public class FreemarkerProcessor extends MLog implements ResourceProcessor {

	@Override
	public ProcessorContext createContext(TransformConfig config) throws Exception {

		return new Context(config);
	}

	private class Context implements ProcessorContext {

		private TransformConfig context;
		private Configuration cfg;
		private File projectRoot;

		public Context(TransformConfig context) throws TemplateException, MException, IOException {
			
			this.context = context;
			
			cfg = new Configuration(Configuration.VERSION_2_3_27);
			
			File templateRoot = context.getTemplateRoot();
			if (templateRoot == null) throw new MException("template root not set");
			projectRoot = context.getProjectRoot();
			if (projectRoot == null) projectRoot = templateRoot;

			IReadProperties config = context.getProcessorConfig();
			if (config == null) config = new MProperties();

			cfg.setDirectoryForTemplateLoading(projectRoot);
			cfg.setDefaultEncoding(config.getString("encoding", "UTF-8"));
			switch (config.getString("exceptionHandler", "rethrow").toLowerCase()) {
			case "rethrow":
				cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
				break;
			case "htmldebug":
				cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
				break;
			case "debug":
				cfg.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
				break;
			case "ignore":
				cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
				break;
			default:
				log().w("exceptionHandler unknown",config.getString("exceptionHandler", "rethrow"));
			}

			cfg.setLogTemplateExceptions(config.getBoolean("logExceptions", false));
			cfg.setWrapUncheckedExceptions(config.getBoolean("wrapUncheckedExceptions", true));
			
			for (Entry<String, Object> entry : config.entrySet()) {
				if (entry.getKey().startsWith("setting_")) {
					cfg.setSetting(entry.getKey().substring(8), String.valueOf(entry.getValue()));
				}
			}
			
		}

		@Override
		public void doProcess(File from, File to) throws Exception {
			
			String templatePath = from.getAbsolutePath().substring(projectRoot.getAbsolutePath().length()+1);
			Template temp = cfg.getTemplate(templatePath);
			FileOutputStream fos = new FileOutputStream(to);
			Writer out = new OutputStreamWriter(fos);
			try {
				temp.process(context.getParameters(), out);
			} finally {
				out.flush();
				fos.close();
			}
			
		}

		@Override
		public void close() {
			context = null;
			cfg = null;
		}

		@Override
		public void doProcess(File from, OutputStream out) throws Exception {
			String templatePath = from.getAbsolutePath().substring(projectRoot.getAbsolutePath().length()+1);
			Template temp = cfg.getTemplate(templatePath);
			OutputStreamWriter writer = new OutputStreamWriter(out, context.getCharset() );
			temp.process(context.getParameters(), writer);
			writer.flush();
		}
		
	}
	
}
