package com.bswtechconsulting.wsdl

import org.junit.Before
import org.junit.Test

import static org.hamcrest.Matchers.containsString
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.is
import static org.junit.Assert.assertThat

class InlinerTest {
    File tmpDir

    @Before
    void cleanup() {
        tmpDir = new File('build/tmp')
        if (tmpDir.exists()) {
            assert tmpDir.deleteDir()
        }
    }

    @Test
    void testInline() {
        // arrange

        // act
        def result = new Inliner().inline new File('src/test/resources/input.wsdl'),
                                          new File('build/tmp/output.wsdl')

        // assert
        def expected = new File('src/test/resources/expectedOutput.wsdl').text
        def outputFile = new File('build/tmp/output.wsdl')
        assert outputFile.exists()
        assertThat outputFile.text,
                   is(equalTo(expected))
        assertThat result.portName,
                   is(equalTo('SOAPTestPort'))
        assertThat result.serviceName,
                   is(equalTo('SOAPTestService'))
    }

    @Test
    void bigEnum() {
        // arrange

        // act
        def result = new Inliner().inline new File('src/test/resources/bigenum/input.wsdl'),
                                          new File('build/tmp/output.wsdl')

        // assert
        def outputFile = new File('build/tmp/output.wsdl')
        assert outputFile.exists()
        assertThat outputFile.text,
                   is(containsString('<xs:enumeration value="item361"/>'))
        assertThat result.portName,
                   is(equalTo('SOAPTestPort'))
        assertThat result.serviceName,
                   is(equalTo('SOAPTestService'))
    }

}
