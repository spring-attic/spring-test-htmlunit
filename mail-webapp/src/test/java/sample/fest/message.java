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
package sample.fest;

import org.fest.assertions.ObjectAssert;
import sample.data.Message;

import java.util.Calendar;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Rob Winch
 */
public class message extends ObjectAssert {

	public message(Message message) {
		super(message);
	}

	@Override
	public message isEqualTo(Object expected) {
		isEqualToIgnoringGeneratedFields(expected);
		Message expectedMessage = (Message) expected;
		Message actualMessage = (Message) actual;
		Calendar expectedCreated = clearMilliseconds(expectedMessage);
		assertThat(actualMessage.getId()).isEqualTo(expectedMessage.getId());
		assertThat(actualMessage.getCreated()).isEqualTo(expectedCreated);
		return this;
	}

	/**
	 * We aren't interested in being that precise
	 * @param expectedMessage
	 * @return
	 */
	private Calendar clearMilliseconds(Message expectedMessage) {
		Calendar expectedCreated = Calendar.getInstance(expectedMessage.getCreated().getTimeZone());
		expectedCreated.setTime(expectedMessage.getCreated().getTime());
		expectedCreated.set(Calendar.MILLISECOND, 0); // we don't care about milliseconds
		return expectedCreated;
	}

	public message isEqualToIgnoringGeneratedFields(Object expected) {
		assertThat(expected).isInstanceOf(Message.class);
		Message expectedMessage = (Message) expected;
		Message actualMessage = (Message) actual;
		assertThat(actualMessage.getSummary()).isEqualTo(expectedMessage.getSummary());
		assertThat(actualMessage.getText()).isEqualTo(expectedMessage.getText());
		return this;
	}
}
