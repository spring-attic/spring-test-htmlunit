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
package org.springframework.test.web.servlet.htmlunit;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.htmlunit.geb.GebSpecTestExecutionListener;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriver;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.util.Assert;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebConnection;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

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
	private final List<RequestPostProcessor> postProcessors;

	private final CookieManager cookieManager;

	private final Map<String, MockHttpSession> sessions = new HashMap<String, MockHttpSession>();

	private final MockMvc mockMvc;

	public MockMvcWebConnection(MockMvc mockMvc) {
		this(mockMvc, defaultPostProcessors());
	}

	public MockMvcWebConnection(MockMvc mockMvc, RequestPostProcessor... postProcessors) {
		this(mockMvc, Arrays.asList(postProcessors));
	}

	public MockMvcWebConnection(MockMvc mockMvc, List<RequestPostProcessor> postProcessors) {
		Assert.notNull(mockMvc, "mockMvc cannot be null");
		Assert.notNull(postProcessors, "postProcessors cannot be null");
		this.mockMvc = mockMvc;
		this.cookieManager = new CookieManager();
		this.postProcessors = postProcessors;
	}

	public WebResponse getResponse(WebRequest webRequest) throws IOException {
		long startTime = System.currentTimeMillis();
		RequestBuilder requestBuilder = new HtmlUnitRequestBuilder(postProcessors, sessions, cookieManager, webRequest);

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

	private static List<RequestPostProcessor> defaultPostProcessors() {
		ClassLoader classLoader = MockMvcWebConnection.class.getClassLoader();
		if(ClassUtils.isPresent(SECURITY_PP_CLASS_NAME, classLoader)) {
			Class<?> clazz = ClassUtils.resolveClassName(SECURITY_PP_CLASS_NAME, classLoader);
			Method method = ReflectionUtils.findMethod(clazz, "testSecurityContext");
			return Arrays.asList((RequestPostProcessor) ReflectionUtils.invokeMethod(method, null));
		}
		return Collections.emptyList();
	}

	private static final String SECURITY_PP_CLASS_NAME = "org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors";
}