package com.bswtechconsulting.wsdl

import org.apache.cxf.tools.java2ws.JavaToWS
import org.apache.cxf.tools.wsdlto.WSDLToJava
import org.reflections.Reflections
import org.reflections.util.ConfigurationBuilder

import javax.jws.WebService
import javax.tools.DiagnosticCollector
import javax.tools.ToolProvider

/**
 * Created by brady on 2/17/17.
 */
class Inliner {
    // will return the name of the service and port since CXF might rename them from the original WSDL
    static WsdlData inline(File inputWsdl, File outputWsdl) {
        File.createTempDir().with {
            deleteOnExit()
            WSDLToJava.main('-d',
                            absolutePath,
                            inputWsdl.absolutePath)
            def filenames = new FileNameFinder().getFileNames(absolutePath, '**/*.java')
            // javatows needs compiled code to re-generate the WSDL
            compileCode filenames
            def klasses = getServiceClasses absoluteFile
            assert klasses.size() == 1
            def klass = klasses[0]
            def webServiceAnnotation = klass.annotations.find { a ->
                a.annotationType() == WebService
            }
            // TODO: Get port/service/etc. values from annotation
            // inlines schema by default
            outputWsdl.parentFile.mkdirs()
            JavaToWS.main('-wsdl',
                          '-cp',
                          absolutePath,
                          '-o',
                          outputWsdl.absolutePath,
                          klass.name)
        }
    }

    private static getServiceClasses(File classPath) {
        URLClassLoader loader = ClassLoader.systemClassLoader
        def classpathUrl = classPath.toURL()
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
