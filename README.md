This is a small library which produces "Test Execution Reports", with Screenshots, Summary and some bells and whistles,
for functional UI front-end tests written using Selenium 2.0 (AKA  WebDriver) and JUnit.

See [SampleGoogleSearchReportTest.java](https://github.com/vorburger/webdriver-reporting/blob/master/webdriver-reporting/src/test/java/ch/vorburger/webdriver/reporting/tests/SampleGoogleSearchReportTest.java)
for a usage example.  After running a (suite of) tests, you'll find an index.html report home page
in the module's target/surefire-reports/tests directory, which [looks like this](http://www.vorburger.ch/webdriver-reporting/).

Get the binary of it from [my Maven repo](https://github.com/vorburger/m2p2-repository), like this:
```
  <dependency>
			<groupId>ch.vorburger.webdriver</groupId>
			<artifactId>webdriver-reporting</artifactId>
			<version>1.1.0-SNAPSHOT</version>
	</dependency>
		
	...
  <repositories>
      <repository>
         <id>vorburger-releases</id>
         <url>http://vorburger.github.com/m2p2-repository/maven/releases</url>
      </repository>
      <repository>
         <id>vorburger-snapshots</id>
         <url>http://vorburger.github.com/m2p2-repository/maven/snapshots</url>
      </repository>
   </repositories>
```
Deployment to this Maven repo is currently manual (no continous integration set-up yet).. 
so to get latest SNAPSHOT, you better do a clone of this repo (src) and "mvn install" it locally if you can.

Please fork it on GitHub, improve it massively, and send pull requests! ;-)
