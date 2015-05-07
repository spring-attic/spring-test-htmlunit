package org.springframework.test.web.servlet.htmlunit;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import com.gargoylesoftware.htmlunit.WebConnection;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import static org.fest.assertions.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author Rob Winch
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class MockMvcConnectionBuilderSupportTests {
	@Autowired
	WebApplicationContext context;

	MockMvc mockMvc;

	WebConnection delegateConnection;

	WebConnection connection;

	@Before
	public void setup() {
		delegateConnection = mock(WebConnection.class);
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

		connection = new MockMvcWebConnectionBuilderSupport(mockMvc){}
				.createConnection(delegateConnection);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructorMockMvcNull() {
		new MockMvcWebConnectionBuilderSupport((MockMvc)null){};
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructorContextNull() {
		new MockMvcWebConnectionBuilderSupport((WebApplicationContext)null){};
	}

	@Test
	public void context() throws Exception {
		connection = new MockMvcWebConnectionBuilderSupport(context){}
				.createConnection(delegateConnection);

		assertMvcProcessed("http://localhost/");
		assertDelegateProcessed("http://example.com/");
	}

	@Test
	public void mockMvc() throws Exception {
		assertMvcProcessed("http://localhost/");
		assertDelegateProcessed("http://example.com/");
	}

	@Test
	public void mockMvcExampleDotCom() throws Exception {
		connection = new MockMvcWebConnectionBuilderSupport(context){}
				.useMockMvcForHosts("example.com")
				.createConnection(delegateConnection);

		assertMvcProcessed("http://localhost/");
		assertMvcProcessed("http://example.com/");
		assertDelegateProcessed("http://other.com/");
	}

	@Test
	public void mockMvcAlwaysUseMockMvc() throws Exception {
		connection = new MockMvcWebConnectionBuilderSupport(context){}
				.alwaysUseMockMvc()
				.createConnection(delegateConnection);

		assertMvcProcessed("http://other.com/");
	}

	@Test
	public void defaultContextPathEmpty() throws Exception {
		connection = new MockMvcWebConnectionBuilderSupport(context){}
				.createConnection(delegateConnection);

		assertThat(getWebResponse("http://localhost/abc").getContentAsString()).isEqualTo("");
	}

	@Test
	public void defaultContextPathCustom() throws Exception {
		connection = new MockMvcWebConnectionBuilderSupport(context){}
				.contextPath("/abc")
				.createConnection(delegateConnection);

		assertThat(getWebResponse("http://localhost/abc/def").getContentAsString()).isEqualTo("/abc");
	}

	private void assertMvcProcessed(String url) throws Exception {
		assertThat(getWebResponse(url)).isNotNull();
	}

	private void assertDelegateProcessed(String url) throws Exception {
		assertThat(getWebResponse(url)).isNull();
	}

	private WebResponse getWebResponse(String url) throws IOException {
		return connection.getResponse(new WebRequest(new URL(url)));
	}

	@Configuration
	@EnableWebMvc
	static class Config {
		@RestController
		static class ContextPathController {
			@RequestMapping
			public String contextPath(HttpServletRequest request) {
				return request.getContextPath();
			}
		}
	}
}