/*
 * Copyright 2014 the original author or authors.
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
package org.springframework.test.web.servlet.htmlunit.webdriver;

import com.gargoylesoftware.htmlunit.WebClient;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebConnection;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

/**
 * <p>
 * Builder that simplifies creation of an adapted {@link }HtmlUnitDriver} that
 * allows running tests off line by utilizing Spring's {@link MockMvc} to
 * bridge between a request and a response. By doing this we are able to avoid
 * making any HTTP calls when our tests are running. This implies we do not
 * need to start a web container to run our tests.
 * </p>
 * <p>
 * Example usage can be seen below:
 * </p>
 * <h2>Instantiate with WebApplicationContext</h2>
 *
 * <pre>
 * WebApplicationContext context = ...
 * HtmlUnitDriver driver = MockMvcHtmlUnitDriverBuilder.connectTo(context).build();
 *
 * ... use driver as you would a "normal" HtmlUnitDriver ...
 * </pre>
 *
 * <h2>Instantiate with MockMvc</h2>
 *
 * <pre>
 * MockMvc mockMvc = ...
 * HtmlUnitDriver driver = MockMvcHtmlUnitDriverBuilder.connectTo(mockMvc).build();
 *
 * ... use driver as you would a "normal" HtmlUnitDriver ...
 * </pre>
 *
 * <h2>Instantiate with your own MockMvcWebConnection</h2>
 * <p>
 * Sometimes it is useful to adapt the settings of the
 * {@code MockMvcWebConnection} used. For example it can be necessary to tune
 * the connection if you want to set the {@code contextPath} used. For example
 * if you want to configure no context path at all you can use:
 * </p>
 *
 * <pre>
 * MockMvc mockMvc = ...
 * MockMvcWebConnection connection = new MockMvcWebConnection(mockMvc, "");
 * HtmlUnitDriver driver = MockMvcHtmlUnitDriverBuilder.via(connection).build();
 *
 * ... use driver as you would a "normal" HtmlUnitDriver ...
 * </pre>

 *
 * @author Stefan Pennndorf
 * @see MockMvc
 * @see MockMvcWebConnection
 *
 */
public final class MockMvcHtmlUnitDriverBuilder {

    private final MockMvcWebConnection connection;

    private final Capabilities capabilities;

    private MockMvcHtmlUnitDriverBuilder(MockMvcWebConnection connection) {
        this(connection, null);
    }

    private MockMvcHtmlUnitDriverBuilder(MockMvcWebConnection connection, Capabilities capabilities) {
        Assert.notNull(connection, "connection cannot be null");
        this.connection = connection;
        this.capabilities = capabilities;
    }

    public static MockMvcHtmlUnitDriverBuilder connectTo(final MockMvc mockMvc) {
        final MockMvcWebConnection webConnection = new MockMvcWebConnection(mockMvc);
        return new MockMvcHtmlUnitDriverBuilder(webConnection);
    }

    public static MockMvcHtmlUnitDriverBuilder connectTo(final WebApplicationContext webContext) {
        Assert.notNull(webContext, "webContext cannot be null");
        final MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webContext).build();
        return connectTo(mockMvc);
    }

    public static MockMvcHtmlUnitDriverBuilder via(MockMvcWebConnection connection) {
        return new MockMvcHtmlUnitDriverBuilder(connection);
    }

    public MockMvcHtmlUnitDriverBuilder with(Capabilities capabilities) {
        return new MockMvcHtmlUnitDriverBuilder(connection, capabilities);
    }

    public HtmlUnitDriver build() {
        class MockMvcHtmlUnitDriver extends HtmlUnitDriver {
            private MockMvcHtmlUnitDriver() {
                super();
            }

            private MockMvcHtmlUnitDriver(final Capabilities capabilities) {
                super(capabilities);
            }

            @Override
            protected WebClient modifyWebClient(WebClient client) {
                client = super.modifyWebClient(client);
                client.setWebConnection(connection);
                return client;
            }
        }

        return (capabilities == null)
                ? new MockMvcHtmlUnitDriver()
                : new MockMvcHtmlUnitDriver(capabilities);
    }
}
