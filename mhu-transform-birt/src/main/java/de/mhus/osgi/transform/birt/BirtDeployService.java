package de.mhus.osgi.transform.birt;

import java.io.File;

import org.osgi.service.component.annotations.Component;

import de.mhus.osgi.services.DeployService;

@Component
public class BirtDeployService implements DeployService {

	public static File dir;

	@Override
	public String[] getResourcePathes() {
		return new String[] {"ReportEngine"};
	}

	@Override
	public void setDeployDirectory(String path, File dir) {
		BirtDeployService.dir = dir;
	}

	@Override
	public File getDeployDirectory() {
		return dir;
	}

}
