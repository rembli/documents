$(function() {
	// render template
	refresh();	

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
	
	// show content
	$("#rembli-body").hide();
	document.getElementById('rembli-body').style.visibility='visible';		
	$("#rembli-body").fadeIn(200);
});


function refresh () {
	renderTemplate ('document-thumbnail-template', '/documents/api/documents', 'document-thumbnail-table');
}

