<?php
    $buildingNames = array();
    $buildingIDs = array();
    $imagePaths = array();
    
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
    $sql = "SELECT * FROM buildings WHERE owner = '$currentUser'";

    if ($res = mysqli_query($conn, $sql)) {
        if (mysqli_num_rows($res) > 0) {
            while ($row = mysqli_fetch_array($res)) {
                array_push($buildingNames, $row['name']);
                array_push($buildingIDs, $row['id']);
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

    for ($i = 0; $i < count($buildingIDs); $i++) {
        $tempID = $buildingIDs[$i];
        $sql = "SELECT * FROM floors WHERE buildingID = '$tempID' AND floor_number = '1'";

        if ($res = mysqli_query($conn, $sql)) {
            if (mysqli_num_rows($res) > 0) {
                while ($row = mysqli_fetch_array($res)) {
                    array_push($imagePaths, $row['path_to_image']);
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
    }
    
    mysqli_close($conn);

    $data = array
    (
        $buildingNames,
        $imagePaths
    );

    $dataJSON = json_encode($data);
    echo $dataJSON; 
?>