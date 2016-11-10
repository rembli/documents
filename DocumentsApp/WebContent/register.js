$(function() {
	// show content
	document.getElementById('rembli-body').style.visibility='visible';
});

  
function newUser () {
	var url = host+"/api/userInfo";
	var client = new XMLHttpRequest();
	client.open('POST', url, true);
	client.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	// wenn die Authentifzierung erfolgreich war, wird das Token in der Session abgespeichert
	client.onreadystatechange = function(){
			if(this.readyState == 4){
				if(this.status == 200){
					// wenn die Registierung funktioniert hat, bekommen wir vom Server eine OK
					alert ("Registrierung war erfolgreich! Sie können sich jetzt anmelden.");
					loadContent ('login.html');
				}
				else{
					// Wenn es nicht geklappt hat, geben wir einen Hinweis aus 
					alert ("Registrierung war nicht erfolgreich: "+this.statusText);
				}
			}
	};
	// hier senden wir username und passwort
	client.send("username="+window.document.loginForm.username.value+"&password="+window.document.loginForm.password.value+"&email="+window.document.loginForm.email.value);
}


