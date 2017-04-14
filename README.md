# WSDL Inliner

[![Build Status](http://img.shields.io/travis/wied03/wsdl_inliner/master.svg?style=flat)](http://travis-ci.org/wied03/wsdl_inliner)

Inlines WSDLs (meaning schemas, etc.) by using CXF to go from WSDL to Java and then back to WSDL. Can be useful for tools that don't work properly with external schemas (some ESBs, CICS, etc.)

# Building/installing

This will install a Maven artifact into your local repository (have not uploaded this to Maven central yet).

```
gradlew clean install
```

# Using

(Groovy example)

```groovy
import com.bswtechconsulting.wsdl.Inliner
def inliner = new Inliner()
def wsdlData = inliner.inline(new File('input.wsdl'), new File('output.wsdl'))
// output.wsdl will contain the inlined WSDL
// wsdlData is a com.bswtechconsulting.wsdl.WsdlData object with some information about service and port name of the inlined WSDL since CXF may tweak them
```

# License
Copyright (c) 2016, BSW Technology Consulting LLC
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

