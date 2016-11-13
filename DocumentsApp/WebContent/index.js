$(function() {
	if (getCache ("index-table-display") == null)
		setCache ("index-table-display","thumbnail");
	
	// load and initialize dropzone
	loadScript ("./js-lib/dropzone.js", function () {
		
		log ("Initialize dropzone");
		
		$("#my-dropzone").dropzone({ 
			url: "./api/documents",
			paramName: "RemoteFile", // The name that will be used to transfer the file
	        maxFilesize: 5, // MB,
	        maxFiles: 1, 
			
	        init: function() {
				
				  this.on("success", function (file) {
					  this.removeFile(file);
					  refresh();
			  	   });
				  this.on("error", function (file, errorMessage, client) {
					  this.removeFile(file);
					  alert ("ERROR: \n\n"+errorMessage);
			  	   });
			} 				
		});
	});	
	
	// pop-over im menu initialisieren
	/*
	$('#queryMail').popover({
		placement: 'bottom',
		trigger: 'focus',
		title: "query your mail account",
		content: "New content?"
	})
	*/;
	
	// render template
	refresh();			
	
	// show content
	$("#rembli-body").hide();
	document.getElementById('rembli-body').style.visibility='visible';		
	$("#rembli-body").fadeIn(200);
});


function refresh () {

	if (getCache ("index-table-display") == "thumbnail") {
		renderTemplate ('document-thumbnail-table-template', '/documents/api/documents', 'document-thumbnail-table', function (response) {
			var currentDocumentList = new Array();
			for (i in response)	currentDocumentList [i] = ""+response[i].idDocument;
			setCache ("currentDocumentList", currentDocumentList);
		});
		document.getElementById('document-list-table').style.display='none';
		document.getElementById('document-thumbnail-table').style.display='block';	
	}
	else {
		renderTemplate ('document-list-table-template', '/documents/api/documents', 'document-list-table', function (response) {
			var currentDocumentList = new Array();
			for (i in response) currentDocumentList [i] = ""+response[i].idDocument;
			setCache ("currentDocumentList", currentDocumentList);
		});
		document.getElementById('document-thumbnail-table').style.display='none';	
		document.getElementById('document-list-table').style.display='block';		
		
	}
}

