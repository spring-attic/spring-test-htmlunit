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
package sample.geb

import geb.spock.GebReportingSpec
import org.openqa.selenium.WebDriver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder
import org.springframework.web.context.WebApplicationContext
import sample.config.MockDataConfig
import sample.config.WebMvcConfig
import sample.config.WebSecurityConfig
import sample.data.Message
import sample.geb.pages.CreateMessagePage
import sample.geb.pages.ViewMessagePage

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity

/**
 *
 * @author Rob Winch
 *
 */
// tag::spock-spring-setup[]
@ContextConfiguration(classes=[WebMvcConfig, WebSecurityConfig, MockDataConfig]) // <1>
@WebAppConfiguration // <2>
@WithMockUser // <3>
class GebCreateMessagesSpec
		extends GebReportingSpec { // <4>

	@Autowired
	WebApplicationContext context;
// end::spock-spring-setup[]

	@Autowired
	Message expectedMessage;

	// tag::webdriver[]
	def setup() {
		browser.driver = MockMvcHtmlUnitDriverBuilder
			.webAppContextSetup(context, springSecurity())
			.createDriver()
	}
	// end::webdriver[]

	def destroy() {
		browser.driver?.close()
	}

	def 'missing field with javascript validation displays error'() {
		setup:
			to CreateMessagePage
			at CreateMessagePage
		when:
			submit.click(CreateMessagePage)
		// tag::at-create-message[]
		then:
			at CreateMessagePage
			errors.contains('This field is required.')
		// end::at-create-message[]
	}

	def 'missing field server side validation displays error'() {
		setup:
			to CreateMessagePage
			at CreateMessagePage
		when:
			form.summary = 'Summary'
			submit.click(CreateMessagePage)
		then:
			errors.contains('Message is required.')
	}

	def 'successfully create new message'() {
		setup:
			def expectedSummary = 'Spring Rocks'
			def expectedMessage = 'In case you didn\'t know, Spring Rocks!'
			// tag::to-create-message[]
			to CreateMessagePage
			// end::to-create-message[]
			at CreateMessagePage
		// tag::create-message[]
		when:
			form.summary = expectedSummary
			form.text = expectedMessage
			submit.click(ViewMessagePage)
		// end::create-message[]
		// tag::create-message-assert[]
		then:
			at ViewMessagePage
			success == 'Successfully created a new message'
			id
			date
			summary == expectedSummary
			message == expectedMessage

		// end::create-message-assert[]
	}
}