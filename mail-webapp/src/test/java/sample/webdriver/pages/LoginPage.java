/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package sample.webdriver.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Represents the page where a {@link sample.data.Message} is created.
 *
 * @author Rob Winch
 *
 */
public class LoginPage extends AbstractPage {

    private WebElement username;

    private WebElement password;

    private WebElement submit;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void login(String username, String password) {
        this.username.sendKeys(username);
        this.password.sendKeys(password);
        this.submit.click();
    }

    public static void login(WebDriver driver) {
        get(driver, "login");
        LoginPage loginPage = PageFactory.initElements(driver, LoginPage.class);
        loginPage.login("user","password");
    }
}