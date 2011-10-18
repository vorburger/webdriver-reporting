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

package ch.vorburger.webdriver.reporting;

import java.io.File;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.openqa.selenium.support.events.EventFiringWebDriver;


/**
 * A WebDriverEventListener which logs method calls.
 *
 * This class should not have any JUnit or I/O dependencies/imports, only WebDriver.
 *
 * @author Michael Vorburger
 */
public class LoggingWebDriverEventListener extends AbstractWebDriverEventListener {

	private final TestCaseReportWriter clsObj;

	public LoggingWebDriverEventListener(TestCaseReportWriter object) {
		clsObj = object;
	}

	@Override
	public void beforeClickOn(WebElement element, WebDriver driver) {
//		try {
//			if (getTagNameSafely(element) != null) {
//				if ((getTagNameSafely(element).equalsIgnoreCase("span"))
//						|| (getTagNameSafely(element).equalsIgnoreCase("button"))
//						|| (getTagNameSafely(element).equalsIgnoreCase("div"))
//						|| (getTagNameSafely(element).equalsIgnoreCase("img"))) {

					logAndTakeSnapShot(driver, element, "Before Clicking");
//				}
//			}
//		} catch (RuntimeException e) {
//		}
	}

	@Override
	public void beforeNavigateBack(WebDriver driver) {
	}

	@Override
	public void afterChangeValueOf(WebElement element, WebDriver driver) {
		try {
			String tagName = getTagNameSafely(element);
			if ("input".equalsIgnoreCase(tagName) || "select".equalsIgnoreCase(tagName) ) {
				logAndTakeSnapShot(driver, element, "Setting value '" + getValueSafely(element) + "' on");
			}
		} catch (RuntimeException e) {
			// "Shit happens" - guess we can't log this one then :-(
		}
	}

	@Override
	public void afterClickOn(WebElement element, WebDriver driver) {
		logAndTakeSnapShot(driver, element, "After Clicking");
	}

	@Override
	public void beforeNavigateForward(WebDriver driver) {
		clsObj.infoWithFlag(" navigateForward");
	}

	@Override
	public void beforeNavigateTo(String url, WebDriver driver) {
		clsObj.infoWithFlag(" Go to URL " + url);
	}

	/**
	 * This method will take snap shots of screens and save them.
	 *
	 * @param driver WebDriver
	 * @param element WebElement
	 * @param log message from the action
	 */
	protected void logAndTakeSnapShot(WebDriver driver, WebElement element, String log) {
		addStyleBeforeSnapShot(element, driver);

		if (driver instanceof EventFiringWebDriver) {
			EventFiringWebDriver eventFiringWebDriver = (EventFiringWebDriver) driver;
			driver = eventFiringWebDriver.getWrappedDriver();
		}
		if (driver instanceof TakesScreenshot) {
			TakesScreenshot takesScreenshotWebDriver = (TakesScreenshot) driver;
			File srcFile = takesScreenshotWebDriver.getScreenshotAs(OutputType.FILE);
			log(element, log, srcFile);
			removeStyleafterSnapShot(element, driver);
		} else {
			log(element, log, null);
		}
	}

	/**
	 * Log, with a screenshot.
	 *
	 * @param element the WebElement, to give context, can be null if the previous message already gave it
	 * @param message the message to log, never null
	 * @param screenshot the Screenshot File, can be null if no screenshot is to be logged. The File is copied.
	 */
	private void log(WebElement element, String message, File screenshot) {
		StringBuilder sb = new StringBuilder();
		sb.append(message);
		if (element != null) {
			sb.append(" ");
			String userVisibleLabel = getUserVisibleText(element);
			if (userVisibleLabel != null) {
				sb.append("'" + userVisibleLabel + "', ");
			}
			sb.append("element");
			if (getAttributeSafely(element, "id") != null) {
				String id = getAttributeSafely(element, "id");
				sb.append(" with ID " + id);
			}
			// Show the name only if there is no ID (id is stronger)
			else if (getAttributeSafely(element, "name") != null) {
				sb.append(" with name " + element.getAttribute("name"));
			}
		}
		clsObj.infoWithFlagAndScreenshot(sb.toString(), screenshot);
	}

	/**
	 * Clearest is an element text (content), if none (e.g. icons) try title, else alt.
	 */
	private String getUserVisibleText(WebElement element) {
		String text = getTextSafely(element);
		String title = getAttributeSafely(element, "title");
		String alt = getAttributeSafely(element, "alt");
		if ((text != null) && (!text.trim().isEmpty())) {
			return text;
		} else if (title != null) {
			return title;
		} else if (alt != null) {
			return alt;
		} else {
			return null;
		}
	}

	private String getTextSafely(WebElement element) {
		try {
			return element.getText();
		} catch (Exception e) {
			// If we couldn't get the attribute, something is wrong, it probably
			// doesn't have one, so let's just return null:
			return null;
		}
	}

	private String getAttributeSafely(WebElement element, String attributeName) {
		try {
			return element.getAttribute(attributeName);
		} catch (WebDriverException e) {
			// If we couldn't get the attribute, something is wrong, it probably
			// doesn't have one, so let's just return null:
			return null;
		}
	}

	private String getValueSafely(WebElement element) {
	  return getAttributeSafely(element, "value");
	}

	private String getTagNameSafely(WebElement element) {
		try {
			return element.getTagName();
		} catch (WebDriverException e) {
			// If we couldn't get the attribute, something is wrong, it probably
			// doesn't have one, so let's just return null:
			return null;
		}
	}

	public void addStyleBeforeSnapShot(WebElement element, WebDriver driver) {
		String webElementId;
		if (element != null) {
			try {
				webElementId = element.getAttribute("id");
			} catch (StaleElementReferenceException e) {
				webElementId = null;
			}

			if (webElementId != null && ! webElementId.isEmpty() && driver instanceof JavascriptExecutor) {
				try {
					((JavascriptExecutor) driver).executeScript("document.getElementById('" + webElementId
							+ "').setAttribute('style','border:solid 2px #73A6FF;background:#EFF5FF;')", "");
				} catch (Throwable e) {
					// Highlight ON didn't work, tant pis.
				}
			}
		}
	}

	public void removeStyleafterSnapShot(WebElement element, WebDriver driver) {
		String webElementId;
		if (element != null) {
			webElementId = getAttributeSafely(element, "id");

			if (webElementId != null && ! webElementId.isEmpty() && driver instanceof JavascriptExecutor) {
				try {
					((JavascriptExecutor) driver).executeScript("document.getElementById('" + webElementId
							+ "').setAttribute('style','border:;background:;')", "");
				} catch (Throwable e) {
					// Highlight OFF didn't work, tant pis.
				}
			}
		}
	}
}
