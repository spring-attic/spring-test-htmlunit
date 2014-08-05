package org.springframework.test.web.servlet.htmlunit.matchers;

import com.gargoylesoftware.htmlunit.WebRequest;

/**
 * Strategy to match on a WebRequest
 *
 * @author Rob Winch
 */
public interface WebRequestMatcher {
	/**
	 * Return true if matches on WebRequest, else false
	 *
	 * @param request the WebRequest to attempt to match on
	 * @return true if matches on WebRequest, else false
	 */
	boolean matches(WebRequest request);
}
