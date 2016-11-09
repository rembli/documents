#rembli /documents

Demo web application which features:

* Rich UI client built with HTML/CSS/Javascript 
* Bootstrap as grid-system 
* Dust.js as template engine
* Custom HTML tag for client-side-includes of html-templates (e.g. header and footer)
* Custom HTML tag to translate labels 
* ROCA-style system design with smooth transitions of a SPA (Single-page-application)
* REST-API layer built with JAX-RS and Swagger-Annotations
* API documentation using Swagger (incl. Try-it-out and dedicated resources different from backend-model)
* CORS Header
* Multi-part file upload using Drag and Drop 
* Demo of a web scanner
* Token-based authentication for back-end and front-end
* "Login with facebook"-button
* Dedicated business layer
* Database persistence using SQL2O
* Import Mails from POP server

### Demo

* Demo deployment at [rembli.com](http://www.rembli.com)
* Swagger-UI to [test API](http://www.rembli.com/swagger/index.html?url=/documents/api/swagger.json)
* [Swagger-Spec](http://www.rembli.com/documents/api/swagger.json)
* Application runs on [Jelastic](https://app.jelastic.dogado.eu/) 

## Development

### Localhost

* Application:	http://localhost:8080/documents
* API: 		http://localhost:8080/documents/api/login
* Swagger-UI:	http://localhost:8080/documents/Swagger
* Swagger-Spec:	http://localhost:8080/documents/api/swagger.json
* GitHub:	https://github.com/rembli/documents 

### ToDos

* Mehrmandantenf�higkeit (Org-ID)
* Admin UI f�r User
* Tags/Properties
* Suche
* Asynchrone Thumbnails
* Volltextsuche (Asprise f�r OCR +  + Lucene)
* Automatisches Nachladen (a la Facebook)
* JS is authenticated: Gueltigkeit des tokens und Token wieder in der DB speichern, falls im Cluster-Betrieb

### Dev log

* Added import of mails (senden an rembli adresse: <username>@rembli.com)
* Add SSL support at rembli.com (https://coolestguidesontheplanet.com/redirecting-http-https-tomcat)
* AuthenticationToken und AuthenticationUser umbenannt in AccessToken und CurrentUser
* Navigation zwischen einzelnen Dokumenten erm�glicht (statt immer �ber die Liste gehen zu m�ssen)
* Fehler bei Ersatz des Bodies und Upload von Datein in der DropZone (nach Wechsel in Edit geht es nicht mehr, da Javascript fehlt und Initialisierung fehlerhaft)
* Anpassen der Browser-History entsprechend der aktuell gew�hlten Seite
* Dynamisches Laden und Ersetzen von Teilen einer Seite (Single-Page-Application)
* Caching f�r die Templates, Includes und Dictionaries
* Swagger-UI auslagern
* Dust.js-Templates, um HTML aus Javascript raus zu halten
* Neues Tag <lang> f�r �bersetzungen / Mehrsprachigkeit
* Neues Tag <include> als Ersatz f�r Server-seitige Includes
* Login mit facebook
* Scanner Ressourcen als separate web app
* Erstellung von Thumbnails von Images
* Links (bisher nur self) wird als Struktur zur�ck gegeben
* _self Links �ber eigenen Datentyp FileInfoRessource abgebildet
* �nderung von iddocument auf idDocument 
* Token kann auch als Query-Parameter "AuthorizationToken" mitgegeben werden
* Log-API
* UI Neuen Benutzer anlegen
* API f�r User anlegen
* API f�r Passwort �ndern
* es k�nnen nur noch die Dateien gesehen und bearbeitet werden, die man selber angelegt hat
* L�schen von Dateien
* 204 bei L�schen von Dokument
* API Links aus WebApp raus
* Upload Size auf 5 MB erh�hen
* POST bei /logout
* Anlegen eines neuen Dokuments gibt die ID zur�ck
* CORS Headers
* JavaDoc
* Scanner (Dynamic Web Twain)
* Drag & Drop Upload
* Bootstrap & Jquery
* Swagger aus JAX-RS Spec generieren
* File-Upload
* File-Download
* Tokens aus der DB raus und In-memory
* DELETE und PUT OPERATIONEN

### Used libraries

**Java**

* Jersey - als Implementierung von JAX-RS f�r REST
* Swagger Core Jersey 2.X - zur Erzeugung der Swagger-Spec aus der JAX-RS-Annotation (Ab hier: MAVEN wegen massiver Abh�ngigkeiten)
* JAXB - XML aus Java Objekten erzeugen
* Lombok - um sich viel "Boilerplate"-Code (v.a. Setter und Getter) zu sparen (https://projectlombok.org/)
* Genson - um Java-Objekte in JSON zu konvertieren (sehr gut mit JAXB integriert!) http://owlike.github.io/genson/
* SQL2O - als minimales Tool f�r DB-Queries und Mapping SQL-Result --> Java Objekt http://www.sql2o.org/
* Hikary - als "High-Performance" DB-Connection-Pool
* mimepull - braucht man f�r MediaTypes, wenn man Dateien (Multi-part) hochladen/runterladen will
* thumbnailator - zur Erzeugung der Thumbnails 

**Javascript**

* jQuery
* Bootstrap - Responsive Layout
* dust.js - Templating
* dropzone.js

**Tools**

* Lombok installieren durch Klick auf lombok.jar

**Best practices**

* [https://sparktutorials.github.io](https://sparktutorials.github.io)

