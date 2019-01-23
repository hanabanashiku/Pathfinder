<?php
session_start();

// is the session expired?
if(isset($_SESSION["expires_in"]) && $_SESSION["expires_in"] <= time()){
    unset($_SESSION["expires_in"]);
    unset($_SESSION["username"]);
}

// we need to log in!
if(!isset($_SESSION["username"]) || !isset($_SESSION["expires_in"]))
    http_redirect("index");
?>