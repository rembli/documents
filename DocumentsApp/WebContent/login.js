$(function() {
	// show content
	document.getElementById('rembli-body').style.visibility='visible';
});

//LOGIN NORMAL ********************  

function loginWithEMail () {
	var url = host+"/api/loginWithEMail";
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
					window.sessionStorage.setItem("accessToken",this.responseText);
					
					//user info abrufen
					$.ajax({url: host+"/api/userInfo"}).then(function(userinfo) {
						window.sessionStorage.setItem("currentUser",userinfo.username);
						window.sessionStorage.setItem("currentUserEMail",userinfo.email);

						// dann gibt es einen redirekt zur Startseite						
						window.document.location.href = host+"/index.html";
					});
					
				}
				else{
					// Wenn es nicht geklappt hat, geben wir einen Hinweis aus und löschen das gespeichert Token
					alert (this.statusText);
					window.sessionStorage.removeItem("accessToken");
				}
			}
	};
	// hier senden wir email und passwort
	client.send("email="+window.document.loginForm.email.value+"&password="+window.document.loginForm.password.value);
}  
  
// LOGIN MIT FACEBOOK ********************

  window.fbAsyncInit = function() {
    FB.init({
      appId      : '1636016296673576',
      xfbml      : true,
      version    : 'v2.5'
    });
  };

  (function(d, s, id){
     var js, fjs = d.getElementsByTagName(s)[0];
     if (d.getElementById(id)) {return;}
     js = d.createElement(s); js.id = id;
     js.src = "//connect.facebook.net/en_US/sdk.js";
     fjs.parentNode.insertBefore(js, fjs);
   }(document, 'script', 'facebook-jssdk'));
  

  // This is called with the results from from FB.getLoginStatus().
  function statusChangeCallback(response) {
    console.log('statusChangeCallback');
    console.log(response);
    // The response object is returned with a status field that lets the
    // app know the current login status of the person.
    // Full docs on the response object can be found in the documentation
    // for FB.getLoginStatus().
    if (response.status === 'connected') {
      // Logged into your app and Facebook.
    	loginWithFacebook (response);
    } else if (response.status === 'not_authorized') {
      // The person is logged into Facebook, but not your app.
      document.getElementById('status').innerHTML = 'Please log ' +
        'into this app.';
    } else {
      // The person is not logged into Facebook, so we're not sure if
      // they are logged into this app or not.
      document.getElementById('status').innerHTML = 'Please log ' +
        'into Facebook.';
    }
  }

  // This function is called when someone finishes with the Login
  // Button.  See the onlogin handler attached to it in the sample
  // code below.
  function checkLoginState() {
    FB.getLoginStatus(function(response) {
      statusChangeCallback(response);
    });
  }

  // Now that we've initialized the JavaScript SDK, we call 
  // FB.getLoginStatus().  This function gets the state of the
  // person visiting this page and can return one of three states to
  // the callback you provide.  They can be:
  //
  // 1. Logged into your app ('connected')
  // 2. Logged into Facebook, but not your app ('not_authorized')
  // 3. Not logged into Facebook and can't tell if they are logged into
  //    your app or not.
  //
  // These three cases are handled in the callback function.

  FB.getLoginStatus(function(response) {
    statusChangeCallback(response);
  });

  // Here we run a very simple test of the Graph API after login is
  // successful.  See statusChangeCallback() for when this call is made.
  function loginWithFacebook (response) {
    log('Successful login in facebook');
    
    var uid = response.authResponse.userID;
    log('UID: ' + uid);
    
    var accessToken = response.authResponse.accessToken;   
    log('AccessToken: ' + accessToken);    
    
    FB.api('/me', function(response) {
    	var username = response.name;
    	window.sessionStorage.setItem("currentUser", username);
    	document.getElementById('fbLoginState').innerHTML = '<br>' + lang('login_success') + ' ' + username + '!<br> '+ lang('Please wait') + ' ...';
    });

    var url = host+"/api/loginWithAccessToken";
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
					window.sessionStorage.setItem("accessToken",this.responseText);
					
					// dann gibt es einen redirekt zur Startseite
					window.document.location.href = host+"/index.html";
				}
				else{
					// Wenn es nicht geklappt hat, geben wir einen Hinweis aus und löschen das gespeichert Token
					alert (this.statusText);
					window.sessionStorage.removeItem("accessToken");
				}
			}
	};
	// hier senden wir das accessToken von Facebook
	client.send("identityProvider=FACEBOOK&accessToken="+accessToken);    
    
  }

