<?php
// exceptions
mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

  class Database extends mysqli {
    public function __construct(){
      $json = json_decode("../assets/sql_login.json");
      try{
          $host = $json["host"];
          $user = $json["user"];
          $pw = $json["pw"];
          $db = $json["db"];
      }
      catch(Exception $e){
          die("Could not authenticate with the database.");
      }
      parent::__construct($host, $user, $pw, $db);
    }

    public function verify_user($username, $password){
      $q = $this->query("SELECT * FROM users WHERE (username = '".$username."' OR email = '".$username."') AND password = '" . $password . "'");
      if($q->num_rows != 1){
        return false;
      }
      return $q->fetch_assoc();
    }
  }
?>
