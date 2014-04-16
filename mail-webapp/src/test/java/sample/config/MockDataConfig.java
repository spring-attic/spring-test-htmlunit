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
package sample.config;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;
import sample.data.Message;
import sample.data.MessageRepository;
import sample.data.mock.MockConversionService;

import java.util.Calendar;
import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Rob Winch
 */
@Configuration
public class MockDataConfig {

	@Bean
	public MessageRepository messageRepository() {
		final Message message = createMessage();
		MessageRepository messages = mock(MessageRepository.class);
		when(messages.save(any(Message.class))).thenAnswer(new Answer<Message>() {
			@Override
			public Message answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				Message result = (Message) args[0];
				result.setId(message.getId());
				result.setCreated(message.getCreated());
				return result;
			}
		});
		when(messages.findOne(anyLong())).thenReturn(message);
		return messages;
	}

	@Bean
	public <T extends ConversionService & ConverterRegistry> MockConversionService<T> conversionService(T conversionService) {
		return new MockConversionService<T>(conversionService);
	}

	@Bean
	public Message createMessage() {
		Calendar created = Calendar.getInstance(Locale.ENGLISH);
		// we need to clear out the milliseconds since we are not interested in being that precise
		created.set(Calendar.MILLISECOND, 0);
		created.getTime().setTime(1397672456000L);
		Message message = new Message();
		message.setCreated(created);
		message.setId(123L);
		message.setSummary("Spring Rocks");
		message.setText("In case you didn't know, Spring Rocks!");
		return message;
	}
}
