var host = ".";

// WENN DIESES SCRIPT EINGEBUNDEN WIRD, WIRD BEI DER GEPRÜFT, OB DER ANWENDER ANGEMELDET IST
// token aus der session holen
var token = window.sessionStorage.getItem("Authentication-Token");

// wenn es keine session gibt, dann auf die login-seite umleiten
if (token==null) {
	window.document.location.href = host+"/login.html";
}
else {
	isAuthenticated = true;
}
