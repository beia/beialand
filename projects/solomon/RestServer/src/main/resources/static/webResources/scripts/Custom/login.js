

window.onload = function() {
    // Get the input field
    var nameField = document.getElementById("nameField");
    var passField = document.getElementById("passField");

    // Execute a function when the user releases a key on the keyboard
    nameField.addEventListener("keyup", function(event) {
        // Number 13 is the "Enter" key on the keyboard
        if (event.keyCode === 13) {
            document.getElementById("loginBtn").click();
        }
    });
    passField.addEventListener("keyup", function(event) {
        // Number 13 is the "Enter" key on the keyboard
        if (event.keyCode === 13) {
            document.getElementById("loginBtn").click();
        }
    });
}


function submitForm(){
    if(checkFields()){
        document.getElementById('login-form').submit();
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

