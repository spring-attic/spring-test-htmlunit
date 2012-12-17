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
package org.springframework.test.web.server.htmlunit.geb

import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.support.DirtiesContextTestExecutionListener
import org.springframework.test.context.transaction.TransactionalTestExecutionListener
import org.springframework.test.context.web.WebAppConfiguration

/**
 *
 * @author Rob Winch
 *
 */
@ContextConfiguration(locations = ["file:src/main/webapp/WEB-INF/message-servlet.xml","file:src/main/webapp/WEB-INF/spring/*.xml"])
@TestExecutionListeners([ DependencyInjectionTestExecutionListener,
		DirtiesContextTestExecutionListener,
		TransactionalTestExecutionListener,
		GebSpecTestExecutionListener])
@WebAppConfiguration
class MockMvcCreateMessagesSpec extends CreateMessagesISpec {

}