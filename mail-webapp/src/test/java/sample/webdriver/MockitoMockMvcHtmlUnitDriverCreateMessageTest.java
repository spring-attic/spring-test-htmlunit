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
package sample.webdriver;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriver;
import sample.config.MockDataConfig;
import sample.config.WebMvcConfig;
import sample.data.Message;
import sample.data.MessageRepository;
import sample.webdriver.pages.CreateMessagePage;
import sample.webdriver.pages.ViewMessagePage;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebMvcConfig.class, MockDataConfig.class})
@WebAppConfiguration
public class MockitoMockMvcHtmlUnitDriverCreateMessageTest extends AbstractWebDriverTest {
	@Autowired
	private WebApplicationContext context;

	@Autowired
	private Message expectedMessage;

	@Test
	public void missingFieldWithJavascriptValidationDisplaysError() {
		CreateMessagePage messagePage = CreateMessagePage.to(getDriver());
		messagePage = messagePage.createMessage(CreateMessagePage.class, "", "");
		assertThat(messagePage.getErrors()).isEqualTo("This field is required.");
	}

	@Test
	public void missingFieldServerSideValidationDisplaysError() {
		CreateMessagePage messagePage = CreateMessagePage.to(getDriver());
		messagePage = messagePage.createMessage(CreateMessagePage.class, "Summary", "");
		assertThat(messagePage.getErrors()).isEqualTo("Message is required.");
	}

	@Test
	public void successfullyCreateMessage() {
		String expectedSummary = expectedMessage.getSummary();
		String expectedText = expectedMessage.getText();
		Date expectedTime = expectedMessage.getCreated().getTime();
		String expectedCreated = new DateFormatter("MMMM d, yyyy h:mm:ss a z")
				.print(expectedTime, Locale.getDefault());

		CreateMessagePage page = CreateMessagePage.to(getDriver());

		ViewMessagePage viewMessagePage = page.createMessage(ViewMessagePage.class, expectedSummary, expectedText);
		assertThat(viewMessagePage.getId()).isEqualTo(expectedMessage.getId().toString());
		assertThat(viewMessagePage.getCreated()).isEqualTo(expectedCreated);
		assertThat(viewMessagePage.getSummary()).isEqualTo(expectedSummary);
		assertThat(viewMessagePage.getText()).isEqualTo(expectedText);
		assertThat(viewMessagePage.getSuccess()).isEqualTo("Successfully created a new message");
	}

	@Override
	protected WebDriver createDriver() {
		return new MockMvcHtmlUnitDriver(context, true);
	}
}
