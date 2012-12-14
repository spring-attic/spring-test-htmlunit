/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.springframework.test.web.htmlunit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.geb.GebSpecTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.webdriver.MockMvcHtmlUnitDriver;
import org.springframework.util.Assert;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebConnection;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;

/**
 * <p>
 * Allows {@link MockMvc} to transform a {@link WebRequest} into a {@link WebResponse}. This is the core integration
 * with <a href="http://htmlunit.sourceforge.net/">HTML Unit</a>.
 * </p>
 * <p>
 * Example usage can be seen below:
 * </p>
 *
 * <pre>
 * MockMvc mockMvc = ...
 * MockMvcWebConnection webConnection = new MockMvcWebConnection(mockMvc);
 * WebClient webClient = new WebClient();
 * webClient.setWebConnection(webConnection);
 *
 * ... use webClient as normal ...
 * </pre>
 * <p>
 * Currently the WebConnection assumes that the first path segment is the context root of the application. For example,
 * the URL http://localhost/context/test/this would use /context as the context root.
 * </p>
 * @author Rob Winch
 * @see MockMvcHtmlUnitDriver
 * @see GebSpecTestExecutionListener
 */
public final class MockMvcWebConnection implements WebConnection {
	private final CookieManager cookieManager;

	private final Map<String, MockHttpSession> sessions = new HashMap<String, MockHttpSession>();

	private final MockMvc mockMvc;

	public MockMvcWebConnection(MockMvc mockMvc) {
		Assert.notNull(mockMvc, "mockMvc cannot be null");
		this.mockMvc = mockMvc;
		this.cookieManager = new CookieManager();
	}

	public WebResponse getResponse(WebRequest webRequest) throws IOException {
		long startTime = System.currentTimeMillis();
		HtmlUnitRequestBuilder requestBuilder = new HtmlUnitRequestBuilder(sessions, cookieManager, webRequest);

		ResultActions resultActions;
		try {
			resultActions = mockMvc.perform(requestBuilder);
		}
		catch (Exception e) {
			throw (IOException) new IOException(e.getMessage()).initCause(e);
		}

		MockHttpServletResponse httpServletResponse = resultActions.andReturn().getResponse();

		return new MockWebResponseBuilder(startTime, webRequest, httpServletResponse).build();
	}
}