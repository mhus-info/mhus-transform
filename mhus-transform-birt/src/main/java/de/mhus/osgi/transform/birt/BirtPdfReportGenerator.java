package de.mhus.osgi.transform.birt;

import java.io.File;
import java.io.OutputStream;
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
			MFile.writeFile(xmlFile, "<print></print>");
			createPDF(from.getAbsolutePath(), xmlFile.getAbsolutePath(), to.getAbsolutePath());
			
			if (!context.getProcessorConfig().getBoolean("debug", false))
				xmlFile.delete();
		}

		@Override
		public void doProcess(File from, OutputStream out) throws Exception {
			
		}

		@Override
		public void close() {
			
		}

		private void createXML() throws Exception {
/*			
			Document document = DocumentHelper.createDocument();
	        Element ePrint = document.addElement( "print" );

	        // <search name="Search Name" plz="12345">
	        Element eSearch = ePrint.addElement("search")
	        	.addAttribute("name", search.name)
	        	.addAttribute("description", search.description)
	        	.addAttribute("plz", "" + search.plz)
	        	.addAttribute("surrounding", "" + search.surrounding)
				.addAttribute("regional", "" + search.regionalOnly)
				.addAttribute("keywordsin", search.keywordsIn)
				.addAttribute("keywordsout", search.keywordsOut);
			
	        // <attributes>
	        Element eAttributes = eSearch.addElement("attributes");
	        Collections.sort(search.parameters,new Comparator<ISearchParameter>() {

	        	ParamComparator pc = new ParamComparator();
	        	
				@Override
				public int compare(ISearchParameter o1, ISearchParameter o2) {
					try {
			        	UUID pid1 = o1.getParamId();
			        	Param paramObj1 = app.getReadManager().getObject(Param.class, pid1);
			        	UUID pid2 = o2.getParamId();
			        	Param paramObj2 = app.getReadManager().getObject(Param.class, pid2);
			        	
			        	return pc.compare(paramObj1, paramObj2);
					} catch (Throwable t) {
						t.printStackTrace();
					}
					return 0;
				}
			});
	        for (ISearchParameter param : search.parameters) {
	        	UUID pid = param.getParamId();
	        	Param paramObj = app.getReadManager().getObject(Param.class, pid);
	        	if (paramObj != null)
	        		// <attribute name="Pb 1" max="1.5" unit="mg" />
		        	eAttributes.addElement("attribute")
		        		.addAttribute("name", paramObj.getName())
		        		.addAttribute("description", paramObj.getDescription())
		        		.addAttribute("type", paramObj.getTypeName())
		        		.addAttribute("unit", paramObj.getUnitName())
		        		.addAttribute("max", "" + param.getMax());
	        }

	        // <results>
	        Element eResults = ePrint.addElement("results");
	        for (Place p : places) {
	        	// <place name="Deponie Nord" address="hier und da" note="note">
	        	Element ePlace = eResults.addElement("place")
	        			.addAttribute("name", p.getName())
	        			.addAttribute("address", p.getAddress())
	        			.addAttribute("description", p.getDescription())
	        			.addAttribute("keywords", p.getKeywords())
	        			.addAttribute("links", p.getLinks())
	        			.addAttribute("plz", p.getPlz())
	        			.addAttribute("note", p.getNote())
	        			.addAttribute("town", p.getTown())
	        			.addAttribute("distance", String.valueOf( ResultsTab.getDistance(app, p, search.plz) ) )
	        			;
	            // <attributes>
	            Element ePAttributes = ePlace.addElement("attributes");
	            for ( PlaceParam pp : p.getParameters().getRelations() ) {
	            	Param paramObj = pp.getParam().getRelation();
	            	if (paramObj != null)
	            		// <attribute name="Pb 1" max="1.5" unit="mg" />
	    	        	ePAttributes.addElement("attribute")
	    	        		.addAttribute("name", paramObj.getName())
	    	        		.addAttribute("description", paramObj.getDescription())
	    	        		.addAttribute("type", paramObj.getTypeName())
	    	        		.addAttribute("unit", paramObj.getUnitName())
	    	        		.addAttribute("max", "" + pp.getMax())
	    	        		.addAttribute("min", "" + pp.getMin());
	            }
	        }
	        
	        // <transporters>
	        Element eTransporters = ePrint.addElement("transporters");
	        if (transporterList != null) {
		        for ( TransporterResult obj : transporterList) {
		        	Transporter tra = obj.getTransporter();
					long distance = obj.getDistance();
					// <transporter name="nr 1" distance="1.8"/>
					eTransporters.addElement("transporter")
							.addAttribute("name", tra.getName())
							.addAttribute("address", tra.getAddress())
							.addAttribute("plz", "" + tra.getPlz())
							.addAttribute("town", tra.getTown())
							.addAttribute("priority", "" + tra.getPriority())
							.addAttribute("distance", "" + distance);
		
		        }
			}   
	        OutputFormat format = OutputFormat.createPrettyPrint();
	        XMLWriter writer = new XMLWriter( new FileOutputStream( xmlFile ), format );
	        writer.write( document );
	        writer.close();
*/	        
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
