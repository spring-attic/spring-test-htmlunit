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
package org.springframework.test.web.server.htmlunit.webdriver;

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
import org.springframework.test.web.server.htmlunit.mail.data.Message;
import org.springframework.test.web.server.htmlunit.mail.data.MessageRepository;
import org.springframework.test.web.server.htmlunit.webdriver.MockMvcHtmlUnitDriver;
import org.springframework.test.web.server.htmlunit.webdriver.pages.CreateMessagePage;
import org.springframework.test.web.server.htmlunit.webdriver.pages.ViewMessagePage;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:mock-spring-data.xml",
		"file:src/main/webapp/WEB-INF/message-servlet.xml" })
@WebAppConfiguration
public class MockitoMvcCreateMessageTest extends AbstractWebDriverTest {
	@Autowired
	private WebApplicationContext context;

	@Autowired
	private MessageRepository messages;

	@Before
	public void setUp() {
		Message message = getExpectedMessage();
		when(messages.save(any(Message.class))).thenAnswer(new Answer<Message>() {
			@Override
			public Message answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				Message result = (Message) args[0];
				result.setId(123L);
				return result;
			}
		});
		when(messages.findOne(anyLong())).thenReturn(message);
	}

	@After
	public void tearDown() {
		reset(messages);
	}

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
		Message expectedMessage = getExpectedMessage();
		String expectedSummary = expectedMessage.getSummary();
		String expectedText = expectedMessage.getText();
		Date expectedTime = expectedMessage.getCreated().getTime();
		String expectedCreated = new DateFormatter("MMMM dd, yyyy h:mm:ss a z")
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

	private Message getExpectedMessage() {
		Message message = new Message();
		message.setCreated(Calendar.getInstance());
		message.setId(123L);
		message.setSummary("Summary");
		message.setText("Detailed message that you can see");
		return message;
	}
}
