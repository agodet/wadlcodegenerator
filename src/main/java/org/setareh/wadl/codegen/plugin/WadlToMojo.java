package org.setareh.wadl.codegen.plugin;

import org.setareh.wadl.codegen.WadlCodeGenerator;
import org.setareh.wadl.codegen.model.CGConfig;
import org.setareh.wadl.codegen.module.ModuleName;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.io.*;
import java.net.URISyntaxException;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Generate Code From a wadl
 *
 */
@Mojo( name = "wadlto", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class WadlToMojo extends AbstractMojo
{

    /**
     * The greeting to display.
     */
    @Parameter( property = "wadlto.file", defaultValue = "file:/${basedir}/src/main/resources/wadl/main.wadl" )
    private String wadlFile;

    @Parameter( property = "wadlto.package.name", defaultValue = "com" , required = false)
    private String packageName;

    @Parameter( property = "wadlto.prefix.name", defaultValue = "WT" , required = false)
    private String prefixName;

    @Parameter(property = "wadl.sourceRoot", defaultValue = "${project.build.directory}/generated-sources/wadlto")
    File sourceRoot;

    @Parameter(property = "project")
    private MavenProject project;

    @Component
    private MavenProjectHelper projectHelper;


    public void execute() throws MojoExecutionException
    {
        CGConfig cgConfig = new CGConfig();
        cgConfig.wadlPath = wadlFile;
        cgConfig.packageName = packageName;
        cgConfig.prefix = prefixName;

        if(!sourceRoot.exists())
        {
            sourceRoot.mkdirs();
        }

        WadlCodeGenerator wadlCodeGenerator = new WadlCodeGenerator();

        for(ModuleName module : ModuleName.values())
        {
            cgConfig.module = module;
            cgConfig.targetDir = sourceRoot.getPath() + "/" + module.name();

            try {
                wadlCodeGenerator.generateSchemaCodeAndInfo(cgConfig);
            } catch (URISyntaxException e) {
                throw new MojoExecutionException("error",e);
            } catch (IOException e) {
                throw new MojoExecutionException("error",e);
            }


            try {
                String jarFileName = project.getArtifactId() + "-" + module.name() + "-" + project.getVersion() + ".jar";
                // jar it
                File file = new File(project.getBuild().getDirectory(), jarFileName);
                makeJar(cgConfig.targetDir, file);

                projectHelper.attachArtifact(this.project, ".jar", module.name(), file);
            } catch (Exception e) {
                throw new MojoExecutionException("error",e);
            }
        }
    }

    public void makeJar(String directoryToJar, File jarFilePath) throws IOException
    {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        JarOutputStream target = new JarOutputStream(new FileOutputStream(jarFilePath), manifest);
        add(directoryToJar, new File(directoryToJar), target);
        target.close();
    }

    private void add(String targetDir, File source, JarOutputStream target) throws IOException
    {
        BufferedInputStream in = null;
        try
        {
            if (source.isDirectory())
            {
                String name = source.getPath().replace("\\", "/").replace(targetDir.replace("\\", "/"),"");
                if (!name.isEmpty())
                {
                    if (!name.endsWith("/"))
                        name += "/";
                    JarEntry entry = new JarEntry(name);
                    entry.setTime(source.lastModified());
                    target.putNextEntry(entry);
                    target.closeEntry();
                }
                for (File nestedFile: source.listFiles())
                    add(targetDir, nestedFile, target);
                return;
            }

            String name = source.getPath().replace("\\", "/").replace(targetDir.replace("\\", "/"),"");
            JarEntry entry = new JarEntry(name);
            entry.setTime(source.lastModified());
            target.putNextEntry(entry);
            in = new BufferedInputStream(new FileInputStream(source));

            byte[] buffer = new byte[1024];
            while (true)
            {
                int count = in.read(buffer);
                if (count == -1)
                    break;
                target.write(buffer, 0, count);
            }
            target.closeEntry();
        }
        finally
        {
            if (in != null)
                in.close();
        }
    }
}