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
package sample.webdriver.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import sample.data.Message;

/**
 * Represents the page where the {@link Message} details can be viewed.
 *
 * @author Rob Winch
 *
 */
public class ViewMessagePage extends AbstractPage {
	@FindBy(className = "alert-success")
	private WebElement success;

	private WebElement id;

	private WebElement created;

	private WebElement summary;

	private WebElement text;

	public ViewMessagePage(WebDriver driver) {
		super(driver);
	}

	public String getSuccess() {
		return success.getText();
	}

	public String getId() {
		return id.getText();
	}

	public String getCreated() {
		return created.getText();
	}

	public String getSummary() {
		return summary.getText();
	}

	public String getText() {
		return text.getText();
	}
}
