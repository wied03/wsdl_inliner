package com.bswtechconsulting.wsdl

import org.junit.Test

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.is
import static org.junit.Assert.assertThat

class InlinerTest {
    @Test
    void testInline() {
        // arrange
        def tmpDir = new File('build/tmp')
        if (tmpDir.exists()) {
            tmpDir.deleteDir()
        }

        // act
        def result = Inliner.inline new File('src/test/resources/input.wsdl'),
                                    new File('build/tmp/output.wsdl')

        // assert
        def expected = new File('src/test/resources/expectedOutput.wsdl').text
        def outputFile = new File('build/tmp/output.wsdl')
        assert outputFile.exists()
        assertThat outputFile.text,
                   is(equalTo(expected))
        assertThat result.portName,
                   is(equalTo('newPort'))
        assertThat result.serviceName,
                   is(equalTo('newService'))
    }
}
