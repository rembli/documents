var currentID = 0;
var nextID = 0;
var prevID = 0;

$(function() {
	currentID = getParameterByName("id");
	refresh();
	
	$("#rembli-body").hide();
	document.getElementById('rembli-body').style.visibility='visible';		
	$("#rembli-body").fadeIn(200);
});

function refresh () {
	// set prev and next ID
	currentDocumentList = getCache ("currentDocumentList").split(",");
	currentDocumentPos = currentDocumentList.indexOf (currentID);
	if (currentDocumentPos < currentDocumentList.length) 
		nextID = currentDocumentList [currentDocumentPos+1];
	if (currentDocumentPos > 0) 
		prevID = currentDocumentList [currentDocumentPos-1];
	
	renderTemplate ('document-header-form-template', '/documents/api/documents/'+currentID,"document-header-form");
	renderTemplate ('preview-section-template', '/documents/api/documents/'+currentID+"/files","preview-section");	
	renderTemplate ('file-table-template', '/documents/api/documents/'+currentID+"/files","file-table");	
	renderTemplate ('logEntry-table-template', '/documents/api/log?entity=DOCUMENT&entityid='+currentID,"logEntry-table");
}

function updateDocument () {
	var url = host+"/api/documents/"+currentID;
	var client = new XMLHttpRequest();
	client.open('PUT', url, true);
	client.onload = function () {
		refresh();
	};
	client.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	client.setRequestHeader("Authorization", accessToken);		
	client.send("id="+currentID+"&note="+window.docForm.note.value);
	if (client.status == 401) window.document.location.href = host+"/login.html";
}

function attachFile () {
	var url = host+"/api/documents/"+currentID+"/files";
	var client = new XMLHttpRequest();
	client.open('POST', url, true);
	client.onload = function () {
		window.document.uploadFileForm.RemoteFile.value = "";
		refresh();
	};
	client.setRequestHeader("Authorization", accessToken);	

	var formData = new FormData(window.document.uploadFileForm);
	client.send(formData);
	if (client.status == 401) window.document.location.href = host+"/login.html";
}	

function deleteFile (id) {
	if (confirm ("Do You really want to delete file with id "+id)) {
		var url = host+"/api/documents/"+currentID+"/files/"+id;
		var client = new XMLHttpRequest();
		client.open('DELETE', url, true);
		client.onload = function () {
			refresh();
		};
		client.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		client.setRequestHeader("Authorization", accessToken);	
		client.send("id="+id);
		if (client.status == 401) window.document.location.href = host+"/login.html";
	}
}	

function deleteDocument (id) {
	if (confirm ("Do You really want to delete document with id "+id)) {
		var url = host+"/api/documents/"+id;
		var client = new XMLHttpRequest();
		client.open('DELETE', url, true);
		client.onload = function () {
			loadContent ("index.html");
			//window.document.location.href = host+"/index.html"
		};
		client.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		client.setRequestHeader("Authorization", accessToken);	
		client.send("id="+id);
		if (client.status == 401) window.document.location.href = host+"/login.html";
	}
}

