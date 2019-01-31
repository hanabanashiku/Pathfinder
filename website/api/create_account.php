<?php
    require_once("Database.php");
    
  // set output headers
  header("Access-Control-Allow-Origin: localhost");
  header("Content-Type: text/plain; charset=UTF-8");
  
  $d = json_decode(file_get_contents("php://input"), true);
  
  // missing some 
  if(!isset($d["username"]) || !isset($d["password"]) || !isset($d["email"]) || !isset($d["last_name"]) || !isset($d["first_name"])){
    http_response_code(400); // bad request
    echo -1;
    die();
  }
  
  $username = mysqli_real_escape_string($d["username"]);
  $password = password_hash(mysqli_real_escape_string($d["password"]));
  $email = mysqli_real_escape_string($d["email"]);
  $lname = mysqli_real_escape_string($d["last_name"]);
  $fname = mysqli_real_escape_string($d["first_name"]);
  
  // verify email format
  if(!filter_var($d["email"], FILTER_VALIDATE_EMAIL)){
    http_response_code(400); // bad request
    echo -2;
    die();
  }
  
  // verify username format
  if(preg_match("^[0-9a-zA-Z_-]{6,15}$", $d["username"]) !== 1){
    http_response_code(400); // bad request
    echo -3;
    die();
  }
  
  // verify password format
  if(preg_match("^[0-9a-zA-Z!@#$%^&*()`~\-+_=\\\/|?,.<>]{8,100}$", $d["password"]) !== 1){
    http_response_code(400); // bad request
    echo -4;
    die();
  }
  
  $db = new Database();
  
  try{
    $q = $db->query("INSERT INTO users (username, password, email, lname, fname) VALUES (`$username`, `$password`, `$email`, `$lname`, `$fname`)");
  }
  // we have violated a constraint.. probably, the user already exists.
  catch(mysqli_sql_exception $e){
    http_response_code(409); // conflict
    echo 0;
  }
  
  // verification email
  $q = $db->query("SELECT id FROM users WHERE username = `$username`");
  $q = $q->fetch_assoc();
  $id = $q["id"];
  
  $code = uniqid($username, true);
  $q = $db->query("INSERT INTO pending_emails (user, code) VALUES (`$id`, `$code`)");
  
  $msg = "<p>Thank you for choosing to utilize Pathfinder for your building navigation system. Before you can begin, you will need to verify your account by clicking <a href=\"https://path-finder.tk/verify?q=$code\">here</a> or copying the URL below:</p>";
  $msg += "<p>https://path-finder.tk/verify?q=$code</p>";
  $msg += "<p>Thank you,</p>";
  $msg += "<p align=\"center\">Pathfinder Team</p>";
  
  $header = "From: noreply@path-finder.tk";
  
  mail($email, "Email Verification", $msg, $headers);
  
  
  // send response
  http_response_code(201); // Created
  echo 1;
?>