package de.mhus.osgi.transform.jtwig;

import java.io.File;
import java.io.FileOutputStream;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import aQute.bnd.annotation.component.Component;
import de.mhus.osgi.transform.api.ResourceProcessor;
import de.mhus.osgi.transform.api.TransformContext;

@Component(properties="extension=twig")
public class JtwigProcessor implements ResourceProcessor {

	@Override
	public void doProcess(File from, File to, TransformContext context) throws Exception {
		JtwigTemplate template = JtwigTemplate.fileTemplate(from);
        JtwigModel model = JtwigModel.newModel(context.getParameters());

        FileOutputStream os = new FileOutputStream(to);
        template.render(model, os);
        os.close();
        
	}

}
