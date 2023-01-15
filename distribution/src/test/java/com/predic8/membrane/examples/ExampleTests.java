/* Copyright 2009, 2012 predic8 GmbH, www.predic8.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. */
package com.predic8.membrane.examples;

import com.predic8.membrane.examples.config.*;
import com.predic8.membrane.examples.env.*;
import com.predic8.membrane.examples.tests.*;
import com.predic8.membrane.examples.tests.loadbalancing.*;
import com.predic8.membrane.examples.tests.ssl.*;
import com.predic8.membrane.examples.tests.validation.FormValidationTest;
import com.predic8.membrane.examples.tests.validation.JSONSchemaValidationTest;
import com.predic8.membrane.examples.tests.validation.XMLValidationTest;
import com.predic8.membrane.examples.tests.versioning.RoutingTest;
import com.predic8.membrane.examples.tests.versioning.XsltExampleTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * If some Java processes are not cleaned up after a test run you can remove them with:
 * <p>
 * Mac OS:
 * sudo kill -9 $(lsof -i -n -P | grep LISTEN | grep "2000" | tr -s " " | cut -f 2 -d " ")
 */
@Suite
@SelectClasses({
        AntInPath.class,
        ConsistentVersionNumbers.class,
        DefaultConfigTest.class,
        HelpLinkExistenceTest.class,
        JavaLicenseInfoTest.class,

        ACLTest.class,
        BasicAuthTest.class,
        CBRTest.class,
        FileExchangeStoreTest.class,
        GroovyTest.class,
        Loadbalancing_1_static_Test.class,
        Loadbalancing_3_client_Test.class,
        Loadbalancing_5_multiple_Test.class,
        Loadbalancing_4_xml_session_Test.class,
        LoggingCSVTest.class,
        LoggingJDBCTest.class,
        LoggingTest.class,
        LoginTest.class,
//        QuickstartRESTTest.class,
//        QuickstartSOAPTest.class,
        REST2SOAPTest.class,
        REST2SOAPJSONTest.class,
        RewriterTest.class,
        SSLServerApiWithTlsTest.class,
        ToBackendTest.class,
        ThrottleTest.class,
        XSLTTest.class,

        FormValidationTest.class,
        JSONSchemaValidationTest.class,
        XMLValidationTest.class,

        CustomInterceptorTest.class,
        StaxExampleInterceptorTest.class,
        AddSoapHeaderTest.class,
        BasicXmlInterceptorTest.class,
        Xml2JsonTest.class,
        Json2XmlTest.class,
        RoutingTest.class,
        XsltExampleTest.class,
        RoutingTest.class,
        OAuth2ApiTest.class
})
public class ExampleTests {
}
