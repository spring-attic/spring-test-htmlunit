/*
 * Copyright 2014 the original author or authors.
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
package org.springframework.test.web.servlet.htmlunit.webdriver;

import com.gargoylesoftware.htmlunit.WebClient;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.HelloController;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebConnection;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.fest.assertions.Assertions.assertThat;

/**
 *
 * @author Stefan Penndorf
 */
public class MockMvcHtmlUnitDriverBuilderTest {

    private MockMvc mockMvc;

    private MockMvcWebConnection connection;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new HelloController())
                .build();
        connection = new MockMvcWebConnection(mockMvc);
    }

    @Test
    public void canBuildDriverFromMockMvc() {
        final HtmlUnitDriver driver = MockMvcHtmlUnitDriverBuilder.connectTo(mockMvc).build();

        assertThat(driver).isNotNull();
    }

    @Test
    public void canBuildViaMockMvcConnection() {
        final HtmlUnitDriver driver = MockMvcHtmlUnitDriverBuilder.via(connection).build();

        assertThat(driver).isNotNull();
    }


    @Test
    public void usesMockMvcWebConnection() {
        final HtmlUnitDriver driver = MockMvcHtmlUnitDriverBuilder.connectTo(mockMvc).build();

        final WebClient webClient = (WebClient) ReflectionTestUtils.getField(driver, "webClient");
        assertThat(webClient.getWebConnection()).isInstanceOf(MockMvcWebConnection.class);
    }

    @Test
    public void usesIndividualMockMvcWebConnectionPassedToVia() {
        final HtmlUnitDriver driver = MockMvcHtmlUnitDriverBuilder.via(connection).build();

        final WebClient webClient = (WebClient) ReflectionTestUtils.getField(driver, "webClient");
        assertThat(webClient.getWebConnection()).isSameAs(connection);
    }


    @Test(expected = IllegalArgumentException.class)
    public void requiresMockMvc() {
        MockMvcHtmlUnitDriverBuilder.connectTo((MockMvc) null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void requiresWebApplicationContext() {
        MockMvcHtmlUnitDriverBuilder.connectTo((WebApplicationContext) null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void requiresMockMvcWebConnection() {
        MockMvcHtmlUnitDriverBuilder.via(null).build();
    }

}