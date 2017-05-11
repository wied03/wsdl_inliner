package com.bswtechconsulting.wsdl

import org.apache.cxf.tools.java2ws.JavaToWS
import org.apache.cxf.tools.wsdlto.WSDLToJava
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder

import javax.jws.WebService
import javax.tools.DiagnosticCollector
import javax.tools.ToolProvider

class Inliner {
    // will return the name of the service and port since CXF might rename them from the original WSDL
    static WsdlData inline(File inputWsdl, File outputWsdl) {
        File tempDir = File.createTempDir()
        try {
            tempDir.with {
                WSDLToJava.main('-d',
                                absolutePath,
                                inputWsdl.absolutePath)
                def filenames = new FileNameFinder().getFileNames(absolutePath, '**/*.java')
                // javatows needs compiled code to re-generate the WSDL
                compileCode filenames
                def klasses = getServiceClasses absoluteFile
                assert klasses.size() == 1
                def klass = klasses[0]
                // inlines schema by default
                outputWsdl.parentFile.mkdirs()
                JavaToWS.main('-wsdl',
                              '-cp',
                              absolutePath,
                              '-o',
                              outputWsdl.absolutePath,
                              klass.name)
                WebService annotation = klass.getAnnotation(WebService)
                def name = annotation.name()
                // this is the convention that JavaToWS will use
                new WsdlData(serviceName: "${name}Service",
                             portName: "${name}Port")
            }
        }
        finally {
            tempDir.deleteDir()
        }
    }

    private static Set<Class> getServiceClasses(File classPath) {
        URLClassLoader loader = ClassLoader.systemClassLoader as URLClassLoader
        def classpathUrl = classPath.toURI().toURL()
        // TODO: Find a way to get reflections to take in a new classloader so we don't have to use a protected method
        loader.addURL(classpathUrl)
        def config = new ConfigurationBuilder().addUrls(classpathUrl)
        new Reflections(config).getTypesAnnotatedWith(WebService)
    }

    private static compileCode(List<String> filenames) {
        def diagnostics = new DiagnosticCollector()
        def compiler = ToolProvider.systemJavaCompiler
        def manager = compiler.getStandardFileManager(diagnostics, null, null)
        def sources = manager.getJavaFileObjectsFromStrings(filenames)
        def compileTask = compiler.getTask(null, manager, diagnostics, null, null, sources)
        if (!compileTask.call()) {
            def messages = diagnostics.diagnostics.collect { d ->
                "${d.getMessage(null)} ${d.lineNumber} ${d.source.name}"
            }
            throw new Exception("Unable to compile CXF generated code! ${messages}")
        }
    }
}
