<?php
// exceptions
mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

  class Database extends mysqli {
    public function __construct(){
      $json = json_decode(file_get_contents("assets/sql_login.json"), true);
      $host; $user; $pw; $db;
      try{
          $host = $json["host"];
          $user = $json["user"];
          $pw = $json["pw"];
          $db = $json["db"];
      }
      catch(Exception $e){
          throw new Exception("Could not authenticate with the database.");
      }
      parent::__construct($host, $user, $pw, $db);
    }

    public function verify_user($username, $password){
      $q = $this->query("SELECT * FROM users WHERE (username = '$username' OR email = '$username')");
      if($q->num_rows != 1){
        return false;
      }
      $q = $q->fetch_assoc();
      if(!password_verify($password, $q["password"])){
        return false;
      }
      return $q;
    }
  }
?>
