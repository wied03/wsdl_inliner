package com.bswtechconsulting.wsdl

import org.apache.cxf.tools.java2ws.JavaToWS
import org.apache.cxf.tools.wsdlto.WSDLToJava

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
            // TODO: Have to find out what the interface is automatically (reflections??)
            // inlines schema by default
            outputWsdl.parentFile.mkdirs()
            JavaToWS.main('-wsdl',
                          '-cp',
                          absolutePath,
                          '-o',
                          outputWsdl.absolutePath,
                          "com.quintiles.services.soaptesting.v1.SOAPTest")
        }
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
