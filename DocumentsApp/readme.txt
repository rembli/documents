Nutzung der Anwendung
--------------------------------------------------------------------------------
- Starts der Anwendung im Browser: 	http://localhost:8080/documents
- Swagger-Dokumentation: 			http://localhost:8080/documents/Swagger
- Nutzung der API: 					http://localhost:8080/documents/api/login
- Swagger-Spec: 					http://localhost:8080/documents/api/swagger.json
- GitHub							https://github.com/rembli/documents 

TODOS
--------------------------------------------------------------------------------
- Fehler bei Ersatz des Bodies und Upload von Datein in der DropZone (nach Wechsel in Edit geht es nicht mehr, da Javascript fehlt und Initialisierung fehlerhaft)
- Mehrmandantenf�higkeit (Org-ID)
- Admin UI f�r User
- Tags/Properties
- Suche

EVENTUELL
--------------------------------------------------------------------------------
- Email-integration (senden an rembli adresse: client-id@postbox.rembli.com)
- Asynchrone Thumbnails
- Volltextsuche (Asprise f�r OCR +  + Lucene)
- Automatisches Nachladen (a la Facebook)
- JS is authenticated: Gueltigkeit des tokens
- Token wieder in der DB speichern, falls im Cluster-Betrieb

ERLEDIGT
--------------------------------------------------------------------------------
- Swagger-UI auslagern
- Dust.js-Templates, um HTML aus Javascript raus zu halten
- Neues Tag <lang> f�r �bersetzungen / Mehrsprachigkeit
- Neues Tag <include> als Ersatz f�r Server-seitige Includes
- Login mit facebook
- Loeschen in die details
- Scanner Ressourcen als separate web app
- Erstellung von Thumbnails von Images
- GitHub
- Beim FileUpload via DropZone kommt der Dateiname nicht mehr mit
- Links (bisher nur self) wird als Struktur zur�ck gegeben
- Es wird das Authentication-Token gleich mit dem Prefix "Bearer " ausgeliefert
_ _self Links �ber eigenen Datentyp FileInfoRessource abgebildet
- �nderung von iddocument auf idDocument 
- Token kann auch als Query-Parameter "AuthorizationToken" mitgegeben werden
- Scrolling bei den Log-Files
- Code reorganisiert
- Log-API
- UI Neuen Benutzer anlegen
- Initialisierung der DropZone erst wenn Dokument geladen ist
- API f�r User anlegen
- API f�r Passwort �ndern
- es k�nnen nur noch die Dateien gesehen und bearbeitet werden, die man selber angelegt hat
- L�schen von Dateien
- 204 bei L�schen von Dokument
- API Links aus WebApp raus
- Upload Size auf 5 MB erh�hen
- Auch bei Anlagen wird die DropZone verwendet
- Fehler bei Upload via Blackberry (NullPointer)
- POST bei /logout
- Anlegen eines neuen Dokuments gibt die ID zur�ck
- CORS Headers
- JavaDoc
- Scanner (Dynamic Web Twain)
- Drag & Drop Upload
- Style
- Bootstrap & Jquery
- Swagger aus JAX-RS Spec generieren
- File-Upload
- File-Download
- Tokens aus der DB raus und In-memory
- DELETE und PUT OPERATIONEN

LIBRARIES
--------------------------------------------------------------------------------
*** JAVA ***
Jersey - als Implementierung von JAX-RS f�r REST
SQL2O - als minimales Tool f�r DB-Queries und Mapping SQL-Result --> Java Objekt http://www.sql2o.org/
Hikary - als "High-Performance" DB-Connection-Pool
Lombok - um sich viel "Boilerplate"-Code (v.a. Setter und Getter) zu sparen (https://projectlombok.org/)
JAXB - XML aus Java Objekten erzeugen
Genson - um Java-Objekte in JSON zu konvertieren (sehr gut mit JAXB integriert!) http://owlike.github.io/genson/
mimepull - braucht man f�r MediaTypes, wenn man Dateien (Multi-part) hochladen/runterladen will
Swagger Core Jersey 2.X - zur Erzeugung der Swagger-Spec aus der JAX-RS-Annotation (Ab hier: MAVEN wegen massiver Abh�ngigkeiten)
thumbnailator - zur Erzeugung der Thumbnails 

*** JAVASCRIPT ***
jQuery
Bootstrap - Responsive Layout
dust.js - Templating
dropzone.js


TOOLS
--------------------------------------------------------------------------------
Postman (Chrome Plugin) um REST-APIs zu testen
SOAP-UI
JUnit
Maven
JavaDoc

Best Practice Referenzen
--------------------------------------------------------------------------------
https://sparktutorials.github.io


Konfiguration (einmal pro Eclipse-IDE)
--------------------------------------------------------------------------------
- Lombok installieren durch Klick auf lombok.jar
