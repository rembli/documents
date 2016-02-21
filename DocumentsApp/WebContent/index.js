$(function() {
	// render template
	refresh();	
	// show content
	$("#rembliBody").hide();
	document.getElementById('rembliBody').style.visibility='visible';		
	$("#rembliBody").fadeIn(400);
});


function refresh () {
	renderTemplate ('document-thumbnail-template', '/documents/api/documents', 'document-thumbnail-table');
	
	Dropzone.options.myDropzone = {
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
	};	
}

