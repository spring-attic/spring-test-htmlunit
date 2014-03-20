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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebConnection;
import sample.data.Message;
import sample.data.MessageRepository;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Calendar;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * @author Rob Winch
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:mock-spring-data.xml",
		"file:src/main/webapp/WEB-INF/message-servlet.xml" })
@WebAppConfiguration
public class MockMvcHtmlUnitCreateMessageTest {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private MessageRepository messages;

	private WebClient webClient;

	@Before
	public void setup() {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
		webClient = new WebClient();
		webClient.setWebConnection(new MockMvcWebConnection(mockMvc));

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
	public void cleanup() {
		reset(messages);
		this.webClient.closeAllWindows();
	}

	@Test
	public void createMessage() throws IOException {
		HtmlPage createMsgFormPage = webClient.getPage("http://localhost/mail/messages/form");

		HtmlForm form = createMsgFormPage.getHtmlElementById("messageForm");

		HtmlTextInput summaryInput = createMsgFormPage.getHtmlElementById("summary");
		summaryInput.setValueAttribute("Spring Rocks");
		HtmlTextArea textInput = createMsgFormPage.getHtmlElementById("text");
		textInput.setText("In case you didn't know, Spring Rocks!");
		HtmlSubmitInput submit = form.getOneHtmlElementByAttribute("input", "type", "submit");

		HtmlPage newMessagePage = submit.click();
		assertThat(newMessagePage.getUrl().toString()).endsWith("/messages/123");
		String id = newMessagePage.getHtmlElementById("id").getTextContent();
		assertThat(id).isEqualTo("123");
		String summary = newMessagePage.getHtmlElementById("summary").getTextContent();
		assertThat(summary).isEqualTo("Spring Rocks");
		String text = newMessagePage.getHtmlElementById("text").getTextContent();
		assertThat(text).isEqualTo("In case you didn't know, Spring Rocks!");
	}

	private Message getExpectedMessage() {
		Message message = new Message();
		message.setCreated(Calendar.getInstance());
		message.setId(123L);
		message.setSummary("Spring Rocks");
		message.setText("In case you didn't know, Spring Rocks!");
		return message;
	}
}
