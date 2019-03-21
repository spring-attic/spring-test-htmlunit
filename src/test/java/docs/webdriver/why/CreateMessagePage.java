/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package docs.webdriver.why;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * @author Rob Winch
 */
 // tag::class[]
public class CreateMessagePage {
  	HtmlPage currentPage;

	HtmlTextInput summaryInput;

	HtmlSubmitInput submit;

	public CreateMessagePage(HtmlPage currentPage) {
		this.currentPage = currentPage;
		this.summaryInput = currentPage.getHtmlElementById("summary");
		this.submit = currentPage.getHtmlElementById("submit");
	}

	public <T> T createMessage(String summary, String text) throws Exception {
		setSummary(summary);

		HtmlPage result = submit.click();
		boolean error = CreateMessagePage.at(result);

		return (T) (error ? new CreateMessagePage(result) : new ViewMessagePage(result));
	}

	public void setSummary(String summary) throws Exception {
		summaryInput.setValueAttribute(summary);
	}

	public static boolean at(HtmlPage page) {
		return "Create Message".equals(page.getTitleText());
	}
}
// end::class[]
