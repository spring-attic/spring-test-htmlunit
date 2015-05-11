package sample.webdriver;

import java.text.ParseException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import sample.config.MockDataConfig;
import sample.config.WebMvcConfig;
import sample.config.WebSecurityConfig;
import sample.data.Message;
import static sample.fest.Assertions.assertThat;
import sample.webdriver.pages.CreateMessagePage;
import sample.webdriver.pages.ViewMessagePage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
import org.springframework.web.context.WebApplicationContext;

/**
 * An end to end test that validates the {@link CreateMessagePage}. A few things to notice:
 *
 * <ul>
 * <li>You will see that all the tests are the same as {@link WebDriverCreateMessageITests}. This shows how little difference
 * there is in how you would write the tests.</li>
 * <li>The only difference is how we initialize our {@link WebDriver}</li>
 * <li>We do not need to run the web application on a server for this test since we are using
 * {@link org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriver}</li>
 * </ul>
 *
 * @author Rob Winch
 *
 */
// tag::junit-spring-setup[]
@RunWith(SpringJUnit4ClassRunner.class) // <1>
@ContextConfiguration(classes = {WebMvcConfig.class, WebSecurityConfig.class, MockDataConfig.class}) // <2>
@WebAppConfiguration // <3>
@WithMockUser // <4>
public class MockMvcHtmlUnitDriverCreateMessageTests {
	@Autowired
	WebApplicationContext context;
	// end::junit-spring-setup[]

	@Autowired
	Message expectedMessage;

	// tag::webdriver[]
	WebDriver driver;

	@Before
	public void setup() {
		driver = MockMvcHtmlUnitDriverBuilder
			.webAppContextSetup(context, springSecurity())
			.createDriver();
	}
	// end::webdriver[]

	// tag::cleanup[]
	@After
	public void destroy() {
		if(driver != null) {
			driver.close();
		}
	}
	// end::cleanup[]

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

		// tag::to-create-message[]
		CreateMessagePage page = CreateMessagePage.to(driver);
		// end::to-create-message[]

		// tag::create-message[]
		ViewMessagePage viewMessagePage =
			page.createMessage(ViewMessagePage.class, expectedSummary, expectedText);
		// end::create-message[]

		// tag::create-message-assert[]
		assertThat(viewMessagePage.getMessage()).isEqualTo(expectedMessage);
		assertThat(viewMessagePage.getSuccess()).isEqualTo("Successfully created a new message");
		// end::create-message-assert[]
	}
}
