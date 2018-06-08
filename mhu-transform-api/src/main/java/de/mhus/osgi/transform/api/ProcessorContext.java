package de.mhus.osgi.transform.api;

import java.io.File;
import java.io.OutputStream;

public interface ProcessorContext {

	void doProcess(File from, File to) throws Exception;

	void doProcess(File from, OutputStream out) throws Exception;

	void close();

}
