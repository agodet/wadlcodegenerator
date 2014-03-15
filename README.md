wadlcodegenerator
=================

Generate services and objects from a WADL in any language

## READ THIS BEFORE ALL :
This tools is provided to generates sources for all languages.
XSD is fully supported.
But AT THIS TIME, it will generate code ONLY for OBJECTIVEC/IOS and JAVA/ANDROID.
Moreover, the tool doesn't support all of wadl specification. It will work ONLY for simple post method with one json request and one json response as follow :

    <application xmlns="http://wadl.dev.java.net/2009/02"
                 xmlns:ns="http://www.setareh.org/test">
        <grammars>
            <include href="bo.xsd"/>
        </grammars>
        <resources base="http://localhost:8080/api">
            <resource path="/test/">
                <resource path="resource/">
                    <method name="POST">
                        <request>
                            <representation mediaType="application/json" element="ns:requestParam"/>
                        </request>
                        <response>
                            <representation mediaType="application/json" element="ns:responseParam"/>
                        </response>
                    </method>
                </resource>
            </resource>
        </resources>
    </application>

## Command line use :

org.setareh.wadl.codegen.WadlCodeGenerator FILEPATH PACKAGE_NAME OUTPUT_DIRECTORY MODULE_NAME

FILEPATH : path of the file add "file:" before the local path
PACKAGE_NAME : name of the package for java or prefix for objectiveC
OUTPUT_DIRECTORY : where you want to generate files
MODULE_NAME : name of the module use to generate code. Actually "ANDROID" or "IOS"

## Maven plugin use :

    > mvn clean install (or deploy)

Then create another project with a single pom file like this (change UPPERCASE names to fit your needs) :

    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>

        <groupId>MY_GROUP_ID</groupId>
        <artifactId>restApi</artifactId>
        <version>1.0-SNAPSHOT</version>

        <build>
            <plugins>
                <plugin>
                    <groupId>org.setareh</groupId>
                    <artifactId>wadl-codegen-maven-plugin</artifactId>
                    <version>1.9</version>
                    <configuration>
                        <wadlFile>file:${basedir}/MY_WADL.wadl</wadlFile>
                        <packageName>MY_PACKAGE_NAME</packageName>
                        <prefixName>MY_PREFIX_NAME</prefixName>
                    </configuration>
                    <executions>
                        <execution>
                            <id>wadlto</id>
                            <goals>
                                <goal>wadlto</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>
    </project>

Then use maven to generate sources :
    > mvn clean compile

In the target directory, there is two jars, one for ANDROID and one for IOS.

## This tools is based on :
MXJC : https://github.com/bulldog2011/mxjc thanks to Bulldog
WADLTO : org.apache.cxf.tools.wadlto
JAXB : https://jaxb.java.net
FREEMARKER : http://freemarker.org







