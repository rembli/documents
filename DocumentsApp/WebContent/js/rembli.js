//# APP SETTINGS ##################################################

var debug = true;
var caching = false;
var host = ".";
var rembliLocation = null;

//# INIT ##########################################################

var accessToken = window.sessionStorage.getItem("accessToken");
var currentUser = window.sessionStorage.getItem("currentUser");

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
		
		// Page load im Fall von Back-Button
		$(window).on("popstate", function () {
			  // die url wird automatisch gesetzt, aber das reload muss aktiv angestossen werden
			  location.reload();
		});		
}

function loadContent (url) {
	
	$("#rembli-body").fadeOut(200, function () {
	
		// muss hier gesetzt werden, damit später auch die QueryParameter ausgelesen werden können (statt von der loation.search)
		rembliLocation = url;
		
		var client = new XMLHttpRequest();
		client.open("GET",url,true);
		client.setRequestHeader("Accept", "application/json");
		client.setRequestHeader("Authorization", accessToken);
		client.onload = function (e) {
			if (client.status == 401) window.document.location.href = host+"/login.html";
	
			// url im Browser richtig setzen für Favoriten
		    window.history.pushState(null, '', url);
		    
			// ersetzen des rembliBody-Tags durch den neuen Inhalt
			$("#rembli-body").replaceWith ("<div id='rembli-body' class='container' style='visibility:hidden'>"+$(client.responseText).filter("#rembli-body").html()+"</div>");
			loadIncludes();
			translate ();
		    	
			// auch hier wieder die entsprechende JS laden (Bsp. edit.html -> edit.js)
		    var currentHTML = url.match(/[^\/]+$/);
		    if (currentHTML==null) 
		    	currentHTML = "index.html";
		    else 
		    	currentHTML = currentHTML[0];
		    var currentJS = currentHTML.split (".")[0]+".js";
		    loadScript (currentJS);
		    
		};
		client.send();
	});
}	

function loadBase () {
	// caching enablen
	$.ajaxSetup({
		cache: true
	});
  
	// CSS Dateien laden
    loadCSS ("./css/bootstrap.min.css");
    loadCSS ("./css/bootstrap-theme.min.css");
    loadCSS ("./css/style.css");
    loadScript ("./js-lib/bootstrap.min.js");
    loadScript ("./js-lib/tooltip.js");	
    
    // internationalization fix for IE
    loadScript ("//cdn.polyfill.io/v2/polyfill.min.js?features=Intl.~locale.en,Intl.~locale.de");
    
    // templating and internationalization  with Dust
    $.getScript( "./js-lib/dust-full.min.js", function( data, textStatus, jqxhr ) {
	    $.getScript( "./js-lib/dust-intl.min.js", function( data, textStatus, jqxhr ) {
			// register helper DustIntl
		    DustIntl.registerWith(dust);

		    loadScript ("./js-lib/locale-data/en.js");	    
		    loadScript ("./js-lib/locale-data/de.js");	 	    
	    });
	    
	    // nachdem Dust geladen wurde, kann wg. Abhängigkeit die seiten-spezifische Dateien geladen werden
	    // e.g. index.html > load index.js
	    var currentHTML = document.location.href.match(/[^\/]+$/);
	    if (currentHTML==null) 
	    	currentHTML = "index.html";
	    else 
	    	currentHTML = currentHTML[0];
	    var currentJS = currentHTML.split (".")[0]+".js";
	    loadScript (currentJS);    	    
    });
}

function loadCSS (url) {
    var link = document.createElement("LINK");
    link.href = url;
    link.rel = 'stylesheet';
    document.getElementsByTagName("head")[0].appendChild(link);	
}

function loadScript (url, f) {
    if (f!=undefined) {
    	$.getScript( url, function( data, textStatus, jqxhr ) {
    		log ("Loaded script from "+url);
    		f();
    	});
    }
    else {
        var script = document.createElement("SCRIPT");
        script.src = url;
        script.type = 'text/javascript';
        script.async = false;
        document.getElementsByTagName("head")[0].appendChild(script);    	
    }
}

//# TEMPLATING, TRANSLATION & INCLUDES ################################

function loadIncludes () {
	var includesCache = {};
	if (!caching || getParameterByName("reloadCache") !=  "") { 
		includesCache = {};
		deleteCache ("includesCache");
	}
	if (getCache ("includesCache")!=null) {
		// load string from cache and translate it to json-hashmap
		includesCache = JSON.parse(getCache ("includesCache"));
	}

	do {
		var includes = $("include");
		
		for (i=0; i<includes.length; i++) {
			var currentInclude = includes[i];
			var includeTemplateName = currentInclude.getAttribute("template");
			var includeTemplate = "";
			
			if (includesCache[includeTemplateName]==null) {
				log ("PUT template '"+includeTemplateName+"' into cache");
				
				var client = new XMLHttpRequest();
				client.open("GET",includeTemplateName,false);
				client.setRequestHeader("Authorization", accessToken);	
				client.send();
				includeTemplate = client.responseText;
				
				includesCache[includeTemplateName] = includeTemplate;
			}
			else {
				log ("LOAD template '"+includeTemplateName+"' from cache");
				includeTemplate = includesCache[includeTemplateName];
			}
			
			$( "include[template='"+includeTemplateName+"']" ).replaceWith (includeTemplate); 
		}
	} 
	while ($("include").length>0);
	
	// load json hashmap as string to sessionStorage
	setCache ("includesCache", JSON.stringify(includesCache));
	log ("PUT includes cache to localStorage");
}

function loadDictionary () {
	var dictionary = null;
	var language = (navigator.language || navigator.browserLanguage).split('-')[0].toUpperCase();
	if (language != "EN" && language != "DE") language = "EN";

	if (getCache ("dictionary-"+language) != null)
		dictionary = JSON.parse(getCache ("dictionary-"+language));
	
	if (dictionary == null || !caching || getParameterByName("reloadCache") !=  "") {
		var client = new XMLHttpRequest();
		client.open("GET","./lang/"+language,false);
		client.send();
		setCache ("dictionary-"+language, client.responseText);
		dictionary = JSON.parse(client.responseText);
		
		log ("PUT dictionary to localStorage: \n"+JSON.stringify(dictionary));
	}
	return dictionary;
}

function lang (id) {
	var dictionary =  loadDictionary ();
	return dictionary[id];
}

function translate () {
	var dictionary =  loadDictionary ();

	var translation = $("lang").not("[translated='true']");
	for (i=0; i<translation.length; i++) {
		var currentTranslation = translation[i].innerHTML;
		var result = dictionary[currentTranslation];
		translation[i].innerHTML = result;
		log ("- translated '"+currentTranslation+"': "+result);
	}
	$("lang").attr("translated", "true");
}		

function renderTemplate (template, url, output, fn) {
	if (output==null) output = template;

	// load template 
	var templatesCache = {};
	if (!caching || getParameterByName("reloadCache") !=  "") { 
		templatesCache = {};
		deleteCache ("templatesCache");
	}
	if (getCache ("templatesCache")!=null) {
		templatesCache = JSON.parse(getCache ("templatesCache"));
	}	
	
	var compiled;
	if (templatesCache[template]==null) {
		// template is not in cache --> needs to be compiled
		var src = document.getElementById(template).innerHTML;
		compiled = dust.compile(src, template);
		templatesCache[template] = compiled;
		setCache ("templatesCache", JSON.stringify(templatesCache));
		log ("PUT compiled template '"+template+"'to localStorage: \n"+compiled);
	}
	else {
		// template is already compiled in cache
		compiled = templatesCache[template];
		log ("LOADED compiled template '"+template+"' from localStorage");
	}
	dust.loadSource(compiled);

	// Get data to be rendered
	var client = new XMLHttpRequest();
	client.open("GET",url,true);
	client.setRequestHeader("Accept", "application/json");
	client.setRequestHeader("Authorization", accessToken);
	client.onload = function (e) {
		if (client.status == 401) window.document.location.href = host+"/login.html";

		// finally render it
		dust.render(template, JSON.parse(client.responseText), function(err, out) {
			document.getElementById(output).innerHTML = out;
			translate ();
		});
		if (fn != undefined) fn (JSON.parse(client.responseText));
	};
	client.send();
}	

//# AUTHENTICATION ####################################################

 function isAuthenticated() {
 	var accessToken = window.sessionStorage.getItem("accessToken");
 	if (accessToken!=null) 
 		return true;
 	else
 		return false;
 }


 function getCurrentUser () {
 	if (isAuthenticated()) 
 		return  window.sessionStorage.getItem("currentUser");
 	else
 		return "-";
 }
 
 function getCurrentUserEMail () {
	 	if (isAuthenticated()) 
	 		return  window.sessionStorage.getItem("currentUserEMail");
	 	else
	 		return "-";
	 } 


 function getAccessToken () {
 	if (isAuthenticated()) 
 		return  window.sessionStorage.getItem("accessToken");
 	else
 		return "";
 }

 function logout () {
 	// token aus der session entfernen
 	window.sessionStorage.removeItem("accessToken");
 	window.sessionStorage.removeItem("currentUser");	
 	
 	// token aus der DB entfernen
 	var url = host+"/api/logout";
 	var client = new XMLHttpRequest();
 	client.open ('POST', url, true);
 	client.send ();
 	
 	// auf die login seite umleiten
 	window.document.location.href = host+"/login.html";
 }

 //# HELPER ##################################################################
 
function setCache (key, value) {
	try {
		window.localStorage.setItem(key, value);
	} 
	catch (e) {
		log ("Local Storage not supported");
	}
}

function getCache (key) {
	var value = null;
	try {
		value = window.localStorage.getItem(key);
	} 
	catch (e) {
		log ("Local Storage not supported");
	}
	return value;
}

function deleteCache (key) {
	try {
		window.localStorage.removeItem(key);
	} 
	catch (e) {
		log ("Local Storage not supported");
	}
}
  
function log (str) {
	 if (debug) {
		 console.log (str);
	 }
 }
 
 function getParameterByName(name) {
	 if (rembliLocation == null)
		 rembliLocation = location.search;
     
     name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
     var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
         results = regex.exec(rembliLocation);
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


 
 
 
 
 
	     