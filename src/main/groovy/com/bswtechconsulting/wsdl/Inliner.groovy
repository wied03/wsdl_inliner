package com.bswtechconsulting.wsdl

import org.apache.cxf.tools.java2ws.JavaToWS
import org.apache.cxf.tools.wsdlto.WSDLToJava

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
            // TODO: Windows paths/javac location
            def command = "javac ${filenames.join(' ')}"
            def result = command.execute()
            def exitValue = result.waitFor()
            if (exitValue != 0) {
                throw new Exception("Compilation error ${result.text}")
            }
            // TODO: Have to find out what the interface is automatically (grep files for the annotation)
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
}
