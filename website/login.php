<?php
const EXPIRE_TIME = 3600;

if(isset($_GET["error"])){
    die($_GET["error"]);
}

else if(isset($_GET["logout"])){
    session_start();
    unset($_SESSION["username"]);
    unset($_SESSION["expires_in"]);
    header("Location: index");
}

else{
    if(!isset($_POST["username"]) || !isset($_POST["password"]))
        header("Location: index");
        
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
    if($conn->connect_errno){
        header("Location: login?error=" . htmlentities($conn->connect_error));
        die();
    }
    
    $q = $conn->query("SELECT * FROM users WHERE username = '".$username."' AND password = '" . $password . "'");
    if($q->num_rows != 1){ // password didn't match
        header("Location: login?error=" . html_entities("Invalid username or password."));
        die();
    }
    
    // set the session
    session_start();
    $_SESSION["username"] = $username;
    $_SESSION["expires_in"] = time() + EXPIRE_TIME;
    header("Location: home");
}
?>