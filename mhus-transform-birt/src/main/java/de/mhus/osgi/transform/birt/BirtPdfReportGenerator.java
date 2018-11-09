package de.mhus.osgi.transform.birt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;

import aQute.bnd.annotation.component.Component;
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

		private TransformConfig context;
		private String home = BirtDeployService.dir.getAbsolutePath();

		public Context(TransformConfig context) {
			this.context = context;
		}

		@Override
		public void doProcess(File from, File to) throws Exception {
			String id = UUID.randomUUID().toString();
			File xmlFile = new File(context.getProjectRoot(), "birt_" + id + ".xml");
			createXML(from, xmlFile);
			
			MFile.writeFile(xmlFile, "<print></print>");
			createPDF(from.getAbsolutePath(), xmlFile.getAbsolutePath(), to.getAbsolutePath());
			
			if (context.getProcessorConfig() == null || !context.getProcessorConfig().getBoolean("debug", false))
				xmlFile.delete();
		}

		@Override
		public void doProcess(File from, OutputStream out) throws Exception {
			
		}

		@Override
		public void close() {
			
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		private void createXML(File from, File out) throws Exception {

			Document document = DocumentHelper.createDocument();
	        Element eConfig = document.addElement( "config" );
	        Element eParam = eConfig.addElement("parameters");
	        
			if (context.getParameters() != null) {

				for (Entry<String, Object> entry : context.getParameters().entrySet()) {
					if (entry.getValue() instanceof String)
						eParam.addAttribute(entry.getKey(), String.valueOf(entry.getValue()));
				}
				for (Entry<String, Object> entry : context.getParameters().entrySet()) {
					if (entry.getValue() instanceof List) {
						Element eList = eConfig.addElement(entry.getKey() + "_");
						List<Object> list = (List)entry.getValue();
						for (Object entry2 : list) {
							if (entry2 instanceof Map) {
								Element eEntry = eList.addElement(entry.getKey());
								Map<Object,Object> map = (Map)entry2;
								for (Map.Entry<Object,Object> entry3 : map.entrySet()) {
									eEntry.addAttribute(String.valueOf(entry3.getKey()), String.valueOf(entry3.getValue()));
								}
							}	
						}
					}
				}
			}
			
	        OutputFormat format = OutputFormat.createPrettyPrint();
	        XMLWriter writer = new XMLWriter( new FileOutputStream( out ), format );
	        writer.write( document );
	        writer.close();
   
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
