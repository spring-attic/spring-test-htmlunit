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
import org.junit.After
import org.junit.Before
import org.openqa.selenium.WebDriver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriver
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import sample.config.MockDataConfig
import sample.config.WebMvcConfig
import sample.data.Message
import sample.geb.pages.CreateMessagePage
import sample.geb.pages.ViewMessagePage

/**
 *
 * @author Rob Winch
 *
 */
@ContextConfiguration(classes=[WebMvcConfig,MockDataConfig])
@WebAppConfiguration
class GebCreateMessagesSpec extends GebReportingSpec {
	@Autowired
	WebApplicationContext context;

	@Autowired
	Message expectedMessage;

	WebDriver driver;

	def setup() {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
		driver = new MockMvcHtmlUnitDriver(mockMvc, true);
		browser.driver = driver
	}

	def destroy() {
		if(driver != null) {
			driver.close();
		}
	}

	def 'missing field with javascript validation displays error'() {
		setup:
			to CreateMessagePage
			at CreateMessagePage
		when:
			submit.click(CreateMessagePage)
		then:
			errors.contains('This field is required.')
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
			to CreateMessagePage
			at CreateMessagePage
		when:
			form.summary = expectedSummary
			form.text = expectedMessage
			submit.click(ViewMessagePage)
		then:
			at ViewMessagePage
			success == 'Successfully created a new message'
			id
			date
			summary == expectedSummary
			message == expectedMessage
	}
}