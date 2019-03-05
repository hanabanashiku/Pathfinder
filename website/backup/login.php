<?php
// Get dependencies
require_once('vendor/autoload.php');
require_once('api/Database.php');

const EXPIRE_TIME = 3600;

// start the user session
function start_user_session($uname, $fname, $lname){
  if(session_status() == PHP_SESSION_NONE){
    session_start();
  }
  $_SESSION["username"] = $uname;
  $_SESSION["first_name"] = $fname;
  $_SESSION["last_name"] = $lname;
  $_SESSION["expires_in"] = time() + EXPIRE_TIME;
}

// There was an error
// TODO error page
if(isset($_GET["error"])){
    die($_GET["error"]);
    ?>
    <?php
}

// The user wants to logout
else if(isset($_GET["logout"])){
    session_start();
    unset($_SESSION["username"]);
    unset($_SESSION["expires_in"]);
    header("Location: index");
    die();
}

// The user has to verify their account using 2FA-OTP
else if(isset($_GET["2fa"])){
  session_start();
  if(!isset($_SESSION["2fa"])){
    header("Location: index");
    die();
  }

  // sucessful 2fa
  elseif(isset($_SESSION["2fa_success"]) && $_SESSION["2fa_success"]){
    start_user_session($_SESSION["2fa"]["username"], $_SESSION["2fa"]["first_name"],
      $_SESSION["last_name"]);
      unset($_SESSION["2fa"]);
      unset($_SESSION["2fa_success"]);
      header("Location: home");
      die();
  }

  // TODO 2fa form
  ?>

  <?php
}

else{
    if(!isset($_POST["username"]) || !isset($_POST["password"])){
      header("Location: index");
      die();
    }

    $conn;
    try{
      $conn = new Database();
    }
    catch(Exception $e){
      header("Location: login?error=" . htmlentities($e->getMessage()));
      die();
    }
    
    // get user data
    $username = $conn->real_escape_string($_POST["username"]);
    $password = $conn->real_escape_string($_POST["password"]);

    
    $q = $conn->verify_user($username, $password);
    if(!$q){ // password didn't match
        header("Location: login?error=" . htmlentities("Invalid username or password."));
        die();
    }

    // 2FA check
    if($q["2fa_uri"] != null){
      $_SESSION["2fa"] = array(
        "username" => $q["username"],
        "first_name" => $q["fname"],
        "last_name" => $q["lname"],
        "2fa_uri" => $q["2fa_uri"]
      );
      header("Location: login?2fa");
      die();
    }

    // start the user session and redirect
    start_user_session($q["username"], $q["fname"], $q["lname"]);
    header("Location: home");
}
?>
