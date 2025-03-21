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
package com.predic8.membrane.core.interceptor.rewrite;

import com.predic8.membrane.core.*;
import com.predic8.membrane.core.http.Header;
import com.predic8.membrane.core.http.*;
import com.predic8.membrane.core.interceptor.rewrite.RewriteInterceptor.*;
import com.predic8.membrane.core.proxies.*;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.http.params.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class RewriteInterceptorIntegrationTest {

	private static HttpRouter router;

    @BeforeAll
	public static void setUp() throws Exception {

        RewriteInterceptor interceptor = new RewriteInterceptor();
		interceptor.getMappings().add(new Mapping("/blz-service\\?wsdl", "/axis2/services/BLZService?wsdl", null));

		ServiceProxy proxy = new ServiceProxy(new ServiceProxyKey("localhost", "POST", ".*", 3006), "thomas-bayer.com", 80);
		proxy.getInterceptors().add(interceptor);

		router = new HttpRouter();
		router.getRuleManager().addProxyAndOpenPortIfNew(proxy);
	}

	@AfterAll
	public static void tearDown() {
		router.shutdown();
	}

	@Test
	public void testRewriting() throws Exception {
		HttpClient client = new HttpClient();
		client.getParams().setParameter(HttpProtocolParams.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		int status = client.executeMethod(getPostMethod());

		assertEquals(200, status);
	}

	private PostMethod getPostMethod() {
		PostMethod post = new PostMethod("http://localhost:3006/blz-service?wsdl");
		post.setRequestEntity(new InputStreamRequestEntity(this.getClass().getResourceAsStream("/getBank.xml")));
		post.setRequestHeader(Header.CONTENT_TYPE, MimeType.TEXT_XML_UTF8);
		post.setRequestHeader(Header.SOAP_ACTION, "");

		return post;
	}


}
