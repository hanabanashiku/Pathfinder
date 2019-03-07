<?php
  require_once("../vender/autoload.php");
  use OTPHP/TOTP;
  use OTPHP/Factory;

  // set output headers
  header("Access-Control-Allow-Origin: localhost");
  header("Content-Type: text/plain; charset=UTF-8");

  $d = json_decode(file_get_contents("php://input"), true);

  session_start();
  // missing required inputs
  if(!isset($_SESSION["2fa"]) || !isset($d["code"]))){
    http_response_code(400); // bad request
    echo "-1";
    die();
  }

  // verify the code
  try{
    $otp = Factory::loadFromProvisioningUri($_SESSION["2fa"]["2fa_uri"]);
  }
  catch(Exception $e){
    http_response_code(400); // bad request
    echo "-2";
    die();
  }

  if($otp->verify($d["code"])){
    http_response_code(200); // OK
    echo "1";
    // make a note of success on the server
    session_start();
    $_SESSION["2fa_success"] = true;
    die();
  }
  else{
    http_response_code(403); // forbidden
    echo "0";
    die();
  }
 ?>
