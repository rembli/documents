
function isAuthenticated() {
	var token = window.sessionStorage.getItem("authenticationToken");
	if (token!=null) 
		return true;
	else
		return false;
}


function getUser () {
	if (isAuthenticated()) 
		return  window.sessionStorage.getItem("authenticationUser");
	else
		return "";
}


function getToken () {
	if (isAuthenticated()) 
		return  window.sessionStorage.getItem("authenticationToken");
	else
		return "";
}

function logout () {
	// token aus der session entfernen
	window.sessionStorage.removeItem("authenticationToken");
	window.sessionStorage.removeItem("authenticationUser");	
	
	// token aus der DB entfernen
	var url = host+"/api/logout";
	var client = new XMLHttpRequest();
	client.open ('POST', url, true);
	client.send ();
	
	// auf die login seite umleiten
	window.document.location.href = host+"/login.html";
}

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

function sleep(delay) {
    var start = new Date().getTime();
    while (new Date().getTime() < start + delay);
  }

function DateFmt(fstr) {
	  this.formatString = fstr

	  var mthNames = ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"];
	  var dayNames = ["Sun","Mon","Tue","Wed","Thu","Fri","Sat"];
	  var zeroPad = function(number) {
	     return ("0"+number).substr(-2,2);
	  }

	  var dateMarkers = {
	    d:['getDate',function(v) { return zeroPad(v)}],
	    m:['getMonth',function(v) { return zeroPad(v+1)}],
	    n:['getMonth',function(v) { return mthNames[v]; }],
	    w:['getDay',function(v) { return dayNames[v]; }],
	    y:['getFullYear'],
	    H:['getHours',function(v) { return zeroPad(v)}],
	    M:['getMinutes',function(v) { return zeroPad(v)}],
	    S:['getSeconds',function(v) { return zeroPad(v)}],
	    i:['toISOString']
	  };

	  this.format = function(date) {
	    var dateTxt = this.formatString.replace(/%(.)/g, function(m, p) {
	      var rv = date[(dateMarkers[p])[0]]()

	      if ( dateMarkers[p][1] != null ) rv = dateMarkers[p][1](rv)

	      return rv

	    });

	    return dateTxt
	  }

	}


function setCookie(name, value, days) {
	    if (days) {
	        var date = new Date();
	        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
	        var expires = "; expires=" + date.toGMTString();
	    }
	    else var expires = "";
	    document.cookie = name + "=" + value + expires + "; path=/";
}


function getCookie(c_name) {
	    if (document.cookie.length > 0) {
	        c_start = document.cookie.indexOf(c_name + "=");
	        if (c_start != -1) {
	            c_start = c_start + c_name.length + 1;
	            c_end = document.cookie.indexOf(";", c_start);
	            if (c_end == -1) {
	                c_end = document.cookie.length;
	            }
	            return unescape(document.cookie.substring(c_start, c_end));
	        }
	    }
	    return "";
}


function validateEmail(email) { 
  var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return re.test(email);
} 


function checkForApp () {
	if (navigator.userAgent.search(/iPhone/i)>0  || navigator.userAgent.search(/iPad/i)>0)
			return true;
	else
			return false;
}
