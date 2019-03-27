<?php
    session_start();
    $servername = "localhost";
    $username = "uploader";
    $password = "Senior19!";
    $dbname = "Navigation";

    // Create connection
    $conn = mysqli_connect($servername, $username, $password, $dbname);
    // Check connection
    if (!$conn) {
        die("Connection failed: " . mysqli_connect_error());
    }

    $currentUser = $_SESSION['username'];
    $buildingName = $_POST['building'];

    $sql = "SELECT * FROM buildings WHERE owner = '$currentUser' AND name = '$buildingName'";
    
    if ($res = mysqli_query($conn, $sql)) {
        if (mysqli_num_rows($res) > 0) {
            while ($row = mysqli_fetch_array($res)) {
                $buildingID = $row['id'];
            }
            // mysqli_free_res($res);  //This is breaking the .php file, not sure why...
        }
        else {
            // echo "No matching records are found.";
        }
    }
    else {  
        // echo "ERROR: Could not able to execute $sql. " .mysqli_error($conn);
    }
    
    $floorNumber = $_POST['floor'];

    $sql = "SELECT * FROM floors WHERE floor_number = '$floorNumber' AND buildingID = '$buildingID'";
    
    if ($res = mysqli_query($conn, $sql)) {
        if (mysqli_num_rows($res) > 0) {
            while ($row = mysqli_fetch_array($res)) {
                $floorID = $row['floor_id'];
            }
            // mysqli_free_res($res);  //This is breaking the .php file, not sure why...
        }
        else {
            // echo "No matching records are found.";
        }
    }
    else {  
        // echo "ERROR: Could not able to execute $sql. " .mysqli_error($conn);
    }

    $xPos = $_SESSION['xPos'];
    $yPos = $_SESSION['yPos'];

    $sql = "SELECT * FROM nodes WHERE floorID = '$floorID' AND buildingID = '$buildingID' AND x = '$xPos' AND y = '$yPos'";

    if ($res = mysqli_query($conn, $sql)) {
        if (mysqli_num_rows($res) > 0) {
            while ($row = mysqli_fetch_array($res)) {
                $nodeID = $row['id'];
            }
            // mysqli_free_res($res);  //This is breaking the .php file, not sure why...
        }
        else {
            // echo "No matching records are found.";
        }
    }
    else {  
        // echo "ERROR: Could not able to execute $sql. " .mysqli_error($conn);
    }
    mysqli_close($conn);

    $nodeIDJSON = json_encode($nodeID);
    echo $nodeIDJSON;
?>