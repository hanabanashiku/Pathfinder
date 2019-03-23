<?php
    require_once("Database.php");

    header("Content-Type: application/json; charset=UTF8");

    try{
        $db = new Database();
    }
    catch(Exception $e){
        $res['error_type'] = 'Database Failed to Initialize';
        $res['details'] = $e->getMessage();
        http_response_code(500); // internal server error
        echo json_encode($res);
        die();
    }

    // listing all beacons
    if(isset($_GET["list"])){
        try{
            $res = $db->list_beacons();
        }
        catch(Exception $e){
            $res['error_type'] = 'SQL Error';
            $res['details'] = $e->getMessage();
            http_response_code(500);
            echo json_encode($res);
            die();
        }

        $res = json_encode($res);
        http_response_code(200); // OK
        echo $res;
    }

    elseif(isset($_GET["map"])){
        $id = $db->real_escape_string($_GET["map"]);
        try{
            $res = $db->get_map_beacons($id);
        }
        catch(Exception $e){
            $res['error_type'] = 'SQL Error';
            $res['details'] = $e->getMessage();
            http_response_code(500);
            echo json_encode($res);
            die();
        }

        echo json_encode($res);
        http_response_code(200); // OK
    }