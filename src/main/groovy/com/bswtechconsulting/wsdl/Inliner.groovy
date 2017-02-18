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
            println "using tmp dir ${absolutePath}"
            deleteOnExit()
            WSDLToJava.main('-d',
                            absolutePath,
                            inputWsdl.absolutePath)
            def filenames = new FileNameFinder().getFileNames(absolutePath, '**/*.java')
            // javatows needs compiled code to re-generate the WSDL
            "javac ${filenames}".execute().waitFor()
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
