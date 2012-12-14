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
package org.springframework.test.web.webdriver;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.test.web.htmlunit.MockMvcWebConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebConnection;

/**
 * <p>
 * Allows running tests off line by utilizing Spring's {@link MockMvc} to bridge between a request and a response. By
 * doing this we are able to avoid making any HTTP calls when our tests are running. This implies we do not need to
 * start a web container to run our tests.
 * </p>
 * <p>
 * Example usage can be seen below:
 * </p>
 * <h2>Instantiate with WebApplicationContext</h2>
 *
 * <pre>
 * WebApplicationContext context = ...
 * MockHtmlUnitDriver driver = new MockHtmlUnitDriver(context, true);
 *
 * ... use driver as you would HtmlUnitDriver ...
 * </pre>
 *
 * <h2>Instantiate with MockMvc</h2>
 *
 * <pre>
 * MockMvc mockMvc = ...
 * MockHtmlUnitDriver driver = new MockHtmlUnitDriver(mockMvc, true);
 *
 * ... use driver as you would HtmlUnitDriver ...
 * </pre>
 * <p>
 * The only reason for this class is in order to customize the {@link WebConnection} used by {@link HtmlUnitDriver}. We
 * cannot use a simple bean methods since there are no accessor methods for {@link WebClient} or the {@link WebClient}'s
 * underlying {@link WebConnection}. The only means to update the {@link WebConnection} is to override the
 * {@link #modifyWebClient(WebClient)} method. Hence for the existence of this class.
 * </p>
 *
 * @author Rob Winch
 * @see MockMvc
 * @see MockMvcWebConnection
 *
 */
public final class MockMvcHtmlUnitDriver extends HtmlUnitDriver {
	private WebClient webClient;

	public MockMvcHtmlUnitDriver(WebApplicationContext webContext) {
		setWebContext(webContext);
	}

	public MockMvcHtmlUnitDriver(WebApplicationContext webContext, boolean enableJavascript) {
		super(enableJavascript);
		setWebContext(webContext);
	}

	public MockMvcHtmlUnitDriver(WebApplicationContext webContext, BrowserVersion version) {
		super(version);
		setWebContext(webContext);
	}

	public MockMvcHtmlUnitDriver(WebApplicationContext webContext, Capabilities capabilities) {
		super(capabilities);
		setWebContext(webContext);
	}

	public MockMvcHtmlUnitDriver(MockMvc mockMvc) {
		setMockMvc(mockMvc);
	}

	public MockMvcHtmlUnitDriver(MockMvc mockMvc, boolean enableJavascript) {
		super(enableJavascript);
		setMockMvc(mockMvc);
	}

	public MockMvcHtmlUnitDriver(MockMvc mockMvc, BrowserVersion version) {
		super(version);
		setMockMvc(mockMvc);
	}

	public MockMvcHtmlUnitDriver(MockMvc mockMvc, Capabilities capabilities) {
		super(capabilities);
		setMockMvc(mockMvc);
	}

	@Override
	protected WebClient modifyWebClient(WebClient client) {
		webClient = super.modifyWebClient(client);
		return webClient;
	}

	private void setWebContext(WebApplicationContext webContext) {
		Assert.notNull(webContext, "webContext cannot be null");
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webContext).build();
		setMockMvc(mockMvc);
	}

	private void setMockMvc(MockMvc mockMvc) {
		Assert.notNull(mockMvc, "mockMvc cannot be null");
		webClient.setWebConnection(new MockMvcWebConnection(mockMvc));
	}
}
