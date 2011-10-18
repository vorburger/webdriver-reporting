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
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.runners.model.FrameworkMethod;

/**
 * Report Writer.
 *
 * This class should not have any JUnit or WebDriver dependencies/imports, only I/O.
 *
 * @author Nasir Raza
 * @author Michael Vorburger
 */
public class TestCaseReportWriter
{
	private static final String START_TEST = "Start";
	private static final String END_TEST = "End";
	private static final String LOG_FLAG = "Logflag";
	private static final String LINE_SEP = System.getProperty("line.separator");
	private static final String APPENDED_JS = "jquery-ui-1.8.5.custom.min.js";
	private final static String SCREENSHOTS_DIR_NAME = "screenshots/";

	private final List<String> packageNamesList = new LinkedList<String>();
	private final File reportDirFile = new File(System.getProperty("user.dir") + "/target/webdriver-reporting/");
	private final File jsFile = new File(reportDirFile, APPENDED_JS);
	private final File screenshotsDirFile = new File(reportDirFile, SCREENSHOTS_DIR_NAME);

	private StringBuffer infoString = new StringBuffer("");
	private File logFile;


	private StringBuffer getInfoString() {
		return infoString;
	}

	private void setInfoString(StringBuffer infoString) {
		this.infoString = infoString;
	}

	/* package local */
	void clearInfoString(){
		setInfoString(new StringBuffer(""));
	}

	public File getLogFile() {
		return logFile;
	}

	public void setLogFile(File logFile) {
		this.logFile = logFile;
	}

	public void createNewTestReportFile(FrameworkMethod method, String reportFileName) {
		BufferedWriter bufferedWriter = null;
		try {
			File file = new File(reportDirFile, reportFileName);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			bufferedWriter = new BufferedWriter(new FileWriter(file));
			bufferedWriter.write(getHeader());
			setLogFile(file);

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

	/**
	 * Returns appropriate HTML headers.
	 */
	protected String getHeader() {
		// TODO Should copy this out from a HTML fragment (!) on classpath... just need to deal with Date below
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">" + LINE_SEP);
		sbuf.append("<html>" + LINE_SEP);
		sbuf.append("<head>" + LINE_SEP);
		sbuf.append("<title>WebDriver Report</title>" + LINE_SEP);
		sbuf.append(" <LINK href=\"../jquery-ui-1.8.5.custom.css\" rel=\"stylesheet\" type=\"text/css\">");
		sbuf.append(" <LINK href=\"../style.css\" rel=\"stylesheet\" type=\"text/css\">");
		sbuf.append("<script type=\"text/javascript\" src=\"../jquery-1.4.2.min.js\"></script>");
		sbuf.append("<script type=\"text/javascript\" src=\"../jquery-ui-1.8.5.custom.min.js\"></script>");
		sbuf.append("<script type=\"text/javascript\" src=\"../util.js\"></script>");
		sbuf.append("<style type=\"text/css\">" + LINE_SEP);
		sbuf.append("<!--" + LINE_SEP);
		sbuf.append("body, table {font-family: arial,sans-serif; font-size: x-small;}.buttonStyle { color: #900; border: 1px solid #900; font-weight: bold;  float: right;}" + LINE_SEP);
		sbuf.append("table{table-layout: fixed;}td{text-align: left;padding-top: 4px;padding-bottom: 4px;padding-left: 8px;padding-right: 0px;}th {background: #336699; color: #FFFFFF; text-align: left;}.timeClass{width:5%;}" + LINE_SEP);
		sbuf.append("-->" + LINE_SEP);
		sbuf.append("</style>" + LINE_SEP);

		sbuf.append("</head>" + LINE_SEP);
		sbuf.append("<body onload=\"init();\" bgcolor=\"#D8D8D8\" topmargin=\"0\" leftmargin=\"0\">" + LINE_SEP);
		sbuf.append("<font size=\"2\">Log session start time " + new java.util.Date() + "</font><br>" + LINE_SEP);
		sbuf.append("<hr size=\"1\" noshade>" + LINE_SEP);
		sbuf.append("<div id=\"status\" width=\"100%\"> </div>" + LINE_SEP);

		return sbuf.toString();
	}

	public void infoWithFlag(String message) {
		info(LOG_FLAG + message);
	}

	public void info(String message) {
		// Create all the required files to run this log HTML file.
		createAdditionalFiles();
		String rowClass = "row";
		boolean isBizLog = false;
		boolean stackTraceFlag = false;
		String methodName = "";
		String temp = "";
		infoString = getInfoString();
		if (isBizLog) {
			rowClass = "rowBiz";
		}
		if (message.contains(".png")) {
			rowClass = "rowImg";
		}

		boolean startTestFlag = message.trim().startsWith(START_TEST);
		boolean endTestFlag = message.trim().startsWith(END_TEST);
		long tableId = System.currentTimeMillis();
		double randomId = Math.random();

		// Check if there is stack trace
		if (message.contains("STACKTRACE")) {
			//rowClass = "rowStackTrace";
			stackTraceFlag = true;
			// Extract the method name
			//int indx1 = message.indexOf("STACKTRACE");
			//message = message.substring(indx1+1,message.length());
			//indx1 = methodName.indexOf(":");
			//methodName = methodName.substring(indx1 + 1, methodName.length());
		}

		if (startTestFlag) {
			int indx2 = message.lastIndexOf(".");
			String methodName1 = message.substring(indx2 + 1, message.length());

			infoString.append(LINE_SEP + "<div class=\"ui-tabs ui-widget ui-widget-content ui-corner-all\" id=\""
					+ tableId + "\" ALIGN=\"left\" border=\"1\" width=\"100%\">" + LINE_SEP);
			infoString.append(LINE_SEP + "<div class=\"container\" id=\"" + tableId + "container\" name=\""
					+ methodName1 + "\">");
			infoString.append("<div class=\"row\">" + LINE_SEP);
		} else {
			if (stackTraceFlag) {
				infoString.append("<div class=\"" + rowClass + "\" style=\"display:none;\" id=\"" + methodName + "\">"
						+ LINE_SEP);
			} else {
				infoString.append("<div class=\"" + rowClass + "\" style=\"display:none;\">" + LINE_SEP);
			}
		}

		infoString.append("<div class=\"cell1\">");

		// sbuf.append(event.timeStamp - LoggingEvent.getStartTime());
		//infoString.append(tableId);
		// ts = event.timeStamp;
		infoString.append("</div>" + LINE_SEP);

		// String escapedLogger = event.getLoggerName();

		// Check if the message has images's name as well.
		int indx = message.indexOf("^");

		// if (event.getLevel().equals(Level.TRACE)) {
		if (indx > 0) {
			//if(message.contains(""))
			temp = message.substring(indx + 1, message.length());
		} else {
			temp = message;
		}

		// }

		if (message.indexOf(LOG_FLAG) < 0) {
			if (startTestFlag) {
				infoString.append("<div class=\"cell2\">");
				infoString.append("<input class=\"ui-button ui-widget ui-state-default ui-corner-all buttonStyle\" id=\""
						+ tableId
						+ "showId\" type=\"button\" align=\"right\" value=\"Show Technical Logs\" onclick=\"showLog("
						+ tableId + ")\" />");

				infoString.append("<input class=\"ui-button ui-widget ui-state-default ui-corner-all buttonStyle\" style=\"display:none;\" id=\""
								+ tableId
								+ "hideId\" type=\"button\" align=\"right\" value=\"Hide Technical Logs\" onclick=\"hideLog("
								+ tableId + ")\" />");

				infoString.append("<input class=\"ui-button ui-widget ui-state-default ui-corner-all buttonStyle\" id=\""
								+ tableId
								+ "showIdBiz\" type=\"button\" align=\"right\" value=\"Show Business Logs\" onclick=\"showLogBiz("
								+ tableId + ")\" />");

				infoString.append("<input class=\"ui-button ui-widget ui-state-default ui-corner-all buttonStyle\" style=\"display:none;\" id=\""
								+ tableId
								+ "hideIdBiz\" type=\"button\" align=\"right\" value=\"Hide Business Logs\" onclick=\"hideLogBiz("
								+ tableId + ")\" />");

			} else if (!stackTraceFlag) {
				infoString.append("<div class=\"cell2\">");
				infoString.append("Class: " + message);
			} else if (stackTraceFlag) {
				infoString.append("<div class=\"cell2\">");
				infoString.append(message);
			} else {
				infoString.append("<div class=\"cell2\">");
				infoString
						.append("<span>Test Method: "
								+ methodName
								+ "<input class=\"ui-button ui-widget ui-state-default ui-corner-all buttonStyle\" id=\""
								+ (tableId + randomId)
								+ "showId\" type=\"button\" align=\"right\" value=\"Show Stacktrace\" onclick=\"showST('"
								+ (tableId + randomId)
								+ "')\" />"
								+ "<input class=\"ui-button ui-widget ui-state-default ui-corner-all buttonStyle\" style=\"display:none;\" id=\""
								+ (tableId + randomId)
								+ "hideId\" type=\"button\" align=\"right\" value=\"Hide Stacktrace\" onclick=\"hideST('"
								+ (tableId + randomId) + "')\" /></span>");
			}
		} else {
			infoString.append("<div class=\"cell2\">");
		}

		if(message.contains(LOG_FLAG)){
			int flagIndex = message.indexOf(LOG_FLAG);
			message = message.substring(flagIndex+7,message.length());
		}

		if (indx > 0) {
			infoString.append("<span class=\"spanClass\">Action taken: " + message.substring(0, indx) + "</span>");
		} else {
			if (startTestFlag) {
				infoString.append("<span class=\"spanClass\">Test Name: " + message + "</span>");
			} else if (stackTraceFlag) {
				int tempIndx = message.indexOf("$");
				infoString.append("<span class=\"spanClassST\" id=\"" + (tableId + randomId) + "ST\">Exception Message: "
						+ message.substring(tempIndx + 1, message.length()) + "</span>");
			} else {
				infoString.append("<span class=\"spanClass\">Action taken: " + message + "</span>");
			}
		}

		if (temp.length() > 0) {
			if (temp.contains(".png")) {
				infoString.append("<img src='../" + SCREENSHOTS_DIR_NAME + temp + "' alt='test'></img>");
			}
		} else {
			infoString.append(temp);
		}
		infoString.append("</div>" + LINE_SEP);
		infoString.append("</div>" + LINE_SEP);

		if (endTestFlag) {
			infoString.append(LINE_SEP + "</div></div>" + LINE_SEP);
			infoString.append("<br>" + LINE_SEP);
			infoString.append("</div></body></html>");
			setInfoString(infoString);
			writeToFile(infoString.toString());
		}
	}

	public void writeToFile(String message) {
		// TODO Use something in org.apache.commons.io.FileUtils which does this..
		if (message.indexOf(END_TEST) > 0) {
			BufferedWriter bufferedWriter = null;
			try {
				bufferedWriter = new BufferedWriter(new FileWriter(getLogFile().getPath(), true));
				bufferedWriter.newLine();
				bufferedWriter.append(message);
			} catch (IOException e) {
				// e.printStackTrace();
			} finally {
				try {
					if (bufferedWriter != null) {
						bufferedWriter.flush();
						bufferedWriter.close();
					}
				} catch (IOException ex) {
					// ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * This file will create all all the required JavaScript file and CSS file
	 * used for the HTML file. These files already exists in the workspace, but
	 * we need them in the artifacts of the target build, hence we will have to
	 * copy them out.
	 */
	private void createAdditionalFiles() {
		String[] files = { "index.html", "jquery-1.4.2.min.js", APPENDED_JS, "jquery-ui-1.8.5.custom.css", "style.css", "util.js" };
		for (String fileName : files) {
			File targetFile = new File(reportDirFile , fileName);
			if (!targetFile.exists()) {
				URL sourceFileURL = TestCaseReportWriter.class.getResource(fileName);
				if (sourceFileURL == null) {
					throw new RuntimeException("Could not find resource on classpath: " + fileName);
				}
				try {
					FileUtils.copyURLToFile(sourceFileURL, targetFile);
				} catch (IOException e) {
					throw new RuntimeException("Failed to copy resource from classpath to file: " + fileName, e);
				}
			}
		}
	}

	// ---

	private void addPakageNameToJS(String packageName) throws IOException {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(jsFile, true));
			bw.write("packageArray.push(\"" + packageName.toUpperCase() + "\");");
			bw.newLine();
			bw.flush();
		} catch (IOException ioe) {
			// DO Nothing
		} finally { // always close the file
			if (bw != null)
				try {
					bw.close();
				} catch (IOException ioe2) {
					// just ignore it
				}
		} // end try/catch/finally
	}

	// package local
	void addTestClassNameToJS(String className) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(jsFile, true));
			bw.write("testClassArray.push(\"" + className + "\");");
			bw.newLine();
			bw.flush();
		} catch (IOException ioe) {
			// DO Nothing
		} finally { // always close the file
			if (bw != null)
				try {
					bw.close();
				} catch (IOException ioe2) {
					// just ignore it
				}
		} // end try/catch/finally
	}

	// package local
	String getPackageName(String arg0) {
		int indx = arg0.indexOf("Package Name");
		arg0 = arg0.substring(indx, arg0.length());
		indx = arg0.lastIndexOf(".");

		try {
			addPakageNameToJS(arg0.substring(indx + 1, arg0.length()));
			if (!packageNamesList.contains(arg0.substring(indx + 1, arg0.length()))) {
				packageNamesList.add(arg0.substring(indx + 1, arg0.length()));
			}
		} catch (IOException e) {
			// Ignore (?!)
		}

		return arg0.substring(indx + 1, arg0.length());
	}

	// package local
	void addFailedTestClassNameToJS(String className) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(jsFile, true));
			bw.write("failedTestClassArray.push(\"" + className + "\");");
			bw.newLine();
			bw.flush();
		} catch (IOException ioe) {
			// DO Nothing
		} finally { // always close the file
			if (bw != null)
				try {
					bw.close();
				} catch (IOException ioe2) {
					// just ignore it
				}
		} // end try/catch/finally
	}

	public void infoWithFlagAndScreenshot(String message, File screenshot) {
		if (screenshot != null) {
			try {
				FileUtils.copyFileToDirectory(screenshot, screenshotsDirFile, true);
			} catch (IOException e) {
				throw new RuntimeException("Oups, WebDriver Report could't copy screenshot file?!", e);
			}

			message = message + "^" + screenshot.getName();
		}
		infoWithFlag(message);
	}
}
