cd webdriver-reporting/
mvn clean test
rm -rf /tmp/webdriver-sample-report/
mkdir  /tmp/webdriver-sample-report/
cp -R target/webdriver-reporting/* /tmp/webdriver-sample-report/
git checkout gh-pages
cp -R /tmp/webdriver-sample-report/* ..
git commit -a -m "Updated sample report on gh-pages"
# git push origin gh-pages
# git checkout master

