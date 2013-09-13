/*
 * Copyright 2011 Michael Vorburger & other contributors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package ch.vorburger.webdriver.reporting.tests;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ch.vorburger.webdriver.reporting.LoggingTestWatchman;
import ch.vorburger.webdriver.reporting.LoggingWebDriverEventListener;
import ch.vorburger.webdriver.reporting.TestCaseReportWriter;

/**
 * Example & Test for Selenium-WebDriver HTML Report.
 *
 * @author Michael Vorburger
 */
public class SampleGoogleSearchReportTest {

	static private final TestCaseReportWriter LOG_FILE_WRITER = new TestCaseReportWriter();

	@Rule
	public MethodRule logRule = new LoggingTestWatchman(LOG_FILE_WRITER);

	@Test
	public void testGoogleSearch() {
		EventFiringWebDriver driverWithReporting;
		{
			// System.setProperty("webdriver.chrome.driver", "/opt/google/chrome/chrome");
			// WebDriver driver = new ChromeDriver();
			WebDriver driver = new FirefoxDriver(); 

			WebDriverEventListener loggingListener = new LoggingWebDriverEventListener(LOG_FILE_WRITER);;
			driverWithReporting = new EventFiringWebDriver(driver);
			driverWithReporting.register(loggingListener);
		}

		driverWithReporting.get("http://www.google.com");
		WebElement element = driverWithReporting.findElement(By.name("q"));
		element.sendKeys("Mifos");
		element.submit();
		
        (new WebDriverWait(driverWithReporting, 10))
        	.until(ExpectedConditions.presenceOfElementLocated(By.id("bfoot")));
        
        driverWithReporting.quit();
	}
}
