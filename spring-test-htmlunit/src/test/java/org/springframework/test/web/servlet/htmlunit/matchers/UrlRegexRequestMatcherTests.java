/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.test.web.servlet.htmlunit.matchers;

import com.gargoylesoftware.htmlunit.WebRequest;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Rob Winch
 */
public class UrlRegexRequestMatcherTests {

	@Test
	public void classlevelJavadoc() throws Exception {
		WebRequestMatcher cdnMatcher = new UrlRegexRequestMatcher(".*?//code.jquery.com/.*");

		boolean matches = cdnMatcher.matches(new WebRequest(new URL("https://code.jquery.com/jquery-1.11.0.min.js")));
		assertThat(matches).isTrue();

		matches = cdnMatcher.matches(new WebRequest(new URL("http://localhost/jquery-1.11.0.min.js")));
		assertThat(matches).isFalse();
	}
}