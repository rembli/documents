
// wenn es keine session gibt, dann auf die login-seite umleiten
if (!isAuthenticated()) {
	window.document.location.href = host+"/login.html";
}
