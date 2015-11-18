var host = ".";

(function() {
	 
	 // load jquery
     var script1 = document.createElement("SCRIPT");
     script1.src = host+'/js-lib/jquery.min.js';
     script1.type = 'text/javascript';
     document.getElementsByTagName("head")[0].appendChild(script1);

     var checkReady = function(callback) {
         if (window.jQuery) {
             callback(jQuery);
         }
         else {
             window.setTimeout(function() { checkReady(callback); }, 20);
         }
     };

     // when jquery is loaded --> inititalize rembli
     checkReady(function($) {
         $(function() {
        	 loadRessources ();
         });
     });
 })();


 function loadRessources () {
 
	  $.get("_header.html", function(data) {
	    $("R-HEADER").html(data);
	  });

	  $.get("_footer.html", function(data) {
	    $("R-FOOTER").html(data);
	  });

 }
 


 
 
 
 
 
	     