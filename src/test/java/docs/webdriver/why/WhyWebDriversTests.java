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
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * @author Rob Winch
 */
public class WhyWebDriversTests {

	// tag::create-message[]
	public HtmlPage createMessage(HtmlPage currentPage, String summary, String text) {
		setSummary(currentPage, summary);
		// end::create-message[]
		return null;
	}

	// tag::set-summary[]
	public void setSummary(HtmlPage currentPage, String summary) {
		// tag::set-summary-input[]
		HtmlTextInput summaryInput = currentPage.getHtmlElementById("summary");
		summaryInput.setValueAttribute(summary);
		// end::set-summary-input[]
	}
	// end::set-summary[]
}
