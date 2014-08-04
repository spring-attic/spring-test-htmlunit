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
package sample.mockmvc;

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
import sample.config.MockDataConfig;
import sample.config.WebMvcConfig;
import sample.data.Message;
import sample.data.MessageRepository;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

/**
 * @author Rob Winch
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebMvcConfig.class, MockDataConfig.class})
@WebAppConfiguration
public class MockMvcCreateMessageTests {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	public void createMessage() throws Exception {
		MockHttpServletRequestBuilder createMessage = post("/messages/")
			.param("summary", "Spring Rocks")
			.param("text", "In case you didn't know, Spring Rocks!");

		mockMvc.perform(createMessage)
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/messages/123"));
	}

	@Test
	public void createMessageForm() throws Exception {
		mockMvc.perform(get("/messages/form"))
			.andExpect(xpath("//input[@name='summary']").exists())
			.andExpect(xpath("//textarea[@name='text']").exists());
	}

	@Test
	public void createMessageFormSubmit() throws Exception {
		String summaryParamName = "summary";
		String textParamName = "text";
		mockMvc.perform(get("/messages/form"))
				.andExpect(xpath("//input[@name='" + summaryParamName + "']").exists())
				.andExpect(xpath("//textarea[@name='" + textParamName + "']").exists());

		MockHttpServletRequestBuilder createMessage = post("/messages/")
				.param(summaryParamName, "Spring Rocks")
				.param(textParamName, "In case you didn't know, Spring Rocks!");

		mockMvc.perform(createMessage)
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/messages/123"));
	}
}
