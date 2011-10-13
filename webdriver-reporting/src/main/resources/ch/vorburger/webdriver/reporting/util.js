var browserName = navigator.appName;
var failTestCount = 0;
var tempPackageName = "";
var oldId = "";
var stackTraceFlag = false;
packageArray = unique(packageArray);
testClassArray = unique(testClassArray);
failedTestClassArray = unique(failedTestClassArray);


function unique(a){
    var r = new Array();
    o: for (var i = 0, n = a.length; i < n; i++) {
        for (var x = 0, y = r.length; x < y; x++) {
            if (r[x] == a[i]) 
                continue o;
        }
        r[r.length] = a[i];
    }
    return r;
}


if (!Array.prototype.contains) {
    Array.prototype.contains = function(obj){
        var len = this.length;
        for (var i = 0; i < len; i++) {
            if (this[i] === obj) {
                return true;
            }
        }
        return false;
    };
}


$(function(){
    $("#tabs").tabs({
        select: function(e, ui){
            for (var i = 1; i <= packageArray.length; i++) {
                getSummary("tabs-" + i);
            }
        }
        
    });
});

this.reloadChildFrames = function(){

    return top.location.href = top.location.href;
}


/**
 * This method will populate the package name in the frame.
 */
$(function(){
    var $frame = $('frame');
    setTimeout(function(){
        if ($frame[0] != undefined) {
        
            setJSandCSS();
            
            var doc = $frame[0].contentWindow.document;
            
            var $body = $('body', doc);
            var frameHtml = "<script language='javascript'>function showClasses(id){}</script><h3><a href='javascript:void(0);' onClick='top.reloadChildFrames();'>Home</a></h3><h2>Test Packages</h2><ul>";
            for (var i = 0; i < packageArray.length; i++) {
                var stmt = "return showClasses(\"" + packageArray[i].toUpperCase() + "\");";
                frameHtml = frameHtml + "<li><a href='javascript:void(0);' title='" +
                packageArray[i].toUpperCase() +
                "' onClick='" +
                stmt +
                "'>" +
                packageArray[i].toUpperCase() +
                "</a><label id='" +
                packageArray[i] +
                "'></label></li>";
            }
            $body.html("</ul>" + frameHtml);
            
            
            top.packageFrame.document.body.style.backgroundColor = '#B9C9FE';
            top.packageListFrame.document.body.style.backgroundColor = '#B9C9FE';
            top.classFrame.document.body.style.backgroundColor = '#B9C9FE';
        }
    }, 500);
});


/**
 * This method will populate the test classes name in the frame.
 */
$(function(){
    var $frame = $('frame');
    setTimeout(function(){
        if ($frame[1] != undefined) {
        
            var headIDCss = top.packageFrame.document.getElementsByTagName("head")[0];
            var cssNode = top.packageFrame.document.createElement('link');
            cssNode.type = 'text/css';
            cssNode.rel = 'stylesheet';
            cssNode.href = '../style.css';
            cssNode.media = 'screen';
            headIDCss.appendChild(cssNode);
            
            
            var doc = $frame[1].contentWindow.document;
            var $body = $('body', doc);
            var frameHtml = "<span id='packageNameSpan'></span><h3>Test Methods</h3>";
            for (var j = 0; j < testClassArray.length; j++) {
                var indx = testClassArray[j].lastIndexOf(".");
                var fileName = testClassArray[j].substring(indx + 1, testClassArray[j].length);
                var packageName = testClassArray[j].substring(0, indx).toUpperCase();
                
                if (j == 0) {
                    tempPackageName = packageName;
                    frameHtml = frameHtml + "<span id='" + packageName + "' style='display:none;'><ul>";
                }
                
                if (tempPackageName != packageName) {
                    frameHtml = frameHtml + "</ul></span><span id='" + packageName + "' style='display:none;'><ul>";
                }
                
                var loc = "./" + packageName + "/" + fileName + "_log.html";
                
                var styleStr = "";
                if (failedTestClassArray.contains(testClassArray[j])) {
                    styleStr = 'color: #D00;';
                }
                else {
                    styleStr = 'color: #090;';
                }
                
                
                frameHtml = frameHtml + "<li style='" + styleStr + "'><span style='color: #090;'><a target='classFrame' onclick=\"copyStackTrace('" + fileName + "')\" title='" + fileName + "' href='" + loc + "'>" + fileName + "</a></span></li>";
                
                tempPackageName = packageName;
            }
            
            $body.html(frameHtml);
        }
    }, 500);
});


$(function(){
    var $frame = $('frame');
    setTimeout(function(){
        if ($frame[2] != undefined) {
        
        
            var headIDCss = top.classFrame.document.getElementsByTagName("head")[0];
            var cssNode = top.classFrame.document.createElement('link');
            cssNode.type = 'text/css';
            cssNode.rel = 'stylesheet';
            cssNode.href = '../style.css';
            cssNode.media = 'screen';
            headIDCss.appendChild(cssNode);
            
            
            var headID = top.classFrame.document.getElementsByTagName("head")[0];
            var newScript = top.classFrame.document.createElement('script');
            
            newScript.type = 'text/javascript';
            newScript.src = '../jquery-1.4.2.min.js';
            headID.appendChild(newScript);
            
            var headID1 = top.classFrame.document.getElementsByTagName("head")[0];
            var newScript1 = top.classFrame.document.createElement('script');
            
            newScript1.type = 'text/javascript';
            newScript1.src = '../jquery-ui-1.8.5.custom.min.js';
            headID1.appendChild(newScript1);
            
            var headID2 = top.classFrame.document.getElementsByTagName("head")[0];
            var newScript2 = top.classFrame.document.createElement('script');
            
            newScript2.type = 'text/javascript';
            newScript2.src = '../util.js';
            headID2.appendChild(newScript2);
            
            
            var doc = $frame[2].contentWindow.document;
            
            var $body = $('body', doc);
            var frameHtml = "";
            if (failedTestClassArray.length > 0) {
                var hrefLink = "";
                var tempLink = "";
                hrefLink = testClassArray[testClassArray.length - 1];
                var indexDot = hrefLink.indexOf(".");
                hrefLink = hrefLink.substring(indexDot + 1, hrefLink.length);
                tempLink = testClassArray[testClassArray.length - 1].substring(0, indexDot);
                //frameHtml = "<h3><a href='./" + tempLink + "/" + hrefLink + "_log.html?stackTraceFlag=true' onclick='showStackTrace();' target='classFrame'>Show StackTrace</a></h3>";
			}
            frameHtml = frameHtml + "<h2>Test Summary</h2><table id=\"box-table-a\"><tr><th>Package Name</th><th>Total Tests</th><th>Pass Tests</th><th>Fail tests</th></tr>";
            for (var index1 = 0; index1 < packageArray.length; index1++) {
                frameHtml = frameHtml + "<tr><td>" + packageArray[index1] + "</td>";
                
                var tempArray = new Array();
                for (var j1 = 0; j1 < testClassArray.length; j1++) {
                    var indx = testClassArray[j1].lastIndexOf(".");
                    if (testClassArray[j1].substring(0, indx) == packageArray[index1]) {
                        tempArray.push(testClassArray[j1].substring(indx + 1, testClassArray[j1].length));
                    }
                }
                
                var tempArrayFail = new Array();
                for (var k1 = 0; k1 < failedTestClassArray.length; k1++) {
                    var indx2 = failedTestClassArray[k1].lastIndexOf(".");
                    if (failedTestClassArray[k1].substring(0, indx2) == packageArray[index1]) {
                        tempArrayFail.push(failedTestClassArray[k1].substring(indx + 1, failedTestClassArray[k1].length));
                    }
                }
                
                frameHtml = frameHtml + "<td>" + tempArray.length + "</td>";
                frameHtml = frameHtml + "<td>" + (tempArray.length - tempArrayFail.length) + "</td>";
                frameHtml = frameHtml + "<td>" + tempArrayFail.length + "</td></tr>";
                
                innerHtmltext = "(" + tempArray.length + "/" + (tempArray.length - tempArrayFail.length) + "/" + tempArrayFail.length + ")";
                
                top.packageListFrame.document.getElementById(packageArray[index1]).innerHTML = innerHtmltext;
                
            }
            
            
            frameHtml = frameHtml + "<tr style=\"background-color: #006699; color: #000000\"><td  style=\"background-color:#006699; font-weight: bold; color: #000000\">Total</td><td  style=\"background-color: #006699; font-weight: bold; color: #000000\">" +
            testClassArray.length +
            "</td><td  style=\"background-color: #006699; font-weight: bold; color: #000000\">" +
            (testClassArray.length - failedTestClassArray.length) +
            "</td><td  style=\"background-color: #006699; font-weight: bold; color: #000000\">" +
            failedTestClassArray.length +
            "</td></tr>"
            $body.html("</table>" + frameHtml);
            
        }
    }, 500);
});


function showStackTrace(){
    //stackTraceFlag=true;

}


function setJSandCSS(){

    var headID = top.packageListFrame.document.getElementsByTagName("head")[0];
    var newScript = top.packageListFrame.document.createElement('script');
    
    newScript.type = 'text/javascript';
    newScript.src = '../jquery-1.4.2.min.js';
    headID.appendChild(newScript);
    
    var headID1 = top.packageListFrame.document.getElementsByTagName("head")[0];
    var newScript1 = top.packageListFrame.document.createElement('script');
    
    newScript1.type = 'text/javascript';
    newScript1.src = '../jquery-ui-1.8.5.custom.min.js';
    headID1.appendChild(newScript1);
    
    var headID2 = top.packageListFrame.document.getElementsByTagName("head")[0];
    var newScript2 = top.packageListFrame.document.createElement('script');
    
    newScript2.type = 'text/javascript';
    newScript2.src = '../util.js';
    headID2.appendChild(newScript2);
    
    
    var headID = top.packageFrame.document.getElementsByTagName("head")[0];
    var newScript = top.packageFrame.document.createElement('script');
    
    newScript.type = 'text/javascript';
    newScript.src = '../jquery-1.4.2.min.js';
    headID.appendChild(newScript);
    
    var headID1 = top.packageFrame.document.getElementsByTagName("head")[0];
    var newScript1 = top.packageFrame.document.createElement('script');
    
    newScript1.type = 'text/javascript';
    newScript1.src = '../jquery-ui-1.8.5.custom.min.js';
    headID1.appendChild(newScript1);
    
    var headID2 = top.packageFrame.document.getElementsByTagName("head")[0];
    var newScript2 = top.packageFrame.document.createElement('script');
    
    newScript2.type = 'text/javascript';
    newScript2.src = '../util.js';
    headID2.appendChild(newScript2);
    
    
    
}

function showClasses(id){

    top.packageFrame.document.getElementById(id).style.display = "";
    if (oldId != "" && oldId != id) {
        top.packageFrame.document.getElementById(oldId).style.display = "none";
    }
    top.packageFrame.document.getElementById("packageNameSpan").innerHTML = id;
    oldId = id;
}

function toggle_it(itemID){
    // Toggle visibility between none and inline
    if ((document.getElementById(itemID).style.display == 'none')) {
        document.getElementById(itemID).style.display = 'inline';
    }
    else {
        document.getElementById(itemID).style.display = 'none';
    }
}

var showImageFlag = false;
var showImageBizFlag = false;
/**
 * This method will show the logs
 * @param {Object} buttonId
 */
function showLog(buttonId){
    var id = buttonId + "container";
    var objs = document.getElementById(id).childNodes;
    for (var i = 1; i < objs.length; i++) {
        if (objs[i].className == "row" || objs[i].className == "rowImg") {
            objs[i].style.display = '';
        }
    }
    showImageFlag = true;
    hideDuplicateDivs();
    document.getElementById(buttonId + "showId").style.display = "none";
    document.getElementById(buttonId + "hideId").style.display = "";
}

/**
 * This method will hide the logs
 * @param {Object} buttonId
 */
function hideLog(buttonId){
    var id = buttonId + "container";
    var objs = document.getElementById(id).childNodes;
    for (var i = 1; i < objs.length; i++) {
        if (objs[i].className == "row" || (objs[i].className == "rowImg" && !showImageBizFlag)) {
            objs[i].style.display = 'none';
        }
    }
    showImageFlag = false;
    document.getElementById(buttonId + "showId").style.display = "";
    document.getElementById(buttonId + "hideId").style.display = "none";
}


/**
 * This method will show the logs
 * @param {Object} buttonId
 */
function showLogBiz(buttonId){
    var id = buttonId + "container";
    var objs = document.getElementById(id).childNodes;
    for (var i = 1; i < objs.length; i++) {
        if (objs[i].className == "rowBiz" || objs[i].className == "rowImg") {
            objs[i].style.display = '';
        }
    }
    showImageBizFlag = true;
    hideDuplicateDivs();
    
    document.getElementById(buttonId + "showIdBiz").style.display = "none";
    document.getElementById(buttonId + "hideIdBiz").style.display = "";
}


/**
 * This method will hide the logs
 * @param {Object} buttonId
 */
function hideLogBiz(buttonId){
    var id = buttonId + "container";
    var objs = document.getElementById(id).childNodes;
    for (var i = 1; i < objs.length; i++) {
        if (objs[i].className == "rowBiz" || (objs[i].className == "rowImg" && !showImageFlag)) {
            objs[i].style.display = 'none';
        }
    }
    showImageBizFlag = false;
    document.getElementById(buttonId + "showIdBiz").style.display = "";
    document.getElementById(buttonId + "hideIdBiz").style.display = "none";
}

/**
 * This method will show the Stacktrace
 * @param {Object} buttonId
 */
function showST(buttonId){
    document.getElementById(buttonId + "ST").style.display = "block";
    document.getElementById(buttonId + "hideId").style.display = "block";
    document.getElementById(buttonId + "showId").style.display = "none";
}


/**
 * This method will hide the stacktrace
 * @param {Object} buttonId
 */
function hideST(buttonId){
    document.getElementById(buttonId + "ST").style.display = "none";
    document.getElementById(buttonId + "hideId").style.display = "none";
    document.getElementById(buttonId + "showId").style.display = "block";
}

function hideDuplicateDivs(){

    var oldHTML = "";
    var objRow1 = $(".rowImg");
    objRow1.each(function(){
        var objTemp = $(this);
        //Hiding all the rows which are outside container div
        if (oldHTML == $(objTemp).html()) {
            $(objTemp).css("display", "none");
        }
        oldHTML = $(objTemp).html();
    });
}

function copyStackTrace(methodName){
    //alert(methodName);
    //alert(window.top.classFrame.document.getElementById(trimAll(methodName)).html());
}


function trimAll(sString){
    while (sString.substring(0, 1) == ' ') {
        sString = sString.substring(1, sString.length);
    }
    while (sString.substring(sString.length - 1, sString.length) == ' ') {
        sString = sString.substring(0, sString.length - 1);
    }
    return sString;
}

/**
 * This method is called when the Log HTML page loads.
 */
function init(){

    var objRow = $(".row");
    objRow.each(function(){
        var objTemp = $(this);
        //Hiding all the rows which are outside container div
        if ($(objTemp).parent().get(0).className != "container") {
            $(objTemp).css("display", "none");
        }
    });
    
    //Copying to there respective tabs
    var objCell = $(".cell2");
    objCell.each(function(){
        var objTemp = $(this);
        for (var i = 1; i <= packageArray.length; i++) {
            if ($(objTemp).attr('id').indexOf(packageArray[i - 1]) > 0) {
                copyHTML("tabs-" + i, objTemp);
            }
        }
    });
    
    //hiding extra divs appearing due to log4j's append issue
    var extraDivs = $(".ui-tabs.ui-widget.ui-widget-content.ui-corner-all");
    if (extraDivs.length > 1) {
        for (var i = 1; i < extraDivs.length; i++) {
            extraDivs[i].style.display = "none";
        }
    }
    
    //Setting status
    if (extraDivs[0].innerHTML.indexOf("Failed") > 0) {
        document.getElementById("status").style.backgroundColor = 'red';
        document.getElementById("status").innerHTML = "<b>Status: Fail</b>";
    }
    else {
        document.getElementById("status").style.backgroundColor = 'green';
        document.getElementById("status").innerHTML = "<b>Status: Pass</b>";
    }
    
    //Should we show Stacktrace or the actual page
    var windowLocation = window.location.href;
    if (windowLocation.indexOf("stackTraceFlag=true") > 0) {
        document.getElementById("status").style.display = "none";
        
        var objCell = $(".row");
        objCell.each(function(){
            var objTemp = $(this);
            objTemp.hide();
        });
        
        var objCell = $(".rowStackTrace");
        objCell.each(function(){
            var objTemp = $(this);
            objTemp.show();
            objTemp.appendTo('.container');
        });
        
    }
    
    
}

function createHeader(){

    var headerHTML = "<ul class=\"ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all\">";
    for (var i = 0; i < packageArray.length; i++) {
        headerHTML += "<li class=\"ui-state-default ui-corner-top\"><a href=\"#tabs-" + (i + 1) + "\">";
        headerHTML += packageArray[i];
        headerHTML += "</a></li>";
    }
    
    headerHTML += "</ul>";
    headerHTML += "<div class=\"container\" bordercolor=\"#224466\" width=\"100%\">";
    headerHTML += "<div class=\"row\">";
    headerHTML += "<div class=\"cell1\">Summary</div>";
    headerHTML += "<div class=\"cell2\" id=\"summaryId\"></div>";
    headerHTML += "</div></div>";
    
    headerHTML += "<div class=\"container\" bordercolor=\"#224466\" width=\"100%\">";
    headerHTML += "<div class=\"row\">";
    headerHTML += "<div class=\"cell1\">Time taken (in ms)</div>";
    headerHTML += "<div class=\"cell2\">Logs</div>";
    headerHTML += "</div></div>";
    for (var i = 0; i < packageArray.length; i++) {
        headerHTML += "<div id=\"tabs-" + (i + 1) + "\"></div>";
    }
    
    $("#tabs").append(headerHTML);
    
}

/**
 * This method will copy the divs of a particular class to there respective tabs like general or cdm etc.
 * @param {Object} tabId
 * @param {Object} objTemp
 */
function copyHTML(tabId, objTemp){
    var objNew = objTemp2;
    var id = $(objTemp).attr('id');
    
    
    var objTemp2 = $(objTemp).parent();
    objTemp2 = $(objTemp2).parent();
    var htmlToCopy = $(objTemp2).parent().get(0).innerHTML;
    
    
    if (htmlToCopy.search("Failed:") > 0) {
        document.getElementById(id).className = "failedtest";
        //objTemp2.removeClass().addClass("failedtest");
        htmlToCopy = $(objTemp2).parent().get(0).innerHTML;
    }
    
    $("#" + tabId).append(htmlToCopy);
    //set the first tab as default and show its content
    if (tabId == "tabs-1") {
        $("#" + tabId).removeClass().addClass("container");
    }
    else {
        // hide all the other tabs
        $("#" + tabId).removeClass().addClass("container ui-tabs-hide");
    }
    
    $(objTemp2).parent().remove();
    
}


/**
 *This method will calulate the number of test cases for each category and then will find out the number of failed tests as well.
 * @param {Object} tabId
 */
function getSummary(tabId){

    var numberOfTotalTest = $("#" + tabId + " > div").size();
    var numberOfFailedTests = $("#" + tabId + " .failedtest").length;
    
    $('#summaryId').html("");
    $('#summaryId').html("Total test: <span>" + numberOfTotalTest + "</span> <span style=\"background-color:#ff6f6f;\">Failed: " + numberOfFailedTests + "</span>");
    return false;
}

function insertDivWithId(htmlToCopy){
    var divObj = document.getElementsByTagName();
    
}


