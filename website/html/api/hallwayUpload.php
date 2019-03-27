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

    // Escape user inputs for security
    $node1 = mysqli_real_escape_string($conn, $_POST['node1']);
    $node2 = mysqli_real_escape_string($conn, $_POST['node2']);

    $sql = "INSERT INTO edges (building_id, node1, node2) VALUES
    ('$buildingID', '$node1', '$node2')";

    if (mysqli_query($conn, $sql)) {
        // echo "New record created successfully";
    } else {
        // echo "Error: " . $sql . "<br>" . mysqli_error($conn);
    }
    mysqli_close($conn);
?>