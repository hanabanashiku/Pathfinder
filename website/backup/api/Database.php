<?php
// exceptions
mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

/**
 * Class for managing calls to the mysql database.
 */
class Database extends mysqli {
    private $user_db;
    private $nav_db;

    /**
     * Database constructor.
     * @throws Exception if the connection could not be made.
     */
    public function __construct(){
      $json = json_decode(file_get_contents("/var/www/html/assets/sql_login.json"), true);
      try{
          $host = $json["host"];
          $user = $json["user"];
          $pw = $json["pw"];
          $this->user_db = $json["user_db"];
          $this->nav_db = $json["nav_db"];
      }
      catch(Exception $e){
          throw new Exception("Could not authenticate with the database.");
      }
      parent::__construct($host, $user, $pw, $this->user_db);
    }

    public function verify_user($username, $password){
      $this->select_db($this->user_db);
      $q = $this->query("SELECT * FROM users WHERE (username = '$username' OR email = '$username')");
      if($q->num_rows != 1){
        return false;
      }
      $q = $q->fetch_assoc();
      if(!password_verify($password, $q["userPassword"])){
        return false;
      }
      return $q;
    }


      /**
       * Get list of building maps from database.
       * @return array associative array with property 'buildings' containing all buildings, and 'total' with number of entries.
       */
    public function list_maps(){
      $this->select_db($this->nav_db);
      $q = $this->query("SELECT id, name, address FROM buildings");
      $res['buildings'] = [];
      while($row = $q->fetch_assoc()){
        array_push($res['buildings'], $row);
      }
      $res['total'] = sizeof($res['buildings']);
      return $res;
    }

      /**
       * Get list of building maps matching a given search term.
       * @param $needle string The search term to use.
       * @return array associative array with property 'buildings' containing all buildings, and 'total' with number of entries.
       */
    public function find_maps($needle) {
        $this->select_db($this->nav_db);
        $needle = $this->real_escape_string($needle);
        $q = $this->query("SELECT id, name, address FROM buildings WHERE name LIKE '$needle'");
        $res['buildings'] = [];
        while ($row = $q->fetch_assoc()) {
            array_push($res['buildings'], $row);
        }
        $res['total'] = sizeof($res['buildings']);
        return $res;
    }

      /***
       * Get a map from the database.
       * @param int $id The database index of the map.
       * @return mixed false on failure, or an associative array containing map information.
       */
    public function get_map(int $id){
      $this->select_db($this->nav_db);
      $id = $this->real_escape_string($id);
      $q = $this->query("SELECT * FROM buildings WHERE id = '$id'");
      // invalid id!
      if($q->num_rows != 1)
        return false;
      $q = $q->fetch_assoc();

      $res['id'] = $q['id'];
      $res['name'] = $q['name'];
      $res['address'] = $q['address'];
      $res['map_image'] = file_get_contents($q['map_image_path']);
      $res['nodes'] = []; // list of nodes

      $q = $this->query("SELECT * FROM nodes WHERE building = '$id'");
      while($r = $q->fetch_assoc()) {
          $n_id = $r['id'];
          $node['id'] = $n_id;
          $node['x'] = $r['x'];
          $node['y'] = $r['y'];
          $node['type'] = $r['type'];

          switch ($r['type']) {
              case "room":
                  $rm = $this->query("SELECT * FROM rooms WHERE id = '$n_id'");
                  if ($rm->num_rows != 1)
                      break; // not a room?
                  $rm = $rm->fetch_assoc();
                  $node['room_number'] = $rm['room_num'];
                  $node['name'] = $rm['name'];
                  $node['requires_auth'] = $rm['requires_auth'];
                  break;

              case "floor_connector":
                  $fc = $this->query("SELECT * FROM floor_connectors WHERE id = '$n_id'");
                  if ($fc->num_rows != 1)
                      break;
                  $fc = $fc->fetch_assoc();
                  $node['name'] = $fc['name'];
                  $node['connector_type'] = $fc['type'];
                  $node['requires_auth'] = $fc['requires_auth'];
                  $node['is_operational'] = $fc['is_operational'];
                  break;
          }
          array_push($res['nodes'], $node);
      }
      return $res;
    }
  }