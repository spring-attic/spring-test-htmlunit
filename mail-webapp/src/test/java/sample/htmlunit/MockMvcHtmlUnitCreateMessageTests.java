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
package sample.htmlunit;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebConnection;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sample.config.MockDataConfig;
import sample.config.WebMvcConfig;
import sample.config.WebSecurityConfig;

import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * @author Rob Winch
 */
// tag::junit-spring-setup[]
@RunWith(SpringJUnit4ClassRunner.class) // <1>
@ContextConfiguration(classes = {WebMvcConfig.class, WebSecurityConfig.class, MockDataConfig.class}) // <2>
@WebAppConfiguration // <3>
@WithMockUser // <4>
public class MockMvcHtmlUnitCreateMessageTests {

	@Autowired
	WebApplicationContext context;
	// end::junit-spring-setup[]


	// tag::webclient[]
	WebClient webClient;

	@Before
	public void setup() {
		webClient = MockMvcWebClientBuilder
			.webAppContextSetup(context, springSecurity())
			// for illustration only - defaults to ""
			.contextPath("")
			.createWebClient();
	}
	// end::webclient[]

	@After
	public void cleanup() {
		this.webClient.close();
	}

	@Test
	public void createMessage() throws IOException {
		// Load the Create Message Form
		// tag::create-form[]
		HtmlPage createMsgFormPage = webClient.getPage("http://localhost/messages/form");
		// end::create-form[]

		// Submit the create message form
		// tag::submit-form[]
		HtmlForm form = createMsgFormPage.getHtmlElementById("messageForm");
		HtmlTextInput summaryInput = createMsgFormPage.getHtmlElementById("summary");
		summaryInput.setValueAttribute("Spring Rocks");
		HtmlTextArea textInput = createMsgFormPage.getHtmlElementById("text");
		textInput.setText("In case you didn't know, Spring Rocks!");
		HtmlSubmitInput submit = form.getOneHtmlElementByAttribute("input", "type", "submit");
		HtmlPage newMessagePage = submit.click();
		// end::submit-form[]

		// verify we successfully created a message and displayed the newly create message
		// tag::verify-form[]
		assertThat(newMessagePage.getUrl().toString()).endsWith("/messages/123");
		String id = newMessagePage.getHtmlElementById("id").getTextContent();
		assertThat(id).isEqualTo("123");
		String summary = newMessagePage.getHtmlElementById("summary").getTextContent();
		assertThat(summary).isEqualTo("Spring Rocks");
		String text = newMessagePage.getHtmlElementById("text").getTextContent();
		assertThat(text).isEqualTo("In case you didn't know, Spring Rocks!");
		// end::verify-form[]
	}
}
