$(function() {
	// render template
	refresh();	
	// show content
	document.getElementById('rembli-body').style.visibility='visible';
});


function refresh () {
	renderTemplate ('document-thumbnail-template', '/documents/api/documents', 'document-thumbnail-table');
}
