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
      $q = $this->query("SELECT id, name, owner FROM buildings");
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
        $q = $this->query("SELECT id, name, owner FROM buildings WHERE name LIKE '$needle'");
        $res['buildings'] = [];
        while ($row = $q->fetch_assoc()) {
            array_push($res['buildings'], $row);
        }
        $res['total'] = sizeof($res['buildings']);
        return $res;
    }

    private function get_floor_ids($map_id){
        $this->select_db($this->nav_db);
        $q = $this->query("SELECT floor_id, floor_number FROM floors WHERE buildingID = $map_id");
        $res = [];
        while($r = $q->fetch_assoc())
            $res[$r['floor_id']] = $r['floor_number'];
        return $res;
    }

    /**
     * Get all nodes relating to a map.
     * @param $map_id int The id of the map to grab.
     * @param array|null $floors The array associating floor ids to floor numbers.
     * @return array An array of node information.
     */
    public function get_map_nodes(int $map_id, $floors = null){
        $this->select_db($this->nav_db);
        $map_id = $this->real_escape_string($map_id);
        $q = $this->query("SELECT * FROM nodes WHERE buildingID = '$map_id'");
        if($floors == null)
            $floors = $this->get_floor_ids($map_id);
        $res = [];

        while($r = $q->fetch_assoc()){
            $node = array(
                'id' => $r['id'],
                'coordinate' => array(
                    'x' => $r['x'],
                    'y' => $floors[$r['floorID']],
                    'z' => $r['y']
                ),
                'type' => $r['type']
            );

            switch($node['type']){
                case 'room':
                    $rm = $this->query("SELECT * FROM rooms WHERE id = '" . $node['id'] ."'");
                    if($rm->num_rows == 0)
                        continue;
                    $r_row = $rm->fetch_assoc();
                    $node['room_number'] = $r_row['room_num'];
                    $node['name'] = $r_row['name'];
                    $node['requires_auth'] = $r_row['requires_auth'] == 1;
                    break;

                case 'floor_connector':
                    $fc = $this->query("SELECT * FROM  floor_connectors WHERE id = '". $node['id'] . "'");
                    if($fc->num_rows == 0)
                        continue;
                    $f_row = $fc->fetch_assoc();
                    $node['name'] = $f_row['name'];
                    $node['connector_type'] = $f_row['type'];
                    $node['requires_auth'] = $f_row['requires_auth'] == 1;
                    $node['is_operational'] = $f_row['is_operational'] == 1;
                    break;

                case 'intersection': // no extra properties
                    break;
                default: // unknown node type
                    continue;
            }
            array_push($res, $node);
        }
        return $res;
    }

    public function get_map_edges(int $map_id){
        $this->select_db($this->nav_db);
        $map_id = $this->real_escape_string($map_id);
        $q = $this->query("SELECT * FROM edges WHERE building_id = $map_id");
        $edges = [];
        while($r = $q->fetch_assoc()){
            array_push($edges, array($r['node1'], $r['node2']));
        }
        return $edges;
    }

    /**
     * Get all beacons related to a map.
     * @param $map_id int The map's ID.
     * @param array|null $floors The array associating floor ids to floor numbers.
     * @return array An array of beacons.
     */
    public function get_map_beacons($map_id, $floors = null){
        $this->select_db($this->nav_db);
        $map_id = $this->real_escape_string($map_id);
        if($floors == null)
            $floors = $this->get_floor_ids($map_id);
        $q = $this->query("SELECT * FROM beacons WHERE buildingID = '$map_id'");
        $beacons = [];
        while($r = $q->fetch_assoc()){
            $b = array(
                'id' => $r['beaconID'],
                'ssid' => 'PF_' . $map_id . '_' . $r['beaconID'],
                'coordinate' => array(
                    'x' => $r['x'],
                    'y' => $floors[$r['floorID']],
                    'z' => $r['y']
                )
            );
            array_push($beacons, $b);
        }
        return $beacons;
    }

    /**
     * Get all beacons from the database.
     * @return array An array of all beacons in the database.
     */
    public function list_beacons(){
        $this->select_db($this->nav_db);
        $beacons = [];
        $q = $this->query("SELECT id from buildings");
        while($r = $q->fetch_assoc())
            foreach($this->get_map_beacons($r['id']) as $i)
                array_push($beacons, $i);
        return $beacons;
    }

    /**
     * Get a map from the database.
     * @param int $id The id of the map.
     * @param bool $showImage Whether to pull images.
     * @return array|false The map, or false on failure.
     */
    public function get_map(int $id, $showImage = false){
        $this->select_db($this->nav_db);
        $id = $this->real_escape_string($id);
        $floors = $this->get_floor_ids($id);
        $q = $this->query("SELECT * FROM buildings WHERE id = $id");
        if($q->num_rows == 0)
            return false;
        $r = $q->fetch_assoc();

        $map = array(
            'id' => $id,
            'name' => $r['name'],
            'address' => $r['address'],
            'owner' => $r['owner'],
            'nodes' => $this->get_map_nodes($id, $floors),
            'edges' => $this->get_map_edges($id),
            'beacons' => $this->get_map_beacons($id, $floors)
        );

        if($showImage){
            $map['floor_images'] = [];
            $q = $this->query("SELECT floor_number, path_to_image FROM floors WHERE buildingID = $id ORDER BY floor_number ASC");
            while($r = $q->fetch_assoc())
                $map['floor_images'][$r['floor_number']] = file_get_contents($r['path_to_image']);
        }

        return $map;
    }
  }
