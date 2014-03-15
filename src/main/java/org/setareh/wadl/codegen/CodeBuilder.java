package org.setareh.wadl.codegen;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

import org.setareh.wadl.codegen.model.FileInfo;
import org.setareh.wadl.codegen.writer.ICodeWriter;

public class CodeBuilder {
	
	/**
	 * Generate codes with the specific ICodeWriter
	 * 
	 * @param files, a set of files to be generated
	 * @param writer, ICodeWriter instance
	 * @throws java.io.IOException
	 */
	public static void build(Set<FileInfo> files, ICodeWriter writer) throws IOException {
		for(FileInfo file: files) {
			OutputStream os = writer.openStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			bos.write(file.getContent());
			bos.close();
			os.close();
		}
		writer.close();
	}

}
