/*
 * Copyright 2002-2015 the original author or authors.
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
package docs.htmlunit;

import com.gargoylesoftware.htmlunit.WebClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebConnection;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author Rob Winch
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class HtmlUnitDocTests {
	@Autowired
	WebApplicationContext context;

	WebClient webClient;

	@Test
	public void webclientWithMockMvc() {
		// tag::webclient-mockmvc[]
		MockMvc mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();

		webClient = MockMvcWebClientBuilder
				.mockMvcSetup(mockMvc)
				// for illustration only - defaults to ""
				.contextPath("")
				.createWebClient();
		// end::webclient-mockmvc[]
	}

	@Test
	public void webclientVerbose() {
		// tag::webclient-verbose[]
		String contextPath = "";
		MockMvc mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();

		webClient = new WebClient();
		webClient.setWebConnection(new MockMvcWebConnection(mockMvc, contextPath));
		// end::webclient-verbose[]
	}

	@Configuration
	@EnableWebMvc
	@EnableWebSecurity
	static class Config {
		@Autowired
		public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
			auth
				.inMemoryAuthentication();
		}
	}
}
