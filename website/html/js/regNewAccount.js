function createAccount() {
    var formData = new FormData();
    formData.set('username', document.getElementById("usernameField").value);
    formData.set('password', document.getElementById("passwordField").value);

    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "../api/createAccount.php", true);
    xhttp.addEventListener("load", function(){
        window.location.href = "http://www.w3schools.com";
    }); 
    xhttp.send(formData);
}