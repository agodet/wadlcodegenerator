package org.setareh.wadl.codegen;

import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.xjc.ModelLoader;
import com.sun.tools.xjc.api.ErrorListener;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.util.ErrorReceiverFilter;
import org.setareh.wadl.codegen.builder.ModelBuilder;
import org.setareh.wadl.codegen.generated.bo.Application;
import org.setareh.wadl.codegen.generated.bo.Include;
import org.setareh.wadl.codegen.model.CGConfig;
import org.setareh.wadl.codegen.model.CGModel;
import org.setareh.wadl.codegen.model.CGServices;
import org.setareh.wadl.codegen.utils.IOUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import javax.xml.bind.JAXB;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: alexandre_godet
 * @since: MXXX
 */
public class WadlCodeGenerator {

    public static void main(String args[])
    {
        try
        {
            WadlCodeGenerator generator = new WadlCodeGenerator();

            generator.generateSchemaCodeAndInfo(CGConfig.createCGConfigFromArgs(args));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void generateSchemaCodeAndInfo(CGConfig cgConfig) throws URISyntaxException, IOException {

        URI wadlUri = new URI(cgConfig.wadlPath);

        System.out.println("Reading wadl from URI : " + wadlUri);
        org.setareh.wadl.codegen.generated.bo.Application application = JAXB.unmarshal(new StringReader(readWadl(wadlUri.toString())), org.setareh.wadl.codegen.generated.bo.Application.class);

        Map<String, InputStream> schemaMap = getSchemaElements(application, wadlUri.toString());

        if (schemaMap != null && !schemaMap.isEmpty()) {
            com.sun.tools.xjc.api.SchemaCompiler shemaCompiler = com.sun.tools.xjc.api.XJC.createSchemaCompiler();
            shemaCompiler.setErrorListener(new ErrorListener() {
                @Override
                public void error(SAXParseException e) {
                    throw new RuntimeException(e);
                }

                @Override
                public void fatalError(SAXParseException e) {
                    throw new RuntimeException(e);
                }

                @Override
                public void warning(SAXParseException e) {
                    throw new RuntimeException(e);
                }

                @Override
                public void info(SAXParseException e) {
                    throw new RuntimeException(e);
                }
            });
            //shemaCompiler.setEntityResolver(OASISCatalogManager.getCatalogManager(bus).getEntityResolver());
            addSchemas(schemaMap, shemaCompiler);
        /*for (InputSource is : bindingFiles) {
            shemaCompiler.getOptions().addBindFile(is);
        }*/


            ErrorReceiver receiver = new ErrorReceiverFilter() {
                @Override
                public void error(SAXParseException exception) {
                    throw new RuntimeException(exception);
                }

                @Override
                public void fatalError(SAXParseException exception) {
                    throw new RuntimeException(exception);
                }
            };

            Model model = ModelLoader.load(shemaCompiler.getOptions(), new com.sun.codemodel.JCodeModel(), receiver);
            Outline outline = model.generateCode(shemaCompiler.getOptions(),receiver);

            // create target dir
            File targetDir = new File(cgConfig.targetDir);
            if(!targetDir.exists())
            {
                targetDir.mkdirs();
            }

            // GENERATE MODEL
            CGModel cgModel = ModelBuilder.buildCodeGenModel(outline, cgConfig);

            //GENERATE SERVICES
            CGServices cgServices = ModelBuilder.buildServicesGenModel(application);

            CodeGenerator.generateModel(cgModel, cgConfig);
            CodeGenerator.generateServices(cgServices, cgConfig);
            CodeGenerator.generateProjectModel(cgConfig);
        }
    }

    protected String readWadl(String wadlURI) {
        try {
            URL url = new URL(wadlURI);
            InputStream in = url.openStream();
            Reader reader = new InputStreamReader(in, "UTF-8");
            return IOUtils.toString(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, InputStream> getSchemaElements(Application application, String wadlPath) {

        Map<String, InputStream> schemaMap = new HashMap<String, InputStream>(1);

        /*List<Element> schemasEls = DOMUtils.getChildrenWithName(grammarEls.get(0),
                XmlSchemaConstants.XSD_NAMESPACE_URI, "schema");
        for (int i = 0; i < schemasEls.size(); i++) {
            String systemId = wadlPath.toString();
            if (schemasEls.size() > 1) {
                systemId += "#grammar" + (i + 1);
            }
            schemas.add(createSchemaInfo(schemasEls.get(i), systemId));
        }*/


        List<Include> includes = application.getGrammars().getInclude();

        int i = 0;

        for(Include include : includes)
        {
            String href = include.getHref();

            String schemaURI = null; //resolveLocationWithCatalog(href);
            if (schemaURI == null) {
                if (!URI.create(href).isAbsolute() && wadlPath != null) {
                    String baseWadlPath = getBasePath(wadlPath);
                    if  (!href.startsWith("/") && !href.contains("..")) {
                        schemaURI = baseWadlPath + href;
                    } else {
                        try {
                            schemaURI = new URL(new URL(baseWadlPath), href).toString();
                        } catch (Exception ex) {
                            schemaURI = URI.create(baseWadlPath).resolve(href).toString();
                        }
                    }
                } else {
                    schemaURI = href;
                }
            }

            if(schemaURI == null)
            {
                schemaURI = Integer.toString(i);
            }

            schemaMap.put(schemaURI, readIncludedDocument(schemaURI));
        }
        return schemaMap;
    }

    private String getBasePath(String docPath) {
        int lastSep = docPath.lastIndexOf("/");
        return lastSep != -1 ? docPath.substring(0, lastSep + 1) : docPath;
    }

    private InputStream readIncludedDocument(String href) {

        try {
            InputStream is = null;
            if (href.startsWith("file")) {
                is = new FileInputStream(href.replace("file:",""));
                //is = ResourceUtils.getResourceStream(href, bus);
            }
            else if(href.startsWith("http"))
            {
                is = URI.create(href).toURL().openStream();
            }
            return is;
        } catch (Exception ex) {
            throw new RuntimeException("Resource " + href + " can not be read");
        }
    }

    private void addSchemas(Map<String, InputStream> schemas, SchemaCompiler compiler) {
        // handle package customizations first
        /*for (int i = 0; i < schemaPackageFiles.size(); i++) {
            compiler.parseSchema(schemaPackageFiles.get(i));
        }*/

        for(String schemaKey : schemas.keySet())
        {
            String key = schemaKey;
            // TODO: CXF code should have a better solution somewhere, we'll get back to it
            // when addressing the issue of retrieving WADLs with included schemas
            /*if (key.startsWith("classpath:")) {
                String resource = key.substring(10);
                URL url = null; //ResourceUtils.getClasspathResourceURL(resource,SourceGenerator2.class, bus);
                if (url != null) {
                    try {
                        key = url.toURI().toString();
                    } catch (Exception ex) {
                        // won't happen
                    }
                }
            }*/
            InputSource is = new InputSource((InputStream) null);
            is.setSystemId(key);
            is.setPublicId(key);
            compiler.getOptions().addGrammar(is);
        }
    }
}
