This is a small library which produces "Test Execution Reports", with Screenshots, Summary and some bells and whistles,
for functional UI front-end tests written using Selenium 2.0 (AKA  WebDriver) and JUnit.

See [SampleGoogleSearchReportTest.java](https://github.com/vorburger/webdriver-reporting/blob/master/webdriver-reporting/src/test/java/ch/vorburger/webdriver/reporting/tests/SampleGoogleSearchReportTest.java)
for a usage example.  After running a (suite of) tests, you'll find a logReport.html home page
in the module's target/surefire-reports/tests directory.

Get the binary of the latest released version from [my Maven repo](https://github.com/vorburger/m2p2-repository), but better do clone the latest SNAPSHOT src instead of the release *src.jar.

Please fork it on GitHub, improve it massively, and send pull requests! ;-)