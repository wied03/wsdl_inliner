import org.apache.cxf.tools.java2ws.JavaToWS
import org.apache.cxf.tools.wsdlto.WSDLToJava

/**
 * Created by brady on 2/17/17.
 */
class Inliner {
    static void main(String[] args) {
        'rm -rf tmp'.execute().waitFor()
        'rm -rf stuff.wsdl'.execute().waitFor()
        // TODO: Clean out the tmp directory
        WSDLToJava.main('-d', 'tmp',
                        "/Users/brady/code/eclipse_workspaces/mule/studio621/soaptesting/src/main/wsdl/test.wsdl")
        def filenames = new FileNameFinder().getFileNames('tmp', '**/*.java')
        // javatows needs compiled code to re-generate the WSDL
        // TODO: Log this instead
        println "compiling ${filenames.join ' '}"
        "javac ${filenames}".execute().waitFor()
        // TODO: Have to find out what the interface is automatically (grep files for the annotation)
        // inlines schema by default
        // TODO: Stuff.wsdl should go into a generated resources directory using the Maven Mojo
        JavaToWS.main('-wsdl', '-cp', 'tmp', '-o', 'stuff.wsdl', "com.quintiles.services.soaptesting.v1.SOAPTest")
    }
}
