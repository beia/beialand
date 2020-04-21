function submitForm(){
    if(checkFields()){
        requestData = {};
        requestData["requestType"] = "login";
        requestData["username"] = document.getElementById("nameField").value;
        requestData["password"] = document.getElementById("passField").value;



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
            if(response["authToken"] == null){
              document.getElementById("messageDiv").innerHTML = "<div class='ui negative message'>" +
                                                                  "<div class='header'>Authentication error </div>" +
                                                                  "Wrong UserName or Password!" +
                                                                 "</div>";
            } else {
              window.location.href = "/solomon?authToken=" + response["authToken"] + "&name=" + document.getElementById("nameField").value;
            }

        });
    }
}


 function checkFields(){
  if(document.getElementById("nameField").value.length == 0){
    document.getElementById("messageDiv").innerHTML = "<div class='ui negative message'>" +
                                                        "<div class='header'>Authentication error </div>" +
                                                        "User Name field is empty!" +
                                                       "</div>";
    return false;
  }
  if(document.getElementById("passField").value.length == 0){
    document.getElementById("messageDiv").innerHTML = "<div class='ui negative message'>" +
                                                        "<div class='header'>Authentication error </div>" +
                                                        "Password field is empty!" +
                                                       "</div>";
    return false;
  }

  return true;
}
