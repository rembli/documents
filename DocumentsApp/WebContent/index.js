$(function() {
	// render template
	renderTemplate ('document-thumbnail-template', '/documents/api/documents', 'document-thumbnail-table');	
	// show content
	document.getElementById('rembli-body').style.visibility='visible';
});

