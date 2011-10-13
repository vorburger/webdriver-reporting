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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
 * @author Michael Vorburger
 */
public class LoggingTestWatchman extends TestWatchman {

	private final TestCaseLogFileWriter claObject;
	private final TestReportWriter customLayoutObj = new TestReportWriter();

	public LoggingTestWatchman(TestCaseLogFileWriter claObj) {
		this.claObject = claObj;
	}

	@Override
	public void starting(FrameworkMethod method) {
		claObject.clearInfoString();
		createFile(method).info("Start Test: " + testName(method));
		createOrUpdateIndexHtmlFile("Start Test: " + testName(method) + " :Package Name " + packageName(method));
		new TestReportWriter().addTestClassNameToJS(createPackageNamewithTestName(method));
	}

	@Override
	public void finished(FrameworkMethod method) {
		claObject.info("End Test: " + testName(method));
	}

	@Override
	public void failed(Throwable t, FrameworkMethod method) {
		claObject.info("STACKTRACE:" + getStackTrace(t));
		claObject.info("Failed: " + testName(method));
		new TestReportWriter().addFailedTestClassNameToJS(createPackageNamewithTestName(method));
	}

	@Override
	public void succeeded(FrameworkMethod method) {
		claObject.info("Test succeeded: " + testName(method));
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

	private TestCaseLogFileWriter createFile(FrameworkMethod method) {
		BufferedWriter bufferedWriter = null;
		try {
			String directoryStructure = System.getProperty("user.dir") + "/target/surefire-reports/tests/"
					+ extractPackageName(packageName(method));
			File file = new File(directoryStructure + "/"
					+ getLogFileName(method.getMethod().getDeclaringClass().getCanonicalName(), method.getMethod()
							.getDeclaringClass().getCanonicalName()
							+ "." + method.getName()) + "_log.html");
			if (!file.exists()) {
				File tempFile = new File(directoryStructure);
				tempFile.mkdirs();
				file = new File(directoryStructure + "/"
						+ getLogFileName(method.getMethod().getDeclaringClass().getCanonicalName(), method.getMethod()
								.getDeclaringClass().getCanonicalName()
								+ "." + method.getName()) + "_log.html");

			}
			bufferedWriter = new BufferedWriter(new FileWriter(file.getPath()));
			bufferedWriter.write(claObject.getHeader());
			claObject.setLogFile(file);

		} catch (IOException e) {
			throw new RuntimeException("Oups, could't create WebDriver Report Log files?!", e);
		} finally {
			try {
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException ex) {
				// Zucchero says "Nothing To Loose" if this happens - ignore.
			}
		}
		return claObject;
	}

	/**
	 * This method will generate the index file of log HTML.
	 * It will be used to* integrate all the individual test cases.
	 *
	 * @param packageName Name of Package
	 */
	private void createOrUpdateIndexHtmlFile(String packageName) {
		BufferedWriter bufferedWriter = null;
		try {

			File file = new File(System.getProperty("user.dir") + "/target/surefire-reports/tests/logReport.html");
			if (!file.exists()) {
				bufferedWriter = new BufferedWriter(new FileWriter(file.getPath()));
				bufferedWriter.write(customLayoutObj.getHeader());
				customLayoutObj.getPackageName(packageName);
			} else {
				customLayoutObj.getPackageName(packageName);
			}

		} catch (IOException e) {
			throw new RuntimeException("Oups, could't create WebDriver Report Log files?!", e);
		} finally {
			try {
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException ex) {
				// Zucchero says "Nothing To Loose" if this happens - ignore.
			}
		}
	}

	private String extractPackageName(String fullPackageName) {
		int indx = fullPackageName.lastIndexOf(".");
		fullPackageName = fullPackageName.substring(indx + 1, fullPackageName.length());
		return fullPackageName.toUpperCase();
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
