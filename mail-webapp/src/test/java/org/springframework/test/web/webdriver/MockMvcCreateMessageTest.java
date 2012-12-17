package org.springframework.test.web.webdriver;

import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.webdriver.MockMvcHtmlUnitDriver;
import org.springframework.test.web.webdriver.pages.CreateMessagePage;
import org.springframework.web.context.WebApplicationContext;

/**
 * An end to end test that validates the {@link CreateMessagePage}. A few things to notice:
 *
 * <ul>
 * <li>Notice that we are able to reuse the same page objects as the {@link MockMvcCreateMessageTest} and the
 * {@link MockitoMvcCreateMessageTest}. This saves time testing since we do not need to reinvent the wheel for our
 * integration and end to end testing.</li>
 * <li>You will see that all the tests are inherited from {@link CreateMessageITest}. This shows how little difference
 * there is in how you would write the tests.</li>
 * <li>The only difference is how we initialize our {@link WebDriver}</li>
 * <li>We do not need to run the web application on a server for this test since we are using
 * {@link MockMvcHtmlUnitDriver}</li>
 * </ul>
 *
 * @author Rob Winch
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/message-servlet.xml",
		"file:src/main/webapp/WEB-INF/spring/*.xml" })
@WebAppConfiguration
public class MockMvcCreateMessageTest extends CreateMessageITest {
	@Autowired
	private WebApplicationContext context;

	@Override
	protected WebDriver createDriver() {
		return new MockMvcHtmlUnitDriver(context, true);
	}
}
