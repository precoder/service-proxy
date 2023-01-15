/* Copyright 2012 predic8 GmbH, www.predic8.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. */

package com.predic8.membrane.examples.tests;

import static com.predic8.membrane.test.AssertUtils.assertContains;
import static com.predic8.membrane.test.AssertUtils.postAndAssert200;
import static java.lang.Thread.sleep;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.readFileToString;

import java.io.File;
import java.io.IOException;
import java.nio.charset.*;

import com.predic8.membrane.examples.util.*;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import com.predic8.membrane.examples.util.Process2;

public class CBRTest extends DistributionExtractingTestcase {

	@Override
	protected String getExampleDirName() {
		return "cbr";
	}

	@Test
	public void test() throws Exception {

		try(Process2 ignored = startServiceProxyScript()) {
			sleep(1000);
			assertContains("Normal order received.", postAndAssert200("http://localhost:2000", readFile("order.xml")));
			assertContains("Express order received.", postAndAssert200("http://localhost:2000", readFile("express.xml")));
			assertContains("Order contains import items.", postAndAssert200("http://localhost:2000", readFile("import.xml")));
		}
	}

}
