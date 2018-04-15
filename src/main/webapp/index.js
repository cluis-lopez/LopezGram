$(document).ready(function(){

   // Check if WebApp LocalStorage is available
	if (localStorage.getItem("mail") && localStorage.getItem("token")){
		$("#user").html("User: " + localStorage.getItem("mail"));
		tokenCheck(localStorage.getItem("mail"), localStorage.getItem("token"));
	} else {
		$("#user").html("User: No Valid User");
		$(".login").css("display", "block");
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
					} else { //User must login again
						$(".login").css("display", "block");
					}
				}
			});
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
});