$(function() {
	renderTemplate ('document-header-form-template', '/documents/api/documents/'+getParameterByName("id"));
	renderTemplate ('file-table-template', '/documents/api/documents/'+getParameterByName("id")+"/files","file-table");	
	renderTemplate ('logEntry-table-template', '/documents/api/log?entity=DOCUMENT&entityid='+getParameterByName("id"),"logEntry-table");	
});

function updateDocument () {
	var url = host+"/api/documents/"+getParameterByName("id");
	var client = new XMLHttpRequest();
	client.open('PUT', url, true);
	client.onload = function () {
		renderTemplate ('logEntry-table-template', '/documents/api/log?entity=DOCUMENT&entityid='+getParameterByName("id"),"logEntry-table");
	};
	client.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	client.setRequestHeader("Authorization", token);		
	client.send("id="+getParameterByName("id")+"&note="+window.docForm.note.value);
	if (client.status == 401) window.document.location.href = host+"/login.html";
}

function attachFile () {
	var url = host+"/api/documents/"+getParameterByName("id")+"/files";
	var client = new XMLHttpRequest();
	client.open('POST', url, true);
	client.onload = function () {
		window.document.uploadFileForm.RemoteFile.value = "";
		window.location.reload();
	};
	client.setRequestHeader("Authorization", token);	

	var formData = new FormData(window.document.uploadFileForm);
	client.send(formData);
	if (client.status == 401) window.document.location.href = host+"/login.html";
}	

function deleteFile (id) {
	if (confirm ("Do You really want to delete file with id "+id)) {
		var url = host+"/api/documents/"+getParameterByName("id")+"/files/"+id;
		var client = new XMLHttpRequest();
		client.open('DELETE', url, true);
		client.onload = function () {
			window.location.reload();
		};
		client.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		client.setRequestHeader("Authorization", token);	
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
			window.document.location.href = host+"/index.html"
		};
		client.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		client.setRequestHeader("Authorization", token);	
		client.send("id="+id);
		if (client.status == 401) window.document.location.href = host+"/login.html";
	}
}



