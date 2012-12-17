Spring Test MVC and HtmlUnit Integration
=======================

This project's aim is to provide integration between [Spring Test MVC](https://github.com/SpringSource/spring-test-mvc) and [HtmlUnit](http://htmlunit.sourceforge.net/). This allows for developers to reuse code between their integration tests and end to end tests. It also makes for cleaner API's to use for testing HTML based views that use templates. Note JSP views would not benefit from this project since they cannot easily be renered outside of the container.

Highlights
===========
* [Sample project](#running-the-sample-project)
* We are able to test from our services with Spring Test MVC all the way to the views easily using libraries that support HtmlUnit
* Improves the speed of tests giving faster feedback to developers
* Since we use HtmlUnit, our Spring MVC Test's can test flows that involve javascript. See MockMvcCreateMessageTest#missingFieldWithJavascriptValidationDisplaysError. Note that the method is actually in its super class CreateMessageITest which demos that you can use the same code for end to end tests as integration tests
* [WebDriver](http://seleniumhq.org/projects/webdriver/) integration
	* While still running writing code using the WebDriver APIs we can run our code quickly using HtmlUnit and using Sprint MVC Test with no need to start an application server
	* Often times, the same test can be ran in full integration tests simply by swapping the WebDriver implementation. Observe that MockMvcCreateMessageTest runs the same code to test as CreateMessageITest. However, MockMvcCreateMessageTest does not need to have the application deployed to a server.
	* Using the [Page Object](http://code.google.com/p/selenium/wiki/PageObjects) pattern allows our end to end tests and full integration tests to share most of the same logic
	* We can use mock services in our tests
	* Allows for testing of edge cases. For example, we could know that there are 0 results, the creation date is exactly at a specific time, what is rendered with any error condition, etc. This is much more difficult and time consuming to do with real services since the state of the database must be consistently cleaned up.
	* Further improves the speed of our tests
	* For an example of using mock services see the MockitoMvcCreateMessageTest which asserts the Message.created and Message.id exactly rather than only being able to ensure they are non-empty Strings.
* [Geb Spock](http://www.gebish.org/manual/current/testing.html#spock_junit__testng) integration
	* Gain the same benefits of WebDriver, but use Geb Spock integration instead
	* See MockMvcCreateMessagesSpec for an example using Geb with Spring Test MVC
	* See CreateMessagesISpec for a comparison of how Geb is used with standard HtmlUnitDriver (i.e. WebDriver)

Running the sample project
==================

The following provides information on setting up a development environment that can run the sample in [Spring Tool Suite 3.0.0](http://www.springsource.org/sts). Other IDE's should work using Gradle's IDE support, but have not been tested.

* IDE Setup
	* Install Spring Tool Suite 3.0.0+
	* You will need the following plugins installed (can be found on the Extensions Page)
	* Gradle Eclipse
	* Groovy Eclipse plugin
	* Groovy 1.8 compiler should be enabled in Window->Preferences Groovy->Compiler
* Importing the project into Spring Tool Suite
	* File->Import...->Gradle Project

Any tests ending in ITest or ISpec require the application to be deployed to http://localhost:9990/mail/. You should be able to do this easily using Eclipse WTP. Other tests run using Sprint Test MVC and do not require the application to be deployed.

Minimum JDK
==============
The miniumum JDK is 1.6 Not only has [Java 1.5 reached EOL](http://www.oracle.com/technetwork/java/eol-135779.html ), but this is necessary to keep up to date with dependencies since [Selenium requires JDK 1.6](https://groups.google.com/forum/#!searchin/selenium-developers/java$206/selenium-developers/aB5NqZkJIpQ/VDZhrLuh7IIJ).

Contributing
==============
Before contributing or logging an issue please be sure to the issue does not already exist in this project's [issue tracking](https://github.com/rwinch/spring-test-mvc-htmlunit/issues). If one does not exist, please create an issue.

If you see anything you'd like to change we encourage taking advantage of github's social coding features by making the change in a [fork of this repository](http://help.github.com/forking/) and sending a pull request.

Before we accept a non-trivial patch or pull request we will need you to sign the [contributor's agreement](https://support.springsource.com/spring_committer_signup). Signing the contributor's agreement does not grant anyone commit rights to the main repository, but it does mean that we can accept your contributions, and you will get an author credit if we do. Active contributors might be asked to join the core team, and given the ability to merge pull requests.

License
==============
The Spring Test MVC HtmlUnit project is available under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).

