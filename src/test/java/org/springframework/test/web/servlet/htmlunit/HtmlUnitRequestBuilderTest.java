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

import static org.fest.assertions.Assertions.assertThat;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.htmlunit.HtmlUnitRequestBuilder;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import org.springframework.util.AntPathMatcher;

/**
 *
 * @author Rob Winch
 *
 */
public class HtmlUnitRequestBuilderTest {
	private WebRequest webRequest;

	private ServletContext servletContext;

	private Map<String, MockHttpSession> sessions;

	private CookieManager cookieManager;

	private HtmlUnitRequestBuilder requestBuilder;

	@Before
	public void setUp() throws Exception {
		sessions = new HashMap<String, MockHttpSession>();
		cookieManager = new CookieManager();
		webRequest = new WebRequest(new URL("http://example.com:80/test/this/here"));
		webRequest.setHttpMethod(HttpMethod.GET);
		webRequest.setRequestParameters(new ArrayList<NameValuePair>());
		requestBuilder = new HtmlUnitRequestBuilder(sessions, cookieManager, webRequest);
		servletContext = new MockServletContext();
	}

	// --- constructor

	@Test(expected = IllegalArgumentException.class)
	public void constructorNullSessions() {
		new HtmlUnitRequestBuilder(null, cookieManager, webRequest);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructorNullCookieManager() {
		new HtmlUnitRequestBuilder(sessions, null, webRequest);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructorNullWebRequest() {
		new HtmlUnitRequestBuilder(sessions, cookieManager, null);
	}

	// --- buildRequest

	@Test
	public void buildRequestBasicAuth() {
		String base64Credentials = "dXNlcm5hbWU6cGFzc3dvcmQ=";
		String authzHeaderValue = "Basic: " + base64Credentials;
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(base64Credentials);
		webRequest.setCredentials(credentials);
		webRequest.setAdditionalHeader("Authorization", authzHeaderValue);

		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getAuthType()).isEqualTo("Basic");
		assertThat(actualRequest.getHeader("Authorization")).isEqualTo(authzHeaderValue);
	}

	@Test
	public void buildRequestCharacterEncoding() {
		String charset = "UTF-8";
		webRequest.setCharset(charset);

		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getCharacterEncoding()).isEqualTo(charset);
	}

	@Test
	public void buildRequestDefaultCharacterEncoding() {
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getCharacterEncoding()).isEqualTo("ISO-8859-1");
	}

	@Test
	public void buildRequestContentLength() {
		String content = "some content that has length";
		webRequest.setHttpMethod(HttpMethod.POST);
		webRequest.setRequestBody(content);

		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getContentLength()).isEqualTo(content.length());
	}

	@Test
	public void buildRequestContentType() {
		String contentType = "text/html;charset=UTF-8";
		webRequest.setAdditionalHeader("Content-Type", contentType);

		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getContentType()).isEqualTo(contentType);
		assertThat(actualRequest.getHeader("Content-Type")).isEqualTo(contentType);
	}

	@Test
	public void buildRequestContextPathUsesFirstSegmentByDefault() {
		String contextPath = requestBuilder.buildRequest(servletContext).getContextPath();

		assertThat(contextPath).isEqualTo("/test");
	}

	@Test
	public void buildRequestContextPathUsesNoFirstSegmentWithDefault() throws MalformedURLException {
		webRequest.setUrl(new URL("http://example.com/"));
		String contextPath = requestBuilder.buildRequest(servletContext).getContextPath();

		assertThat(contextPath).isEqualTo("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildRequestContextPathInvalid() {
		requestBuilder.setContextPath("/invalid");

		requestBuilder.buildRequest(servletContext).getContextPath();
	}

	@Test
	public void buildRequestContextPathEmpty() {
		String expected = "";
		requestBuilder.setContextPath(expected);

		String contextPath = requestBuilder.buildRequest(servletContext).getContextPath();

		assertThat(contextPath).isEqualTo(expected);
	}

	@Test
	public void buildRequestContextPathExplicit() {
		String expected = "/test";
		requestBuilder.setContextPath(expected);

		String contextPath = requestBuilder.buildRequest(servletContext).getContextPath();

		assertThat(contextPath).isEqualTo(expected);
	}

	@Test
	public void buildRequestContextPathMulti() {
		String expected = "/test/this";
		requestBuilder.setContextPath(expected);

		String contextPath = requestBuilder.buildRequest(servletContext).getContextPath();

		assertThat(contextPath).isEqualTo(expected);
	}

	@Test
	public void buildRequestCookiesNull() {
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getCookies()).isNull();
	}

	@Test
	public void buildRequestCookiesSingle() {
		webRequest.setAdditionalHeader("Cookie", "name=value");

		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		Cookie[] cookies = actualRequest.getCookies();
		assertThat(cookies.length).isEqualTo(1);
		assertThat(cookies[0].getName()).isEqualTo("name");
		assertThat(cookies[0].getValue()).isEqualTo("value");
	}

	@Test
	public void buildRequestCookiesMulti() {
		webRequest.setAdditionalHeader("Cookie", "name=value; name2=value2");

		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		Cookie[] cookies = actualRequest.getCookies();
		assertThat(cookies.length).isEqualTo(2);
		Cookie cookie = cookies[0];
		assertThat(cookie.getName()).isEqualTo("name");
		assertThat(cookie.getValue()).isEqualTo("value");
		cookie = cookies[1];
		assertThat(cookie.getName()).isEqualTo("name2");
		assertThat(cookie.getValue()).isEqualTo("value2");
	}

	@Test
	public void buildRequestInputStream() throws Exception {
		String content = "some content that has length";
		webRequest.setHttpMethod(HttpMethod.POST);
		webRequest.setRequestBody(content);

		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(IOUtils.toString(actualRequest.getInputStream())).isEqualTo(content);
	}

	@Test
	public void buildRequestLocalAddr() {
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getLocalAddr()).isEqualTo("127.0.0.1");
	}

	@Test
	public void buildRequestLocaleDefault() {
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getLocale()).isEqualTo(Locale.getDefault());
	}

	@Test
	public void buildRequestLocaleDa() {
		webRequest.setAdditionalHeader("Accept-Language", "da");

		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getLocale()).isEqualTo(new Locale("da"));
	}

	@Test
	public void buildRequestLocaleEnGbQ08() {
		webRequest.setAdditionalHeader("Accept-Language", "en-gb;q=0.8");

		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getLocale()).isEqualTo(new Locale("en", "gb", "0.8"));
	}

	@Test
	public void buildRequestLocaleEnQ07() {
		webRequest.setAdditionalHeader("Accept-Language", "en;q=0.7");

		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getLocale()).isEqualTo(new Locale("en", "", "0.7"));
	}

	@Test
	public void buildRequestLocaleEnUs() {
		webRequest.setAdditionalHeader("Accept-Language", "en-US");

		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getLocale()).isEqualTo(Locale.US);
	}

	@Test
	public void buildRequestLocaleFr() {
		webRequest.setAdditionalHeader("Accept-Language", "fr");

		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getLocale()).isEqualTo(Locale.FRENCH);
	}

	@Test
	public void buildRequestLocaleMulti() {
		webRequest.setAdditionalHeader("Accept-Language", "da, en-gb;q=0.8, en;q=0.7");

		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		// FIXME Locale.ENGLISH is due to fact cannot remove it from MockHttpServletRequest
		List<Locale> expected = Arrays.asList(new Locale("da"), new Locale("en", "gb", "0.8"), new Locale("en", "",
				"0.7"), Locale.ENGLISH);
		assertThat(Collections.list(actualRequest.getLocales())).isEqualTo(expected);
	}

	@Test
	public void buildRequestLocaleName() {
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getLocalName()).isEqualTo("localhost");
	}

	@Test
	public void buildRequestLocalPort() {
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getLocalPort()).isEqualTo(80);
	}

	@Test
	public void buildRequestLocalMissing() throws Exception {
		webRequest.setUrl(new URL("http://localhost/test/this"));
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getLocalPort()).isEqualTo(-1);
	}

	@Test
	public void buildRequestMethods() {
		HttpMethod[] methods = HttpMethod.values();

		for (HttpMethod expectedMethod : methods) {
			webRequest.setHttpMethod(expectedMethod);
			String actualMethod = requestBuilder.buildRequest(servletContext).getMethod();
			assertThat(actualMethod).isEqualTo(actualMethod.toString());
		}
	}

	@Test
	public void buildRequestParameterMap() throws Exception {
		setParameter("name", "value");

		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getParameterMap().size()).isEqualTo(1);
		assertThat(actualRequest.getParameter("name")).isEqualTo("value");
	}

	@Test
	public void buildRequestParameterMapQuery() throws Exception {
		webRequest.setUrl(new URL("http://example.com/example/?name=value"));

		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getParameterMap().size()).isEqualTo(1);
		assertThat(actualRequest.getParameter("name")).isEqualTo("value");
	}

	@Test
	public void buildRequestParameterMapQueryMulti() throws Exception {
		webRequest.setUrl(new URL("http://example.com/example/?name=value&param2=value+2"));

		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getParameterMap().size()).isEqualTo(2);
		assertThat(actualRequest.getParameter("name")).isEqualTo("value");
		assertThat(actualRequest.getParameter("param2")).isEqualTo("value 2");
	}

	@Test
	public void buildRequestPathInfo() throws Exception {
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getPathInfo()).isNull();
	}

	@Test
	public void buildRequestPathInfoNull() throws Exception {
		webRequest.setUrl(new URL("http://example.com/example"));

		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getPathInfo()).isNull();
	}

	@Test
	public void buildRequestAndAntPathRequestMatcher() throws Exception {
		webRequest.setUrl(new URL("http://example.com/app/login/authenticate"));

		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		// verify it is going to work with Spring Security's AntPathRequestMatcher
		assertThat(actualRequest.getPathInfo()).isNull();
		assertThat(actualRequest.getServletPath()).isEqualTo("/login/authenticate");
	}

	@Test
	public void buildRequestProtocol() throws Exception {
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getProtocol()).isEqualTo("HTTP/1.1");
	}

	@Test
	public void buildRequestQuery() throws Exception {
		String expectedQuery = "aparam=avalue";
		webRequest.setUrl(new URL("http://example.com/example?" + expectedQuery));

		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getQueryString()).isEqualTo(expectedQuery);
	}

	@Test
	public void buildRequestReader() throws Exception {
		String expectedBody = "request body";
		webRequest.setHttpMethod(HttpMethod.POST);
		webRequest.setRequestBody(expectedBody);

		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(IOUtils.toString(actualRequest.getReader())).isEqualTo(expectedBody);
	}

	@Test
	public void buildRequestRemoteAddr() throws Exception {
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getRemoteAddr()).isEqualTo("127.0.0.1");
	}

	@Test
	public void buildRequestRemoteHost() throws Exception {
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getRemoteAddr()).isEqualTo("127.0.0.1");
	}

	@Test
	public void buildRequestRemotePort() throws Exception {
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getRemotePort()).isEqualTo(80);
	}

	@Test
	public void buildRequestRemotePort8080() throws Exception {
		webRequest.setUrl(new URL("http://example.com:8080/"));

		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getRemotePort()).isEqualTo(8080);
	}

	@Test
	public void buildRequestRemotePort80WithDefault() throws Exception {
		webRequest.setUrl(new URL("http://example.com/"));

		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getRemotePort()).isEqualTo(80);
	}

	@Test
	public void buildRequestRequestedSessionId() throws Exception {
		String sessionId = "session-id";
		webRequest.setAdditionalHeader("Cookie", "JSESSIONID=" + sessionId);
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getRequestedSessionId()).isEqualTo(sessionId);
	}

	@Test
	public void buildRequestRequestedSessionIdNull() throws Exception {
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getRequestedSessionId()).isNull();
	}

	@Test
	public void buildRequestUri() {
		String uri = requestBuilder.buildRequest(servletContext).getRequestURI();
		assertThat(uri).isEqualTo("/test/this/here");
	}

	@Test
	public void buildRequestUrl() {
		String uri = requestBuilder.buildRequest(servletContext).getRequestURL().toString();
		assertThat(uri).isEqualTo("http://example.com/test/this/here");
	}

	@Test
	public void buildRequestSchemeHttp() throws Exception {
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getScheme()).isEqualTo("http");
	}

	@Test
	public void buildRequestSchemeHttps() throws Exception {
		webRequest.setUrl(new URL("https://example.com/"));
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getScheme()).isEqualTo("https");
	}

	@Test
	public void buildRequestServerName() throws Exception {
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getServerName()).isEqualTo("example.com");
	}

	@Test
	public void buildRequestServerPort() throws Exception {
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getServerPort()).isEqualTo(80);
	}

	@Test
	public void buildRequestServerPortDefault() throws Exception {
		webRequest.setUrl(new URL("https://example.com/"));
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getServerPort()).isEqualTo(-1);
	}

	@Test
	public void buildRequestServletContext() throws Exception {
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getServletContext()).isEqualTo(servletContext);
	}

	@Test
	public void buildRequestServletPath() throws Exception {
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getServletPath()).isEqualTo("/this/here");
	}

	@Test
	public void buildRequestSession() throws Exception {
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		HttpSession newSession = actualRequest.getSession();
		assertThat(newSession).isNotNull();
		assertSingleSessionCookie("JSESSIONID=" + newSession.getId() + "; Path=/test; Domain=example.com");

		webRequest.setAdditionalHeader("Cookie", "JSESSIONID=" + newSession.getId());

		requestBuilder = new HtmlUnitRequestBuilder(sessions, cookieManager, webRequest);
		actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getSession()).isSameAs(newSession);
	}

	@Test
	public void buildRequestSessionWithExistingSession() throws Exception {
		String sessionId = "session-id";
		webRequest.setAdditionalHeader("Cookie", "JSESSIONID=" + sessionId);
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		HttpSession session = actualRequest.getSession();
		assertThat(session.getId()).isEqualTo(sessionId);
		assertSingleSessionCookie("JSESSIONID=" + session.getId() + "; Path=/test; Domain=example.com");

		requestBuilder = new HtmlUnitRequestBuilder(sessions, cookieManager, webRequest);
		actualRequest = requestBuilder.buildRequest(servletContext);
		assertThat(actualRequest.getSession()).isEqualTo(session);

		webRequest.setAdditionalHeader("Cookie", "JSESSIONID=" + sessionId + "NEW");
		actualRequest = requestBuilder.buildRequest(servletContext);
		assertThat(actualRequest.getSession()).isNotEqualTo(session);
		assertSingleSessionCookie("JSESSIONID=" + actualRequest.getSession().getId()
				+ "; Path=/test; Domain=example.com");
	}

	@Test
	public void buildRequestSessionTrue() throws Exception {
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		HttpSession session = actualRequest.getSession(true);
		assertThat(session).isNotNull();
	}

	@Test
	public void buildRequestSessionFalseIsNull() throws Exception {
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		HttpSession session = actualRequest.getSession(false);
		assertThat(session).isNull();
	}

	@Test
	public void buildRequestSessionFalseWithExistingSession() throws Exception {
		String sessionId = "session-id";
		webRequest.setAdditionalHeader("Cookie", "JSESSIONID=" + sessionId);
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		HttpSession session = actualRequest.getSession(false);
		assertThat(session).isNotNull();
	}

	@Test
	public void buildRequestSessionIsNew() throws Exception {
		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getSession().isNew()).isTrue();
	}

	@Test
	public void buildRequestSessionIsNewFalse() throws Exception {
		String sessionId = "session-id";
		webRequest.setAdditionalHeader("Cookie", "JSESSIONID=" + sessionId);

		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getSession().isNew()).isFalse();
	}

	@Test
	public void buildRequestSessionInvalidate() throws Exception {
		String sessionId = "session-id";
		webRequest.setAdditionalHeader("Cookie", "JSESSIONID=" + sessionId);

		MockHttpServletRequest actualRequest = requestBuilder.buildRequest(servletContext);
		HttpSession sessionToRemove = actualRequest.getSession();
		sessionToRemove.invalidate();

		assertThat(!sessions.containsKey(sessionToRemove.getId()));
		assertSingleSessionCookie("JSESSIONID=" + sessionToRemove.getId()
				+ "; Expires=Thu, 01-Jan-1970 00:00:01 GMT; Path=/test; Domain=example.com");

		webRequest.removeAdditionalHeader("Cookie");
		requestBuilder = new HtmlUnitRequestBuilder(sessions, cookieManager, webRequest);

		actualRequest = requestBuilder.buildRequest(servletContext);

		assertThat(actualRequest.getSession().isNew()).isTrue();
		assertThat(!sessions.containsKey(sessionToRemove.getId()));
	}

	// --- setContextPath

	@Test
	public void setContextPathNull() {
		requestBuilder.setContextPath(null);

		assertThat(getContextPath()).isNull();
	}

	@Test
	public void setContextPathEmptyString() {
		requestBuilder.setContextPath("");

		assertThat(getContextPath()).isEmpty();
	}

	@Test(expected = IllegalArgumentException.class)
	public void setContextPathDoesNotStartWithSlash() {
		requestBuilder.setContextPath("abc/def");
	}

	@Test(expected = IllegalArgumentException.class)
	public void setContextPathEndsWithSlash() {
		requestBuilder.setContextPath("/abc/def/");
	}

	@Test
	public void setContextPath() {
		String expectedContextPath = "/abc/def";
		requestBuilder.setContextPath(expectedContextPath);

		assertThat(getContextPath()).isEqualTo(expectedContextPath);
	}

	private void assertSingleSessionCookie(String expected) {
		com.gargoylesoftware.htmlunit.util.Cookie jsessionidCookie = cookieManager.getCookie("JSESSIONID");
		if (expected == null || expected.contains("Expires=Thu, 01-Jan-1970 00:00:01 GMT")) {
			assertThat(jsessionidCookie).isNull();
			return;
		}
		String actual = jsessionidCookie.getValue();
		assertThat("JSESSIONID=" + actual + "; Path=/test; Domain=example.com").isEqualTo(expected);
	}

	private void setParameter(String name, String value) {
		webRequest.getRequestParameters().add(new NameValuePair(name, value));
	}

	private String getContextPath() {
		return (String) ReflectionTestUtils.getField(requestBuilder, "contextPath");
	}
}
