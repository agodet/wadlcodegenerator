package org.setareh.wadl.codegen.builder;

import org.setareh.wadl.codegen.model.CGConfig;
import org.setareh.wadl.codegen.utils.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Common {
    public static String[] generatePersistentData(String filePath) throws URISyntaxException, IOException {

        String[] persistentClassArray = {};

        URI persistentFileUri = new URI(filePath);

        System.out.println("Reading persistentFile from URI : " + persistentFileUri);
        String persistentData = readPersistentFile(persistentFileUri.toString());

        persistentClassArray = persistentData.split("\\r?\\n");

        return persistentClassArray;
    }

    private static String readPersistentFile(String persistentFileURI) {
        try {
            URL url = new URL(persistentFileURI);
            InputStream in = url.openStream();
            Reader reader = new InputStreamReader(in, "UTF-8");
            return IOUtils.toString(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
