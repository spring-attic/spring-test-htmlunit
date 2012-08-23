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
package org.springframework.test.web.geb;

import org.openqa.selenium.Capabilities;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.web.webdriver.MockMvcHtmlUnitDriver;
import org.springframework.web.context.WebApplicationContext;

/**
 * <p>
 * A {@link TestExecutionListener} that is intended to automatically inject a {@link MockMvcHtmlUnitDriver} instance in
 * subclasses of GebSpec.
 * </p>
 * <p>
 * To use it ensure that you have the Spock Spring integration jar on your classpath and then update your test to look
 * something like this:
 * </p>
 *
 * <pre>
 * {@literal @}ContextConfiguration(locations = ["file:src/main/webapp/WEB-INF/message-servlet.xml","file:src/main/webapp/WEB-INF/spring/*.xml"],
 *     loader = WebContextLoader)
 * {@literal @}TestExecutionListeners([ DependencyInjectionTestExecutionListener,
 *     DirtiesContextTestExecutionListener,
 *     TransactionalTestExecutionListener,
 *     GebSpecTestExecutionListener])
 * class MockMvcCreateMessagesTest extends GebReportingSpec
 * </pre>
 *
 * @author Rob Winch
 *
 */
public class GebSpecTestExecutionListener extends AbstractTestExecutionListener {

	@Override
	public void prepareTestInstance(TestContext testContext) throws Exception {
		WebApplicationContext context = (WebApplicationContext) testContext.getApplicationContext();
		Object testInstance = testContext.getTestInstance();
		BeanWrapper testInstanceBeanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(testInstance);
		Capabilities capabilities = (Capabilities) testInstanceBeanWrapper
				.getPropertyValue("browser.driver.capabilities");
		MockMvcHtmlUnitDriver mockMvcHtmlUnitDriver = new MockMvcHtmlUnitDriver(context, capabilities);
		testInstanceBeanWrapper.setPropertyValue("browser.driver", mockMvcHtmlUnitDriver);
	}
}