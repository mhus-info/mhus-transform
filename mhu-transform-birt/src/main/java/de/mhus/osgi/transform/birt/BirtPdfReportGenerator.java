package de.mhus.osgi.transform.birt;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;

import org.osgi.service.component.annotations.Component;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MLog;
import de.mhus.osgi.transform.api.ProcessorContext;
import de.mhus.osgi.transform.api.ResourceProcessor;
import de.mhus.osgi.transform.api.TransformConfig;

@Component(properties={"processor=pdfreport","extension=rptdesign"})
public class BirtPdfReportGenerator extends MLog implements ResourceProcessor {
	
//	private OsgiBundleClassLoader bundleClassLoader;
//	private URLClassLoader classLoader;

	@Override
	public ProcessorContext createContext(TransformConfig config) throws Exception {
//		if (classLoader == null) {
//			LinkedList<URL> jars = new LinkedList<>();
//			for (File jar : new File(BirtDeployService.dir, "lib").listFiles()) {
//				if (jar.isFile() && jar.getName().endsWith(".jar") && !jar.getName().startsWith("org.eclipse.birt.runtime_")) {
//					jars.add(jar.toURL());
//				}
//			}
//			bundleClassLoader = new OsgiBundleClassLoader();
//			classLoader = new URLClassLoader(jars.toArray(new URL[jars.size()]), bundleClassLoader);
//		}
		
		return new Context(config);
	}

	private class Context implements ProcessorContext {

		private static final String PARAMETER_XML_CONTENT = "_xml_content";
		private TransformConfig context;
		private String home = BirtDeployService.dir.getAbsolutePath();

		public Context(TransformConfig context) {
			this.context = context;
		}

		@Override
		public void doProcess(File from, File to) throws Exception {
			File xmlFile = null;
			if (context.getParameters().get(PARAMETER_XML_CONTENT) != null) {
				String id = UUID.randomUUID().toString();
				xmlFile = new File(context.getProjectRoot(), "birt_" + id + ".xml");
				createXML(from, xmlFile);
			}
			
			createPDF(from.getAbsolutePath(), xmlFile == null ? null : xmlFile.getAbsolutePath(), to.getAbsolutePath());
			
			if (xmlFile != null && (context.getProcessorConfig() == null || !context.getProcessorConfig().getBoolean("_debug", false)))
				xmlFile.delete();
		}

		@Override
		public void doProcess(File from, OutputStream out) throws Exception {
			File to = new File(context.getProjectRoot(), "output_"+UUID.randomUUID()+".pdf");
			doProcess(from, to);
			FileInputStream is = new FileInputStream(to);
			MFile.copyFile(is, out);
			to.delete();
		}

		@Override
		public void close() {
			
		}

		private void createXML(File from, File out) throws Exception {
			String content = context.getParameters().getString(PARAMETER_XML_CONTENT, null);
			MFile.writeFile(out, content);
		}
		
		private void createPDF(String report, String xmlFile, String pdfFile) throws BirtException {
			EngineConfig config = new EngineConfig(); 
			config.setBIRTHome(home);
			config.setLogConfig(null, Level.FINEST);
			Platform.startup(config);
			final IReportEngineFactory FACTORY = (IReportEngineFactory) Platform
		            .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
		        IReportEngine engine = FACTORY.createReportEngine(config);

		        // Open the report design
		        IReportRunnable design = null;
		        design = engine.openReportDesign(report); 
		        
		        IRunAndRenderTask task = engine.createRunAndRenderTask(design);
		        
		        for (Entry<String, Object> entry : context.getParameters().entrySet())
		        		if (!entry.getKey().startsWith("_")) 
		        			task.setParameterValue(entry.getKey(), entry.getValue());
		        
		        if (xmlFile != null)
		        		task.setParameterValue("xmlfile", xmlFile);
		        
		        // task.setParameterValue("Top Count", (new Integer(5)));
		        // task.validateParameters();
		 
//		        final HTMLRenderOption HTML_OPTIONS = new HTMLRenderOption();       
//		        HTML_OPTIONS.setOutputFileName("output/resample/Parmdisp.html");
//		        HTML_OPTIONS.setOutputFormat("html");
		        // HTML_OPTIONS.setHtmlRtLFlag(false);
		        // HTML_OPTIONS.setEmbeddable(false);
		        // HTML_OPTIONS.setImageDirectory("C:\\test\\images");
		 
		         PDFRenderOption PDF_OPTIONS = new PDFRenderOption();

		         PDF_OPTIONS.setOutputFileName(pdfFile);
		         PDF_OPTIONS.setOutputFormat("pdf");
		 
		        task.setRenderOption(PDF_OPTIONS);
		        task.run();
		        task.close();
		        engine.destroy();
		        
		}
		
	}
}
