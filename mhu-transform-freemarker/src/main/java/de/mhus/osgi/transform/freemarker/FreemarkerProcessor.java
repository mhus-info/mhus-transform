package de.mhus.osgi.transform.freemarker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map.Entry;

import aQute.bnd.annotation.component.Component;
import de.mhus.lib.core.IReadProperties;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MProperties;
import de.mhus.osgi.transform.api.ResourceProcessor;
import de.mhus.osgi.transform.api.TransformContext;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

@Component(properties="extension=ftl")
public class FreemarkerProcessor extends MLog implements ResourceProcessor {

	@Override
	public void doProcess(File from, File to, TransformContext context) throws Exception {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_27);
		
		File templateRoot = context.getTemplateRoot();
		if (templateRoot == null) templateRoot = from.getParentFile();
		File projectRoot = context.getProjectRoot();
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

}
