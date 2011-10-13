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
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * CustomLayout
 *
 * This class is used to give a layout of our choice to the logs by overwriting
 * log4j's HTMLLayout.
 *
 * @author Nasir Raza
 */
public class TestReportWriter extends HTMLLayout {

	// RegEx pattern looks for <tr> <td> nnn...nnn </td> (all whitespace ignored)
	private static final String TIMESTAMP_RX = "\\s*<\\s*tr\\s*>\\s*<\\s*td\\s*>\\s*(\\d*)\\s*<\\s*/td\\s*>";

	private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	private final List<String> packageNamesList = new ArrayList<String>();

	public TestReportWriter() {
		super();
	}

	protected final int BUF_SIZE = 256;
	protected final int MAX_CAPACITY = 1024;
	protected final String START_TEST = "Start";
	protected final String END_TEST = "End";

	static String TRACE_PREFIX = "<br>&nbsp;&nbsp;&nbsp;&nbsp;";

	/**
	 * A string constant used in naming the option for setting the the location
	 * information flag. Current value of this string constant is
	 * <b>LocationInfo</b>.
	 *
	 * <p>
	 * Note that all option keys are case sensitive.
	 *
	 * @deprecated Options are now handled using the JavaBeans paradigm. This
	 *             constant is not longer needed and will be removed in the
	 *             <em>near</em> term.
	 */
	@Deprecated
	public static final String LOCATION_INFO_OPTION = "LocationInfo";

	/**
	 * A string constant used in naming the option for setting the the HTML
	 * document title. Current value of this string constant is <b>Title</b>.
	 */
	public static final String TITLE_OPTION = "Title";

	// Print no location info by default
	boolean locationInfo = false;

	String title = "Log4J Log Messages";

	/**
	 * The <b>LocationInfo</b> option takes a boolean value. By default, it is
	 * set to false which means there will be no location information output by
	 * this layout. If the the option is set to true, then the file name and
	 * line number of the statement at the origin of the log statement will be
	 * output.
	 *
	 * <p>
	 * If you are embedding this layout within an
	 * {@link org.apache.log4j.net.SMTPAppender} then make sure to set the
	 * <b>LocationInfo</b> option of that appender as well.
	 */
	@Override
	public void setLocationInfo(boolean flag) {
		locationInfo = flag;
	}

	/**
	 * Returns the current value of the <b>LocationInfo</b> option.
	 */
	@Override
	public boolean getLocationInfo() {
		return locationInfo;
	}

	/**
	 * The <b>Title</b> option takes a String value. This option sets the
	 * document title of the generated HTML document.
	 *
	 * <p>
	 * Defaults to 'Log4J Log Messages'.
	 */
	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the current value of the <b>Title</b> option.
	 */
	@Override
	public String getTitle() {
		return title;
	}

	/**
	 * Returns the content type output by this layout, i.e "text/html".
	 */
	@Override
	public String getContentType() {
		return "text/html";
	}

	/**
	 * No options to activate.
	 */
	@Override
	public void activateOptions() {
	}

	public void addPakageNameToJS(String packageName) throws IOException {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir")
					+ "\\target\\surefire-reports\\jquery-ui-1.8.5.custom.min.js", true));
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

	public void addTestClassNameToJS(String className) {

		BufferedWriter bw = null;

		try {
			bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir")
					+ "\\target\\surefire-reports\\jquery-ui-1.8.5.custom.min.js", true));
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

	@Override
	public String format(LoggingEvent event) {
		return "";
	}

	public String getPackageName(String arg0) {
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

	public StringBuffer changeTimeStamp(StringBuffer record, LoggingEvent event) {
		Pattern pattern = Pattern.compile(TIMESTAMP_RX); // RegEx to find the default timestamp
		Matcher matcher = pattern.matcher(record);

		if (!matcher.find()) // If default timestamp cannot be found,
		{
			return record; // Just return the unmodified log record.
		}

		StringBuffer buffer = new StringBuffer(record);

		buffer.replace(matcher.start(1), // Replace the default timestamp with one formatted as desired.
				matcher.end(1), sdf.format(new Date(event.timeStamp)));

		return buffer; // Return the log record with the desired timestamp format.
	}


	/**
	 * Returns appropriate HTML headers.
	 */
	@Override
	public String getHeader() {
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" \"http://www.w3.org/TR/html4/frameset.dtd\">" + Layout.LINE_SEP);
		sbuf.append("<html>" + Layout.LINE_SEP);
		sbuf.append("<head>" + Layout.LINE_SEP);
		sbuf.append("<title>" + title + "</title>" + Layout.LINE_SEP);
		sbuf.append(" <LINK href=\"../jquery-ui-1.8.5.custom.css\" rel=\"stylesheet\" type=\"text/css\">");
		sbuf.append(" <LINK href=\"../style.css\" rel=\"stylesheet\" type=\"text/css\">");
		sbuf.append("<script type=\"text/javascript\" src=\"../jquery-1.4.2.min.js\"></script>");
		sbuf.append("<script type=\"text/javascript\" src=\"../jquery-ui-1.8.5.custom.min.js\"></script>");
		sbuf.append("<script type=\"text/javascript\" src=\"../util.js\"></script>");
		sbuf.append("<style type=\"text/css\">" + Layout.LINE_SEP);
		sbuf.append("<!--" + Layout.LINE_SEP);
		sbuf.append("body, table {font-family: arial,sans-serif; font-size: x-small;}.buttonStyle { color: #900; border: 1px solid #900; font-weight: bold;  float: right;}"
						+ Layout.LINE_SEP);
		sbuf.append("table{table-layout: fixed;}td{text-align: left;padding-top: 4px;padding-bottom: 4px;padding-left: 8px;padding-right: 0px;}th {background: #336699; color: #FFFFFF; text-align: left;}.timeClass{width:5%;}"
						+ Layout.LINE_SEP);
		sbuf.append("-->" + Layout.LINE_SEP);
		sbuf.append("</style>" + Layout.LINE_SEP);

		sbuf.append("</head>" + Layout.LINE_SEP);

		sbuf.append("<frameset cols=\"20%,80%\" frameborder=\"1\">");
		sbuf.append("<frameset onload=\"\" title=\"\" rows=\"52%,48%\">");
		sbuf.append("<frame id=\"packageName\" frameborder=\"1\" title=\"All Packages\" name=\"packageListFrame\" src=\"\">");
		sbuf.append("<frame id=\"testClasses\" frameborder=\"1\" title=\"All classes and interfaces\" name=\"packageFrame\" src=\"\">");
		sbuf.append("</frameset>");
		sbuf.append("<frame scrolling=\"yes\" title=\"Package, class and interface descriptions\" id=\"classFrameId\" name=\"classFrame\" src=\"\"></frame>");
		sbuf.append("</frame>");
		sbuf.append("</frameset>");
		sbuf.append("</body></html>");

		return sbuf.toString();
	}

	/**
	 * Returns the appropriate HTML footers.
	 */
	@Override
	public String getFooter() {
		StringBuffer sbuf = new StringBuffer();
		/* sbuf.append("</table>" + Layout.LINE_SEP); */
		sbuf.append("<br>" + Layout.LINE_SEP);
		sbuf.append("</body></html>");
		return sbuf.toString();
	}

	/**
	 * The HTML layout handles the throwable contained in logging events. Hence,
	 * this method return <code>false</code>.
	 */
	@Override
	public boolean ignoresThrowable() {
		return false;
	}

	/**
	 * @param createPackageNamewithTestName
	 */
	public void addFailedTestClassNameToJS(String className) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir")
					+ "\\target\\surefire-reports\\jquery-ui-1.8.5.custom.min.js", true));
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
}
