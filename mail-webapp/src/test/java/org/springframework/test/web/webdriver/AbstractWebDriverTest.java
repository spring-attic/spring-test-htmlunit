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
package org.springframework.test.web.webdriver;

import org.junit.AfterClass;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * Abstract Test that automatically sets up and shuts down the {@link WebDriver}.
 *
 * @author Rob Winch
 *
 */
public class AbstractWebDriverTest {
	private static WebDriver driver;

	@Before
	public void setup() {
		if (driver == null) {
			driver = createDriver();
		}
	}

	/**
	 * Creates the {@link WebDriver} instance. Can be overriden by subclasses.
	 * @return
	 */
	protected WebDriver createDriver() {
		return new HtmlUnitDriver(true);
	}

	@AfterClass
	public static void destroy() {
		driver.close();
		driver = null;
	}

	protected final WebDriver getDriver() {
		return driver;
	}
}
