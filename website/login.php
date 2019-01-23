<?php
const EXPIRE_TIME = 3600;

if(defined($_GET["error"])){
    // error message   
}

else if(defined($_GET["logout"])){
    session_start();
    unset($_SESSION["username"]);
    unset($_SESSION["expires_in"]);
    http_redirect("index");
}

else{
    if(!defined($_POST["username"]) || !defined($_POST["password"]))
        http_redirect("index");
        
    $json = json_decode("assets/sql_login.json");
    try{
         $host = $json["host"];
        $user = $json["user"];
        $pw = $json["pw"];
    }
    catch(Exception $e){
        die("Could not authenticate with the database.");
    }
    
    // get user data
    $username = mysqli_real_escape_string($_POST["username"]);
    $password = password_hash(mysqli_real_escape_string($_POST["password"]));
    
    $conn = new mysqli($host, $user, $pw);
    if($conn->connect_error){
        header("Location: https://path-finder.tk/login?error=" . html_entities($conn->connect_error));
        die();
    }
    
    $q = $conn->query("SELECT * FROM users WHERE username = '".$username."' AND password = '" . $password . "'");
    if($q->num_rows != 1){ // password didn't match
        http_redirect("login?error=" . html_entities("Invalid username or password."));
        die();
    }
    
    // set the session
    session_start();
    $_SESSION["username"] = $username;
    $_SESSION["expires_in"] = time() + EXPIRE_TIME;
    http_redirect("home");
}
?>