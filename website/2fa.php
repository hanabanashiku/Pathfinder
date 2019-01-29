<?php
  require_once("vender/autoload.php");
  use OTPHP/TOTP;
  use OTPHP/Factory;

  // set output headers
  header("Access-Control-Allow-Origin: localhost");
  header("Content-Type: text/plain; charset=UTF-8");

  $d = json_decode(file_get_contents("php://input"), true);

  // missing required inputs
  if(!isset($d["uri"] || !isset($d["code"]))){
    http_response_code(400); // bad request
    echo "-1";
    die();
  }

  try{
    $otp = Factory::loadFromProvisioningUri($d["uri"]);
  }
  catch(Exception $e){
    http_response_code(400); // bad request
    echo "-2";
    die();
  }

  if($otp->verify($d["code"])){
    http_response_code(200); // OK
    echo "1";
    die();
  }
  else{
    http_response_code(403); // forbidden
    echo "0";
    die();
  }
 ?>
