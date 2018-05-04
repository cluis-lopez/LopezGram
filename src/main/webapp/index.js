$(document).ready(function(){
	
	var loc_lat;
	var loc_long;
	var camera_state = false;
	var resizedImage = "";
	
   // Check if WebApp LocalStorage is available
	if (localStorage.getItem("mail") && localStorage.getItem("token")){
		$("#username").html("<b>User:</b> " + localStorage.getItem("mail"));
		tokenCheck(localStorage.getItem("mail"), localStorage.getItem("token"));
	} else {
		$("#username").html("User: No Valid User");
		$("#modallogin").css("display", "block");
	};
	
	function tokenCheck(m, t) {
		$.ajax({
				url: "/TokenCheck",
				type: "POST",
				data: {mail: m, token: t},
				dataType: "json",
				success: function (data,status,xhr){
					if(data.mail == m && data.token ==t){ //Token is OK
						getEvents(m, t, 5);
					} else { //User must login again
						$("#modallogin").css("display", "block");
					}
				}
			});
	};

/*	Load and display the latest events
 */	
	function getEvents(m , t, ne){
		$.ajax({
			url: "/GetEvent",
			type: "POST",
			data: {mail:m, token:t, numevents:ne},
			dataType: "json",
			success: function (data, status, xhr){
				if (data.status != "UNAUTHORIZED"){
					for (i=0; i<data.NumberOfEvents; i++){
						loadEvent(data.Events[i]);
					};
				}
			},
			error: function(xhr, status, message){
				console.log("Cannot load events");
			}
		});
	};
	
	function loadEvent(event){
		console.log(event);
		html = '<div class="eventcontainer" id="'+ event.id +'">';
		html = html + '<div class="autorevent">' + event.name + '</div>'
		html = html + '<div class="pictureevent"><img src="' + event.picture + '"></div>';
		html = html + '<div class="textevent">' + event.text + '</div>'
		html = html + '<div class="footerevent"></div></div>';
		$("#megacontainer").append(html);
	};
	
	$("#logoutbutton").click(function(){
		localStorage.setItem("token", "");
		localStorage.setItem("mail", "");
		localStorage.setItem("name", "");
		location.reload();
	});
	
	// Cleanup the events area plus clean and close modals
	
	function refresh(){
		$("#inputpicture").css("display", "none");
		$("#message").val("");
		$("#foto").attr("src","");
		resizedImage = "";
		camera_state = false;
		$("#modalevent").css("display", "none");
		$("#megacontainer").empty();
		getEvents(localStorage.getItem("mail"), localStorage.getItem("token"), 5);
	};
	
/*	End of events section
*/
	
/*	Login user section
 */
	
	$("#loginbutton").click(function(){
		m = $("#mail").val();
		p = $("#password").val();
		$.ajax({
			url: "/LoginUser",
			type: "POST",
			data: {mail: m, password: p},
			dataType: "json",
			success: function (data,status,xhr){
				console.log("data: "+data);
				if(data.mail == m && data.token != "INVALID"){ //Token is OK
					localStorage.setItem("mail", data.mail);
					localStorage.setItem("token", data.token);
					localStorage.setItem("name", data.user);
					location.reload();
				} else { //User must login again
					alert("mail: "+data.mail +" ... token"+data.token);
					$(".login").css("display", "block");
				}
			},
			error: function(xhr, status, message){
				console.log(message);
			}
		});
	});
	
	$("#loginform").validate({ // initialize the plugin
        rules: {
            mail: {
                required: true,
                email: true,
            },
            password: {
                required: true,
                minlength: 5
            }
        },
        messages: {
        	password: "La password debe tener al menos 5 caracteres",
        	mail: "Direcci&oacute;n de mail v&aacute;lida: name@domain.com"
        },
        errorPlacement: function (error, element) {
            element.attr("placeholder", error[0].outerText);
        }
    });
	
	$('#loginform input').on('keyup blur', function () { // fires on every keyup & blur
        if ($('#loginform').valid()) {                   // checks form for validity
            $('#loginbutton').prop('disabled', false);
            $('#loginbutton').css('cursor', 'pointer');// enables button
        } else {
            $('#loginbutton').prop('disabled', 'disabled'); // disables button
            $('#loginbutton').css('cursor', 'not-allowed');
        }
    });

/* Bloque para gestionar la creación de eventos
 **/
 
	$('#publicar').click(function(){
		$('#modalevent').css('display', 'block');
	});
	
	$('#location').click(function(){
		navigator.geolocation.getCurrentPosition(p);
		function p(pos){
			loc_lat = pos.coords.latitude;
			loc_long = pos.coords.longitude; 
		};
	});
	
	$('#camera').click(function(){
		if (!camera_state){
			$('#inputpicture').css('display', 'block');
			$('#message').attr("rows","2");
			camera_state = true;
			$('#hiddeninput').click();
		} else {
			$('#inputpicture').css('display', 'none');
			$('#foto').attr("src", "");
			resizedImage = "";
			$('#message').attr("rows","5");
			camera_state = false;
		}
	});
	
	$('#hiddeninput').change(function(){
		readURL(this);
	});
	
	function readURL(input){
		// var ctx = document.getElementById("foto").getContext("2d");
		if (input.files && input.files[0]) {
			var reader = new FileReader();
			reader.onload = (function(tf) {
			      return function(evt) {
			    	  // resize the image before using the resolved dataURL
			    	  resize(evt.target.result, 1024, 1024, function(dataURL) {
			          $("#foto").attr("src", dataURL)
			        });
			      }
			    })(input.files[0])
			reader.readAsDataURL(input.files[0]);
		};
	};
	
	function resize(src, maxWidth, maxHeight, callback) {
	    var img = document.createElement('img');
	    img.src = src;
	    img.onload = () => {
	      var oc = document.createElement('canvas');
	      var ctx = oc.getContext('2d');
	      // resize to maxWidth px (either width or height)
	      width = img.width;
	      height = img.height;
	      if (width > height) {
              if (width > maxWidth) {
                  height *= maxWidth / width;
                  width = maxWidth;
              }
          } else {
              if (height > maxHeight) {
                  width *= maxHeight / height;
                  height = maxHeight;
              }
          }
	      oc.width = width;
	      oc.height = height;
	      ctx.drawImage(img, 0, 0, oc.width, oc.height);
	      resizedImage = dataURLToBlob(oc.toDataURL());
	      // convert canvas back to dataurl
	      callback(oc.toDataURL());
	    }
	}
	
	/* Utility function to convert a canvas to a BLOB */
	var dataURLToBlob = function(dataURL) {
	    var BASE64_MARKER = ';base64,';
	    if (dataURL.indexOf(BASE64_MARKER) == -1) {
	        var parts = dataURL.split(',');
	        var contentType = parts[0].split(':')[1];
	        var raw = parts[1];

	        return new Blob([raw], {type: contentType});
	    }

	    var parts = dataURL.split(BASE64_MARKER);
	    var contentType = parts[0].split(':')[1];
	    var raw = window.atob(parts[1]);
	    var rawLength = raw.length;

	    var uInt8Array = new Uint8Array(rawLength);

	    for (var i = 0; i < rawLength; ++i) {
	        uInt8Array[i] = raw.charCodeAt(i);
	    }

	    return new Blob([uInt8Array], {type: contentType});
	}
	/* End Utility function to convert a canvas to a BLOB      */
	
	$("#publishbutton").click(function(){
		if ($("#message").val() != "" || resizedImage != ""){
			m = localStorage.getItem("mail");
			t = localStorage.getItem("token");
			u = localStorage.getItem("user");
			fd = new FormData();
			fd.append("mail", m);
			fd.append("token", t);
			fd.append("user", u);
			fd.append("text", $("#message").val());
			fd.append("foto", resizedImage);

			$.ajax({
				url: "/UploadEvent",
				type: "POST",
				encType: "multipart/form-data",
				contentType: false,
				data: fd,
				processData: false,
				dataType: "text",
				success: function (data,status,xhr){
					console.log("data: "+data);
					if(data == "UPLOADED") {
						alert("Publicado");
						refresh();
					} else { // User must login again
						alert ("NO publicado");
						refresh();
					}
				},
				error: function(xhr, status, message){
					console.log(message);
				}
			});
		} else {
			console.log("Nada que publicar")
			$("#message").attr("placeholder", "Nada que publicar");
		}
		
	});
	
	$("#cancelbutton").click(refresh);
	
	/* Bloque para la modificación de dadtos de usuario
	 **/
	$("#user").click(function(){
		$("#modalprofile").css("display","block");
		$("#profilemail").val(localStorage.getItem("mail"));
	});
});