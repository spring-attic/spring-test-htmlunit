package org.springframework.test.web.servlet.htmlunit.webdriver;

import com.gargoylesoftware.htmlunit.WebConnection;
import com.gargoylesoftware.htmlunit.WebRequest;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Rob Winch
 */
// tag::junit-spring-setup[]
@RunWith(MockitoJUnitRunner.class)
public class WebConnectionHtmlUnitDriverTests {
	@Mock
	WebConnection connection;

	WebConnectionHtmlUnitDriver driver;

	@Before
	public void setup() throws Exception {
		driver = new WebConnectionHtmlUnitDriver();

		when(connection.getResponse(any(WebRequest.class))).thenThrow(new InternalError(""));
	}

	@Test
	public void getWebConnectionDefaultNotNull() {
		assertThat(driver.getWebConnection()).isNotNull();
	}

	@Test
	public void setWebConnection() {
		driver.setWebConnection(connection);

		assertThat(driver.getWebConnection()).isEqualTo(connection);
		try {
			driver.get("https://example.com");
			fail("Expected Exception");
		} catch (InternalError success) {}
	}

	@Test(expected = IllegalArgumentException.class)
	public void setWebConnectionNull() {
		driver.setWebConnection(null);
	}
}