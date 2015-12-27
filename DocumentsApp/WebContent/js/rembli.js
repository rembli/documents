//# INIT ##########################################################

var host = ".";
var token = window.sessionStorage.getItem("authenticationToken");

window.onload = function () {
	
	 // load jquery
     var jQueryScript = document.createElement("SCRIPT");
     jQueryScript.src = './js-lib/jquery.min.js';
     jQueryScript.type = 'text/javascript';
     jQueryScript.async = false;
     document.getElementsByTagName("head")[0].appendChild(jQueryScript);

     var checkReady = function(callback) {
         if (window.jQuery) {
             callback(jQuery);
         }
         else {
             window.setTimeout(function() { checkReady(callback); }, 20);
         }
     };

     // when jquery is loaded --> inititalize rembli
     checkReady(function($) {
         $(function() {
        	 init();
         });
     });
     
 };

function init () {
		// alle zentralen Bibliotheken laden
		loadBase ();
		// die includes auf der Seite laden
		loadIncludes ();
		// übersetzen der markups mit <lang>xzy</lang> oder lang('xyz');
		translate ();
		// show body
		 rb = document.getElementById('rembli-body');
		 if (rb != null) rb.style.visibility='visible';
}

function loadBase () {

	    loadCSS ("./css/bootstrap.min.css");
	    loadCSS ("./css/bootstrap-theme.min.css");
	    loadCSS ("./css/style.css");
	    
	    loadScript ("./js-lib/jquery.min.js");	
	    loadScript ("./js-lib/bootstrap.min.js");	
	    loadScript ("./js-lib/dust-full.min.js");
	    
	    // load page-related js-file, e.g. index.html > load index.js
	    
	    var currentHTML = document.location.href.match(/[^\/]+$/);
	    if (currentHTML==null) 
	    	currentHTML = "index.html";
	    else 
	    	currentHTML = currentHTML[0];
	    var currentJS = currentHTML.split (".")[0]+".js";
	    loadScript (currentJS);
}

function loadCSS (url) {
    var link = document.createElement("LINK");
    link.href = url;
    link.rel = 'stylesheet';
    document.getElementsByTagName("head")[0].appendChild(link);	
}

function loadScript (url) {
    var script = document.createElement("SCRIPT");
    script.src = url;
    script.type = 'text/javascript';
    script.async = false;
    document.getElementsByTagName("head")[0].appendChild(script);
}

//# TEMPLATING, TRANSLATION & INCLUDES ################################

function loadIncludes () {
	var includesCache = {};
	if (getParameterByName("reloadCache") !=  "") { 
		includesCache = {};
		window.sessionStorage.removeItem ("includesCache");
	}
	if (window.sessionStorage.getItem ("includesCache")!=null) {
		// load string from cache and translate it to json-hashmap
		includesCache = JSON.parse(window.sessionStorage.getItem ("includesCache"));
	}

	do {
		var includes = $("include");
		
		for (i=0; i<includes.length; i++) {
			var currentInclude = includes[i];
			var includeTemplateName = currentInclude.getAttribute("template");
			var includeTemplate = "";
			
			if (includesCache[includeTemplateName]==null) {
				console.log ("Template "+includeTemplateName+" is not in cache");
				
				var client = new XMLHttpRequest();
				client.open("GET",includeTemplateName,false);
				client.setRequestHeader("Authorization", token);	
				client.send();
				includeTemplate = client.responseText;
				
				includesCache[includeTemplateName] = includeTemplate;
			}
			else {
				// console.log ("Template "+includeTemplateName+" is in cache");
				includeTemplate = includesCache[includeTemplateName];
			}
				
			
			$( "include[template='"+includeTemplateName+"']" ).replaceWith (includeTemplate); 
		}
	} 
	while ($("include").length>0);
	
	// load json hashmap as string to sessionStorage
	window.sessionStorage.setItem ("includesCache", JSON.stringify(includesCache));
	// console.log ("updated includesCache");
}

function loadDictionary () {
	var language = (navigator.language || navigator.browserLanguage).split('-')[0].toUpperCase();
	if (language != "EN" && language != "DE") language = "EN";

	var dictionary = JSON.parse(window.sessionStorage.getItem("dictionary-"+language));
	if (dictionary == null || getParameterByName("reloadCache") !=  "") {
		console.log ("Load dictionary to session storage");
		var client = new XMLHttpRequest();
		client.open("GET","./lang/"+language,false);
		client.send();

		window.sessionStorage.setItem("dictionary-"+language, client.responseText);
		dictionary = JSON.parse(client.responseText);
	}
	// console.log( JSON.stringify(dictionary, null, "    ") );
	return dictionary;
}

function lang (id) {
	var dictionary =  loadDictionary ();
	return dictionary[id];
}

function translate () {
	var dictionary =  loadDictionary ();

	var translation = $("lang");
	for (i=0; i<translation.length; i++) {
		var currentTranslation = translation[i].innerHTML;
		var result = dictionary[currentTranslation];
		translation[i].innerHTML = result;
	}
}		

function renderTemplate (template, url, output) {
	if (output==null) output = template;
	var src = document.getElementById(template).innerHTML;
	var compiled = dust.compile(src, template);
	dust.loadSource(compiled);

	var client = new XMLHttpRequest();
	client.open("GET",url,false);
	client.setRequestHeader("Accept", "application/json");
	client.setRequestHeader("Authorization", token);
	client.send();
	if (client.status == 401) window.document.location.href = host+"/login.html";

	dust.render(template, JSON.parse(client.responseText), function(err, out) {
		document.getElementById(output).innerHTML = out;
	});
}	

//# AUTHENTICATION ####################################################

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

 //# HELPER ##################################################################
 
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


 
 
 
 
 
	     