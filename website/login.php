<?php
// Get dependencies
require_once('vendor/autoload.php')

const EXPIRE_TIME = 3600;

// start the user session
function start_session($uname, $fname, $lname){
  session_start();
  $_SESSION["username"] = $uname;
  $_SESSION["first_name"] = $fname;
  $_SESSION["last_name"] = $lname;
  $_SESSION["expires_in"] = time() + EXPIRE_TIME;
}

// There was an error
// todo error page
if(isset($_GET["error"])){
    die($_GET["error"]);
}

// The user wants to logout
else if(isset($_GET["logout"])){
    session_start();
    unset($_SESSION["username"]);
    unset($_SESSION["expires_in"]);
    header("Location: index");
}

// The user has to verify their account using 2FA-OTP
else if(isset($_GET["2fa"])){
  if(!isset($_SESSION["2fa"])){
    header("Location: login");
    die();
  }
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

    $row = $q->fetch_asssoc();
    // 2FA check
    if($row["2fa_uri"] != null){
      $_SESSION["2fa"] = array(
        "username" = $row["username"],
        "first_name" = $row["fname"],
        "last_name" = $row["lname"],
        "2fa_uri" = $row["2fa_uri"]
      );
      header("Location: login?2fa")
      die();
    }

    // start the user session and redirect
    start_session($row["username"], $row["fname"], $row["lname"])
    header("Location: home");
}
?>
