function submitForm(){
    if(checkFields()){
        requestData = {};
        requestData["requestType"] = "register";
        requestData["username"] = document.getElementById("emailField").value;
        requestData["password"] = document.getElementById("passField").value;
        requestData["name"] = document.getElementById("nameField").value;


        var request = $.ajax({
          type: "post",
          url: window.location.origin,
          contentType: "application/json",
          data: JSON.stringify(requestData),
        });

        request.fail(function(jqXHR,textStatus,errorThrown){
          alert("ERROR occurred: " + errorThrown);
        });

        request.done(function(response, textStatus, jqXHR){
            if(response["success"] == false){
              document.getElementById("messageDiv").innerHTML = messageText + "This username is already associated to another account!</div>";
            } else {
              window.location.href = "/login";
            }
        });
    }
}

function checkFields(){
  messageText = "<div class='ui negative message'><div class='header'>Registration error </div>";
  if(document.getElementById("nameField").value.length == 0){
    document.getElementById("messageDiv").innerHTML = messageText + "User Name field is empty!</div>";
    return false;
  }
  if(document.getElementById("emailField").value.length == 0){
    document.getElementById("messageDiv").innerHTML = messageText + "Email field is empty!</div>";
    return false;
  }
  if(document.getElementById("passField").value.length == 0){
    document.getElementById("messageDiv").innerHTML = messageText + "Password field is empty!</div>";
    return false;
  }
  if(document.getElementById("confirmField").value.length == 0){
    document.getElementById("messageDiv").innerHTML = messageText + "Confirm password field is empty!</div>";
    return false;
  }
  if(document.getElementById("passField").value != document.getElementById("confirmField").value){
    document.getElementById("messageDiv").innerHTML = messageText + "Password is not corectly confirmed!</div>";
    return false;
  }

  return true;
}
