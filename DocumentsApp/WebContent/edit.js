function readDocument (id) {
	var url = host+"/api/documents/"+id;
	var client = new XMLHttpRequest();
	client.open("GET",url,false);
	client.setRequestHeader("Accept", "application/json");
	client.setRequestHeader("Authorization", token);
	client.send();
	if (client.status == 401) window.document.location.href = host+"/login.html";
	
	var doc = JSON.parse(client.responseText);
	window.document.docForm.id.value = doc.id;
	window.document.docForm.note.value = doc.note;
}


function writeFilesToTable () {
	var url = host+"/api/documents/"+getParameterByName("id")+"/files";
	var client = new XMLHttpRequest();
	client.open("GET",url,false);
	client.setRequestHeader("Accept", "application/json");
	client.setRequestHeader("Authorization", token);
	client.send();
	if (client.status == 401) window.document.location.href = host+"/login.html";
	
	var fileInfos = JSON.parse(client.responseText);
	for(var i = 0; i < fileInfos.length; i++) {
	    var fileInfo = fileInfos[i];
		window.document.write ("<tr>");
		window.document.write ("<td>"+fileInfo.idFile+"</td>");
		window.document.write ("<td><a href='"+host+"/api/documents/"+getParameterByName("id")+"/files/"+fileInfo.idFile+"' target='_new'>"+fileInfo.fileName+"</a></td>");		
		window.document.write ("<td><img src='"+host+"/api/documents/"+getParameterByName("id")+"/files/"+fileInfo.idFile+"/thumbnail' onError='this.src = \"./img/thumbnail_not_available.jpg\"'></td>");		
		window.document.write ("<td><a href='javascript:deleteFile(\""+fileInfo.idFile+"\")'><span class='glyphicon glyphicon-trash'></span></a></td></tr>");		
		window.document.write ("</tr>");
	}
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

function updateDocument () {
	var url = host+"/api/documents/"+getParameterByName("id");
	var client = new XMLHttpRequest();
	client.open('PUT', url, true);
	client.onload = function () {
		writeLogEntriesToTable ();
	};
	client.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	client.setRequestHeader("Authorization", token);		
	client.send("id="+getParameterByName("id")+"&note="+window.docForm.note.value);
	if (client.status == 401) window.document.location.href = host+"/login.html";
}


function writeLogEntriesToTable () {
	var url = host+"/api/log?entity=DOCUMENT&entityid="+getParameterByName("id");
	var client = new XMLHttpRequest();
	client.open("GET",url,false);
	client.setRequestHeader("Accept", "application/json");
	client.setRequestHeader("Authorization", token);
	client.send();
	if (client.status == 401) window.document.location.href = host+"/login.html";
	
	var logEntries = JSON.parse(client.responseText);
	logEntryTable = "<table class='table'>";				
	for(var i = 0; i < logEntries.length; i++) {
	    var logEntry = logEntries[i];
	    	var newDate = new Date();
	   		newDate.setTime(logEntry.timestamp);
	    	dateString = newDate.toUTCString();

	    	logEntryTable +=  
	    		"<tr>"+
	    		"<td>"+logEntry.id+"</td>"+
	    		"<td>"+dateString+"</td>"+
	    		"<td>"+logEntry.username+"</td>"+
	    		"<td>"+logEntry.action+"</td>"+
	    		"<td>"+logEntry.comment+"</td>"+
	    		"</tr>";
	}
	logEntryTable += "</table>";
	$('#logEntryTable').html (logEntryTable);
}




