<?php
    require_once("Database.php");

    // set output headers
    header("Content-Type: application/json; charset=UTF8");

    try{
        $db = new Database;
    }
    catch(Exception $e){
        $res['error_type'] = 'Database Failed to Initialize';
        $res['details'] = $e->getMessage();
        http_response_code(500); // internal server error
        echo json_encode($res);
        die();
    }

    // we are listing all maps
    if(isset($_GET['list'])){
        try{
            $res = $db->list_maps();
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

    // get a specific map
    elseif(isset($_GET['id'])){
        try{
            $id = $db->real_escape_string($_GET['id']);
            $res = $db->get_map($id);
        }
        catch(Exception $e){
            $res['error_type'] = 'SQL Error';
            $res['details'] = $e->getMessage();
            http_response_code(500);
            echo json_encode($res);
            die();
        }

        if(!$res){
            http_response_code(204); // No content
            die();
        }

        http_response_code(200); // OK
        echo json_encode($res);
    }

    // get map by search keyword.
    elseif(isset($_GET['q'])){
        try{
            $q = $db->real_escape_string($_GET['q']);
            $res = $db->find_maps($q);
        }
        catch(Exception $e){
            $res['error_type'] = 'SQL Error';
            $res['details'] = $e->getMessage();
            http_response_code(500);
            echo json_encode($res);
            die();
        }

        if($res['total'] == 0)
            http_response_code(204);
        else http_response_code(200);

        echo json_encode($res);
    }

    else
        http_response_code(400); // bad request