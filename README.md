# rembli /documents

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
* PDF Viewer (ViewerJS)

## Development Localhost

* Application:	http://localhost:8080/documents
* API: 		http://localhost:8080/documents/api/login
* Swagger-UI:	http://localhost:8080/documents/Swagger
* Swagger-Spec:	http://localhost:8080/documents/api/swagger.json
* GitHub:	https://github.com/rembli/documents 

### ToDos

* Mehrmandantenfähigkeit (Org-ID)
* Admin UI für User
* Tags/Properties
* Suche
* Asynchrone Thumbnails
* Volltextsuche (Asprise für OCR +  + Lucene)
* Automatisches Nachladen (a la Facebook)
* JS is authenticated: Gueltigkeit des tokens und Token wieder in der DB speichern, falls im Cluster-Betrieb

### Dev log

* create thumbnail for pdf and eml-files 
* integrated pdf view ViewJS
* Added import of mails (send to rembli address: <username>@rembli.com)
* Add SSL support at rembli.com (https://coolestguidesontheplanet.com/redirecting-http-https-tomcat)
* AuthenticationToken und AuthenticationUser umbenannt in AccessToken und CurrentUser
* Navigation zwischen einzelnen Dokumenten ermöglicht (statt immer über die Liste gehen zu müssen)
* Fehler bei Ersatz des Bodies und Upload von Datein in der DropZone (nach Wechsel in Edit geht es nicht mehr, da Javascript fehlt und Initialisierung fehlerhaft)
* Anpassen der Browser-History entsprechend der aktuell gewählten Seite
* Dynamisches Laden und Ersetzen von Teilen einer Seite (Single-Page-Application)
* Caching für die Templates, Includes und Dictionaries
* Swagger-UI auslagern
* Dust.js-Templates, um HTML aus Javascript raus zu halten
* Neues Tag <lang> für Übersetzungen / Mehrsprachigkeit
* Neues Tag <include> als Ersatz für Server-seitige Includes
* Login mit facebook
* Scanner Ressourcen als separate web app
* Erstellung von Thumbnails von Images
* Links (bisher nur self) wird als Struktur zurück gegeben
* _self Links über eigenen Datentyp FileInfoRessource abgebildet
* Änderung von iddocument auf idDocument 
* Token kann auch als Query-Parameter "AuthorizationToken" mitgegeben werden
* Log-API
* UI Neuen Benutzer anlegen
* API für User anlegen
* API für Passwort ändern
* es können nur noch die Dateien gesehen und bearbeitet werden, die man selber angelegt hat
* Löschen von Dateien
* 204 bei Löschen von Dokument
* API Links aus WebApp raus
* Upload Size auf 5 MB erhöhen
* POST bei /logout
* Anlegen eines neuen Dokuments gibt die ID zurück
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

* Jersey - als Implementierung von JAX-RS für REST
* Swagger Core Jersey 2.X - zur Erzeugung der Swagger-Spec aus der JAX-RS-Annotation (Ab hier: MAVEN wegen massiver Abhängigkeiten)
* JAXB - XML aus Java Objekten erzeugen
* Lombok - um sich viel "Boilerplate"-Code (v.a. Setter und Getter) zu sparen (https://projectlombok.org/)
* Genson - um Java-Objekte in JSON zu konvertieren (sehr gut mit JAXB integriert!) http://owlike.github.io/genson/
* SQL2O - als minimales Tool für DB-Queries und Mapping SQL-Result --> Java Objekt http://www.sql2o.org/
* Hikary - als "High-Performance" DB-Connection-Pool
* mimepull - braucht man für MediaTypes, wenn man Dateien (Multi-part) hochladen/runterladen will
* thumbnailator - zur Erzeugung der Thumbnails 

**Javascript**

* jQuery
* Bootstrap - Responsive Layout
* dust.js - Templating
* dropzone.js - Drag 'n Drop Upload
* ViewerJS - PDF

**Tools**

* Lombok installieren durch Klick auf lombok.jar

**Best practices**

* [https://sparktutorials.github.io](https://sparktutorials.github.io)

