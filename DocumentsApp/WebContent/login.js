function submitLogin () {
	var url = host+"/api/login";
	var client = new XMLHttpRequest();
	client.open('POST', url, true);
	client.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	// wenn die Authentifzierung erfolgreich war, wird das Token in der Session abgespeichert
	client.onreadystatechange = function(){
			if(this.readyState == 4){
				if(this.status == 200){
					// wenn die Anmeldung funktioniert hat, bekommen wir vom Server das Token im Klartext zurück
					// das speichern wir uns im SessionStorage des Browswers
					// später müssen wir das bei jedem Request in den Authorization-Header schreiben
					window.sessionStorage.setItem("Authentication-Token",this.responseText);
					window.sessionStorage.setItem("Authentication-User",window.document.login.email.value);
					// dann gibt es einen redirekt zur Startseite
					window.document.location.href = host+"/index.html";
				}
				else{
					// Wenn es nicht geklappt hat, geben wir einen Hinweis aus und löschen das gespeichert Token
					alert (this.statusText);
					window.sessionStorage.removeItem("Authentication-Token");
				}
			}
	};
	// hier senden wir username und passwort
	client.send("username="+window.document.login.email.value+"&password="+window.document.login.password.value);
}

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
				}
				else{
					// Wenn es nicht geklappt hat, geben wir einen Hinweis aus 
					alert ("Registrierung war nicht erfolgreich: "+this.statusText);
				}
			}
	};
	// hier senden wir username und passwort
	client.send("username="+window.document.login.email.value+"&password="+window.document.login.password.value+"&email="+window.document.login.email.value);
}
