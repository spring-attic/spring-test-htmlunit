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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriver;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sample.config.MockDataConfig;
import sample.data.Message;
import sample.webdriver.pages.CreateMessagePage;
import sample.webdriver.pages.LoginPage;
import sample.webdriver.pages.ViewMessagePage;

import java.text.ParseException;

import static sample.fest.Assertions.assertThat;

/**
 * <p>
 * An integration test that validates the {@link CreateMessagePage}. Notice that we are able to reuse the same page
 * objects as the {@link MockMvcHtmlUnitDriverCreateMessageTest}.
 * </p>
 * <p>
 * <strong>NOTE</strong> The web application must actually be running for this test to pass.
 * </p>
 *
 * @author Rob Winch
 * @see MockMvcHtmlUnitDriverCreateMessageTest
 */
public class WebDriverCreateMessageITest {
	private Message expectedMessage = new MockDataConfig().createMessage();

	@Autowired
	private WebApplicationContext context;

	private WebDriver driver;

	@Before
	public void setup() {
		driver = new HtmlUnitDriver(true);
		LoginPage.login(driver);
	}

	@After
	public void destroy() {
		if(driver != null) {
			driver.close();
		}
	}

	@Test
	public void missingFieldWithJavascriptValidationDisplaysError() {
		CreateMessagePage messagePage = CreateMessagePage.to(driver);
		messagePage = messagePage.createMessage(CreateMessagePage.class, "", "");
		assertThat(messagePage.getErrors()).isEqualTo("This field is required.");
	}

	@Test
	public void missingFieldServerSideValidationDisplaysError() {
		CreateMessagePage messagePage = CreateMessagePage.to(driver);
		messagePage = messagePage.createMessage(CreateMessagePage.class, "Summary", "");
		assertThat(messagePage.getErrors()).isEqualTo("Message is required.");
	}

	@Test
	public void successfullyCreateMessage() throws ParseException {
		String expectedSummary = expectedMessage.getSummary();
		String expectedText = expectedMessage.getText();

		CreateMessagePage page = CreateMessagePage.to(driver);

		ViewMessagePage viewMessagePage = page.createMessage(ViewMessagePage.class, expectedSummary, expectedText);
		assertThat(viewMessagePage.getMessage()).isEqualToIgnoringGeneratedFields(expectedMessage);
		assertThat(viewMessagePage.getSuccess()).isEqualTo("Successfully created a new message");
	}
}
