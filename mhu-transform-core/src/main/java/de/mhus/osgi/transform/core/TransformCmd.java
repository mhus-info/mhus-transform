package de.mhus.osgi.transform.core;

import java.io.File;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MProperties;
import de.mhus.osgi.transform.api.TransformUtil;

@Command(scope = "transform", name = "transform", description = "Transform file")
@Service
public class TransformCmd implements Action {

	@Argument(index=0, name="from", required=true, description="")
	String from;

	@Argument(index=1, name="to", required=true, description="")
	String to;

	@Argument(index=2, name="parameters", required=false, description="Parameters", multiValued=true)
    String[] parameters;

	@Option(name="-c", aliases="--config", description="config file",required=false)
	String configFile = null;

	@Option(name="-r", aliases="--root", description="template root directory",required=false)
	String dir = null;
	
	@Option(name="-p", aliases="--print", description="print after creation",required=false)
	boolean print = false;

	@Option(name="-x", aliases="--processor", description="executing processor name",required=false)
	String processorName = null;

	@Override
	public Object execute() throws Exception {
		
		MProperties param = MProperties.explodeToMProperties(parameters);
		MProperties config = null;
		if (configFile != null)
			config = MProperties.load(configFile);
		
		TransformUtil.transform(new File(from), new File(to), dir == null ? null : new File(dir), config, param, processorName);
		
		if (print) {
			System.out.println( MFile.readFile(new File(to)) );
		} else {
			System.out.println("OK");
		}
		return null;
	}

}
