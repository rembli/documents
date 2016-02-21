$(function() {

	loadScript ("/scanner/dynamsoft.webtwain.initiate.js");	
	loadScript ("/scanner/dynamsoft.webtwain.config.js");	
	
	// show content
	$("#rembliBody").hide();
	document.getElementById('rembliBody').style.visibility='visible';		
	$("#rembliBody").fadeIn(400);
});

