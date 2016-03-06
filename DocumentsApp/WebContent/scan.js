var DWObject;

$(function() {

    loadScript ("http://www.rembli.com/scanner/dynamsoft.webtwain.initiate.js", function () {
        loadScript ("./scan.config.js", function () {
        	Dynamsoft.WebTwainEnv.RegisterEvent('OnWebTwainReady', Dynamsoft_OnReady); // Register OnWebTwainReady event. This event fires as soon as Dynamic Web TWAIN is initialized and ready to be used
        } ); 	
    });
	
	// show content
	$("#rembli-body").hide();
	document.getElementById('rembli-body').style.visibility='visible';		
	$("#rembli-body").fadeIn(200);
});



function Dynamsoft_OnReady() {
    DWObject = Dynamsoft.WebTwainEnv.GetWebTwain('dwtcontrolContainer'); // Get the Dynamic Web TWAIN object that is embeded in the div with id 'dwtcontrolContainer'
    if (DWObject) {
        var count = DWObject.SourceCount; // Populate how many sources are installed in the system
        for (var i = 0; i < count; i++)
            document.getElementById("source").options.add(new Option(DWObject.GetSourceNameItems(i), i));  // Add the sources in a drop-down list
    }
}

function AcquireImage() {
    if (DWObject) {
        DWObject.SelectSourceByIndex(document.getElementById("source").selectedIndex);
        DWObject.OpenSource();
        DWObject.IfDisableSourceAfterAcquire = true;	// Scanner source will be disabled/closed automatically after the scan.
        DWObject.AcquireImage();
    }
}

//Callback functions for async APIs
function OnSuccess() {
    console.log('successful');
    window.open ('index.html','_self');
}

function OnFailure(errorCode, errorString) {
    alert(errorString);
    window.open ('index.html','_self');
}

function LoadImage() {
    if (DWObject) {
        DWObject.IfShowFileDialog = true; // Open the system's file dialog to load image
        DWObject.LoadImageEx("", EnumDWT_ImageType.IT_ALL, OnSuccess, OnFailure); // Load images in all supported formats (.bmp, .jpg, .tif, .png, .pdf). sFun or fFun will be called after the operation
    }
}

// OnHttpUploadSuccess and OnHttpUploadFailure are callback functions.
// OnHttpUploadSuccess is the callback function for successful uploads while OnHttpUploadFailure is for failed ones.
function OnHttpUploadSuccess() {
    console.log('successful');
    window.open ('index.html','_self');
}

function OnHttpUploadFailure(errorCode, errorString, sHttpResponse) {
    alert(errorString + sHttpResponse);
}

function UploadImage() {
    if (DWObject) {
        // If no image in buffer, return the function
        if (DWObject.HowManyImagesInBuffer == 0)
            return;

        var strHTTPServer = location.hostname; //The name of the HTTP server. For example: "www.dynamsoft.com";
        var CurrentPathName = unescape(location.pathname);
        var CurrentPath = CurrentPathName.substring(0, CurrentPathName.lastIndexOf("/") + 1);
        var strActionPage = CurrentPath + "api/documents";
        DWObject.IfSSL = false; // Set whether SSL is used
        DWObject.HTTPPort = location.port == "" ? 80 : location.port;

        var Digital = new Date();
        var uploadfilename = Digital.getMilliseconds(); // Uses milliseconds according to local time as the file name

        DWObject.HTTPUploadAllThroughPostAsPDF(strHTTPServer, strActionPage, uploadfilename + ".pdf", OnHttpUploadSuccess, OnHttpUploadFailure);
    }
}
