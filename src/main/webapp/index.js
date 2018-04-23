$(document).ready(function(){
	
	var loc_lat;
	var loc_long;
	var camera_state = false;
	
   // Check if WebApp LocalStorage is available
	if (localStorage.getItem("mail") && localStorage.getItem("token")){
		$("#user").html("User: " + localStorage.getItem("mail"));
		tokenCheck(localStorage.getItem("mail"), localStorage.getItem("token"));
	} else {
		$("#user").html("User: No Valid User");
		$(".modallogin").css("display", "block");
	};
	
	function tokenCheck(m, t) {
		$.ajax({
				url: "/TokenCheck",
				type: "POST",
				data: {mail: m, token: t},
				dataType: "json",
				success: function (data,status,xhr){
					if(data.mail == m && data.token ==t){ //Token is OK
						alert("User is OK");
						getEvents(m, t, 5);
					} else { //User must login again
						$(".modallogin").css("display", "block");
					}
				}
			});
	};
		
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
					localStorage.setItem("user", data.user);
					alert("User is OK");
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
        	password: "Your password must be five characters at least",
        	mail: "Enter a valid email address: name@domain.com"
        },
        errorElement : 'div',
        errorLabelContainer: '.errorTxt'
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

	/* Bloque para gestionar la creación de eventos*/
 
	$('.title').click(function(){
		$('.modalevent').css('display', 'block');
	});
	
	$('#location').click(function(){
		navigator.geolocation.getCurrentPosition(p);
		function p(pos){
			loc_lat = pos.coords.latitude;
			loc_long = pos.coords.longitude; 
		};
	});
	
	$('#camera').click(function(){
		if (camera_state == false){
			$('#inputpicture').css('display', 'block');
			$('#message').attr("rows","2");
			camera_state = true;
			$('#hiddeninput').click();
		} else {
			$('#inputpicture').css('display', 'none');
			$('#foto').attr("src", "");
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
			    	  // resize the image before using the resolved dataURL to set the thumbnail
			    	  resize(evt.target.result, 1024, function(dataURL) {
			          $("#foto").attr("src", dataURL)
			        });
			      }
			    })(input.files[0])
			reader.readAsDataURL(input.files[0]);
		};
	};
	
	function resize(src, maxWidth, callback) {
	    var img = document.createElement('img');
	    img.src = src;
	    img.onload = () => {
	      var oc = document.createElement('canvas');
	      var ctx = oc.getContext('2d');
	      // resize to [maxWidth] px
	      var scale = maxWidth / img.width;
	      oc.width = img.width * scale;
	      oc.height = img.height * scale;
	      ctx.drawImage(img, 0, 0, oc.width, oc.height);
	      // convert canvas back to dataurl
	      callback(oc.toDataURL());
	    }
	}
	
	$("#publishbutton").click(function(){
		m = localStore.getItem("mail");
		t = localStore.getItem("token");
		u = localStore.getItem("user");
		fd = new FormData();
		fd.append("mail", m);
		fd.append("token", t);
		fd.append("user", u);
		fd.append("text", $("#message").val());
		alert("Datos: "+fd);
		
		$.ajax({
			url: "/UploadEvent",
			type: "POST",
			contentType: "multipart/form-data",
			data: fd,
			processData: false,
			dataType: "text",
			success: function (data,status,xhr){
				console.log("data: "+data);
				if(data == "UPLOADED") {
					alert("Publicado");
				} else { //User must login again
					alert ("NO publicado")
				}
			},
			error: function(xhr, status, message){
				console.log(message);
			}
		});
	});
});