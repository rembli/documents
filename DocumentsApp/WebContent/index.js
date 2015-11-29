function writeDocumentsTable () {
	
	var url = host+"/api/documents";
	var client = new XMLHttpRequest();
	client.open("GET",url,false);
	client.setRequestHeader("Accept", "application/json");
	client.setRequestHeader("Authorization", token);
	client.send();
	if (client.status == 401) window.document.location.href = host+"/login.html";
	
	var documents = JSON.parse(client.responseText);
	window.document.write ("<table class='table'>");
	for(var i = 0; i < documents.length; i++) {
	    var doc = documents[i];
		window.document.write ("<tr><td>"+doc.idDocument+"</td>");
		window.document.write ("<td><a href='edit.html?id="+doc.idDocument+"'>"+doc.note+"</a></td>");
		window.document.write ("<td><img src='"+host+"/api/documents/"+doc.idDocument+"/thumbnail' onError='this.src = \"./img/thumbnail_not_available.jpg\" width=\"200px\"></td>");
		window.document.write ("<td><a href='javascript:deleteDocument(\""+doc.idDocument+"\")'><span class='glyphicon glyphicon-trash'></span></a></td></tr>");
	}
	window.document.write ("</table>");
}

function writeDocumentsToThumbnailGrid () {
	
	var url = host+"/api/documents";
	var client = new XMLHttpRequest();
	client.open("GET",url,false);
	client.setRequestHeader("Accept", "application/json");
	client.setRequestHeader("Authorization", token);
	client.send();
	if (client.status == 401) window.document.location.href = host+"/login.html";
	
	window.document.write ("<div class='row'>");
	
	var documents = JSON.parse(client.responseText);
	for(var i = 0; i < documents.length; i++) {
		var doc = documents[i];
		
		window.document.write ("<div class='col-xs-6 col-md-3'>");
		window.document.write ("<div class='thumbnail'><center>");
		window.document.write ("<a href='edit.html?id="+doc.idDocument+"'>");
		window.document.write ("<img src='"+host+"/api/documents/"+doc.idDocument+"/thumbnail' onError='this.src = \"./img/thumbnail_not_available.jpg\"'>");
		window.document.write ("</a>");
		window.document.write ("<div class='caption'>");
		window.document.write (doc.idDocument + " | ");
		window.document.write ("<a href='edit.html?id="+doc.idDocument+"'>"+doc.note + "</a> | ");
		window.document.write ("<a href='javascript:deleteDocument(\""+doc.idDocument+"\")'><span class='glyphicon glyphicon-trash'></span></a>");		
		window.document.write ("</div></center></div></div>");
	}
	window.document.write ("</div>");
}

function deleteDocument (id) {
	
	if (confirm ("Do You really want to delete document with id "+id)) {
		var url = host+"/api/documents/"+id;
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

