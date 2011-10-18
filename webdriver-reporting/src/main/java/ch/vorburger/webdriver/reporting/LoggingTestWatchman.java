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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.rules.TestWatchman;
import org.junit.runners.model.FrameworkMethod;

/**
 * JUnit (v4) Rule which logs all test failures, starts, and ends.
 *
 * Used like this:
 *
 * <pre>
 * public static class WatchmanTest {
 *
 *     &#064;Rule
 *     public MethodRule logRule = new LoggingTestWatchman(...);
 *
 *     &#064;Test
 *     public void fails() {
 *         fail("I'm a loser baby, so why don't you kill me?");
 *     }
 * </pre>
 *
 * This class should not have any or WebDriver or actual Report Writing dependencies/imports, only JUnit.
 *
 * @author Michael Vorburger
 */
public class LoggingTestWatchman extends TestWatchman {

	private final TestCaseReportWriter reportWriter;

	public LoggingTestWatchman(TestCaseReportWriter reportWriter) {
		this.reportWriter = reportWriter;
	}

	@Override
	public void starting(FrameworkMethod method) {
		reportWriter.clearInfoString();
		reportWriter.createNewTestReportFile(method, getReportFileName(method));
		reportWriter.info("Start Test: " + testName(method));
		reportWriter.getPackageName("Start Test: " + testName(method) + " :Package Name " + packageName(method));
		reportWriter.addTestClassNameToJS(createPackageNamewithTestName(method));
	}

	@Override
	public void finished(FrameworkMethod method) {
		reportWriter.info("End Test: " + testName(method));
	}

	@Override
	public void failed(Throwable t, FrameworkMethod method) {
		reportWriter.info("STACKTRACE:" + getStackTrace(t));
		reportWriter.info("Failed: " + testName(method));
		reportWriter.addFailedTestClassNameToJS(createPackageNamewithTestName(method));
	}

	@Override
	public void succeeded(FrameworkMethod method) {
		reportWriter.info("Test succeeded: " + testName(method));
		super.succeeded(method);
	}

	private String getStackTrace(Throwable throwable) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		throwable.printStackTrace(printWriter);
		return writer.toString();
	}

	private String testName(FrameworkMethod method) {
		return method.getMethod().getDeclaringClass().getCanonicalName() + "." + method.getName();
	}

	private String packageName(FrameworkMethod method) {
		return method.getMethod().getDeclaringClass().getPackage().getName();
	}

	private String extractPackageName(String fullPackageName) {
		int indx = fullPackageName.lastIndexOf(".");
		fullPackageName = fullPackageName.substring(indx + 1, fullPackageName.length());
		return fullPackageName.toUpperCase();
	}

	private String getReportFileName(FrameworkMethod method) {
		return extractPackageName(packageName(method)) + "/"
				+ getLogFileName(method.getMethod().getDeclaringClass().getCanonicalName(),
						method.getMethod().getDeclaringClass().getCanonicalName()
						+ "." + method.getName())
				+ "_log.html";
	}

	private String getLogFileName(String className, String methodName) {
		int indx = methodName.lastIndexOf(".");
		methodName = methodName.substring(indx + 1, methodName.length());

		return methodName;
	}

	private String createPackageNamewithTestName(FrameworkMethod method) {
		String methodName = method.getName();
		String packageName = extractPackageName(packageName(method));
		return packageName + "." + methodName;
	}

}
