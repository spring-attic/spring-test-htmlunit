package sample.webdriver;

import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriver;
import sample.webdriver.pages.CreateMessagePage;
import org.springframework.web.context.WebApplicationContext;

/**
 * An end to end test that validates the {@link CreateMessagePage}. A few things to notice:
 *
 * <ul>
 * <li>Notice that we are able to reuse the same page objects as the {@link MockMvcHtmlUnitDriverCreateMessageTest} and the
 * {@link MockitoMockMvcHtmlUnitDriverCreateMessageTest}. This saves time testing since we do not need to reinvent the wheel for our
 * integration and end to end testing.</li>
 * <li>You will see that all the tests are inherited from {@link WebDriverCreateMessageITest}. This shows how little difference
 * there is in how you would write the tests.</li>
 * <li>The only difference is how we initialize our {@link WebDriver}</li>
 * <li>We do not need to run the web application on a server for this test since we are using
 * {@link org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriver}</li>
 * </ul>
 *
 * @author Rob Winch
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/message-servlet.xml",
		"file:src/main/webapp/WEB-INF/spring/*.xml" })
@WebAppConfiguration
public class MockMvcHtmlUnitDriverCreateMessageTest extends WebDriverCreateMessageITest {
	@Autowired
	private WebApplicationContext context;

	@Override
	protected WebDriver createDriver() {
		return new MockMvcHtmlUnitDriver(context, true);
	}
}
