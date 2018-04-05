package de.mhus.osgi.transform.pdf;

import java.io.File;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import aQute.bnd.annotation.component.Component;
import de.mhus.lib.core.IReadProperties;
import de.mhus.lib.core.MLog;
import de.mhus.osgi.transform.api.ResourceProcessor;
import de.mhus.osgi.transform.api.TransformContext;

@Component(properties={"processor=pdfform","extension=pdf"})
public class PdfFormFillProcessor extends MLog implements ResourceProcessor {

	@Override
	public void doProcess(File from, File to, TransformContext context) throws Exception {
		IReadProperties config = context.getProcessorConfig();
		String configName = config.getString("model", from.getName()) + ".xml";
		File configFile = new File( context.getTemplateRoot(), configName);
		log().d("process",configFile,from,to,context.getParameters());
		FillModel model = new FillModel(configFile, context );
		
		// Load the pdfTemplate
	    PDDocument pdfTemplate = PDDocument.load(from);

	    PDDocumentCatalog docCatalog = pdfTemplate.getDocumentCatalog();
	    PDAcroForm acroForm = docCatalog.getAcroForm();

	    // Get field names
	    List<PDField> fieldList = acroForm.getFields();

	    // String the object array
	    String[] fieldArray = new String[fieldList.size()];
	    int i = 0;
	    for (PDField sField : fieldList) {
	        fieldArray[i] = sField.getFullyQualifiedName();
	        i++;
	    }

	    // Loop through each field in the array and do something
	    for (String f : fieldArray) {
	        PDField field = acroForm.getField(f);
	        try {
		        log().d("field",f,field.getMappingName(),field.getFieldType());
		        
		        String value = model.getValue(f,field);
		        log().d("value",f,value);
		        if (value != null) {
		        	try {
		        		field.setValue(value);
		        		
		        		if (model.toReadOnly(f,field))
		        			field.setReadOnly(true);
		        	} catch (IllegalArgumentException e) {
			        	try {
			        		String val = model.getDefaultValue(f,field);
			        		log().d("default1",f,val,e.toString());
			        		if (val != null)
			        			field.setValue(val);
			        	} catch (IllegalArgumentException e1) {
			        		log().e(f,e1);
			        	}
	        		}
		        } else {
		        	try {
		        		String val = model.getDefaultValue(f,field);
		        		log().d("default2",f,val);
		        		if (val != null)
		        			field.setValue(val);
		        	} catch (IllegalArgumentException e1) {
		        		log().e(f,e1);
		        	}
		        }
	        } catch (Throwable t) {
	        	log().w(f, t);
	        }
	        /*
	        if (field != null && field.getFieldType() != null && field.getFieldType().equals("Tx")) {
	        	field.setValue(model.getValue(field));
	        } else
	        if (field != null && field.getFieldType() != null && field.getFieldType().equals("Btn")) {
	        	try {
	        		field.setValue("Ja");
	        	} catch (IllegalArgumentException e) {
	        		String msg = e.getMessage();
	        		System.out.println( msg );
	        		try {
		        		field.setValue("ja");
		        	} catch (IllegalArgumentException e2) {
	        		}
	        	}

	        }
        	*/
	        
	    }

	    // Save edited file
	    pdfTemplate.setAllSecurityToBeRemoved(true);
	    pdfTemplate.save(to);
	    pdfTemplate.close();
	    
		
	}

}
