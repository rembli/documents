$(function() {
	
	// show content
	$("#currentUserMail").replaceWith(getCurrentUser());
	refresh();
	
	$("#rembli-body").hide();
	document.getElementById('rembli-body').style.visibility='visible';		
	$("#rembli-body").fadeIn(200);
});


function refresh () {
	renderTemplate ('incoming-mail-table-template', '/documents/api/mailbox', 'incoming-mail-table');
}

function importMails () {
	var url = host+"/api/mailbox/exportToDocs";
	var client = new XMLHttpRequest();
	client.open('POST', url, true);
	client.onload = function () {
		refresh();
	};
	client.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	client.setRequestHeader("Authorization", accessToken);		
	client.send();
	if (client.status == 401) 
		window.document.location.href = host+"/login.html";
	else {
		alert (lang("Import done!"));
		window.document.location.href = host+"/index.html";
	}
}

